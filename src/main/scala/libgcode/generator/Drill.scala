package libgcode.generator

import libgcode.Command
import libgcode.extractor._

object Drill {

  // bottom of the hole is 'height - depth' (assuming XY workingPlane Z is the height)
  def apply(x: Double, y: Double, z: Double, depth: Double
           )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    val (a,b,c) = conf.toWorkplane(x,y,z)
    buffer += Empty(F(conf.plungFeed))
    buffer += G(0, conf.x(a), conf.y(b))
    var currentDepth = 0.0
    while (currentDepth < depth) {
      buffer += G(0, conf.z(c - currentDepth + conf.deltaPlunge))
      currentDepth = math.min(depth, currentDepth + conf.peckDepth)
      buffer += G(1, conf.z(c - currentDepth))
      if (conf.fullRetract) {
        buffer += G(0, conf.z(c + conf.deltaPlunge))
      }
    }
    buffer += G(0, conf.z(conf.safeHeight))
    buffer += Empty(F(conf.feed))
    buffer.toSeq
  }

}
