package libgcode.utils

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Poly6Test extends AnyFunSuite with Matchers {

  // Helpers
  def close(a: Double, b: Double, tol: Double = 1e-12) =
    assert(math.abs(a - b) < tol, s"$a should be ~ $b (tol $tol)")

  test("pad zero-extends to degree 6") {
    val p = Poly6.pad(Array(1.0, 2.0, 3.0))
    p shouldBe Array(1.0, 2.0, 3.0, 0.0, 0.0, 0.0, 0.0)
  }

  test("apply with a short array zero-extends") {
    val p = Poly6(Array(1.0, 0.0, 2.0)) // 1 + 2x^2
    close(p.coeffs(0), 1.0); close(p.coeffs(1), 0.0); close(p.coeffs(2), 2.0)
    close(p.coeffs(3), 0.0); close(p.coeffs(6), 0.0)
  }

  test("apply with varargs stores in ascending degree order") {
    val p = Poly6(3.0, 2.0, 1.0) // 3 + 2x + x^2
    close(p.coeffs(0), 3.0); close(p.coeffs(1), 2.0); close(p.coeffs(2), 1.0)
    close(p.coeffs(3), 0.0)
  }

  test("require rejects wrong length") {
    intercept[IllegalArgumentException] { new Poly6(Array(1.0, 2.0, 3.0)) }
  }

  test("eval: constant") {
    close(Poly6(5.0).eval(0.0), 5.0)
    close(Poly6(5.0).eval(123.456), 5.0)
  }

  test("eval: linear") {
    val p = Poly6(2.0, 3.0) // 2 + 3x
    close(p.eval(0.0), 2.0)
    close(p.eval(1.0), 5.0)
    close(p.eval(4.0), 14.0)
    close(p.eval(-1.0), -1.0)
  }

  test("eval: cubic matches hand computation") {
    // 1 + 2x + 3x^2 + 4x^3
    val p = Poly6(1.0, 2.0, 3.0, 4.0)
    close(p.eval(0.5), 1 + 2*0.5 + 3*0.25 + 4*0.125) // 1 + 1 + 0.75 + 0.5 = 3.25
    close(p.eval(1.0), 10.0)
    close(p.eval(2.0), 1 + 4 + 12 + 32) // 49
  }

  test("eval: full degree 6") {
    // x^6 (only coeffs(6) = 1)
    val p = Poly6(Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0))
    close(p.eval(0.0), 0.0)
    close(p.eval(1.0), 1.0)
    close(p.eval(2.0), 64.0)
    close(p.eval(0.5), 1.0/64.0) // 0.5^6 = 1/64 = 0.015625
  }

  test("deriv: constant -> 0") {
    close(Poly6(5.0).deriv(3.14), 0.0)
  }

  test("deriv: linear -> slope") {
    val p = Poly6(2.0, 3.0) // 2 + 3x, deriv = 3
    close(p.deriv(0.0), 3.0)
    close(p.deriv(7.0), 3.0)
  }

  test("deriv: quadratic") {
    val p = Poly6(0.0, 2.0, 3.0) // 2x + 3x^2, deriv = 2 + 6x
    close(p.deriv(0.0), 2.0)
    close(p.deriv(1.0), 8.0)
    close(p.deriv(2.0), 14.0)
  }

  test("deriv: cubic") {
    // x^3 (coeffs(3)=1), deriv = 3x^2
    val p = Poly6(Array(0.0, 0.0, 0.0, 1.0))
    close(p.deriv(0.0), 0.0)
    close(p.deriv(1.0), 3.0)
    close(p.deriv(2.0), 12.0)
  }

  test("deriv: degree 6 -> degree 5") {
    // x^6, deriv = 6x^5
    val p = Poly6(Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0))
    close(p.deriv(1.0), 6.0)
    close(p.deriv(2.0), 6 * 32.0) // 6 * 32 = 192
  }

  test("+ : two polynomials") {
    // (1 + 2x) + (3x^2 + 4x^3) = 1 + 2x + 3x^2 + 4x^3
    val a = Poly6(1.0, 2.0)
    val b = Poly6(0.0, 0.0, 3.0, 4.0)
    val s = a + b
    close(s.eval(0.0), 1.0)
    close(s.eval(1.0), 1 + 2 + 3 + 4) // 10
    close(s.eval(2.0), 1 + 4 + 12 + 32) // 49
  }

  test("+ : Array overload zero-extends the argument") {
    // (x^2) + [5, 0, 1] (i.e. 5 + x^2) = 5 + x^2 + x^2? no: 5 + 1*x^2 = 5 + x^2
    // Actually [5,0,1] is 5 + 0x + 1x^2 = 5 + x^2. So (x^2) + (5 + x^2) = 5 + 2x^2.
    val a = Poly6(Array(0.0, 0.0, 1.0)) // x^2
    val s = a + Array(5.0, 0.0, 1.0)
    close(s.eval(0.0), 5.0)
    close(s.eval(1.0), 7.0) // 5 + 2
    close(s.eval(2.0), 5 + 8) // 13
  }

  test("square: constant") {
    val p = Poly6(3.0) // 3, square = 9
    close(p.square.eval(0.0), 9.0)
    close(p.square.eval(10.0), 9.0)
  }

  test("square: linear (x + 1)^2 = 1 + 2x + x^2") {
    val p = Poly6(1.0, 1.0) // 1 + x
    val s = p.square
    close(s.eval(0.0), 1.0)
    close(s.eval(1.0), 4.0) // (1+1)^2 = 4
    close(s.eval(2.0), 9.0) // (1+2)^2 = 9
    close(s.eval(-1.0), 0.0) // (1-1)^2 = 0
  }

  test("square: matches (p(x))^2 numerically at several points") {
    // a genuine cubic (degree 3): its square is degree 6 and fits exactly
    val p = Poly6(1.0, -2.0, 0.5, 3.0)
    for (x <- Seq(-3.0, -1.5, 0.0, 0.7, 1.0, 2.5, 5.0)) {
      close(p.square.eval(x), p.eval(x) * p.eval(x), 1e-9)
    }
  }

  test("square: rejects a polynomial whose square would exceed degree 6") {
    // degree-5 input: its square is degree 10, which does not fit in Poly6
    val p = Poly6(1.0, 0.0, 0.0, 0.0, 0.0, 1.0) // 1 + x^5
    intercept[IllegalArgumentException] { p.square }
  }

  test("square: cubic (the actual usage in intersectArc/Cubic)") {
    // (a0 + a1x + a2x^2 + a3x^3)^2, checked at several points
    val p = Poly6(2.0, 1.0, -1.0, 4.0)
    for (x <- Seq(-2.0, -0.5, 0.0, 0.3, 1.0, 3.0)) {
      close(p.square.eval(x), p.eval(x) * p.eval(x), 1e-9)
    }
  }

  test("square: cross terms are doubled (not squared)") {
    // (1 + x)^2: the 2*1*x cross term must appear as 2x, giving coeff(1) = 2
    val s = Poly6(1.0, 1.0).square
    close(s.coeffs(0), 1.0) // 1*1
    close(s.coeffs(1), 2.0) // 2*(1*x)
    close(s.coeffs(2), 1.0) // x*x
  }

  test("eval/deriv round-trip: deriv of x^6 is 6x^5") {
    val p = Poly6(Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0))
    val d = Poly6(Array(0.0, 0.0, 0.0, 0.0, 0.0, 6.0)) // 6x^5
    for (x <- Seq(0.0, 0.5, 1.0, 2.0, 3.5)) {
      close(p.deriv(x), d.eval(x), 1e-9)
    }
  }
}
