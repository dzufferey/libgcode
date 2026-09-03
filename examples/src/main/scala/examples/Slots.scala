package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*

/**
 * Slots for T-nuts. The origin is the lower left corner of the blank.
 *
 * A simple program that cuts some slots in a 60x60mm aluminium block.
 * The operation is done on both sides of the block.
 * Then, I manually cut along the border of each slot and turned the
 * elements into T-nuts.
 */
class Slots(conf: Config) extends Program(conf) {

  val blankSize = 60.0
  val gap       = 5.0
  assert(conf.endmillDiameter == 5.0)
  val depth     = 2.1

  def body: Seq[Command] = {
    val cmds = scala.collection.mutable.ArrayBuffer.empty[Command]
    val n    = (blankSize / (gap + conf.endmillDiameter)).round.toInt
    var currentDepth = 0.0
    val xStart = -conf.endmillRadius - 1
    val xEnd   = blankSize + conf.endmillRadius + 1
    while (currentDepth < depth) {
      currentDepth = math.min(depth, currentDepth + conf.depthOfCut)
      cmds += G(0, Z(conf.travelHeight))
      cmds += G(0, X(xStart), Y(gap + conf.endmillRadius))
      cmds += G(1, Z(-currentDepth), F(conf.plungeFeed))
      cmds += Empty(F(conf.feed))
      for (i <- 0 until n) {
        cmds += G(1, Y(i * (gap + conf.endmillDiameter) + gap + conf.endmillRadius))
        if (i % 2 == 0) {
          cmds += G(1, X(xEnd))
        } else {
          cmds += G(1, X(xStart))
        }
      }
    }
    cmds.toSeq
  }
}

object Slots {
  def main(args: Array[String]): Unit = {
    val conf = new Config
    conf.safeHeight = 5.0
    conf.feed = 600
    conf.endmillDiameter = 5.0
    conf.depthOfCut = 0.25
    conf.finishingPass = 0.2
    conf.travelHeight = 1.0
    new Slots(conf).save(Examples.out("slots_for_tnuts.nc"))
  }
}
