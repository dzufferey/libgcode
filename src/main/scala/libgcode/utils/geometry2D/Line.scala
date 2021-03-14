package libgcode.utils.geometry2D

import libgcode.utils._
import scala.math

class Line( a1: Double, b1: Double, // start
            a2: Double, b2: Double  // end
          ) extends Curve[Line] {

  assert(!compare((a1, b1), (a2, b2), 1e-6))

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val a =  linearInterpolation(a1, a2, u)
    val b =  linearInterpolation(b1, b2, u)
    (a, b)
  }

  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6) = {
    var w = 0.0
    var n = 0
    if (compare(a1, a2, tolerance) != 0) {
      w += linearInterpolationCoeff(a1, a2, a)
      n += 1
    }
    if (compare(b1, b2, tolerance) != 0) {
      w += linearInterpolationCoeff(b1, b2, b)
      n += 1
    }
    w = w / n
    if (ignoreBounds) {
      val aa = linearInterpolation(a1, a2, w)
      val bb = linearInterpolation(b1, b2, w)
      if (distance(a,b,aa,bb) <= tolerance) {
        Some(w)
      } else {
        None
      }
    } else if (w >= -tolerance && w <= 1 + tolerance) {
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

  def cartesianCoeff = {
    val (na, nb) = normal(0.0)
    (na, nb, -(na*a1 + nb*b1))
  }

  def restrict(_lb: Double, _ub: Double): Line = {
    val la =  linearInterpolation(a1, a2, _lb)
    val lb =  linearInterpolation(b1, b2, _lb)
    val ua =  linearInterpolation(a1, a2, _ub)
    val ub =  linearInterpolation(b1, b2, _ub)
    new Line(la, lb, ua, ub)
  }

  def intersectLine(l2: Line,
                    ignoreBounds: Boolean = false,
                    tolerance: Double = 1e-6): Option[(Double,Double)] = {
    val (u, v, w) = l2.cartesianCoeff
    val (da, db) = derivative(0.0)
    val num = u * a1 + v * b1 + w
    val denom = u * da + v * db
    if (compare(denom, 0.0, tolerance) == 0) {
      if (compare(denom, 0.0, tolerance) == 0) {
        // colinear!
        if (ignoreBounds) {
          Some(a1, b1)
        } else {
          val (l2a1, l2b1) = l2(0.0)
          val c1 = get(l2a1, l2b1, true, tolerance).get
          val (l2a2, l2b2) = l2(1.0)
          val c2 = get(l2a2, l2b2, true, tolerance).get
          val min = math.min(c1, c2)
          val max = math.max(c1, c2)
          if (min >= 0.0 && min <= 1.0) {
            Some(apply(min))
          } else if (min < 0.0 && max >= 0.0) {
            Some(apply(0.0))
          } else {
            None
          }
        }
      } else {
        None
      }
    } else {
      val k = num / denom
      if (ignoreBounds) {
        Some(a1 + k * da, b1 + k * db)
      } else if (k >= -tolerance && k <= 1.0+tolerance) {
        val c = clamp(0.0, 1.0, k)
        val a = a1 + c * da
        val b = b1 + c * db
        if (l2.get(a, b, tolerance = tolerance).isDefined) {
          Some(a, b)
        } else {
          None
        }
      } else {
        None
      }
    }
  }

  def intersectArc(arc: Arc,
                   ignoreBounds: Boolean = false,
                   tolerance: Double = 1e-6): Seq[(Double,Double)] = {
    val (da, db) = derivative(0.0)
    val distA = a1 - arc.a
    val distB = b1 - arc.b
    // reduce to a quadratic equation
    val a = da*da + db*db
    val b = 2*(distA*da + distB*db)
    val c = distA*distA + distB*distB - arc.radius*arc.radius
    // solve the equation
    val solutions = roots(a, b, c, tolerance)
    // check bounds
    val onLine = solutions.flatMap[(Double,Double)](k => {
      if (ignoreBounds) {
        Some((a1+k*da, b1+k*db))
      } else if (k >= -tolerance && k <= 1+tolerance) {
        val c = clamp(0.0, 1.0, k)
        Some((a1+c*da, b1+c*db))
      } else {
        None
      }
    })
    val onCircle = onLine.filter{ case (a, b) => arc.get(a, b, ignoreBounds, tolerance).isDefined }
    onCircle
  }
  
  def intersect(c: AbsCurve,
                ignoreBounds: Boolean = false,
                tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    if (c.isInstanceOf[Arc]) {
      this.intersectArc(c.asInstanceOf[Arc], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Line]) {
      this.intersectLine(c.asInstanceOf[Line], ignoreBounds, tolerance).toSeq
    } else if (c.isInstanceOf[CubicInterpolator]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Path]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else {
      sys.error(s"does not know how to intersect $this and $c")
    }
  }

}

object Line {

  def apply(a1: Double, b1: Double, a2: Double, b2: Double) = {
    new Line(a1, b1, a2, b2)
  }

}
