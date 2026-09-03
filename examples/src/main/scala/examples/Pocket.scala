package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*
import libgcode.utils.*
import scala.collection.mutable.ArrayBuffer

/**
 * Pocket milling: an example to make a rectangular pocket.
 *
 * Roughing in the center, then a helical finishing pass around the sides.
 */
class Pocket(conf: Config, x: Double, y: Double, z: Double,
             width: Double, length: Double, depth: Double,
             sideOnly: Boolean = false) extends Program(conf) {

  implicit val c: Config = conf

  def center(x0: Double, x1: Double,
             y0: Double, y1: Double,
             z0: Double, z1: Double,
             cmds: ArrayBuffer[Command]) = {
    val (nTurn, effectiveDoC) = evenSteps(z0, z1, conf.depthOfCut, conf.roundingError)
    for (i <- 0 until nTurn) {
      cmds ++= Rectangle(x0, y0, z0 + i * effectiveDoC,
                         x1 - x0, y1 - y0, effectiveDoC.abs)
    }
  }

  // TODO: connection to the last part is not working
  def sides(x0: Double, x1: Double,
            y0: Double, y1: Double,
            z0: Double, z1: Double,
            cmds: ArrayBuffer[Command]) = {
    val (nTurn, effectiveDoC) = evenSteps(z0, z1, conf.depthOfCut, conf.roundingError)
    val r = conf.endmillRadius
    cmds += G(0, X(x0 + r), Y(y0 + r))
    cmds += G(0, Z(z0 + conf.travelHeight))
    cmds += G(1, Z(z0))
    for (i <- 0 until nTurn) {
      if (conf.climb) {
        cmds += G(1, X(x1 - r), Y(y0 + r), Z(z0 + (i + 0.25) * effectiveDoC))
        cmds += G(1, X(x1 - r), Y(y1 - r), Z(z0 + (i + 0.50) * effectiveDoC))
        cmds += G(1, X(x0 + r), Y(y1 - r), Z(z0 + (i + 0.75) * effectiveDoC))
        cmds += G(1, X(x0 + r), Y(y0 + r), Z(z0 + (i + 1.00) * effectiveDoC))
      } else {
        cmds += G(1, X(x0 + r), Y(y1 - r), Z(z0 + (i + 0.25) * effectiveDoC))
        cmds += G(1, X(x1 - r), Y(y1 - r), Z(z0 + (i + 0.50) * effectiveDoC))
        cmds += G(1, X(x1 - r), Y(y0 + r), Z(z0 + (i + 0.75) * effectiveDoC))
        cmds += G(1, X(x0 + r), Y(y0 + r), Z(z0 + (i + 1.00) * effectiveDoC))
      }

    }
    cmds ++= Rectangle(x0, y0, z1 - effectiveDoC,
                       x1 - x0, y1 - y0, effectiveDoC.abs)
  }

  def body: Seq[Command] = {
    val cmds = ArrayBuffer.empty[Command]
    val fp   = conf.finishingPass
    if (!sideOnly) {
      center(x + fp, x + width - fp,
             y + fp, y + length - fp,
             0, z - depth + fp,
             cmds)
    } else {
      sides(x + fp, x + width - fp,
            y + fp, y + length - fp,
            0, z - depth + fp,
            cmds)
    }
    // finishing is only side
    if (fp > 0.0) {
      cmds += G(0, Z(z + conf.travelHeight))
      sides(x, x + width,
            y, y + length,
            0, z - depth,
            cmds)
    }
    cmds.toSeq
  }
}

object Pocket {

  private def pocketForBlock(conf: Config, width: Double, length: Double,
                             depth: Double, wall: Double, onlySides: Boolean = false) =
    new Pocket(conf, wall, wall, 0,
               width - 2 * wall, length - 2 * wall, depth - wall, onlySides)

  private def blockConf(finishingPass: Double, depthOfCut: Double): Config = {
    val c = new Config
    c.safeHeight = 5.0
    c.feed = 600
    c.endmillDiameter = 6.0
    c.depthOfCut = depthOfCut
    c.finishingPass = finishingPass
    c.stepOver = 0.75
    c
  }

  def main(args: Array[String]): Unit = {
    // because pla melts and chip evacuation is not great in my setup, two setups:
    // (1) remove bulk, (2) only the side
    pocketForBlock(blockConf(0.0, 1.0), 67.7, 114.6, 24.7, 6).save(Examples.out("block_inner_1.nc"))
    pocketForBlock(blockConf(0.2, 3.0), 67.7, 114.6, 24.7, 5, true).save(Examples.out("block_outer_1.nc"))
    pocketForBlock(blockConf(0.0, 1.0), 67.6, 112.3, 18.2, 6).save(Examples.out("block_inner_2.nc"))
    pocketForBlock(blockConf(0.2, 3.0), 67.6, 112.3, 18.2, 5, true).save(Examples.out("block_outer_2.nc"))
  }
}
