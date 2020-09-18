package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import scala.math

//   helix + circle at bottom, repeat from inside to outside
//or helix + sipral, repeat from top to bottom

object Hole {

  def apply(x: Double, y: Double, z: Double,
            radius: Double, depth: Double,
            insideOut: Boolean = true, depthFirst: Boolean = true
           )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]

    //some values used later
    val (a,b,c) = conf.toWorkplane(x,y,z)
    val endC = c - depth + conf.finishingPass
    val nTurns = (depth / conf.depthOfCut).ceil.toInt //make sure the helix are full turns
    val helixDepth = nTurns * conf.depthOfCut
    val startC = endC + helixDepth
    val maxRadius = radius - conf.endmillRadius - conf.finishingPass 
    //to call Helix, Spiral
    val (x2,y2,z2) = conf.fromWorkplane(a,b,startC)
    val z3 = conf.fromWorkplane(a,b,endC)._3

    if (depthFirst) {
      if (insideOut) {
        var currentRadius = conf.endmillRadius * math.max(0.0, conf.stepOver - 0.5)
        assert(currentRadius < maxRadius, "Hole too small to be carved.")
        val clockwise = !conf.climb
        if (currentRadius == 0.0) {
          // drill ?
          buffer += G(0, conf.x(a), conf.y(b))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += G(1, conf.z(endC), F(conf.plungFeed))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += Empty(F(conf.feed))
        } else {
          buffer ++= Helix(x2, y2, z2, currentRadius, conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
        }
        while (currentRadius < maxRadius) {
          currentRadius = math.min(maxRadius, currentRadius + conf.endmillRadius * conf.stepOver)
          buffer ++= Helix(x2, y2, z2, currentRadius, conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
        }
      } else {
        var currentRadius = maxRadius
        assert(currentRadius >= 0, "Hole too small to be carved.")
        val clockwise = conf.climb
        if (currentRadius == 0) {
          // drill ?
          buffer += G(0, conf.x(a), conf.y(b))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += G(1, conf.z(endC), F(conf.plungFeed))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += Empty(F(conf.feed))
        } else {
          buffer ++= Helix(x2, y2, z2, currentRadius, conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
        }
        while (currentRadius > conf.endmillRadius) {
          currentRadius = currentRadius - conf.endmillRadius * conf.stepOver
          buffer ++= Helix(x2, y2, z2, currentRadius, conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
        }
      }
    } else {
      var currentHeight = c
      while (currentHeight > endC) {
        val (x2,y2,z2) = conf.fromWorkplane(a,b,currentHeight)
        assert(conf.travelHeight >= conf.depthOfCut)
        buffer ++= Spiral(x2, y2, z2, maxRadius, insideOut)
      }
    }
    //finishingPass
    if (conf.finishingPass > 0) {
      if (insideOut) {
        //this assumes that the cutting edge is longer than the depth
        val (x2,y2,z2) = conf.fromWorkplane(a,b,c-depth)
        buffer ++= Spiral(x2, y2, z2, radius - conf.endmillRadius, true)
      } else {
        val (x2,y2,z2) = conf.fromWorkplane(a,b,startC - conf.finishingPass)
        val z3 = conf.fromWorkplane(a,b,endC - conf.finishingPass)._3
        val clockwise = conf.climb
        buffer ++= Helix(x2, y2, z2, radius - conf.endmillRadius, conf.depthOfCut, nTurns, clockwise)
        buffer ++= Spiral(x2, y2, z3, radius - conf.endmillRadius, false)
      }
    }
    buffer.toSeq
  }

}
