package libgcode.utils.geometry

import scala.math


trait Curve2D[A <: Curve2D[A]] {

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

  def offset(x: Double): A

  def translate(a: Double, b: Double): A 

  def startFrom(a: Double, b: Double) = {
    val (a0, b0) = apply(0)
    translate(a - a0, b - b0)
  }

  def endsAt(a: Double, b: Double) = {
    val (a0, b0) = apply(1)
    translate(a - a0, b - b0)
  }

  def flip: A
  
//TODO restrict to [x,y] with 0 ≤ x < y ≤ 1 (could even be wider to increase the bounds)
//TODO project / closest
//TODO intersection? for special cases, it is possible to have analytical solutions, for the rest we can use numerical opt.


}
