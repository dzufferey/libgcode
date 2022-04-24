package libgcode.utils.geometry2D

import libgcode.utils._
import libgcode.generator.Config
import libgcode.Command
import scala.math

/** Abstract 2D curve */
trait AbsCurve {

  def apply(u: Double): (Double, Double)

  /** Returns the parameter for a point on the curve */
  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6): Option[Double]

  def length: Double

  def derivative(u: Double): (Double, Double)

  def direction(u: Double) = {
    val (a, b) = derivative(u)
    val norm = math.hypot(a, b)
    (a / norm, b / norm)
  }

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
    val (a,b) = apply(u)
    val (na,nb) = normal(u)
    val r = 1 / curvature(u)
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

  def flip: AbsCurve

  //restrict to [x,y] with 0 ≤ x < y ≤ 1 (could even be wider to increase the bounds ?)
  def restrict(lb: Double, ub: Double): AbsCurve

  def intersect(c: AbsCurve,
                ignoreBounds: Boolean = false,
                tolerance: Double = 1e-6): Seq[(Double, Double)]
  
  // simple curve are C2, path can have lesser continuity
  def continuity(tolerance: Double = 1e-6): Continuity.Continuity = {
    Continuity.C2
  }

  //assumes the machine is at the start position and the feedrate is already set
  def toGCode(config: Config): Seq[Command]

  //TODO project / closest

}

trait Curve[A <: Curve[A]] extends AbsCurve {

  def offset(x: Double, tolerance: Double = 1e-6): A

  def translate(a: Double, b: Double): A 

  def flip: A

  def restrict(lb: Double, ub: Double): A

}
