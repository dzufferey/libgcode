package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
  
class CubicInterpolatorTest extends AnyFunSuite {

  def goodEnough(t1: (Double,Double), t2: (Double,Double)) = {
    math.hypot(t1._1 - t2._1, t1._2 - t2._2) < 1e-5
  }

  test("cubic 01") {
    val c = CubicInterpolator(0, 0, 1, 0, 1, 1, 1, 0)
    assert(goodEnough(c(0), (0.0, 0.0)))
    assert(goodEnough(c(0.5), (0.5, 0.5)))
    assert(goodEnough(c(1), (1.0, 1.0)))
    assert(goodEnough(c.normal(0), (0.0, 1.0)))
    assert(goodEnough(c.normal(0.5), (-0.8320502,0.554700)))
    assert(goodEnough(c.normal(1), (0.0, 1.0)))
    assert(c.curvature(0.5) == 0.0)
  }

}
