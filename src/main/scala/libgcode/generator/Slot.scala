package libgcode.generator

import libgcode.Command
import libgcode.extractor.*
import libgcode.utils.geometry2D.*
import libgcode.utils.*
import scala.collection.mutable.ArrayBuffer

object Slot {

  def layer(centerLine: Line, radius: Double, buffer: ArrayBuffer[Command], conf: Config) = {
    val (nWidth, stepWidth) = evenSteps(0, radius - conf.endmillRadius, conf.widthOfCut, conf.roundingError)
    val (da, db)            = centerLine.direction(0)
    val angle               = math.atan2(db, da)
    val i                   = -math.sin(angle)
    val j                   = math.cos(angle)
    val dir                 = if (conf.climb) 3 else 2 // TODO: double check!
    for (k <- 1 to nWidth) {
      val currentRadius = k * stepWidth
      val leftLine      = centerLine.offset(currentRadius)
      val rightLine     = centerLine.offset(-currentRadius)
      val (la0, lb0)    = leftLine(0)
      val (la1, lb1)    = leftLine(1)
      val (ra0, rb0)    = rightLine(0)
      val (ra1, rb1)    = rightLine(1)
      if (conf.climb) {
        buffer += G(
          dir,
          conf.x(la1),
          conf.y(lb1),
          I(i * (currentRadius - stepWidth / 2)),
          J(j * (currentRadius - stepWidth / 2))
        )
        buffer += G(1, conf.x(la0), conf.y(lb0))
        buffer += G(dir, conf.x(ra0), conf.y(rb0), I(-i * currentRadius), J(-j * currentRadius))
        buffer += G(1, conf.x(ra1), conf.y(rb1))
      } else {
        buffer += G(
          dir,
          conf.x(ra1),
          conf.y(rb1),
          I(-i * (currentRadius - stepWidth / 2)),
          J(-j * (currentRadius - stepWidth / 2))
        )
        buffer += G(1, conf.x(ra0), conf.y(rb0))
        buffer += G(dir, conf.x(la0), conf.y(lb0), I(i * currentRadius), J(j * currentRadius))
        buffer += G(1, conf.x(la1), conf.y(lb1))
      }
      if (k == nWidth) {
        if (conf.climb) {
          buffer += G(dir, conf.x(la1), conf.y(lb1), I(i * currentRadius), J(j * currentRadius))
        } else {
          buffer += G(dir, conf.x(ra1), conf.y(rb1), I(-i * currentRadius), J(-j * currentRadius))
        }
      }
    }
  }

  def apply(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, radius: Double, depth: Double)(
      implicit conf: Config
  ) = {
    val (a1, b1, c1) = conf.toWorkplane(x1, y1, z1)
    val (a2, b2, c2) = conf.toWorkplane(x2, y2, z2)
    assert((c1 - c2).abs <= conf.roundingError, "depth should be parallel to the working plane.")
    assert(radius >= conf.endmillRadius + conf.finishingPass, "slot is too narrow")
    val buffer              = ArrayBuffer.empty[Command]
    val centerLine          = Line(a1, b1, a2, b2)
    val (nDepth, stepDepth) = evenSteps(0, depth - conf.finishingPass, conf.depthOfCut, conf.roundingError)
    for (i <- 1 to nDepth) {
      val currentDepth = i * stepDepth
      // line in the middle then wider until the outside, and finishing pass at the end
      buffer += G(0, conf.x(a1), conf.y(b1))
      buffer += G(1, conf.z(c1 - currentDepth), F(conf.plungeFeed))
      buffer += G(1, conf.x(a2), conf.y(b2), F(conf.feed))
      layer(centerLine, radius - conf.finishingPass, buffer, conf)
    }
    // finishing (only for the last layer)
    if (conf.finishingPass > 0) {
      buffer += G(0, conf.x(a1), conf.y(b1))
      buffer += G(1, conf.z(c1 - depth), F(conf.plungeFeed))
      buffer += G(1, conf.x(a2), conf.y(b2), F(conf.feed))
      layer(centerLine, radius, buffer, conf)
    }
    buffer += G(0, conf.x(a1), conf.y(b1))
    buffer += G(0, conf.z(c1 + conf.travelHeight))
    buffer.toSeq
  }

}
