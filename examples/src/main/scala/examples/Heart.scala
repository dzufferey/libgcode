package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*
import libgcode.utils.*
import Heart.*

/**
 * A parametric heart surface.
 *
 * With a scale of 1, the heart is about x in [-16,16], y in [-16, 12], z in [-1,0].
 * Different types of curves can be found at
 * http://www.mathematische-basteleien.de/heart.htm
 */
class HeartSurface(xScale: Double, yScale: Double, zScale: Double) {
  import math.*

  def x1(u: Double, v: Double): Double = {
    val u2pi = 2 * Pi * u
    xScale * v * (12 * sin(u2pi) - 4 * sin(3 * u2pi))
  }
  def y1(u: Double, v: Double): Double = {
    val u2pi = 2 * Pi * u
    yScale * v * (13 * cos(u2pi) - 5 * cos(2 * u2pi) - 2 * cos(3 * u2pi) - cos(4 * u2pi))
  }

  def x2(u: Double, v: Double): Double = {
    val u2pi = 2 * Pi * u
    xScale * v * 10 * (1 - cos(u2pi)) * sin(u2pi)
  }
  def y2(u: Double, v: Double): Double = {
    val u2pi = 2 * Pi * u
    yScale * v * 10 * (1 - cos(u2pi)) * cos(u2pi)
  }

  def x(u: Double, v: Double): Double = 0.8 * x1(u, v) + 0.2 * x2(u, v)
  def y(u: Double, v: Double): Double = 0.8 * y1(u, v) + 0.2 * y2(u, v)
  def z(u: Double, v: Double): Double = -zScale * pow(v, 6)
  def apply(u: Double, v: Double): (Double, Double, Double) = (x(u, v), y(u, v), z(u, v))
}

/** The heart surface, carved in a raster of straight moves. */
class Heart(conf: Config) extends Program(conf) {

  val h = new HeartSurface(xScale, yScale, zScale)

  val uRange = (0 to uTics).map(x => x.toDouble / uTics)
  val vRange = (0 to vTics).map(x => math.sqrt(x.toDouble / vTics))

  val minDist = 0.2
  val yPlungeOffset = 10

  // ignore points that are too close to each other
  def keep(x0: Double, y0: Double, z0: Double,
           x1: Double, y1: Double, z1: Double) = {
    val dx = x0 - x1
    val dy = y0 - y1
    val dz = z0 - z1
    math.sqrt(dx * dx + dy * dy + dz * dz) >= minDist
  }

  def body: Seq[Command] = {
    val cmds = scala.collection.mutable.ArrayBuffer.empty[Command]
    val start = h(0, 0)
    var x0: Double = start._1
    var y0: Double = start._2
    var z0: Double = start._3
    cmds += G(0, X(x0), Y(y0 - yPlungeOffset))
    cmds += G(0, Z(z0 + conf.travelHeight))
    cmds += G(0, X(x0), Y(y0), Z(z0))
    for (u <- uRange) {
      val (x1, y1, z1) = h(u, 0)
      if (keep(x0, y0, z0, x1, y1, z1)) {
        cmds += G(1, X(x1), Y(y1), Z(z1))
        x0 = x1; y0 = y1; z0 = z1
      }
    }
    for (vi <- vRange.indices; ui <- uRange.indices) {
      val u  = uRange(ui)
      val v0 = vRange(vi)
      val v1 = vRange(math.min(vi + 1, vTics - 1))
      val v  = linearInterpolation(v0, v1, ui / uTics)
      val (x1, y1, z1) = h(u, v)
      if (keep(x0, y0, z0, x1, y1, z1)) {
        cmds += G(1, X(x1), Y(y1), Z(z1))
        x0 = x1; y0 = y1; z0 = z1
      }
    }
    for (u <- uRange) {
      val (x1, y1, z1) = h(u, 1)
      cmds += G(1, X(x1), Y(y1), Z(z1))
    }
    cmds += G(0, Z(start._3 + conf.travelHeight))
    cmds += G(0, X(start._1), Y(start._2))
    cmds.toSeq
  }
}

/** The heart outline, at v = 1. */
class HeartOutline(conf: Config) extends Program(conf) {
  val h = new HeartSurface(xScale, yScale, zScale)
  val uRange = (0 to uTics).map(x => x.toDouble / uTics)
  val yPlungeOffset = 10

  def body: Seq[Command] = {
    val cmds = scala.collection.mutable.ArrayBuffer.empty[Command]
    val (x0, y0, _) = h(0, 1)
    cmds += G(0, X(x0), Y(y0 - yPlungeOffset))
    cmds += G(0, Z(conf.travelHeight))
    cmds += G(0, X(x0), Y(y0), Z(0))
    for (u <- uRange) {
      val (x, y, _) = h(u, 1)
      cmds += G(1, X(x), Y(y))
    }
    cmds += G(0, Z(conf.travelHeight))
    cmds += G(0, X(x0), Y(y0))
    cmds.toSeq
  }
}

/** Hole in the candle, roughing (6mm ball). */
class HoleRoughing(conf: Config) extends Program(conf) {
  implicit val c: Config = conf
  def body: Seq[Command] =
    Hole.finishing(0, 0, 0, candleDiameter / 2, candleHeight, false)
}

/** Hole in the candle, finishing (12mm flat). */
class HoleFinishing(conf: Config) extends Program(conf) {
  implicit val c: Config = conf
  def body: Seq[Command] =
    Hole.finishing(0, 0, 0, candleDiameter / 2, candleHeight, false)
}

/** Chamfering the hole. */
class HoleChamfer(conf: Config) extends Program(conf) {
  implicit val c: Config = conf
  def body: Seq[Command] =
    Spiral(0, 0, -(chamfer + chamferRadiusDelta), candleDiameter / 2 - chamferRadiusDelta)
}

/**
 * A birthday present: a heart with a candle hole, carved in sandstone.
 *
 * Grinding sandstone with a diamond bit: go slow, with a big depth of cut.
 */
object Heart {

  val xScale = 2.0
  val yScale = 2.0
  val zScale = 15.0
  val uTics  = 500
  val vTics  = 200

  val candleDiameter = 40.0
  val candleHeight   = 15.0
  val chamfer        = 4.0
  val chamferRadiusDelta = 2.0

  // 6mm ball
  def ballConf: Config = {
    val conf = new Config
    conf.safeHeight = 5.0
    conf.feed = 180
    conf.endmillDiameter = 6.0
    conf.depthOfCut = 2.0 // 18.0
    conf.finishingPass = 0.0
    conf.travelHeight = 1.0
    conf.climb = false
    conf.stepOver = 0.2
    conf
  }

  // 12mm flat
  def flatConf: Config = {
    val conf = new Config
    conf.safeHeight = 5.0
    conf.feed = 180
    conf.endmillDiameter = 12.0
    conf.depthOfCut = 8.0
    conf.finishingPass = 0.0
    conf.travelHeight = 1.0
    conf.climb = false
    conf.stepOver = 0.2
    conf
  }

  def main(args: Array[String]): Unit = {
    new Heart(ballConf).save(Examples.out("heart.nc"))
    new HeartOutline(ballConf).save(Examples.out("heart_outline.nc"))
    new HoleRoughing(ballConf).save(Examples.out("hole_roughing.nc"))
    new HoleFinishing(flatConf).save(Examples.out("hole_finishing.nc"))
    new HoleChamfer(ballConf).save(Examples.out("hole_chamfer.nc"))
  }
}
