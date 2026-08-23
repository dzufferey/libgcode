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

  test("cubic length 01") {
    // a straight segment from (0,0) to (3,4): length is 5
    // (derivative (3,4) at both ends)
    val line = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    assert((line.length - 5.0).abs < 1e-6)
  }

  test("cubic length 02") {
    // a straight segment from (0,0) to (1,1): length is √2
    val line = CubicInterpolator(0, 0, 1, 1, 1, 1, 1, 1)
    assert((line.length - math.sqrt(2)).abs < 1e-6)
  }

  test("cubic length 03") {
    // quarter circle of radius 1 from (1,0) to (0,1):
    // x(u) = cos(πu/2), y(u) = sin(πu/2).
    // A cubic Hermite interpolant matching the endpoints and the tangent
    // directions (0,1) at u=0 and (-1,0) at u=1 is not exactly a circle,
    // but for the standard Bezier-style quarter-circle control tangent
    // |p'(0)| = |p'(1)| = 4/3 the resulting curve has a well-known length.
    // We verify against a dense chord-length estimate instead (which
    // converges to the true arc length for smooth curves).
    val r = 1.0
    val k = 4.0 / 3.0 * r
    val q = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val n = 100000
    var chord = 0.0
    var prev = q(0.0)
    for (i <- 1 to n) {
      val p = q(i.toDouble / n)
      chord += math.hypot(p._1 - prev._1, p._2 - prev._2)
      prev = p
    }
    val l = q.length
    // arc length ≥ chord length, and for a smooth curve with 1e5 chords
    // the difference is far below 1e-5
    assert(l >= chord - 1e-12, s"length $l < chord $chord")
    assert((l - chord) < 1e-5, s"length $l far from chord $chord")
    // sanity: it should be close to π/2 (the true quarter-circle length)
    assert((l - math.Pi / 2).abs < 0.05, s"quarter-circle length $l far from π/2")
  }

}
