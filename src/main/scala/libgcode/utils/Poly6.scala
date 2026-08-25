package libgcode.utils

import scala.math

/** A degree-6 polynomial with coefficients in ascending order (`coeffs(0)` is the
  * constant term). Used to represent the squared-distance polynomials that the
  * geometry {@@@code intersect} methods build, so that Horner evaluation,
  * differentiation, and polynomial addition live in one place instead of being
  * hand-rolled (and slightly different) in each method.
  *
  * Coefficients are always stored padded to length 7 (degree 6), so arithmetic
  * results are always degree 6.
  */
final class Poly6(val coeffs: Array[Double]) {
  require(coeffs.length == 7, s"Poly6 expects 7 coefficients, got ${coeffs.length}")

  /** Evaluate by Horner's method. */
  def eval(x: Double): Double = {
    var acc = coeffs(6); var k = 5
    while (k >= 0) { acc = acc * x + coeffs(k); k -= 1 }
    acc
  }

  /** Derivative (degree 5), evaluated by Horner. */
  def deriv(x: Double): Double = {
    var acc = 6.0 * coeffs(6); var k = 5
    while (k >= 1) { acc = acc * x + k * coeffs(k); k -= 1 }
    acc
  }

  /** Coefficient-wise sum, zero-extended to degree 6. */
  def +(that: Poly6): Poly6 = {
    val out = new Array[Double](7)
    for (i <- 0 until 7) out(i) = coeffs(i) + that.coeffs(i)
    new Poly6(out)
  }

  /** Coefficient-wise sum with a lower-degree polynomial (zero-extended). */
  def +(that: Array[Double]): Poly6 =
    this + Poly6(that)

  /** Square of the polynomial via self-convolution with the 2·a(i)·a(j) cross
    * terms.
    *
    * A degree-6 polynomial squared is degree 12, which does not fit in a
    * {@@@code Poly6}. This method is therefore intended for polynomials whose
    * square still fits in degree 6 — in practice the degree-3 coordinate cubics
    * used by the {@@@code intersect} methods. Inputs with a genuinely nonzero
    * coefficient above degree 3 are rejected rather than silently truncated.
    */
  def square: Poly6 = {
    require(coeffs(4).abs == 0.0 && coeffs(5).abs == 0.0 && coeffs(6).abs == 0.0,
      s"Poly6.square requires a cubic (degree <= 3); got degree-6 coefficients ${coeffs(4)}, ${coeffs(5)}, ${coeffs(6)}")
    // convolve the (logical) degree-3 part; result is at most degree 6
    val n = 4
    val out = new Array[Double](2 * n - 1) // degree 0..6
    for (i <- 0 until n; j <- i until n) {
      val v = coeffs(i) * coeffs(j)
      out(i + j) += (if (i == j) v else 2 * v)
    }
    Poly6(out)
  }

  override def toString: String =
    s"Poly6(${coeffs.mkString(", ")})"
}

object Poly6 {
  /** Zero-extend a coefficient vector to length 7 (degree 6). */
  def pad(a: Array[Double]): Array[Double] = {
    val out = new Array[Double](7)
    System.arraycopy(a, 0, out, 0, math.min(a.length, 7))
    out
  }
  /** Build from a coefficient array (zero-extended to degree 6). */
  def apply(c: Array[Double]): Poly6 = new Poly6(pad(c))
  /** Build from individual coefficients in ascending degree order (constant term
    * first). */
  def apply(c0: Double, cs: Double*): Poly6 = apply((c0 +: cs).toArray)
}
