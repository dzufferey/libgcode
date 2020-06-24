package libgcode.utils

import libgcode.abstractmachine._
import libgcode._
import libgcode.extractor._
import Plane._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.StringBuilder

import io.github.dzufferey.x3DomViewer._
import scalatags.Text.all._
import X3D._

class Viewer extends AbstractMachine {

    var circularInterpolationPrecision = 0.5 //roughly one line segment every 0.5 mm

    protected val toDraw = ArrayBuffer.empty[Tag]

    def resultByLines = toDraw.toSeq
    def result = group(toDraw.toSeq)

    override protected def linearMotion(x: Double, y: Double, z: Double,
                                        a: Double, b: Double, c: Double,
                                        f: Double) = {
        val x0 = getX
        val y0 = getY
        val z0 = getZ
        super.linearMotion(x,y,z,a,b,c,f)
        val x1 = getX
        val y1 = getY
        val z1 = getZ
        val points = coordinate( point := s"${x0} ${y0} ${z0}, ${x1} ${y1} ${z1}")
        val appear = if (f == maxFeed) lineAppearance("0.5 0.5 0.5") else lineAppearance("0 0 0")
        val line = shape(lineSet( vertexCount := "2" )( points ), appear )
        toDraw += line
    }

    override protected def circularMotion(_x: Double,_y: Double,_z: Double, // end position
                                          a: Double, b: Double, c: Double, // end orientation
                                          _i: Double,_j: Double,_k: Double, // center of rotation
                                          clockwise: Boolean, p: Int, f: Double) = { // number of turns and feedrate
        val coeff = if (useMillimeters) 1 else 25.4
        val x = coeff * _x
        val y = coeff * _y
        val z = coeff * _z
        val i = coeff * _i
        val j = coeff * _j
        val k = coeff * _k
        // get the center
        val cx = this.x + i
        val cy = this.y + j
        val cz = this.z + k
        val (radius, angle, offset, pitchOver2Pi) = plane match {
            case XY =>
                val radius = math.hypot(i, j)
                val angle = getRotationAngle(cx, cy, this.x, this.y, x, y, clockwise, p)
                val offset = math.atan2(getY - cy, getX - cx)
                val pitchOver2Pi = (z - this.z) / angle
                (radius, angle, offset, pitchOver2Pi)
            case ZX =>
                val radius = math.hypot(k, i)
                val angle = getRotationAngle(cz, cx, this.z, this.x, z, x, clockwise, p)
                val offset = math.atan2(getX - cx, getZ - cz)
                val pitchOver2Pi = (y - this.y) / angle
                (radius, angle, offset, pitchOver2Pi)
            case YZ =>
                val radius = math.hypot(j, k)
                val angle = getRotationAngle(cy, cz, this.y, this.z, y, z, clockwise, p)
                val offset = math.atan2(getZ - cz, getY - cy)
                val pitchOver2Pi = (x - this.x) / angle
                (radius, angle, offset, pitchOver2Pi)
        }
        val delta0 = circularInterpolationPrecision / 2 / math.Pi / radius
        val delta = if (clockwise) -delta0 else delta0
        assert(math.signum(delta) == math.signum(angle), s"delta: $delta, angle: $angle")
        def pointAt(a: Double) = {
            plane match {
                case XY => (cx + radius * math.cos(a+offset), cy + radius * math.sin(a+offset), this.z + pitchOver2Pi * a)
                case ZX => (cx + radius * math.sin(a+offset), this.y + pitchOver2Pi * a, cz + radius * math.cos(a+offset))
                case YZ => (this.x + pitchOver2Pi * a, cy + radius * math.cos(a+offset), cz + radius * math.sin(a+offset))
            }
        }
        var count = 2
        val points = new StringBuilder()
        var a = 0.0
        val (x0, y0, z0) = pointAt(a)
        points ++= s"${x0} ${y0} ${z0}"
        while ((a - angle).abs > delta.abs + 1e-5) {
            val a1 = a + delta
            count += 1
            val (x1, y1, z1) = pointAt(a1)
            points ++= s", ${x1} ${y1} ${z1}"
            a = a1
        }
        points ++= s", ${x} ${y} ${z}"
        val line = shape(lineSet( vertexCount := count.toString )( coordinate( point := points.toString) ), lineAppearance("0 0 0") )
        toDraw += line
        super.circularMotion(_x,_y,_z,a,b,c,_i,_j,_k,clockwise,p,f)
    }

}

object Viewer {

    def display(cmds: Seq[Command],
                debug: Boolean = false,
                conf: Config = Config) = {
        val tracer = new Viewer
        for (c <- cmds) {
            //println("processing " + c.toString)
            tracer.run(c)
        }
        if (debug) {
            val lines = tracer.resultByLines
            almond.display.Text(lines.map(_.render).mkString("\n"))
        } else {
            val lines = tracer.result
            io.github.dzufferey.x3DomViewer.Viewer.display(lines, conf)
        }
    }

}
