package libgcode.utils.geometry2D

import libgcode.utils._
import libgcode.generator.Config
import libgcode.Command
import libgcode.extractor._
import scala.math

class CubicInterpolator(m1: Double, n1: Double, o1: Double, p1: Double,
                        m2: Double, n2: Double, o2: Double, p2: Double
                       ) extends Curve[CubicInterpolator] {

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val u3 = math.pow(u, 3)
    val u2 = math.pow(u, 2)
    val a = m1*u3 + n1*u2 + o1*u + p1
    val b = m2*u3 + n2*u2 + o2*u + p2
    (a,b)
  }

  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6) = {
    //compute L2 distance, the corresponding derivative, and then use Newton's methods
    def f(u: Double): Double = {
      val (ua, ub) = apply(u)
      math.hypot(ua - a, ub - b)
    }
    def fp(u: Double): Double = {
      //chain rule (f(g(x)))' = f'(g(x)) ⋅ g'(x)
      val (da, db) = derivative(u)
      val (ua, ub) = apply(u)
      0.5/f(u) * (2*(ua-a) * da + 2*(ub-b) * db) //TODO not sure about ua-a vs a-ua
    }
    libgcode.utils.newton(f, fp, 0.5, tolerance) match {
      case Some(u) =>
        if (ignoreBounds) {
          Some(u)
        } else if (u > -tolerance && u < 1.0 + tolerance) {
          Some(clamp(0,1,u))
        } else {
          None
        }
      case None => None
    }
  }

  def length = {
    val a = m1/4 + n1/3 + o1/2 + p1
    val b = m2/4 + n2/3 + o2/2 + p2
    math.hypot(a, b)
  }

  def derivative(u: Double) = {
    val u2 = math.pow(u, 2)
    val a = 3*m1*u2 + 2*n1*u + o1
    val b = 3*m2*u2 + 2*n2*u + o2
    (a,b)
  }

  def curvature(u: Double) = {
    val (d1a,d1b) = derivative(u)
    val d2a = 6 * m1 * u + 2 * n1
    val d2b = 6 * m2 * u + 2 * n2
    (d1a * d2b - d1b * d2a) / math.pow(d1a*d1a + d1b*d1b, 3/2)
  }

  //Approxomation based on the paper:
  // "An offset spline approximation for plane cubic splines" by Reinhold Klass, 1983
  // FIXME: instead we could compute the expected position/direction for a finite number of samples then use least square to minimize the error
  // TODO check https://raphlinus.github.io/curves/2021/03/11/bezier-fitting.html for a better solution
  def offset(x: Double, tolerance: Double = 1e-6) = {
    def at(u: Double) = {
      val (a, b) = apply(u)
      val (na, nb) = normal(u)
      val (da, db) = derivative(u)
      val c = 1 + x*curvature(u)
      //Because we are in the 2D case, it is simpler than in the paper
      val ao = a + na * x
      val bo = b + nb * x
      val dao = da * c
      val dbo = db * c
      (ao, bo, dao, dbo)
    }
    val (a1, b1, da1, db1) = at(0)
    val (a2, b2, da2, db2) = at(1)
    CubicInterpolator(a1, b1, da1, db1, a2, b2, da2, db2)
  }

  def translate(ta: Double, tb: Double) = {
    val (a1, b1) = apply(0)
    val (a2, b2) = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    CubicInterpolator(a1 + ta, b1 + tb, da1, db1, a2 + ta, b2 + tb, da2, db2)
  }

  def rotate(a: Double, b: Double, alpha: Double) = {
    val (a1, b1) = apply(0)
    val (a2, b2) = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    val (a3, b3) = rotateAround(a, b, alpha, a1, b1)
    val (a4, b4) = rotateAround(a, b, alpha, a2, b2)
    val (da3, db3) = rotateAround(0, 0, alpha, da1, db1)
    val (da4, db4) = rotateAround(0, 0, alpha, da2, db2)
    CubicInterpolator(a3, b3, da3, db3, a4, b4, da4, db4)
  }

  def flip = {
    val (a1, b1) = apply(0)
    val (a2, b2) = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    CubicInterpolator(a2, b2, -da2, -db2, a1, b1, -da1, -db1)
  }

  def restrict(lb: Double, ub: Double): CubicInterpolator = {
    //TODO this is an approx ...
    val (a1, b1) = apply(0)
    val (a2, b2) = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    CubicInterpolator(a1, b1, da1, db1, a2, b2, da2, db2)
  }

  def intersectLine(l: Line,
                    ignoreBounds: Boolean = false,
                    tolerance: Double = 1e-6) = {
    //TODO coordinate change so 'l' is the x-axis, then solve resulting cubic equations
    val (a,b,c) = l.cartesianCoeff
    // https://www.particleincell.com/2013/cubic-line-intersection/
    ???
  }

  def intersectArc(arc: Arc,
                   ignoreBounds: Boolean = false,
                   tolerance: Double = 1e-6): Seq[(Double,Double)] = {
    // http://www.kevlindev.com/gui/math/intersection/Intersection.js
    ???
  }

  def intersectCubic(c: CubicInterpolator,
                     ignoreBounds: Boolean = false,
                     tolerance: Double = 1e-6): Seq[(Double,Double)] = {
    // http://www.kevlindev.com/gui/math/intersection/Intersection.js
    ???
  }

  def intersect(c: AbsCurve, ignoreBounds: Boolean, tolerance: Double): Seq[(Double, Double)] = {
    if (c.isInstanceOf[Arc]) {
      this.intersectArc(c.asInstanceOf[Arc], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Line]) {
      this.intersectLine(c.asInstanceOf[Line], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[CubicInterpolator]) {
      this.intersectCubic(c.asInstanceOf[CubicInterpolator], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Path]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else {
      sys.error(s"does not know how to intersect $this and $c")
    }
  }

  def toGCode(config: Config): Seq[Command] = {
    //TODO approximate by lines and arcs to create a path and then covert that to g-code
    // try bi arc, check max deviation and refine if needed
    ???
  }

}

object CubicInterpolator {

  def apply(a1: Double, b1: Double,         // start point
            da1: Double, db1: Double,       // start derivative
            a2: Double, b2: Double,         // end point
            da2: Double, db2: Double) = {   // end derivative
    //TODO sanity check for the parameters
    // m x³ + n x² + o x + p
    val m1 =   2*a1 +   da1 - 2*a2 + da2
    val n1 = - 3*a1 - 2*da1 + 3*a2 - da2
    val o1 = da1
    val p1 = a1
    val m2 =   2*b1 +   db1 - 2*b2 + db2
    val n2 = - 3*b1 - 2*db1 + 3*b2 - db2
    val o2 = db1
    val p2 = b1
    new CubicInterpolator(m1, n1, o1, p1,
                          m2, n2, o2, p2)
  }
}
