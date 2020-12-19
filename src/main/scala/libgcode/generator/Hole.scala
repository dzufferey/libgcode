package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import scala.math

//   helix + circle at bottom, repeat from inside to outside
//or helix + sipral, repeat from top to bottom

object Hole {

  def roughing(x: Double, y: Double, z: Double,
               radius: Double, depth: Double,
               insideOut: Boolean = true, depthFirst: Boolean = true
              )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Hole (roughing)")

    //some values used later
    val (a,b,c) = conf.toWorkplane(x,y,z)
    val endC = c - depth
    val nTurns = (depth / conf.depthOfCut).ceil.toInt //make sure the helix are full turns
    val helixDepth = nTurns * conf.depthOfCut
    val startC = endC + helixDepth
    val maxRadius = radius - conf.endmillRadius
    //to call Helix, Spiral
    val (x2,y2,z2) = conf.fromWorkplane(a,b,startC)
    val z3 = conf.fromWorkplane(a,b,endC)._3

    def toNeutral = {
      buffer += G(0, conf.z(endC + conf.travelHeight))
      buffer += G(0, conf.x(a), conf.y(b))
      buffer += G(0, conf.z(startC + conf.travelHeight))
    }

    if (depthFirst) {
      if (insideOut) {
        var currentRadius = conf.endmillRadius * math.max(0.0, conf.stepOver - 0.5)
        assert(currentRadius < maxRadius, "Hole too small to be carved.")
        val clockwise = !conf.climb
        if (currentRadius == 0.0) {
          // drill ?
          buffer += G(0, conf.x(a), conf.y(b))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += G(1, conf.z(endC), F(conf.plungeFeed))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += Empty(F(conf.feed))
        } else {
          buffer ++= Helix(x2, y2, z2, currentRadius, -conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
          toNeutral
        }
        //TODO could change that to have even width of cut
        while (currentRadius < maxRadius) {
          currentRadius = math.min(maxRadius, currentRadius + conf.widthOfCut)
          buffer ++= Helix(x2, y2, z2, currentRadius, -conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
          toNeutral
        }
      } else {
        var currentRadius = maxRadius
        assert(currentRadius >= 0, "Hole too small to be carved.")
        val clockwise = conf.climb
        if (currentRadius == 0) {
          // drill ?
          buffer += G(0, conf.x(a), conf.y(b))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += G(1, conf.z(endC), F(conf.plungeFeed))
          buffer += G(0, conf.z(startC + conf.travelHeight))
          buffer += Empty(F(conf.feed))
        } else {
          buffer ++= Helix(x2, y2, z2, currentRadius, -conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
          toNeutral
        }
        while (currentRadius > conf.endmillRadius) {
          currentRadius = currentRadius - conf.widthOfCut
          buffer ++= Helix(x2, y2, z2, currentRadius, -conf.depthOfCut, nTurns, clockwise)
          buffer ++= Helix(x2, y2, z3, currentRadius, 0, 1, clockwise)
          toNeutral
        }
      }
    } else {
      assert(conf.travelHeight >= conf.depthOfCut)
      var currentHeight = c
      while (currentHeight > endC) {
        currentHeight = math.max(endC, currentHeight - conf.depthOfCut)
        val (x2,y2,z2) = conf.fromWorkplane(a,b,currentHeight)
        buffer ++= Spiral.roughing(x2, y2, z2, radius, insideOut)
      }
      toNeutral
    }

    buffer.toSeq
  }

  def finishing(x: Double, y: Double, z: Double,
                radius: Double, depth: Double,
                insideOut: Boolean = true, depthFirst: Boolean = true
               )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Hole (finishing)")

    val (a,b,c) = conf.toWorkplane(x,y,z)
    val endC = c - depth

    def toNeutral = {
      buffer += G(0, conf.z(c - depth + conf.travelHeight))
      buffer += G(0, conf.x(a), conf.y(b))
      buffer += G(0, conf.z(c + conf.travelHeight))
    }

    if (insideOut) {
      //this assumes that the cutting edge is longer than the depth
      val (x2,y2,z2) = conf.fromWorkplane(a,b,endC)
      buffer ++= Spiral.finishing(x2, y2, z2, radius, true) //Spiral take cutter radius into account
    } else {
      val nTurns = (depth / conf.depthOfCut).ceil.toInt //make sure the helix are full turns
      val helixDepth = nTurns * conf.depthOfCut
      val startC = endC + helixDepth
      val (x2,y2,z2) = conf.fromWorkplane(a, b, startC)
      val (_, _, z3) = conf.fromWorkplane(a, b, endC)
      val clockwise = conf.climb
      buffer ++= Helix(x2, y2, z2, radius - conf.endmillRadius, -conf.depthOfCut, nTurns, clockwise)
      buffer ++= Spiral.finishing(x2, y2, z3, radius, false)
    }
    toNeutral

    buffer.toSeq
  }

  def apply(x: Double, y: Double, z: Double,
            radius: Double, depth: Double,
            insideOut: Boolean = true, depthFirst: Boolean = true
           )(implicit conf: Config) = {
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Hole")
    if (conf.finishingPass > 0.0) {
      val rough = roughing(x, y, z, radius - conf.finishingPass, depth - conf.finishingPass, insideOut, depthFirst)(conf)
      val finish = finishing(x, y, z, radius, depth, insideOut, depthFirst)(conf)
      rough ++ finish
    } else {
      roughing(x, y, z, radius, depth, insideOut, depthFirst)(conf)
    }
  }

}
