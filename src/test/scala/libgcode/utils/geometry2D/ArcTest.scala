package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
import scala.math

class ArcTest extends AnyFunSuite {

  def goodEnough(t1: (Double,Double), t2: (Double,Double)) = {
    math.hypot(t1._1 - t2._1, t1._2 - t2._2) < 1e-10
  }

  test("arc 01") {
    val a = Arc(0, 0, 1, 0, math.Pi)
    assert(goodEnough(a(0), (1.0, 0.0)))
    assert(goodEnough(a(0.5), (0.0, 1.0)))
    assert(goodEnough(a(1), (-1.0, 0.0)))
    assert(goodEnough(a.normal(0), (-1.0, 0.0)))
    assert(goodEnough(a.normal(0.5), (0.0, -1.0)))
    assert(goodEnough(a.normal(1), (1.0, 0.0)))
    assert(a.curvature(0) == 1.0)
  }

  test("arc get") {
    val a = Arc(0, 0, 1, 0, 2*math.Pi)
    assert(a.get( 0.7071067811865475, 0.7071067811865475).isDefined)
    assert(a.get( 0.7071067811865475,-0.7071067811865475).isDefined)
    assert(a.get(-0.7071067811865475, 0.7071067811865475).isDefined)
    assert(a.get(-0.7071067811865475,-0.7071067811865475).isDefined)
  }

  test("arc tangent2Point") {
    val a = Arc(0, 0, 1, 0, 2*math.Pi)
    assert(a.tangents2Point( 0, 0).size == 0)
    assert(a.tangents2Point( 1, 0).size == 1)
    assert(a.tangents2Point( 2, 0).size == 2)
  }

  test("arc tangent2Arc") {
    val a0 = Arc(0, 0, 1, 0, 2*math.Pi)
    val a1 = Arc(0, 1, 1, 0, 2*math.Pi)
    val a2 = Arc(0, 2, 1, 0, 2*math.Pi)
    val a3 = Arc(0, 3, 1, 0, 2*math.Pi)
    assert(a0.tangents2Arc(a0).size == 0)
    assert(a0.tangents2Arc(a1).size == 2)
    assert(a0.tangents2Arc(a2).size == 3)
    assert(a0.tangents2Arc(a3).size == 4)
  }

}
