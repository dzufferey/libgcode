package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import scala.math._
import scala.collection.mutable.ArrayBuffer

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
    
  def noFinishing(x: Double, y: Double, z: Double, // center
            radius: Double, insideOut: Boolean = true,
            offset: Double = 0.0
           )(implicit conf: Config) = {
    // compute the width of the cut so we are doing full turns
    val maxEffectiveRadius = radius - conf.endmillRadius
    val nTurn = (maxEffectiveRadius / conf.widthOfCut).ceil.toInt
    val effectiveCut = maxEffectiveRadius / nTurn
    //
    val (a,b,c) = conf.toWorkplane(x,y,z)
    var clockwise = insideOut != conf.climb
    val dir = if (clockwise) 2 else 3
    //
    def toPos(radius: Double, halfTurn: Boolean = false) = {
      val o = offset + ( if (halfTurn) math.Pi else 0 )
      val ra = a + radius * cos(o)
      val rb = b + radius * sin(o)
      (ra, rb)
    }
    //
    val buffer = ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Spiral")
    //one turn of the spiral
    def oneTurn(startRadius: Double, endRadius: Double) = {
      val delta = endRadius - startRadius
      val (a1, b1) = toPos(startRadius, false)
      val (a2, b2) = toPos(startRadius + delta / 2, true)
      val (a3, b3) = toPos(endRadius, false)
      val ra1 = (a2 - a1) / 2
      val rb1 = (b2 - b1) / 2
      val ra2 = (a3 - a2) / 2
      val rb2 = (b3 - b2) / 2
      buffer += G(dir, conf.x(a2), conf.y(b2), conf.i(ra1), conf.j(rb1))
      buffer += G(dir, conf.x(a3), conf.y(b3), conf.i(ra2), conf.j(rb2))
    }
    if (insideOut) {
      buffer += G(0, conf.x(a), conf.y(b))
      buffer += G(1, conf.z(c), F(conf.plungeFeed))
      buffer += Empty(F(conf.feed))
      var currentRadius = 0.0
      while (currentRadius < maxEffectiveRadius - conf.roundingError) {
        val oldRadius = currentRadius
        currentRadius = min(maxEffectiveRadius, currentRadius + conf.widthOfCut)
        oneTurn(oldRadius, currentRadius)
      }
      circle(x, y, z, maxEffectiveRadius, clockwise, offset, buffer, conf)
    } else {
      // first do a circle
      val (ra, rb) = toPos(maxEffectiveRadius)
      buffer += G(0, conf.x(ra), conf.y(rb))
      buffer += G(1, conf.z(c), F(conf.plungeFeed))
      buffer += Empty(F(conf.feed))
      circle(x, y, z, maxEffectiveRadius, clockwise, offset, buffer, conf)
      // now the spiral
      var currentRadius = maxEffectiveRadius
      while (currentRadius > conf.roundingError) {
        val oldRadius = currentRadius
        currentRadius = max(0.0, currentRadius - conf.widthOfCut)
        oneTurn(oldRadius, currentRadius)
      }
    }
    buffer += G(0, conf.z(c + conf.travelHeight))
    buffer += G(0, conf.x(a), conf.y(b))
    buffer.toSeq
  }

  def apply(x: Double, y: Double, z: Double, // center
            radius: Double, insideOut: Boolean = true,
            offset: Double = 0.0
           )(implicit conf: Config) = {
    if (conf.finishingPass > 0.0) {
      val (a,b,c) = conf.toWorkplane(x,y,z)
      val (x2,y2,z2) = conf.fromWorkplane(a,b,c+conf.finishingPass)
      val rough = noFinishing(x2, y2, z2, radius - conf.finishingPass, insideOut, offset)(conf)
      val finish = noFinishing(x, y, z, radius, insideOut, offset)(conf)
      rough ++ finish
    } else {
      noFinishing(x, y, z, radius, insideOut, offset)(conf)
    }
  }

}
