package libgcode.utils.geometry

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

}
