package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import libgcode.utils.geometry._

object Slot {

  def apply(x1: Double, y1: Double, z1: Double,
            x2: Double, y2: Double, z2: Double,
            radius: Double, depth: Double
           )(implicit conf: Config) = {
    val (a1,b1,c1) = conf.toWorkplane(x1,y1,z1)
    val (a2,b2,c2) = conf.toWorkplane(x2,y2,z2)
    assert((c1 - c2).abs <= conf.roundingError, "depth should be parallel to the working plane.")
    assert(radius > conf.endmillRadius + conf.finishingPass, "slot is too narrow")
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    val step = conf.widthOfCut //TODO adjust to be more regular
    val effectiveRadius = radius - conf.finishingPass - conf.endmillRadius
    val dir = if (conf.climb) 2 else 3
    var currentDepth = 0.0
    while (currentDepth < depth - conf.roundingError) {
      currentDepth = math.min(depth, currentDepth + conf.depthOfCut) //TODO also finishing for the depth?
      //line in the middle then wider until the outside, and finishing pass at the end
      val centerLine = Line(a1, b1, a2, b2)
      buffer += G(0, conf.x(a1), conf.y(b1))
      buffer += G(1, conf.z(c1 - currentDepth), F(conf.plungFeed))
      buffer += G(1, conf.x(a2), conf.y(b2), F(conf.feed))
      var currentRadius = 0.0
      var leftLine = centerLine
      var rightLine = centerLine
      while (currentRadius < effectiveRadius - conf.roundingError) {
        leftLine = leftLine.offset(step)
        rightLine = rightLine.offset(-step)
        currentRadius += step
        val (la0, lb0) = leftLine(0)
        val (la1, lb1) = leftLine(1)
        val (ra0, rb0) = rightLine(0)
        val (ra1, rb1) = rightLine(1)
        if (conf.climb) {
          buffer += G(dir, conf.x(la1), conf.y(lb1), R(currentRadius - step/2))
          buffer += G(1, conf.x(la0), conf.y(lb0))
          buffer += G(dir, conf.x(ra0), conf.y(rb0), R(currentRadius))
          buffer += G(1, conf.x(ra1), conf.y(rb1))
        } else {
          buffer += G(dir, conf.x(ra1), conf.y(rb1), R(currentRadius - step/2))
          buffer += G(1, conf.x(ra0), conf.y(rb0))
          buffer += G(dir, conf.x(la0), conf.y(lb0), R(currentRadius))
          buffer += G(1, conf.x(la1), conf.y(lb1))
        }
      }
      //finishing (only for the last layer)
      val doFinish = conf.finishingPass > 0 && currentDepth >= depth - conf.roundingError
      if (doFinish) {
        leftLine = leftLine.offset(conf.finishingPass)
        rightLine = rightLine.offset(-conf.finishingPass)
      }
      val (la0, lb0) = leftLine(0)
      val (la1, lb1) = leftLine(1)
      val (ra0, rb0) = rightLine(0)
      val (ra1, rb1) = rightLine(1)
      if (conf.climb) {
        buffer += G(dir, conf.x(la1), conf.y(lb1), R(currentRadius + conf.finishingPass/2))
        if (doFinish) {
          buffer += G(1, conf.x(la0), conf.y(lb0))
          buffer += G(dir, conf.x(ra0), conf.y(rb0), R(currentRadius + conf.finishingPass))
          buffer += G(1, conf.x(ra1), conf.y(rb1))
        }
      } else {
        buffer += G(dir, conf.x(ra1), conf.y(rb1), R(currentRadius + conf.finishingPass/2))
        if (doFinish) {
          buffer += G(1, conf.x(ra0), conf.y(rb0))
          buffer += G(dir, conf.x(la0), conf.y(lb0), R(currentRadius + conf.finishingPass))
          buffer += G(1, conf.x(la1), conf.y(lb1))
        }
      }
    }
    buffer += G(0, conf.x(a1), conf.y(b1))
    buffer += G(0, conf.z(c1 + conf.safeHeight))
    buffer.toSeq
  }

}
