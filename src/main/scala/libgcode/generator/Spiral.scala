package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import scala.collection.mutable.ArrayBuffer
import scala.math._

/** An appproximation of an Archimedean spiral with a serial of half circles.
 *  The spiral is terminated with a complete circle to get the right radius. */
object Spiral {

  // assume we are at the correct starting point
  protected def circle(x: Double, y: Double, z: Double,
                       r: Double, clockwise: Boolean, offset: Double,
                       buffer: ArrayBuffer[Command], conf: Config) = {
    val dir = if (clockwise) 2 else 3
    val (a,b,c) = conf.toWorkplane(x,y,z)
    val ra = r * cos(offset)
    val rb = r * sin(offset)
    buffer += G(dir, conf.x(a - ra), conf.y(b - rb), conf.i(-ra), conf.j(-rb))
    buffer += G(dir, conf.x(a + ra), conf.y(b + rb), conf.i( ra), conf.j( rb))
  }

  def apply(x: Double, y: Double, z: Double, // center
            radius: Double, insideOut: Boolean = true,
            offset: Double = 0.0
           )(implicit conf: Config) = {
    // compute the width of the cut so we are doing full turns
    val maxRadius = radius - conf.finishingPass
    val maxEffectiveRadius = maxRadius - conf.endmillRadius
    val nTurn = (maxEffectiveRadius / conf.widthOfCut).ceil.toInt
    val effectiveCut = maxEffectiveRadius / nTurn
    //
    val (a,b,c) = conf.toWorkplane(x,y,z)
    var clockwise = insideOut != conf.climb
    val dir = if (clockwise) 2 else 3
    def toPos(radius: Double, fullTurn: Boolean = false) = {
      val o = if (fullTurn) offset else offset + math.Pi
      val ra = a + radius * cos(o)
      val rb = b + radius * sin(o)
      (ra, rb)
    }
    //
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    if (insideOut) {
      buffer += G(0, conf.x(a), conf.y(b))
      buffer += G(1, conf.z(c), F(conf.plungFeed))
      buffer += Empty(F(conf.feed))
      var currentRadius = 0.0
      while (currentRadius < maxEffectiveRadius) {
        // val (a1, b1) = toPos(currentRadius, true)
        val (a2, b2) = toPos(currentRadius + conf.widthOfCut / 2, false)
        currentRadius = min(maxEffectiveRadius, currentRadius - conf.widthOfCut)
        val (a3, b3) = toPos(currentRadius, true)
        buffer += G(dir, conf.x(a2), conf.y(b2), R(currentRadius + conf.widthOfCut / 4))
        buffer += G(dir, conf.x(a3), conf.y(b3), R(currentRadius + conf.widthOfCut / 4))
      }
      // do the finishing pass
      if (conf.finishingPass > 0) {
        val (ra1, rb1) = toPos(maxEffectiveRadius + conf.finishingPass)
        buffer += G(1, conf.x(ra1), conf.y(rb1))
        circle(x, y, z, maxEffectiveRadius + conf.finishingPass, clockwise, offset, buffer, conf)
      }
    } else {
      // first do a circle
      val (ra, rb) = toPos(maxEffectiveRadius)
      buffer += G(0, conf.x(ra), conf.y(rb))
      buffer += G(1, conf.z(c), F(conf.plungFeed))
      buffer += Empty(F(conf.feed))
      circle(x, y, z, maxEffectiveRadius, clockwise, offset, buffer, conf)
      // do the finishing pass now that we are close to the outer diameter
      if (conf.finishingPass > 0) {
        val (ra1, rb1) = toPos(maxEffectiveRadius + conf.finishingPass)
        buffer += G(1, conf.x(ra1), conf.y(rb1))
        circle(x, y, z, maxEffectiveRadius + conf.finishingPass, clockwise, offset, buffer, conf)
        buffer += G(1, conf.x(ra), conf.y(rb))
      }
      // now the spiral
      var currentRadius = maxEffectiveRadius
      while (currentRadius > 0) {
        // val (a1, b1) = toPos(currentRadius, true)
        val (a2, b2) = toPos(currentRadius - conf.widthOfCut / 2, false)
        currentRadius = max(0.0, currentRadius - conf.widthOfCut)
        val (a3, b3) = toPos(currentRadius, true)
        buffer += G(dir, conf.x(a2), conf.y(b2), R(currentRadius - conf.widthOfCut / 4))
        buffer += G(dir, conf.x(a3), conf.y(b3), R(currentRadius - conf.widthOfCut / 4))
      }
    }
    buffer += G(0, conf.z(conf.travelHeight))
    buffer += G(0, conf.x(a), conf.y(b))
    buffer.toSeq
  }

}
