package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*
import libgcode.utils.*
import scala.collection.mutable.ArrayBuffer
import scala.math.*

/**
 * Reverse engineering a broken part.
 *
 * One part inside one of my windows broke. As this is pretty old, I did not
 * find a replacement at the hardware store, so I decided to make a replacement.
 * (The new one looks a bit crispy, as it is made from some kind of
 * scrapbinium steel.)
 *
 * The shape is pretty regular, so I made the main features by hand.
 * However, the sliding mechanism is too tricky to do by hand:
 * it is formed of two trochoid curves.
 * Fortunately the math is simple, so it is not a problem to have the CNC do that.
 * See img/curtate_trochoid.jpg for the old broken part and the new one.
 *
 * The origin is at the center of the blank; the endmill should be less than 5mm.
 */
class WindowSlider(conf: Config) extends Program(conf) {

  implicit val c: Config = conf

  // parameters for the trochoid
  val r = 19.5 / 2
  val slide = 39.0
  val pinRadius = 2.5 + 0.1 // 0.1 for clearance
  val depth = 3.5

  // parameters for the blank shape
  val length = 51.0
  val width = 27.0
  val height = 4.0
  val recessRadius = 2.5

  // trochoid
  //   0 <= t <= 1
  //   offset = 0 for 1st curve, offset = Pi for 2nd curve
  def x(t: Double, offset: Double) = -slide / 2 + slide * t + r * cos(Pi / 2 - Pi * t - offset)
  def y(t: Double, offset: Double) = r * sin(Pi / 2 - Pi * t - offset)
  // 1st derivative
  def xp(t: Double, offset: Double) = slide + Pi * r * sin(Pi / 2 - Pi * t - offset)
  def yp(t: Double, offset: Double) = -Pi * r * cos(Pi / 2 - Pi * t - offset)
  // 2nd derivative
  def xpp(t: Double, offset: Double) = -Pi * Pi * r * cos(Pi / 2 - Pi * t - offset)
  def ypp(t: Double, offset: Double) = -Pi * Pi * r * sin(Pi / 2 - Pi * t - offset)
  // normal
  def xn(t: Double, offset: Double) = -yp(t, offset) / math.hypot(xp(t, offset), yp(t, offset))
  def yn(t: Double, offset: Double) = xp(t, offset) / math.hypot(xp(t, offset), yp(t, offset))
  //rotated by pi/2
  def x1(t: Double, offset: Double) = -(y(t, 0) + offset * yn(t, 0))
  def y1(t: Double, offset: Double) = x(t, 0) + offset * xn(t, 0)
  //rotated by pi/2
  def x2(t: Double, offset: Double) = -(y(t, Pi) + offset * yn(t, Pi))
  def y2(t: Double, offset: Double) = x(t, Pi) + offset * xn(t, Pi)

  val curveDiscretization = 100

  def sides(cmds: ArrayBuffer[Command]) = {
    val r = conf.endmillRadius + 1
    val x = width / 2 + r
    val y = length / 2 + r
    cmds += Empty.comment("left")
    cmds ++= Slot(-x, -y, 0, -x, y, 0, r, height)
    cmds += Empty.comment("right")
    cmds ++= Slot(x, -y, 0, x, y, 0, r, height)
    cmds += Empty.comment("front")
    cmds ++= Slot(-x, -y, 0, x, -y, 0, r, height)
    cmds += Empty.comment("back")
    cmds ++= Slot(-x, y, 0, x, y, 0, r, height)
  }

  def slots(cmds: ArrayBuffer[Command]) = {
    val r = max(recessRadius, conf.endmillRadius)
    val y = length / 2 - recessRadius + r
    val x = width / 2 - 1 - r
    cmds += Empty.comment("front recess")
    cmds ++= Slot(-x, -y, 0, x, -y, 0, r, height)
    cmds += Empty.comment("back recess")
    cmds ++= Slot(-x, y, 0, x, y, 0, r, height)
  }

  def blank: Seq[Command] = {
    val cmds = ArrayBuffer.empty[Command]
    sides(cmds)
    slots(cmds)
    cmds.toSeq
  }

  def curve(cmds: ArrayBuffer[Command],
            x: (Double, Double) => Double, y: (Double, Double) => Double) = {
    val offset = pinRadius - conf.endmillRadius
    cmds += G(0, X(x(0, offset)), Y(y(0, offset)))
    val (nStep, stepSize) = evenSteps(0, -depth, conf.depthOfCut, conf.roundingError)
    for (i <- 0 to nStep) {
      val z = i * stepSize
      cmds += G(1, X(x(0, offset)), Y(y(0, offset)), Z(z))
      for (i <- 0 to curveDiscretization) {
        val t = i.toDouble / curveDiscretization
        cmds += G(1, X(x(t, offset)), Y(y(t, offset)))
      }
      for (i <- curveDiscretization to 0 by -1) {
        val t = i.toDouble / curveDiscretization
        cmds += G(1, X(x(t, -offset)), Y(y(t, -offset)))
      }
    }
    cmds += G(0, Z(conf.travelHeight))
  }

  def curves: Seq[Command] = {
    val cmds = ArrayBuffer.empty[Command]
    // Helix at the ends of each curve
    cmds ++= Hole.roughing(r, slide / 2, 0, pinRadius, depth, false, false)
    cmds ++= Hole.roughing(-r, slide / 2, 0, pinRadius, depth, false, false)
    cmds ++= Hole.roughing(r, -slide / 2, 0, pinRadius, depth, false, false)
    cmds ++= Hole.roughing(-r, -slide / 2, 0, pinRadius, depth, false, false)
    // curves
    cmds += Empty.comment("curve 1")
    curve(cmds, x1, y1)
    cmds += Empty.comment("curve 2")
    curve(cmds, x2, y2)
    cmds.toSeq
  }

  def body: Seq[Command] = {
    curves ++ blank
  }
}

object WindowSlider {
  def main(args: Array[String]): Unit = {
    // for steel
    // 4 flutes 1/8 inch endmill
    // spindle: 6112 rpm
    // feed: 250 mm/min
    // plunge: 125 mm/min
    val steel = new Config
    steel.feed = 250
    steel.plungeFeed = 125
    steel.endmillDiameter = 3.175
    steel.depthOfCut = 0.2
    steel.finishingPass = 0.0
    new WindowSlider(steel).save(Examples.out("window.nc"))

    // test
    val test = new Config
    test.feed = 600
    test.plungeFeed = 150
    test.endmillDiameter = 4
    test.depthOfCut = 1
    test.finishingPass = 0.0
    new WindowSlider(test).save(Examples.out("window_test_4mm.nc"))
  }
}
