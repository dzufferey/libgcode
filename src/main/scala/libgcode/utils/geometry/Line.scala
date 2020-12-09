package libgcode.utils.geometry

import scala.math

class Line( a1: Double, b1: Double, // start
            a2: Double, b2: Double  // end
          ) extends Curve2D[Line] {

  assert(a1 != a2 || b1 != b2)

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val a =  linearInterpolation(a1, a2, u)
    val b =  linearInterpolation(b1, b2, u)
    (a, b)
  }

  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6) = {
    val u = linearInterpolationCoeff(a1, a2, a)
    val v = linearInterpolationCoeff(b1, b2, b)
    val w = (u+v) / 2
    if (ignoreBounds) {
      val aa = linearInterpolation(a1, a2, w)
      val bb = linearInterpolation(b1, b2, w)
      if (distance(a,b,aa,bb) <= tolerance) {
        Some(w)
      } else {
        None
      }
    } else if (w >= tolerance && w <= 1 + tolerance) {
      val w2 = clamp(0.0, 1.0, w)
      val aa = linearInterpolation(a1, a2, w2)
      val bb = linearInterpolation(b1, b2, w2)
      if (distance(a,b,aa,bb) <= tolerance) {
        Some(w2)
      } else {
        None
      }
    } else {
      None
    }
  }

  def length = {
    math.hypot(a2 - a1, b2 - b1)
  }

  def derivative(u: Double) = {
    (a2 - a1, b2 - b1)
  }

  def curvature(u: Double) = {
    0.0
  }

  def offset(x: Double) = {
    val (na , nb) = normal(0)
    val a = na * x
    val b = nb * x
    new Line(a1 + a, b1 + b, a2 + a, b2 + b)
  }

  def translate(a: Double, b: Double) = {
    new Line(a1 + a, b1 + b, a2 + a, b2 + b)
  }

  def flip = {
    new Line(a2, b2, a1, b1)
  }

  /** Project the given point on the line and returns the corresponding point.
   *  The method returns None is the projection falls outside the bounds.
   */
  def project(a: Double, b: Double,
              ignoreBounds: Boolean = false,
              tolerance: Double = 1e-6) = {
    val xa = a - a1
    val xb = b - b1
    val (da, db) = direction(0)
    val coeff = xa * da + xb * db
    if (ignoreBounds) {
      Some(a1 + coeff * da, b1 + coeff * db)
    } else if (coeff >= -tolerance && coeff <= 1.0+tolerance) {
      val c = clamp(0.0, 1.0, coeff)
      Some(a1 + c * da, b1 + c * db)
    } else {
      None
    }
  }

}

object Line {

  def apply(a1: Double, b1: Double, a2: Double, b2: Double) = {
    new Line(a1, b1, a2, b2)
  }

}
