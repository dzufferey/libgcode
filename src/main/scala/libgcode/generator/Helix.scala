package libgcode.generator

import libgcode.Command
import libgcode.extractor.*
import scala.math.*

object Helix {

  // clockwise relates to climb depending if we are cutting toward the center of the outside
  // radius is the center, depending on the side of the cut the radius needs to be adapted for the endmill size
  def apply(
      x: Double,
      y: Double,
      z: Double,
      radius: Double,
      pitch: Double,
      nbrTurns: Int,
      clockwise: Boolean,
      offset: Double = 0,
      backToStart: Boolean = false,
      flatBottom: Boolean = false
  )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Helix")
    val (a, b, c) = conf.toWorkplane(x, y, z)
    val ra        = radius * cos(offset)
    val rb        = radius * sin(offset)
    buffer += G(0, conf.x(a + ra), conf.y(b + rb))
    buffer += G(0, conf.z(c))
    val dir = if (clockwise) 2 else 3
    // limit the feedrate by checking the pitch and plungeFeed
    val turnLength = hypot(2 * Pi * radius, pitch)
    val feed       = min(conf.feed, turnLength / pitch.abs * conf.plungeFeed)
    if (feed < conf.feed) {
      buffer += Empty(F(feed))
    }
    for (n <- 0 until nbrTurns) yield {
      buffer += G(dir, conf.x(a - ra), conf.y(b - rb), conf.i(-ra), conf.j(-rb), conf.z(c + n * pitch + pitch / 2))
      buffer += G(dir, conf.x(a + ra), conf.y(b + rb), conf.i(ra), conf.j(rb), conf.z(c + n * pitch + pitch))
    }
    if (flatBottom) {
      buffer += G(dir, conf.x(a - ra), conf.y(b - rb), conf.i(-ra), conf.j(-rb))
      buffer += G(dir, conf.x(a + ra), conf.y(b + rb), conf.i(ra), conf.j(rb))
    }
    if (feed < conf.feed) {
      buffer += Empty(F(conf.feed))
    }
    if (backToStart) {
      buffer += G(0, conf.z(c + nbrTurns * pitch + conf.travelHeight))
      buffer += G(0, conf.x(a), conf.y(b))
      buffer += G(0, conf.z(c + conf.travelHeight))
    }
    buffer.toSeq
  }

}
