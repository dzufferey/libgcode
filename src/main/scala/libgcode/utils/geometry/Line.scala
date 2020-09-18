package libgcode.utils.geometry

import scala.math

class Line( a1: Double, b1: Double, // start
            a2: Double, b2: Double  // end
          ) extends Curve2D[Line] {

  assert(a1 != a2 || b1 != b2)

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    (a1 + (a2 - a1) * u, b1 + (b2 - b1) * u)
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

}

object Line {

  def apply(a1: Double, b1: Double, a2: Double, b2: Double) = {
    new Line(a1, b1, a2, b2)
  }

}
