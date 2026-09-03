package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*

/**
 * Flat surface milling: clean a rectangular surface.
 *
 * Takes advantage of `libgcode.generator.Rectangle`, which provides a
 * sub-program that makes one layer of the overall program:
 * rectangular spirals down to the finishing pass, then one finishing pass.
 */
class Surface(conf: Config, width: Double, length: Double, depth: Double) extends Program(conf) {

  implicit val c: Config = conf

  def body: Seq[Command] = {
    val nTurn = math.ceil((depth.abs - conf.finishingPass) / conf.depthOfCut).toInt
    val effectiveDoC = (depth.abs - conf.finishingPass) / nTurn
    val layers = Rectangle(0, 0, 0, width, length, depth - conf.finishingPass, true)
    if (conf.finishingPass > 0) {
      layers ++ Rectangle(0, 0, -depth.abs + conf.finishingPass, width, length, conf.finishingPass, true)
    } else {
      layers
    }
  }
}

object Surface {
  def main(args: Array[String]): Unit = {
    val conf = new Config
    conf.safeHeight = 5.0
    conf.feed = 600
    conf.endmillDiameter = 6.0
    conf.depthOfCut = 0.5
    conf.finishingPass = 0.2
    conf.stepOver = 0.6
    new Surface(conf, 100, 120, 2).save(Examples.out("surface_100x120x2mm.nc"))
  }
}
