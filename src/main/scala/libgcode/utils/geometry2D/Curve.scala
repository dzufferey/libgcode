package libgcode.utils.geometry2D

import libgcode.utils.*
import libgcode.generator.Config
import libgcode.Command
import scala.math

/** Abstract 2D curve
  */
trait AbsCurve {

  // TODO: tolerance as an implicit parameter ?

  /** Find the point on this curve corresponding to parameter value {@@@codeu} .
    *
    * @param u
    *   between 0 and 1 (inclusive)
    * @return
    *   a point
    */
  def apply(u: Double): (Double, Double)

  /** Returns the parameter for a point on the curve
    *
    * @param a
    *   1st coordinate of the point
    * @param b
    *   2nd coordinate of the point
    * @param ignoreBounds
    *   ignore the bounds of this curve (extend u's range to the real numbers)
    * @param tolerance
    *   for approximate numerical comparisions
    * @return
    *   an options containing the parameter value of the given point
    */
  def get(a: Double, b: Double, ignoreBounds: Boolean = false, tolerance: Double = 1e-6): Option[Double]

  /** The length of this curve.
    *
    * @return
    *   the length of this curve
    */
  def length: Double

  /** The derivative of the curve at {@@@codeapply(u)} .
    *
    * @param u
    *   between 0 and 1 (inclusive)
    * @return
    *   the derivative of the curve
    */
  def derivative(u: Double): (Double, Double)

  /** The direction is the normalized derivative.
    *
    * @param u
    *   between 0 and 1 (inclusive)
    * @return
    *   the direction at parameter value {@@@codeu}
    */
  def direction(u: Double) = {
    val (a, b) = derivative(u)
    val norm   = math.hypot(a, b)
    (a / norm, b / norm)
  }

  /** The normal of this curve's direction.
    *
    * @param u
    *   between 0 and 1 (inclusive)
    * @return
    *   the normal at parameter value {@@@codeu}
    */
  def normal(u: Double) = {
    val (a, b) = direction(u)
    (-b, a)
  }

  // signed
  def curvature(u: Double): Double

  // unsigned
  def radiusOfCurvature(u: Double) = {
    1 / curvature(u).abs
  }

  def centerOfCurvature(u: Double) = {
    val (a, b)   = apply(u)
    val (na, nb) = normal(u)
    val r        = 1 / curvature(u)
    (a + na * r, b + nb * r)
  }

  def startFrom(a: Double, b: Double) = {
    val (a0, b0) = apply(0)
    translate(a - a0, b - b0)
  }

  def endsAt(a: Double, b: Double) = {
    val (a0, b0) = apply(1)
    translate(a - a0, b - b0)
  }

  def offset(x: Double, tolerance: Double = 1e-6): AbsCurve

  def translate(a: Double, b: Double): AbsCurve

  def rotate(a: Double, b: Double, alpha: Double): AbsCurve

  def flip: AbsCurve

  // restrict to [x,y] with 0 ≤ x < y ≤ 1 (could even be wider to increase the bounds ?)
  def restrict(lb: Double, ub: Double): AbsCurve

  def intersect(c: AbsCurve, ignoreBounds: Boolean = false, tolerance: Double = 1e-6): Seq[(Double, Double)]

  // simple curve are C2, path can have lesser continuity
  def continuity(tolerance: Double = 1e-6): Continuity.Continuity = {
    Continuity.C2
  }

  // assumes the machine is at the start position and the feedrate is already set
  def toGCode(config: Config): Seq[Command]

  /** Returns the (approximate) distance between two curves.
    *
    * This works by sampling so it is not efficient!!
    *
    * @param c
    *   the curve to which the distance gets computed
    * @param nSamples
    *   number of samples along each curve that are used
    * @return
    *   the distance between {@@@codethis} and {@@@codec}
    */
  def approximateDistance(c: AbsCurve, nSamples: Int = 100): Double = {
    val delta    = 1.0 / (nSamples - 1)
    var distance = Double.PositiveInfinity
    for (i <- 0 until nSamples) {
      val (a1, b1) = apply(i * delta)
      for (j <- 0 until nSamples) {
        val (a2, b2) = c.apply(j * delta)
        distance = scala.math.min(distance, math.hypot(a2 - a1, b2 - b1))
      }
    }
    return distance
  }

  def append(that: AbsCurve, tolerance: Double = 1e-6): Path = {
    if (compare(this(1.0), that(0.0), tolerance)) {
      Path(Seq(this, that))
    } else {
      sys.error("Cannot stitch together unconnected paths!")
    }
  }

  // TODO: project / closest / fromGCode ?

}

trait Curve[A <: Curve[A]] extends AbsCurve {

  def offset(x: Double, tolerance: Double = 1e-6): A

  def translate(a: Double, b: Double): A

  def rotate(a: Double, b: Double, alpha: Double): A

  def flip: A

  def restrict(lb: Double, ub: Double): A

}
