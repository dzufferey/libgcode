package libgcode

import scala.math
import org.apache.commons.math3.complex.Complex

package object utils {

  def newton(f: Double => Double,
             fp: Double => Double,
             x0: Double,
             progress: Double = 1e-10,
             tolerance: Double = 1e-10) = {
    var x = x0
    var curr = f(x)
    var old = curr
    do {
      old = curr
      x = x - curr/fp(x)
      curr = f(x)
    } while (curr.abs < tolerance &&
             (curr - old).abs > progress &&
             curr.abs < old.abs)
    if (curr.abs < tolerance) {
      Some(x)
    } else {
      None
    }
  }

  def compare(x: Double, y: Double, tolerance: Double): Int = {
    if ((x-y).abs <= tolerance) 0
    else if (x < y) -1
    else 1
  }

  def compare(p: (Double, Double), q: (Double, Double), tolerance: Double): Boolean = {
    compare(p._1, q._1, tolerance) == 0 && compare(p._2, q._2, tolerance) == 0
  }

  def distance(x1: Double, y1: Double,
               x2: Double, y2: Double) = {
    val dx = x1 - x2
    val dy = y1 - y2
    math.sqrt(dx*dx + dy*dy)
  }

  def clamp(min: Double, max: Double, value: Double) = {
    math.min(max, math.max(min, value))
  }

  def linearInterpolation(x: Double, y: Double, u: Double): Double = {
    x + u * (y - x)
  }

  def linearInterpolation(x: (Double,Double), y: (Double,Double), u: Double): (Double,Double) = {
    (linearInterpolation(x._1, y._1, u), linearInterpolation(x._2, y._2, u))
  }

  def linearInterpolationCoeff(x: Double, y: Double, a: Double) = {
    (a - x) / (y - x)
  }

  /* returns (n,s) where n the largest integer with "start + n×s = stop" and "s ≤ maxStep" */
  def evenSteps(start: Double, stop: Double, maxStep: Double) = {
    val delta = stop - start
    val nSteps = math.ceil(delta.abs /  maxStep).toInt
    val stepSize = delta / nSteps
    (nSteps, stepSize)
  }

  /* roots of linear equation "ax + b = 0" */
  def roots(a: Double, b: Double, tolerance: Double): Seq[Double] = {
    if (compare(a, 0.0, tolerance) != 0) {
      Seq(-b / a)
    } else if (compare(b, 0.0, tolerance) == 0) {
      Seq(0.0) // ∞ many solution, pick one
    } else {
      Seq() // no solutions
    }
  }

  /* roots of quadratic equation "ax² + bx + c = 0" */
  def roots(a: Double, b: Double, c: Double, tolerance: Double): Seq[Double] = {
    if (compare(a, 0.0, tolerance) != 0) {
      var solutions = Seq.empty[Double]
      val b24ac = b*b - 4*a*c
      if (compare(b24ac, 0.0, tolerance) == 1) {
        solutions +:= (-b + math.sqrt(b24ac)) / (2*a)
        solutions +:= (-b - math.sqrt(b24ac)) / (2*a)
      } else if (compare(b24ac, 0.0, tolerance) == 0) {
        solutions +:= -b / (2*a)
      }
      solutions
    } else {
      roots(b, c, tolerance)
    }
  }

  /* roots of depressed cubic equation "x³ + px + q = 0"
   * https://en.wikipedia.org/wiki/Cubic_equation */
  def depressedCubicRoots(p: Double, q: Double, tolerance: Double): Seq[Double] = {
    //Console.println(s"x³ + $p x + $q = 0")
    val discriminant = - (4*p*p*p + 27*q*q)
    val comp = compare(discriminant, 0.0, tolerance)
    if (comp > 0) {
      // three distinct real roots
      val sq_inner = new Complex(q*q/4 + p*p*p/27)
      val sq = sq_inner.nthRoot(2).get(0) //take the 1st root, any should do
      val cs = sq.add(-q/2).nthRoot(3)
      def root(c: Complex) = {
        val pOver3C = new Complex(p).divide(c.multiply(3))
        val r = c.subtract(pOver3C)
        if (compare(r.getImaginary(), 0.0, tolerance) == 0.0) {
          Some(r.getReal())
        } else {
          Console.err.println(s"not a real root $r")
          None
        }
      }
      Seq(0,1,2).flatMap( i => root(cs.get(i)) )
    } else if (comp == 0) {
      // one or two roots
      if (compare(p, 0.0, tolerance) == 0) {
        Seq(0.0)
      } else {
        Seq(3*q/p, -3*q/(2*p))
      }
    } else {
      // one real root
      val inner = math.sqrt(q*q/4 + p*p*p/27)
      val left = -q/2 + inner
      val right = -q/2 - inner
      val root = math.cbrt(left) + math.cbrt(right)
      Seq(root)
    }
  }

  /* roots of cubic equation "ax³ + bx² + cx + d = 0" */
  def roots(a: Double, b: Double, c: Double, d: Double, tolerance: Double): Seq[Double] = {
    if (compare(a, 0.0, tolerance) == 0) {
      roots(b, c, d, tolerance)
    } else {
      // convert to depressed cubic: t = x + b / 3a
      val p = (3*a*c - b*b) / (3*a*a)
      val q = (2*b*b*b - 9*a*b*c + 27*a*a*d) / (27*a*a*a)
      // solve
      val depressedRoots = depressedCubicRoots(p, q, tolerance)
      // convert back
      depressedRoots.map( t => t - b / (3*a) )
    }
  }

}
