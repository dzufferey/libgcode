package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
import scala.math
import libgcode.utils.*

class ArcTest extends AnyFunSuite {

  def goodEnough(t1: (Double, Double), t2: (Double, Double)) = {
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

  test("arc 02") {
    assert(Arc(1, 0, 0, 0, -1, 0).length <= math.Pi)
    assert(Arc(-1, 0, 0, 0, 1, 0).length <= math.Pi)
    assert(Arc(1, 0, 0, 0, 0, 1).length <= math.Pi)
    assert(Arc(1, 0, 0, 0, 0, -1).length <= math.Pi)
    assert(Arc(-1, 0, 0, 0, 0, 1).length <= math.Pi)
    assert(Arc(-1, 0, 0, 0, 0, -1).length <= math.Pi)
    assert(Arc(0, 1, 0, 0, 0, -1).length <= math.Pi)
    assert(Arc(0, 1, 0, 0, 1, 0).length <= math.Pi)
    assert(Arc(0, -1, 0, 0, 1, 0).length <= math.Pi)
    assert(Arc(0, 1, 0, 0, -1, 0).length <= math.Pi)
    assert(Arc(0, -1, 0, 0, -1, 0).length <= math.Pi)

    assert(Arc(1, 1, 0, 0, -1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(1, 1, 0, 0, 1, -1).length <= math.Pi * math.sqrt(2))
    assert(Arc(1, 1, 0, 0, -1, -1).length <= math.Pi * math.sqrt(2))

    assert(Arc(1, -1, 0, 0, 1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(1, -1, 0, 0, -1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(1, -1, 0, 0, -1, -1).length <= math.Pi * math.sqrt(2))

    assert(Arc(-1, 1, 0, 0, 1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(-1, 1, 0, 0, 1, -1).length <= math.Pi * math.sqrt(2))
    assert(Arc(-1, 1, 0, 0, -1, -1).length <= math.Pi * math.sqrt(2))

    assert(Arc(-1, -1, 0, 0, 1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(-1, -1, 0, 0, -1, 1).length <= math.Pi * math.sqrt(2))
    assert(Arc(-1, -1, 0, 0, 1, -1).length <= math.Pi * math.sqrt(2))

    assert(Arc(1, 1, 0, 0, -1, 1).ccw)
    assert(Arc(1, 1, 0, 0, 1, -1).cw)

    assert(Arc(1, -1, 0, 0, 1, 1).ccw)
    assert(Arc(1, -1, 0, 0, -1, -1).cw)

    assert(Arc(-1, 1, 0, 0, 1, 1).cw)
    assert(Arc(-1, 1, 0, 0, -1, -1).ccw)

    assert(Arc(-1, -1, 0, 0, -1, 1).cw)
    assert(Arc(-1, -1, 0, 0, 1, -1).ccw)
  }

  test("arc get") {
    val a = Arc(0, 0, 1, 0, 2 * math.Pi)
    assert(a.get(0.7071067811865475, 0.7071067811865475).isDefined)
    assert(a.get(0.7071067811865475, -0.7071067811865475).isDefined)
    assert(a.get(-0.7071067811865475, 0.7071067811865475).isDefined)
    assert(a.get(-0.7071067811865475, -0.7071067811865475).isDefined)
  }

  test("arc tangent2Point") {
    val a = Arc(0, 0, 1, 0, 2 * math.Pi)
    assert(a.tangents2Point(0, 0).size == 0)
    assert(a.tangents2Point(1, 0).size == 1)
    assert(a.tangents2Point(2, 0).size == 2)
  }

  test("arc tangent2Arc") {
    val a0 = Arc(0, 0, 1, 0, 2 * math.Pi)
    val a1 = Arc(0, 1, 1, 0, 2 * math.Pi)
    val a2 = Arc(0, 2, 1, 0, 2 * math.Pi)
    val a3 = Arc(0, 3, 1, 0, 2 * math.Pi)
    val t0 = a0.tangents2Arc(a0)
    assert(t0.size == 0)
    val t1 = a0.tangents2Arc(a1)
    assert(t1.size == 2)
    assert(t1.forall(l => a0.intersectLine(l).size == 1))
    assert(t1.forall(l => a1.intersectLine(l).size == 1))
    val t2 = a0.tangents2Arc(a2)
    assert(t2.size == 3)
    assert(t2.forall(l => a0.intersectLine(l).size == 1))
    assert(t2.forall(l => a2.intersectLine(l).size == 1))
    val t3 = a0.tangents2Arc(a3)
    assert(t3.size == 4)
    assert(t3.forall(l => a0.intersectLine(l).size == 1))
    assert(t3.forall(l => a3.intersectLine(l).size == 1))
  }

  test("arc insersection") {
    val a0 = Arc(0, 0, 1, 0, 2 * math.Pi)
    val a1 = Arc(0, 1, 1, 0, 2 * math.Pi)
    val a2 = Arc(0, 2, 1, 0, 2 * math.Pi)
    val a3 = Arc(0, 3, 1, 0, 2 * math.Pi)
    val a4 = Arc(0, 0.5, 0.5, 0, 2 * math.Pi)
    val a5 = Arc(0, 0.5, 1.0, 0, 2 * math.Pi)
    val a6 = Arc(0, 1.5, 1.0, 0, 2 * math.Pi)
    val a7 = Arc(0, 0.75, 0.5, 0, 2 * math.Pi)
    assert(a0.intersectArc(a0) == Seq((1.0, 0.0)))
    assert(a0.intersectArc(a3) == Seq())
    assert(a0.intersectArc(a2) == Seq((0.0, 1.0)))
    assert(a2.intersectArc(a0) == Seq((0.0, 1.0)))
    assert(a0.intersectArc(a1) == Seq((0.8660254037844386, 0.5), (-0.8660254037844386, 0.5)))
    assert(a1.intersectArc(a0) == Seq((-0.8660254037844386, 0.5), (0.8660254037844386, 0.5)))
    assert(a0.intersectArc(a4) == Seq((0.0, 1.0)))
    assert(a4.intersectArc(a0) == Seq((0.0, 1.0)))
    assert(a0.intersectArc(a5) == Seq((0.9682458365518543, 0.25), (-0.9682458365518543, 0.25)))
    assert(a5.intersectArc(a0) == Seq((-0.9682458365518543, 0.25), (0.9682458365518543, 0.25)))
    assert(a0.intersectArc(a6) == Seq((0.6614378277661477, 0.75), (-0.6614378277661477, 0.75)))
    assert(a6.intersectArc(a0) == Seq((-0.6614378277661477, 0.75), (0.6614378277661477, 0.75)))
    assert(a0.intersectArc(a7) == Seq((0.4841229182759271, 0.875), (-0.4841229182759271, 0.875)))
    assert(a7.intersectArc(a0) == Seq((0.4841229182759271, 0.875), (-0.4841229182759271, 0.875)))
  }

  test("arc rotate") {
    val a0 = Arc(0, 0, 1, 0, 2 * math.Pi)
    val a1 = Arc(0, 0, 1, 0, math.Pi / 2)
    val a2 = a0.rotate(0, 0, math.Pi)
    val a3 = a1.rotate(0, 0, math.Pi)
    val a4 = a0.rotate(1, 1, math.Pi)
    val a5 = a1.rotate(1, 1, math.Pi)
    assert(compare(a2.a, 0.0, 1e-6) == 0)
    assert(compare(a2.b, 0.0, 1e-6) == 0)
    assert(compare(a3.a, 0.0, 1e-6) == 0)
    assert(compare(a3.b, 0.0, 1e-6) == 0)
    assert(compare(a4.a, 2.0, 1e-6) == 0)
    assert(compare(a4.b, 2.0, 1e-6) == 0)
    assert(compare(a5.a, 2.0, 1e-6) == 0)
    assert(compare(a5.b, 2.0, 1e-6) == 0)
    assert(a2.radius == 1.0)
    assert(a3.radius == 1.0)
    assert(a4.radius == 1.0)
    assert(a5.radius == 1.0)
    assert(a2.alpha == math.Pi)
    assert(a3.alpha == math.Pi)
    assert(a4.alpha == math.Pi)
    assert(a5.alpha == math.Pi)
    assert(a2.beta == 3 * math.Pi)
    assert(a3.beta == 3 * math.Pi / 2)
    assert(a4.beta == 3 * math.Pi)
    assert(a5.beta == 3 * math.Pi / 2)
  }

}
