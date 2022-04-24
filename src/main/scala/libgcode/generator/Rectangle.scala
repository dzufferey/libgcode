package libgcode.generator

import libgcode.Command
import libgcode.abstractmachine.Plane
import libgcode.extractor._
import libgcode.utils.evenSteps
import scala.math._
import scala.collection.mutable.{AbstractBuffer, ArrayBuffer}

/** Pocket/Surface a rectangle. */
object Rectangle {

  /** 
   * @param x
   * @param y
   * @param z
   * @param width
   * @param length
   * @param depth the bottom of the surface is z-depth
   * @param inside stays within the bound (pocket) or cover the whole surface including the corner (facing)
   */
  def apply(x: Double, y: Double, z: Double, // lower left corner
            width: Double, length: Double, depth: Double,
            inside: Boolean = true
           )(implicit conf: Config) = {
    //TODO currently ignores the finishing pass!
    assert(conf.workingPlane == Plane.XY, "Currently only for XY working plane") //FIXME makes the assertion works for the other planes
    val cmds = ArrayBuffer.empty[Command]
    val (nTurn, effectiveDoC) = evenSteps(z, z - depth, conf.depthOfCut, conf.roundingError)
    for (i <- 0 until nTurn) {
        layer(x, y, z + i * effectiveDoC,
              width, length, effectiveDoC.abs,
              inside, false, cmds)
        cmds += G(0, Z(z + i * effectiveDoC + conf.travelHeight))
    }
    cmds += G(0, Z(z + conf.travelHeight))
    cmds.toSeq
  }


  def layer(x: Double, y: Double, z: Double, // lower left corner
            width: Double, length: Double, depth: Double,
            inside: Boolean = true, backToStart: Boolean = true,
            _buffer: AbstractBuffer[Command] = null
           )(implicit conf: Config) = {
    assert(!inside || width >= conf.endmillDiameter)
    assert(!inside || length >= conf.endmillDiameter)
    assert(depth >= 0.0, "depth should be positive")
    assert(depth <= conf.depthOfCut, "Should not plung more than the depth of cut")
    val buffer = if (_buffer != null) _buffer else ArrayBuffer.empty[Command]
    // go to initial position
    val (a,b,c) = conf.toWorkplane(x,y,z)
    val (da,db,dc) = conf.toWorkplane(width,length,depth)
    val offset = if (inside) conf.endmillRadius else conf.widthOfCut - conf.endmillRadius
    val a0 = a + offset
    val a1 = a + da - offset
    val b0 = b + offset
    val b1 = b + db - offset
    buffer += G(0, conf.x(a0), conf.y(b0))
    buffer += G(1, conf.z(c), F(conf.plungeFeed))
    buffer += Empty(F(conf.feed))
    // check if we do a pass of plunging
    if (dc > 0.0) {
      if (conf.climb) {
        buffer += G(1, conf.x(a0), conf.y(b1), conf.z(c - depth * 0.25))
        buffer += G(1, conf.x(a1), conf.y(b1), conf.z(c - depth * 0.50))
        buffer += G(1, conf.x(a1), conf.y(b0), conf.z(c - depth * 0.75))
        buffer += G(1, conf.x(a0), conf.y(b0), conf.z(c - depth * 1.00))
      } else {
        buffer += G(1, conf.x(a1), conf.y(b0), conf.z(c - depth * 0.25))
        buffer += G(1, conf.x(a1), conf.y(b1), conf.z(c - depth * 0.50))
        buffer += G(1, conf.x(a0), conf.y(b1), conf.z(c - depth * 0.75))
        buffer += G(1, conf.x(a0), conf.y(b0), conf.z(c - depth * 1.00))
      }
    }
    // spiral toward the center
    var delta = 0.0
    //the first pass has a much bigger widthOfCut
    if (inside && conf.stepOver != 1.0) {
      if (conf.climb) {
        buffer += G(1, conf.x(a0), conf.y(b1))
        buffer += G(1, conf.x(a1), conf.y(b1))
        buffer += G(1, conf.x(a1), conf.y(b0))
        buffer += G(1, conf.x(a0), conf.y(b0))
        buffer += G(1, conf.x(a0 + conf.widthOfCut), conf.y(b0))
        delta += conf.widthOfCut
      } else {
        buffer += G(1, conf.x(a1), conf.y(b0))
        buffer += G(1, conf.x(a1), conf.y(b1))
        buffer += G(1, conf.x(a0), conf.y(b1))
        buffer += G(1, conf.x(a0), conf.y(b0))
        buffer += G(1, conf.x(a0), conf.y(b0 + conf.widthOfCut))
        delta += conf.widthOfCut
      }
    }
    val fullCover = conf.endmillDiameter * math.max(0, conf.stepOver - math.sqrt(2))
    val ma = (a1 - a0) / 2
    val mb = (b1 - b0) / 2
    while (delta + conf.endmillRadius < ma + conf.widthOfCut &&
           delta + conf.endmillRadius < mb + conf.widthOfCut) {
      if (conf.climb) {
        buffer += G(1, conf.x(a0 + delta), conf.y(b1 - delta))
        buffer += G(1, conf.x(a1 - delta), conf.y(b1 - delta))
        buffer += G(1, conf.x(a1 - delta), conf.y(b0 + delta))
        if (fullCover > 0.0) {
          buffer += G(1, conf.x(a0 + delta + conf.widthOfCut - fullCover), conf.y(b0 + delta))
        }
        buffer += G(1, conf.x(a0 + delta + conf.widthOfCut), conf.y(b0 + delta))
      } else {
        buffer += G(1, conf.x(a1 - delta), conf.y(b0 + delta))
        buffer += G(1, conf.x(a1 - delta), conf.y(b1 - delta))
        buffer += G(1, conf.x(a0 + delta), conf.y(b1 - delta))
        if (fullCover > 0.0) {
          buffer += G(1, conf.x(a0 + delta), conf.y(b0 + delta + conf.widthOfCut - fullCover))
        }
        buffer += G(1, conf.x(a0 + delta), conf.y(b0 + delta + conf.widthOfCut))
      }
      delta += conf.widthOfCut
    }
    // go to a neutral position
    if (backToStart) {
      buffer += G(0, conf.z(c + conf.travelHeight))
    }
    if (_buffer == null) {
      buffer.toSeq
    } else {
      null
    }
  }

}
