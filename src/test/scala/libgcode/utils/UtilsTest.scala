package libgcode.utils

import org.scalatest.funsuite.AnyFunSuite
import scala.math

class UtilsTest extends AnyFunSuite {

  test("compare") {
    assert(compare(1.0,0.0,0.0) ==  1)
    assert(compare(0.0,1.0,0.0) == -1)
    assert(compare(0.0,0.0,0.0) ==  0)
    assert(compare(0.0,0.0,2.0) ==  0)
    assert(compare(1.0,0.0,2.0) ==  0)
    assert(compare(0.0,1.0,2.0) ==  0)
  }

  test("distance") {
    assert(compare(distance(0.0,0.0,0.0,0.0),0.0,1e-6) == 0)
    assert(compare(distance(1.0,0.0,0.0,0.0),1.0,1e-6) == 0)
    assert(compare(distance(1.0,0.0,0.0,1.0),1.414213562,1e-6) == 0)
  }

  test("clamp") {
    assert(clamp(0.0,1.0,0.5) == 0.5)
    assert(clamp(0.0,1.0,1.5) == 1.0)
    assert(clamp(0.0,1.0,-1.5) == 0.0)
  }

  test("linear interplation") {
    assert(linearInterpolation(0.0,1.0,0.5) == 0.5)
    assert(linearInterpolation(0.0,1.0,0.0) == 0.0)
    assert(linearInterpolation(0.0,1.0,1.0) == 1.0)
    assert(linearInterpolation(0.0,1.0,2.0) == 2.0)
    assert(linearInterpolation(0.0,1.0,-2.0) == -2.0)
  }

  test("inverse linear interplation") {
    assert(linearInterpolationCoeff(0.0,1.0,0.5) == 0.5)
    assert(linearInterpolationCoeff(0.0,1.0,0.0) == 0.0)
    assert(linearInterpolationCoeff(0.0,1.0,1.0) == 1.0)
    assert(linearInterpolationCoeff(0.0,1.0,2.0) == 2.0)
    assert(linearInterpolationCoeff(0.0,1.0,-2.0) == -2.0)
  }

  test("even steps") {
    assert(evenSteps(0.0,1.0,1.0,1e-6) == (1,1.0))
    assert(evenSteps(0.0,0.5,1.0,1e-6) == (1,0.5))
    assert(evenSteps(0.0,3.0,1.0,1e-6) == (3,1.0))
    assert(evenSteps(0.0,3.0,2.0,1e-6) == (2,1.5))
    assert(evenSteps(0.0,-3.0,2.0,1e-6) == (2,-1.5))
    assert(evenSteps(0.0,1.01,1.0,2e-2) == (1,1.01))
  }

  test("root linear") {
    assert(roots(1.0,2.0,1e-6) == Seq(-2.0))
    assert(roots(0.0,2.0,1e-6) == Seq())
    assert(roots(0.0,0.0,1e-6) == Seq(0.0))
  }

  test("root quadratic") {
    assert(roots(1.0,0.0,-1.0,1e-6) == Seq(-1.0,1.0))
    assert(roots(1.0,0.0,0.0,1e-6) == Seq(0.0))
    assert(roots(1.0,0.0,1.0,1e-6) == Seq())
    assert(roots(1.0,-1.0,-2.0,1e-6) == Seq(-1.0,2.0))
    assert(roots(0.0,1.0,2.0,1e-6) == Seq(-2.0))
  }

  def testSolutions(found: Seq[Double], expected: Seq[Double]) = {
    val res = found.length == expected.length &&
              expected.forall( a => found.exists( b => compare(a,b,1e-6) == 0))
    if (!res) {
      Console.err.println(found)
      Console.err.println(expected)
    }
    res
  }

  test("root cubic") {
    assert(testSolutions(roots(1.0,3.0,-6.0,-8.0,1e-6), Seq(-4.0,-1.0,2.0)))
    assert(testSolutions(roots(2.0,-3.0,-3.0, 2.0,1e-6), Seq(-1.0,0.5,2.0)))
    assert(testSolutions(roots(2.0,3.0,-3.0, 2.0,1e-6), Seq(-2.328589413348392)))
    assert(testSolutions(roots(1.0,0.0,0.0,0.0,1e-6), Seq(0.0)))
    assert(testSolutions(roots(1.0,0.0,0.0,1.0,1e-6), Seq(-1.0)))
    assert(testSolutions(roots(1.0,0.0,0.0,-1.0,1e-6), Seq(1.0)))
    assert(testSolutions(roots(1.0,0.0,0.0,-8.0,1e-6), Seq(2.0)))
    assert(testSolutions(roots(0.0,1.0,-1.0,-2.0,1e-6), Seq(-1.0,2.0)))
    assert(testSolutions(roots(1.0,-1.0,-1.0,1.0,1e-6), Seq(-1.0,1.0)))
    assert(testSolutions(roots(1.0,1.0,-1.0,-1.0,1e-6), Seq(-1.0,1.0)))
    assert(testSolutions(roots(1.0,3.0,3.0,1.0,1e-6), Seq(-1.0)))
  }

  test("rotate around") {
    assert(compare(rotateAround(0,0,0,0,0), (0,0), 1e-6))
    assert(compare(rotateAround(0,0,0,0,1), (0,1), 1e-6))
    assert(compare(rotateAround(0,0,math.Pi,0,1), (0,-1), 1e-6))
    assert(compare(rotateAround(0,0,math.Pi/2,0,1), (-1,0), 1e-6))
    assert(compare(rotateAround(0,1,math.Pi/2,0,0), (1,1), 1e-6))
  }

}
