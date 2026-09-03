package examples

import libgcode.*
import libgcode.extractor.*
import libgcode.generator.*
import libgcode.utils.*
import scala.collection.mutable.ArrayBuffer

/**
 * A turner's cube (not quite, one face).
 *
 * This is a program to do one face of the cube; repeat 6 times.
 * This is the no-notch version, that makes a free floating cube,
 * as the notched version would need undercut.
 *
 * @param size is the size of the stock's side (assuming the origin in the center of the face)
 * @param n (> 0) is the number of cube
 * @param sideThickness is the tickness of material left on the cube's side after the circle is carved.
 * @param overlap how much material are left between the cubes
 */
abstract class TurnersCube(conf: Config, size: Double, n: Int, sideThickness: Double,
                           overlap: Double = 2.0) extends Program(conf) {

  implicit val c: Config = conf

  def cubeSize(i: Int): Double = {
    if (i == 0) {
      size
    } else {
      val diameter = circleDiameter(i - 1)
      diameter / math.sqrt(2) + 2 * overlap
    }
  }

  def circleDiameter(i: Int): Double = {
    val cs = cubeSize(i)
    cs - 2 * sideThickness
  }

  def printDimension = {
    for (i <- n - 1 to 0 by -1) {
      val cs = cubeSize(i)
      val cd = circleDiameter(i)
      val d  = depth(i + 1)
      Console.println(s"$i: cube side = $cs, circle diameter = $cd, depth = $d")
    }
  }

  def depth(i: Int) = {
    if (i < n) {
      (size - cubeSize(i)) / 2
    } else {
      size / 2 + 1 //add 1 to make sure we push all the way through
    }
  }
}

class TurnersCubeHoles(conf: Config, size: Double, n: Int, sideThickness: Double,
                       overlap: Double = 2.0) extends TurnersCube(conf, size, n, sideThickness, overlap) {

  def body: Seq[Command] = {
    val cmds = ArrayBuffer.empty[Command]
    for (i <- 0 until n) {
      val startDepth = depth(i) - conf.finishingPass
      val endDepth   = depth(i + 1) - conf.finishingPass
      val radius     = circleDiameter(i) / 2 - conf.finishingPass
      //cmds += G(0, Z(startDepth + conf.travelHeight))
      cmds ++= Hole.roughing(0, 0, -startDepth, radius, endDepth - startDepth)
    }
    for (i <- n - 1 to 0 by -1) {
      val startDepth = depth(i)
      val endDepth   = depth(i + 1)
      val radius     = circleDiameter(i) / 2
      cmds += G(0, Z(-startDepth + conf.travelHeight))
      cmds ++= Hole.finishing(0, 0, -startDepth, radius, endDepth - startDepth)
    }
    cmds.toSeq
  }
}

// chamfering the circles (not the outer edges), assume 45 degree bit
class TurnersCubeChamfer(chamfer: Double, conf: Config, size: Double, n: Int,
                         sideThickness: Double,
                         overlap: Double = 2.0) extends TurnersCube(conf, size, n, sideThickness, overlap) {

  def body: Seq[Command] = {
    val cmds = ArrayBuffer.empty[Command]
    val dir  = if (conf.climb) 2 else 3
    val offsetRadius = conf.endmillRadius * 0.9 - chamfer
    assert(offsetRadius > 0.0)
    val offsetDepth = offsetRadius + chamfer
    for (i <- 0 until n) {
      val d = depth(i)
      val r = circleDiameter(i) / 2 - offsetRadius
      cmds += G(0, X(r), Y(0))
      cmds += G(0, Z(-d + conf.travelHeight))
      cmds += G(1, Z(-d - offsetDepth), F(conf.plungeFeed))
      cmds += G(dir, conf.x(-r), conf.i(-r), F(conf.feed))
      cmds += G(dir, conf.x(r), conf.i(r))
    }
    cmds.toSeq
  }
}

object TurnersCube {
  def main(args: Array[String]): Unit = {
    val c = 68.0

    // facing before cutting the holes
    val facing = new Config
    facing.feed = 1200
    facing.stepOver = 0.7
    facing.endmillDiameter = 6
    facing.finishingPass = 0.0
    facing.depthOfCut = 2.0
    new Surface(facing, c, c, 1).save(Examples.out("cube_face.nc"))

    val holes = new Config
    holes.feed = 1000
    holes.stepOver = 0.6
    holes.endmillDiameter = 4.0
    holes.finishingPass = 0.0
    holes.depthOfCut = 2.0
    new TurnersCubeHoles(holes, c, 4, 5).save(Examples.out("cube_holes.nc"))

    val chamfer = new Config
    chamfer.feed = 600
    chamfer.stepOver = 0.6
    chamfer.endmillDiameter = 3.175
    chamfer.finishingPass = 0.0
    chamfer.depthOfCut = 2.0
    new TurnersCubeChamfer(1, chamfer, c, 4, 5).save(Examples.out("cube_chamfer.nc"))
  }
}
