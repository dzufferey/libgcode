package libgcode.generator

import libgcode.abstractmachine.Plane
import libgcode.{Command, Param, CmdType, ParamType, Transducer}
import libgcode.extractor._
import scala.math._

class Config {

  var workingPlane = Plane.XY

  /** safe height for start, stop, long travel */
  var safeHeight = 10.0
  /** for short travel, how much above the part that has already been cut */
  var travelHeight = 1.0

  /** feed rate in the direction of the working plane */
  var feed = 200
  /** feed rate in the direction orthogonal to the working plane */
  var plungeFeed = 100
  /** climb or conventional milling */
  var climb = true

  var endmillDiameter = 6.0
  var depthOfCut = 1.0
  var stepOver = 0.5
  var finishingPass = 0.1

  /** depth of peck drilling cycle */
  var peckDepth = 2.0
  /** fullRetract go all the way out to clear chips. false is just for breaking chips. */
  var fullRetract = true
  /** go down with rapid until previous depth + deltaPlunge */
  var deltaPlunge = 1.0

  def endmillRadius = endmillDiameter / 2.0
  def widthOfCut = endmillDiameter * stepOver

  def toWorkplane(x: Double, y: Double, z: Double) = workingPlane match {
    case Plane.XY => (x,y,z)
    case Plane.ZX => (z,x,y)
    case Plane.YZ => (y,z,x)
  }
  def fromWorkplane(a: Double, b: Double, c: Double) = workingPlane match {
    case Plane.XY => (a,b,c)
    case Plane.ZX => (b,c,a)
    case Plane.YZ => (c,a,b)
  }

  /* Coordinate change for things given as XY to workingPlane */
  def x(x: Double) = workingPlane match {
    case Plane.XY => X(x)
    case Plane.ZX => Z(x)
    case Plane.YZ => Y(x)
  }
  def y(x: Double) = workingPlane match {
    case Plane.XY => Y(x)
    case Plane.ZX => X(x)
    case Plane.YZ => Z(x)
  }
  def z(x: Double) = workingPlane match {
    case Plane.XY => Z(x)
    case Plane.ZX => Y(x)
    case Plane.YZ => X(x)
  }

  def i(x: Double) = workingPlane match {
    case Plane.XY => I(x)
    case Plane.ZX => K(x)
    case Plane.YZ => J(x)
  }
  def j(x: Double) = workingPlane match {
    case Plane.XY => J(x)
    case Plane.ZX => I(x)
    case Plane.YZ => K(x)
  }
  def k(x: Double) = workingPlane match {
    case Plane.XY => K(x)
    case Plane.ZX => J(x)
    case Plane.YZ => I(x)
  }

  var roundingError = 1e-6

}
