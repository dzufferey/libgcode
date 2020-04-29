package libgcode.abstractmachine

import libgcode.{Command, Param, ParamT, CmdType, ParamType}
import libgcode.extractor._
import scala.collection.mutable.Map
import scala.math._
import Plane._

abstract class AbstractMachine {

  // position
  var x = 0.0
  var y = 0.0
  var z = 0.0

  // orientation
  var a = 0.0
  var b = 0.0
  var c = 0.0

  var time = 0.0
  
  var absoluteCoordinates = true

  var useMillimeters = true

  var feedrate = 1.0

  var plane = XY
  
  var selectedTool = 0

  var spindleRPM = 0.0

  var toolLengthOffset = 0.0 // use that to compute the motion

  // TODO ?
  // cooling 
  // cutter radius compensation
  // other axes position (e.g. Extruder)
        
  protected def isEq(a: Double, b: Double) = (a-b).abs < 1e-10

  protected def linearMotion(x: Double, y: Double, z: Double,
                             a: Double, b: Double, c: Double,
                             f: Double) = {
    val coeff = if (useMillimeters) 1 else 25.4
    val x2 = coeff * x
    val y2 = coeff * y
    val z2 = coeff * z
    if (absoluteCoordinates) {
      this.x = x2
      this.y = y2
      this.z = z2
      this.a = a
      this.b = b
      this.c = c
    } else {
      this.x += x2
      this.y += y2
      this.z += z2
      this.a += a
      this.b += b
      this.c += c
    }
    feedrate = f
    //TODO account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    val distance = math.sqrt(x2*x2 + y2*y2 + z2*z2)
    time += feedrate * distance * 60000 // from mm/minutes to ms
  }
  
  protected def circularMotion(_x: Double,_y: Double,_z: Double, // end position
                                a: Double, b: Double, c: Double, // end orientation
                               _i: Double,_j: Double,_k: Double, // center of rotation
                               clockwise: Boolean, p: Int, f: Double) = { // number of turns and feedrate
    // equation of an helix:
    // x(t) = a * cos(t)
    // y(t) = a * sin(t)
    // z(t) = b * t
    // a is the radius
    // b is the pitch / 2π

    // convert to millimeters
    val coeff = if (useMillimeters) 1 else 25.4
    val x = coeff * _x
    val y = coeff * _y
    val z = coeff * _z
    val i = coeff * _i
    val j = coeff * _j
    val k = coeff * _k

    // compute the radius, the angle, and pitch of the helix
    val radius: Double = plane match {
      case XY => hypot( this.x - i, this.y - j)
      case ZX => hypot( this.z - k, this.x - i)
      case YZ => hypot( this.y - j, this.y - k)
    } 
    val radiusCheck = plane match {
      case XY => hypot( x - i, y - j)
      case ZX => hypot( z - k, x - i)
      case YZ => hypot( y - j, y - k)
    } 
    assert(isEq(radius, radiusCheck), "start and end radius of the circle do not agree: " + radius + " and " + radiusCheck)
    def computeArc( i: Double, j: Double, //center of rotation
                    x1: Double, y1: Double, //first point
                    x2: Double, y2: Double //second point
                  ): Double = {
      val dx1 = x1 - i
      val dy1 = y1 - j
      val n1 = hypot(dx1, dy1)
      val dx2 = x2 - i
      val dy2 = y2 - j
      val n2 = hypot(dx2, dy2)
      val a = acos( (dx1 * dx2 + dy1 * dy2) / n1 / n2 )
      if (clockwise && a >= 0) a
      else if (clockwise && a < 0) a + 2*Pi
      else if (!clockwise && a <= 0) -a
      else -a + 2*Pi // !clockwise && a > 0
    }
    val arc = plane match {
      case XY => computeArc(i, j, this.x, this.y, x, y)
      case ZX => computeArc(k, i, this.z, this.x, z, x) 
      case YZ => computeArc(j, k, this.y, this.z, y, z) 
    }
    val turns = 2*p*Pi
    val angle = turns + arc.abs // undirected angle
    val pitchOver2Pi: Double = plane match {
      case XY => (z - this.z) / angle
      case ZX => (y - this.y) / angle
      case YZ => (x - this.x) / angle
    }

    // update the coordinate
    if (absoluteCoordinates) {
      this.x = x
      this.y = y
      this.z = z
      this.a = a
      this.b = b
      this.c = c
    } else {
      this.x += x
      this.y += y
      this.z += z
      this.a += a
      this.b += b
      this.c += c
    }
    feedrate = f
    //TODO account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    val distance = angle * hypot(radius, pitchOver2Pi)
    time += feedrate * distance * 60000 // from mm/minutes to ms
  }

  // helper to compute the end position of a motion: default value of the arguments
  protected def getX = if (absoluteCoordinates) { if (useMillimeters) this.x else this.x / 25.4 } else 0.0
  protected def getY = if (absoluteCoordinates) { if (useMillimeters) this.y else this.y / 25.4 } else 0.0
  protected def getZ = if (absoluteCoordinates) { if (useMillimeters) this.z else this.z / 25.4 } else 0.0
  protected def getA = if (absoluteCoordinates) this.a else 0.0
  protected def getB = if (absoluteCoordinates) this.b else 0.0
  protected def getC = if (absoluteCoordinates) this.c else 0.0

  def run(cmd: Command): String = {
    cmd match {
      case G(0|1, 0, params) =>
        assert(params.nonEmpty)
        var x = getX
        var y = getY
        var z = getZ
        var a = getA
        var b = getB
        var c = getC
        var f = feedrate
        params.foreach{
          case X(v) => x = v
          case Y(v) => y = v
          case Z(v) => z = v
          case A(v) => a = v
          case B(v) => b = v
          case C(v) => c = v
          case F(v) => f = v
          case other => sys.error("in G0 or G1, unexpected axis: " + other)
        }
        linearMotion(x, y, z, a, b, c, f)
      case G(dir @ (2|3), 0, params) =>
        val clockwise = dir == 2
        // end position
        var x = getX
        var y = getY
        var z = getZ
        var a = getA
        var b = getB
        var c = getC
        //circle given either by R (radius), or IJK (center relative to the start)
        var i = x
        var j = y
        var k = z
        var r = 0.0
        // P is the number of turn
        var p = 0
        var f = feedrate
        // parse parameters
        params.foreach{
          case X(v) => x = v
          case Y(v) => y = v
          case Z(v) => z = v
          case A(v) => a = v
          case B(v) => b = v
          case C(v) => c = v
          case I(v) => i = v
          case J(v) => j = v
          case K(v) => k = v
          case R(v) => r = v
          case F(v) => f = v
          case P(v) => p = v
          case other => sys.error("in G92 unexpected axis: " + other)
        }
        assert(r >= 0, "ill-formed command: " + cmd)
        if (r > 0) {
          def findCenter( x1: Double, y1: Double,
                         _x2: Double,_y2: Double): (Double, Double) = {
            val x2 = if (useMillimeters) _x2 else 25.4 * _x2
            val y2 = if (useMillimeters) _y2 else 25.4 * _y2
            assert(!isEq(x1, x2) || !isEq(y1, y2), "ill-formed command: " + cmd)
            // middle point
            val mx = (x1 + x2) / 2
            val my = (y1 + y2) / 2
            // vector from p₁ to p₂
            val dx = x2 - x1
            val dy = y2 - y1
            // normal to d (take the direction into account)
            val nx = (if (clockwise)  dy else -dy) / hypot(dx, dy)
            val ny = (if (clockwise) -dx else  dx) / hypot(dx, dy)
            // how far from m should we go ???
            val toM2 = (mx - x1) * (mx - x1) + (my - y1) * (my - y1)
            val k = sqrt(r*r - toM2)
            val px = mx + nx * k
            val py = my + ny * k
            (px, py)
          }
          plane match {
            case XY =>
              val (c1, c2) = findCenter(this.x, this.y, x, y)
              i = c1
              j = c2
            case ZX =>
              val (c1, c2) = findCenter(this.z, this.x, z, x)
              k = c1
              i = c2
            case YZ =>
              val (c1, c2) = findCenter(this.y, this.z, y, z)
              j = c1
              k = c2
          }
        } else {
          assert(!isEq(i, x) || !isEq(j, y) || !isEq(k, z), "ill-formed command: " + cmd)
        }
        circularMotion(x, y, z, a, b, c, i, j, k, clockwise, p, f)
      case G(4, 0, Seq(P(ms))) => time += ms
      case G(4, 0, Seq(X(s))) =>  time += 1000 * s
      case G(4, 0, Seq(S(s))) =>  time += 1000 * s
      case G(17, 0, Seq()) =>   plane = XY
      case G(18, 0, Seq()) =>   plane = ZX
      case G(19, 0, Seq()) =>   plane = YZ
      case G(20, 0, Seq()) =>   useMillimeters = false
      case G(21, 0, Seq()) =>   useMillimeters = true
      case G(28, 0, params) =>
        if (params.isEmpty) {
          x = 0
          y = 0
          z = 0
          a = 0
          b = 0
          c = 0
        } else {
          params.foreach{
            case ParamT(ParamType.X) => x = 0
            case ParamT(ParamType.Y) => y = 0
            case ParamT(ParamType.Z) => z = 0
            case ParamT(ParamType.A) => a = 0
            case ParamT(ParamType.B) => b = 0
            case ParamT(ParamType.C) => c = 0
            case other => sys.error("in G28, unexpected axis: " + other)
          }
        }
      case G(90, 0, Seq()) =>   absoluteCoordinates = true
      case G(91, 0, Seq()) =>   absoluteCoordinates = false
      case G(92, 0, params) =>
        assert(params.nonEmpty)
        val coeff = if (useMillimeters) 1 else 25.4
        params.foreach{
          case X(v) => x = coeff * v
          case Y(v) => y = coeff * v
          case Z(v) => z = coeff * v
          case A(v) => a = v
          case B(v) => b = v
          case C(v) => c = v
          case other => sys.error("in G92 unexpected axis: " + other)
        }
      case M(3, 0, Seq(S(clockwise))) =>    spindleRPM = clockwise
      case M(4, 0, Seq(S(cclockwise))) =>   spindleRPM = -cclockwise
      case M(5, 0, Seq()) =>                spindleRPM = 0
      case M(114, 0, Seq()) =>
        if (useMillimeters) {
          return "X:" + x + " Y:" + y + " Z:" + z + " A:" + a + " B:" + b + " C:" + c
        } else {
          return "X:" + x/25.4 + " Y:" + y/25.4 + " Z:" + z/25.4 + " A:" + a + " B:" + b + " C:" + c
        }
      case Empty( Seq(T(i))) =>
        selectedTool = i
      // XXX more commands
      case _ =>
        sys.error("Command not supported or ill-formed: " + cmd)
    }
    ""
  }

}
