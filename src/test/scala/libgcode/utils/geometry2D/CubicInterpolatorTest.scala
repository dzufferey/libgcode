package libgcode.utils.geometry2D

import libgcode.utils.*
import libgcode.{Command, CmdType}
import libgcode.generator.Config
import libgcode.abstractmachine.AbstractMachine
import libgcode.extractor.{G, X, Y, I, J, F, Empty}
import org.scalatest.funsuite.AnyFunSuite

class CubicInterpolatorTest extends AnyFunSuite {

  def goodEnough(t1: (Double, Double), t2: (Double, Double)) = {
    math.hypot(t1._1 - t2._1, t1._2 - t2._2) < 1e-5
  }

  test("cubic 01") {
    val c = CubicInterpolator(0, 0, 1, 0, 1, 1, 1, 0)
    assert(goodEnough(c(0), (0.0, 0.0)))
    assert(goodEnough(c(0.5), (0.5, 0.5)))
    assert(goodEnough(c(1), (1.0, 1.0)))
    assert(goodEnough(c.normal(0), (0.0, 1.0)))
    assert(goodEnough(c.normal(0.5), (-0.8320502, 0.554700)))
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

  test("cubic get 01") {
    // straight segment from (0,0) to (3,4), derivative (3,4) at both ends.
    // On a line, f(u) = distance from the query to the curve has a flat
    // minimum in the normal direction (f' = 0 there), so Newton on (f, f')
    // is degenerate: the update step divides by f', which is 0 at the exact
    // projection. We therefore only check points strictly on the segment
    // (where f(0.5) != 0 and the iteration is well defined).
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    for (u <- Seq(0.1, 0.3, 0.7, 0.9)) {
      val (a, b) = c(u)
      val got    = c.get(a, b)
      assert(got.isDefined, s"get(${a}, $b) on line segment returned None")
      assert((got.get - u).abs < 1e-5, s"get(${a}, $b) = $got != $u")
    }
  }

  test("cubic get 02") {
    // quarter-circle-ish cubic from (1,0) to (0,1), monotonic in both
    // coordinates and with non-zero curvature everywhere, so the distance
    // function has a unique, non-degenerate minimum: Newton should recover
    // u from both sides of the start point u = 0.5. (u = 0.5 is skipped:
    // f(0.5) = 0 there, so the first Newton step 0.5 - f(0.5)/fp(0.5) is
    // 0/0 = NaN and the iteration is undefined; every other u is fine.)
    val r = 1.0
    val k = 4.0 / 3.0 * r
    val q = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    for (u <- Seq(0.1, 0.2, 0.3, 0.4, 0.6, 0.7, 0.8, 0.9)) {
      val (a, b) = q(u)
      val got    = q.get(a, b)
      assert(got.isDefined, s"get(${a}, $b) on quarter circle returned None")
      // get is only precise up to the tolerance: f(u) is a distance, so an
      // error of 1e-5 in u still gives an f residual ~ 1e-5 below the
      // 1e-6 tolerance used as the Newton stopping criterion
      assert((got.get - u).abs < 1e-4, s"get(${a}, $b) = $got != $u")
      // and the found parameter must actually map back to the query point
      assert(goodEnough(q(got.get), (a, b)), s"q(${got.get}) != ($a, $b)")
    }
  }

  test("cubic get 03") {
    // loop cubic (p(0) = p(1) = (0,0), bulging to the right): a point on the
    // right side of the loop has two preimages (one on the way up, one on the
    // way down). Newton starts at u = 0.5 (top of the loop) and converges to
    // one of them; whatever it finds must map back to the query point.
    val c      = CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1)
    val (a, b) = c(0.25) // = c(0.75) by symmetry
    val got    = c.get(a, b)
    assert(got.isDefined, s"get(${a}, $b) on loop cubic returned None")
    assert(goodEnough(c(got.get), (a, b)), s"get(${a}, $b) = $got but c(${got.get}) != (${a}, $b)")
    // and the two preimages really are the symmetric pair
    assert((got.get - 0.25).abs < 1e-4 || (got.get - 0.75).abs < 1e-4, s"get(${a}, $b) = $got, expected 0.25 or 0.75")
  }

  test("cubic get 04") {
    // point on the infinite cubic but outside [0,1]: bounded -> None, unbounded -> u
    // Newton is started at u = 0.5 and only follows the distance function, so
    // the query point must be close enough to the middle of the curve for the
    // iteration to land there.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    // u = -0.2 gives (-0.6, -0.8)
    val (a, b) = (-0.6, -0.8)
    assert(c.get(a, b).isEmpty, s"get(${a}, $b) should be None within bounds")
    val u = c.get(a, b, ignoreBounds = true)
    assert(u.isDefined, s"get(${a}, $b) with ignoreBounds should be defined")
    assert((u.get - (-0.2)).abs < 1e-5, s"get(${a}, $b) with ignoreBounds = $u != -0.2")
    // and beyond the end: u = 1.2 gives (3.6, 4.8)
    val (a2, b2) = (3.6, 4.8)
    assert(c.get(a2, b2).isEmpty, s"get(${a2}, $b2) should be None within bounds")
    val u2 = c.get(a2, b2, ignoreBounds = true)
    assert(u2.isDefined, s"get(${a2}, $b2) with ignoreBounds should be defined")
    assert((u2.get - 1.2).abs < 1e-5, s"get(${a2}, $b2) with ignoreBounds = $u2 != 1.2")
  }

  test("cubic get 05") {
    // point off the curve: should return None (Newton fails to converge
    // within tolerance, or the converged point is not on the curve)
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    // (2, 3) is off the segment (segment passes through (2, 8/3))
    assert(c.get(2.0, 3.0).isEmpty, "get(2, 3) on off-curve point should be None")
    // even with ignoreBounds the closest point does not equal the query
    assert(
      c.get(2.0, 3.0, ignoreBounds = true).isEmpty,
      "get(2, 3) on off-curve point should be None even with ignoreBounds"
    )
  }

  test("cubic restrict 01") {
    // straight segment from (0,0) to (3,4), derivative (3,4) at both ends.
    // Restrict to [0.2, 0.6]: the sub-curve is a line from p(0.2) to p(0.6)
    // with the chain-rule-scaled derivative. This directly checks the
    // (ub - lb) factor that is easy to forget.
    val c  = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    val lb = 0.2
    val ub = 0.6
    val s  = ub - lb // = 0.4
    val r  = c.restrict(lb, ub)
    // endpoints
    assert(goodEnough(r(0), (0.6, 0.8)), s"r(0) = ${r(0)} != (0.6, 0.8)")
    assert(goodEnough(r(1), (1.8, 2.4)), s"r(1) = ${r(1)} != (1.8, 2.4)")
    // derivatives scaled by s = 0.4: (3,4) * 0.4 = (1.2, 1.6)
    assert(goodEnough(r.derivative(0), (1.2, 1.6)), s"r'(0) = ${r.derivative(0)} != (1.2, 1.6)")
    assert(goodEnough(r.derivative(1), (1.2, 1.6)), s"r'(1) = ${r.derivative(1)} != (1.2, 1.6)")
    // length: the original line has length 5, so the sub-segment is 0.4 * 5 = 2.0
    assert((r.length - 2.0).abs < 1e-6, s"r.length = ${r.length} != 2.0")
    // and the sub-curve must agree with the original at the interior
    for (v <- Seq(0.0, 0.25, 0.5, 0.75, 1.0)) {
      val u = lb + v * (ub - lb)
      assert(goodEnough(r(v), c(u)), s"r($v) = ${r(v)} != c($u) = ${c(u)}")
    }
  }

  test("cubic restrict 02") {
    // quarter-circle-ish cubic (non-straight, non-degenerate): restrict to a
    // proper sub-interval and check that the reparameterized curve agrees with
    // the original on that sub-interval, at both positions and derivatives.
    val r  = 1.0
    val k  = 4.0 / 3.0 * r
    val q  = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val lb = 0.3
    val ub = 0.8
    val s  = ub - lb
    val rc = q.restrict(lb, ub)
    for (v <- Seq(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0)) {
      val u = lb + v * (ub - lb)
      assert(goodEnough(rc(v), q(u)), s"rc($v) = ${rc(v)} != q($u) = ${q(u)}")
      // derivative of the reparameterized curve: q'(u) * (ub - lb)
      val (qa, qb) = q.derivative(u)
      assert(
        goodEnough(rc.derivative(v), (qa * s, qb * s)),
        s"rc'($v) = ${rc.derivative(v)} != q'($u)*$s = ${(qa * s, qb * s)}"
      )
    }
  }

  test("cubic restrict 03") {
    // restricting to the whole interval [0, 1] should return an equal curve
    // (s = 1, so the derivatives are unscaled).
    val q  = CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0)
    val rc = q.restrict(0.0, 1.0)
    for (u <- Seq(0.0, 0.2, 0.5, 0.8, 1.0)) {
      assert(goodEnough(rc(u), q(u)), s"rc($u) = ${rc(u)} != q($u) = ${q(u)}")
    }
    assert((rc.length - q.length).abs < 1e-6, s"rc.length = ${rc.length} != q.length = ${q.length}")
  }

  test("cubic restrict 04") {
    // two complementary restrictions cover the whole curve and their lengths
    // add up to the total length.
    val q   = CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0)
    val m   = 0.4
    val rc1 = q.restrict(0.0, m)
    val rc2 = q.restrict(m, 1.0)
    assert((rc1.length + rc2.length - q.length).abs < 1e-6, s"${rc1.length} + ${rc2.length} != ${q.length}")
    // the join point must match
    assert(goodEnough(rc1(1.0), rc2(0.0)), s"rc1(1) = ${rc1(1.0)} != rc2(0) = ${rc2(0.0)}")
  }

  test("cubic restrict 05") {
    val q = CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1)
    val c = q.restrict(0.2, 0.8)
    // the join point must match
    assert(goodEnough(c(0.0), q(0.2)), s"c(0) = ${c(0.0)} != q(0.2) = ${q(0.2)}")
    assert(goodEnough(c(1.0), q(0.8)), s"c(1) = ${c(1.0)} != q(0.2) = ${q(0.8)}")
    // and the sub-curve must agree with the original at the interior
    for (v <- Seq(0.0, 0.25, 0.5, 0.75, 1.0)) {
      val u = 0.2 + v * 0.6
      assert(goodEnough(c(v), q(u)), s"c($v) = ${c(v)} != q($u) = ${c(u)}")
    }
  }

  test("cubic intersectLine 01") {
    // straight segment from (0,0) to (3,4). A line crossing it should give
    // exactly one intersection.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    // vertical line x = 1.5: crosses the segment at (1.5, 2)
    val l  = Line(1.5, -1.0, 1.5, 5.0)
    val is = c.intersectLine(l, false, 1e-6)
    assert(is.size == 1, s"expected 1 intersection, got ${is.size}: ${is.mkString(", ")}")
    assert(goodEnough(is(0), (1.5, 2.0)), s"intersection ${is(0)} != (1.5, 2.0)")
  }

  test("cubic intersectLine 02") {
    // quarter-circle-ish cubic from (1,0) to (0,1). A horizontal line y = 0.5
    // should cross it. Verify the returned point lies on both the curve and the line.
    val r  = 1.0
    val k  = 4.0 / 3.0 * r
    val q  = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val l  = Line(-1.0, 0.5, 2.0, 0.5) // horizontal line y = 0.5
    val is = q.intersectLine(l, false, 1e-6)
    assert(is.size >= 1, s"expected at least 1 intersection, got ${is.size}")
    for (p <- is) {
      // the point must be on the line (y = 0.5)
      assert((p._2 - 0.5).abs < 1e-4, s"intersection ${p._1}, ${p._2} not on y=0.5")
      // and on the curve: there must be a u in [0,1] mapping to it
      assert(q.get(p._1, p._2, false, 1e-4).isDefined, s"intersection $p not on curve")
    }
  }

  test("cubic intersectLine 03") {
    // a line that misses the curve entirely: no intersection.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    // a vertical line far to the right, x = 10 (segment only spans x in [0,3])
    val l  = Line(10.0, -1.0, 10.0, 5.0)
    val is = c.intersectLine(l, false, 1e-6)
    assert(is.isEmpty, s"expected no intersection, got ${is.mkString(", ")}")
  }

  test("cubic intersectLine 04") {
    // parallel (non-collinear) line: no intersection.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4) // the line segment (0,0)-(3,4)
    // a parallel line offset by (1,1): (1,1) to (4,5)
    val l  = Line(1.0, 1.0, 4.0, 5.0)
    val is = c.intersectLine(l, false, 1e-6)
    assert(is.isEmpty, s"expected no intersection (parallel), got ${is.mkString(", ")}")
  }

  test("cubic intersectLine 05") {
    // collinear: the curve lies on the line. The representative point must be in
    // the overlap of the curve's span and the line segment.
    val c  = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4) // the line segment (0,0)-(3,4)
    val l  = Line(0.0, 0.0, 3.0, 4.0)                  // the same segment
    val is = c.intersectLine(l, false, 1e-6)
    assert(is.size == 1, s"expected 1 representative point, got ${is.size}")
    // the overlap starts at the curve's start p(0) = (0,0)
    assert(goodEnough(is(0), (0.0, 0.0)), s"representative point ${is(0)} != (0,0)")
    // and with ignoreBounds it is still a single point
    val is2 = c.intersectLine(l, true, 1e-6)
    assert(is2.size == 1, s"expected 1 representative point (ignoreBounds), got ${is2.size}")
    // a collinear segment that only overlaps partway: (1.5,2)-(4.5,6) overlaps
    // the cubic [0,1] over u in [0.5, 1], so the representative point is at u=0.5
    val l3  = Line(1.5, 2.0, 4.5, 6.0)
    val is3 = c.intersectLine(l3, false, 1e-6)
    assert(is3.size == 1, s"expected 1 representative point, got ${is3.size}")
    assert(goodEnough(is3(0), c(0.5)), s"representative point ${is3(0)} != c(0.5) = ${c(0.5)}")
  }

  test("cubic intersectLine 09") {
    // collinear but DISJOINT: the line segment lies on the same infinite line as
    // the cubic but does not reach it -> no intersection (both-bounds check).
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4) // spans u in [0,1], i.e. (0,0)-(3,4)
    // a segment further along the same line: (4.5,6)-(7.5,10), beyond the cubic
    val l = Line(4.5, 6.0, 7.5, 10.0)
    assert(c.intersectLine(l, false, 1e-6).isEmpty, "collinear but disjoint segments, expect none")
    // with ignoreBounds the line is infinite, so it intersects (representative point)
    assert(c.intersectLine(l, true, 1e-6).size == 1, "collinear with ignoreBounds, expect a representative point")
  }

  test("cubic intersectLine 06") {
    // ignoreBounds: a root outside [0,1] on the extended cubic is kept.
    // The cubic is the straight line (0,0)-(3,4) extended; the vertical line
    // x = 4.5 meets the extended line at (4.5, 6), which is u = 1.5 > 1.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    val l = Line(4.5, -1.0, 4.5, 7.0)
    // within bounds: no intersection (x=4.5 is beyond the cubic segment)
    assert(c.intersectLine(l, false, 1e-6).isEmpty, "x=4.5 out of cubic bounds, expect none")
    // with ignoreBounds: the extended cubic meets the (infinite) line at (4.5, 6)
    val is = c.intersectLine(l, true, 1e-6)
    assert(is.size == 1, s"expected 1 intersection (ignoreBounds), got ${is.size}")
    assert(goodEnough(is(0), (4.5, 6.0)), s"intersection ${is(0)} != (4.5, 6.0)")
  }

  test("cubic intersectLine 08") {
    // the intersection point must lie on BOTH the cubic and the line segment.
    // The cubic segment (0,0)-(3,4) crosses the vertical LINE x=1.5 at (1.5, 2.0),
    // but if the line SEGMENT l is (1.5, 3.0)-(1.5, 10.0), the crossing point
    // (1.5, 2.0) is below the segment (which spans y in [3,10]) -> no intersection.
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    val l = Line(1.5, 3.0, 1.5, 10.0)
    assert(c.intersectLine(l, false, 1e-6).isEmpty, "crossing point (1.5,2) is below segment y in [3,10], expect none")
    // a segment that actually contains the crossing point (1.5, 2.0):
    val l2 = Line(1.5, 1.0, 1.5, 3.0)
    val is = c.intersectLine(l2, false, 1e-6)
    assert(is.size == 1, s"expected 1 intersection, got ${is.size}")
    assert(goodEnough(is(0), (1.5, 2.0)), s"intersection ${is(0)} != (1.5, 2.0)")
  }

  test("cubic intersectLine 07") {
    // a curved cubic that loops back and crosses a line twice.
    // p(0)=(0,0) p'(0)=(1,1) p(1)=(0,0) p'(1)=(1,-1): a loop bulging to the left,
    // symmetric about u = 0.5 (top at (0, 0.25)). The horizontal line y = 0.1875
    // crosses the loop once on the way up (u ≈ 0.25) and once on the way down
    // (u ≈ 0.75), giving two intersections. (y = 0.25 would be a tangent: 1.)
    val c  = CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1)
    val l  = Line(-1.0, 0.1875, 2.0, 0.1875)
    val is = c.intersectLine(l, false, 1e-6)
    assert(is.size == 2, s"expected 2 intersections, got ${is.size}: ${is.mkString(", ")}")
    for (p <- is) {
      assert((p._2 - 0.1875).abs < 1e-4, s"intersection ${p._1}, ${p._2} not on y=0.1875")
    }
    // and the tangent case y = 0.25 gives exactly one (the loop's top)
    val l2  = Line(-1.0, 0.25, 2.0, 0.25)
    val is2 = c.intersectLine(l2, false, 1e-6)
    assert(is2.size == 1, s"expected 1 tangent intersection, got ${is2.size}: ${is2.mkString(", ")}")
    assert(goodEnough(is2(0), (0.0, 0.25)), s"tangent point ${is2(0)} != (0, 0.25)")
  }

  test("cubic intersectArc 01") {
    // straight segment (0,0)-(3,0), r = 3 at both ends (a degenerate cubic on the
    // x-axis). A full circle of radius 1 centered at the origin crosses it at
    // (1, 0), which is u = 1/3. Exactly one intersection.
    val c = CubicInterpolator(0, 0, 3, 0, 3, 0, 3, 0)
    val a = Arc(0, 0, 1, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 1, s"expected 1 intersection, got ${is.size}: ${is.mkString(", ")}")
    assert(goodEnough(is(0), (1.0, 0.0)), s"intersection ${is(0)} != (1.0, 0.0)")
    // the point must be on the circle (arc.get returns a defined parameter)
    assert(a.get(1.0, 0.0, false, 1e-6).isDefined, "(1,0) not on the arc")
  }

  test("cubic intersectArc 02") {
    // straight segment (0,-2)-(0,2) on the y-axis (derivative (0,4)). A full
    // circle of radius 1 centered at the origin crosses it at (0, -1) (u = 0.25)
    // and (0, 1) (u = 0.75): two intersections.
    val c = CubicInterpolator(0, -2, 0, 4, 0, 2, 0, 4)
    val a = Arc(0, 0, 1, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 2, s"expected 2 intersections, got ${is.size}: ${is.mkString(", ")}")
    val rounded = is.map(p => (math.round(p._1 * 1e6).toDouble / 1e6, math.round(p._2 * 1e6).toDouble / 1e6)).toSet
    assert(rounded.contains((0.0, -1.0)), s"missing (0,-1): ${is.mkString(", ")}")
    assert(rounded.contains((0.0, 1.0)), s"missing (0,1): ${is.mkString(", ")}")
    for (p <- is)
      assert(a.get(p._1, p._2, false, 1e-6).isDefined, s"intersection $p not on the arc")
  }

  test("cubic intersectArc 03") {
    // straight segment (0,0)-(3,0). A small circle of radius 0.5 centered at
    // (2, 0) is crossed by the segment at (1.5, 0) (u = 0.5) and (2.5, 0)
    // (u = 5/6): two intersections, both within [0, 1].
    val c = CubicInterpolator(0, 0, 3, 0, 3, 0, 3, 0)
    val a = Arc(2, 0, 0.5, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 2, s"expected 2 intersections, got ${is.size}: ${is.mkString(", ")}")
    assert(goodEnough(is(0), (1.5, 0.0)), s"first intersection ${is(0)} != (1.5, 0.0)")
    assert(goodEnough(is(1), (2.5, 0.0)), s"second intersection ${is(1)} != (2.5, 0.0)")
  }

  test("cubic intersectArc 04") {
    // the segment (0,0)-(3,0) never comes within 1 of the circle centered at
    // (1.5, 10) of radius 1 (the closest point on the segment is about 9 away),
    // so there is no intersection.
    val c = CubicInterpolator(0, 0, 3, 0, 3, 0, 3, 0)
    val a = Arc(1.5, 10.0, 1, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.isEmpty, s"expected no intersection, got ${is.mkString(", ")}")
  }

  test("cubic intersectArc 05") {
    // a curved cubic from (0,0) to (3,1) with start tangent (1,3) and end
    // tangent (1,0). A circle of radius 1 centered at (1.5, 0.5) crosses it
    // twice, near u = 0.23 and u = 0.73.
    val c = CubicInterpolator(0, 0, 1, 3, 3, 1, 1, 0)
    val a = Arc(1.5, 0.5, 1, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 2, s"expected 2 intersections, got ${is.size}: ${is.mkString(", ")}")
    for (p <- is) {
      // each point lies on the circle (radius 1 from (1.5,0.5))
      assert(
        math.abs(math.hypot(p._1 - 1.5, p._2 - 0.5) - 1.0) < 1e-4,
        s"intersection ${p._1}, ${p._2} not at distance 1 from (1.5,0.5)"
      )
      // and on the cubic within [0,1]
      assert(c.get(p._1, p._2, false, 1e-4).isDefined, s"intersection $p not on the cubic")
    }
  }

  test("cubic intersectArc 06") {
    // same curved cubic and a larger circle (radius 1.5, same center): still
    // two intersections, near u = 0.04 and u = 0.94.
    val c = CubicInterpolator(0, 0, 1, 3, 3, 1, 1, 0)
    val a = Arc(1.5, 0.5, 1.5, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 2, s"expected 2 intersections, got ${is.size}: ${is.mkString(", ")}")
    for (p <- is) {
      assert(
        math.abs(math.hypot(p._1 - 1.5, p._2 - 0.5) - 1.5) < 1e-4,
        s"intersection ${p._1}, ${p._2} not at distance 1.5 from (1.5,0.5)"
      )
      assert(c.get(p._1, p._2, false, 1e-4).isDefined, s"intersection $p not on the cubic")
    }
  }

  test("cubic intersectArc 07") {
    // tangent contact: the cubic (0,0)-(0,2) with horizontal tangents (1,0) at
    // both ends reaches its endpoint (0,2) moving horizontally, which is
    // perpendicular to the radius to the circle of radius 2 centered at the
    // origin. The only intersection is the tangent point (0,2) (a double root,
    // which the sign-change scan cannot see and Newton must catch).
    val c = CubicInterpolator(0, 0, 1, 0, 0, 2, 1, 0)
    val a = Arc(0, 0, 2, 0, 2 * math.Pi)
    val is = c.intersectArc(a, false, 1e-6)
    assert(is.size == 1, s"expected 1 tangent intersection, got ${is.size}: ${is.mkString(", ")}")
    assert(goodEnough(is(0), (0.0, 2.0)), s"tangent point ${is(0)} != (0.0, 2.0)")
    // with a radius that misses the curve entirely there is no intersection
    val a2 = Arc(0, 0, 2.5, 0, 2 * math.Pi)
    assert(c.intersectArc(a2, false, 1e-6).isEmpty, "radius 2.5 should miss the curve")
  }

  test("cubic intersectArc 08") {
    // the quarter-circle cubic from (1,0) to (0,1) is contained in the first
    // quadrant (angles [0, pi/2]). A full circle of radius 1 centered at the
    // origin crosses it at both endpoints (1,0) and (0,1). A quarter-arc
    // spanning only [0, pi/4] contains (1,0) but not (0,1): the angle-range
    // filter must drop the (0,1) candidate.
    val q = CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0)
    val full = Arc(0, 0, 1, 0, 2 * math.Pi)
    val isFull = q.intersectArc(full, false, 1e-6)
    assert(isFull.size == 2, s"full circle: expected 2, got ${isFull.size}: ${isFull.mkString(", ")}")
    val quarter = Arc(0, 0, 1, 0, math.Pi / 4)
    val isQ = q.intersectArc(quarter, false, 1e-6)
    assert(isQ.size == 1, s"quarter arc: expected 1, got ${isQ.size}: ${isQ.mkString(", ")}")
    assert(goodEnough(isQ(0), (1.0, 0.0)), s"kept point ${isQ(0)} != (1.0, 0.0)")
      // and (0,1) must NOT be present (angle pi/2 is outside [0, pi/4])
    assert(!isQ.exists(p => goodEnough(p, (0.0, 1.0))), "(0,1) should be filtered out by the arc angle range")
    // with ignoreBounds the arc is treated as the full circle AND the cubic is
    // extended to the whole real line. The angle-range filter is lifted, so the
    // extended cubic meets the circle in the third quadrant too (two points that
    // were excluded when bounded). We assert the robust geometric fact: the
    // bounded result's points are a subset of the ignoreBounds result's, and the
    // ignoreBounds result has additional third-quadrant points.
    val isQb = q.intersectArc(quarter, true, 1e-6)
    assert(isQb.size > isQ.size, s"ignoreBounds should add the extended-cubic intersections: ${isQb.mkString(", ")}")
    assert(
      isQb.exists(p => p._1 < 0 && p._2 < 0),
      s"expected a third-quadrant point (extended cubic) in ignoreBounds result: ${isQb.mkString(", ")}"
    )
  }

  test("cubic intersectArc 09") {
    // ignoreBounds extends the cubic past u = 1: the straight segment
    // (0,0)-(3,0) extended as the whole x-axis meets the circle of radius 3
    // centered at (10, 0) at (7, 0) (u = 7/3) and (13, 0) (u = 13/3), both
    // beyond the [0,1] span. Bounded -> none; ignoreBounds -> both.
    val c = CubicInterpolator(0, 0, 3, 0, 3, 0, 3, 0)
    val a = Arc(10, 0, 3, 0, 2 * math.Pi)
    assert(c.intersectArc(a, false, 1e-6).isEmpty, "bounded: circle beyond u=1, expect none")
    val is = c.intersectArc(a, true, 1e-6)
    assert(is.size == 2, s"ignoreBounds: expected 2, got ${is.size}: ${is.mkString(", ")}")
    val rounded = is.map(p => (math.round(p._1 * 1e6).toDouble / 1e6, math.round(p._2 * 1e6).toDouble / 1e6)).toSet
    assert(rounded.contains((7.0, 0.0)), s"missing (7,0): ${is.mkString(", ")}")
    assert(rounded.contains((13.0, 0.0)), s"missing (13,0): ${is.mkString(", ")}")
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
    val r     = 1.0
    val k     = 4.0 / 3.0 * r
    val q     = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val n     = 100000
    var chord = 0.0
    var prev  = q(0.0)
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

  // ---- intersectCubic ----
  //
  // intersectCubic(c) returns the points that lie on BOTH this curve and the
  // cubic `c`, consistent with the other intersect methods (deduplicated, and
  // each point on both curves). The cases below use analytically-known curves
  // so the expected intersections are exact.

  test("cubic intersectCubic 01") {
    // Two straight segments (both degenerate cubics with zero curvature):
    //   A: (0,0) -> (4,0)   along the x-axis
    //   B: (2,-1) -> (2,3)  the vertical line x = 2
    // They cross at exactly one point: (2, 0).
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(2, -1, 0, 4, 2, 3, 0, 4)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size == 1, s"expected 1 intersection, got ${is.size}: ${is.mkString(", ")}")
    assert(math.hypot(is(0)._1 - 2.0, is(0)._2 - 0.0) < 1e-4, s"expected (2,0), got ${is(0)}")
  }

  test("cubic intersectCubic 02") {
    // A: (0,0) -> (4,0)  x-axis.
    // B: (0,1) -> (4,1)  horizontal line y = 1.
    // Parallel lines: no intersection.
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(0, 1, 4, 0, 4, 1, 4, 0)
    assert(a.intersectCubic(b, false, 1e-6).isEmpty, "parallel segments, expect none")
  }

  test("cubic intersectCubic 03") {
    // A: a genuine cubic, (0,0) -> (3,4) with horizontal start/end tangents
    //    (the same curve used throughout this file). It is a single smooth arc
    //    that stays in the upper half and does not self-intersect.
    // B: the horizontal segment (0,0) -> (3,0), i.e. the x-axis over [0,3].
    // A starts at (0,0) on B and otherwise rises above y=0, so there is exactly
    // one shared point: the shared endpoint (0,0).
    val a = CubicInterpolator(0, 0, 3, 0, 3, 4, 3, 0)
    val b = CubicInterpolator(0, 0, 3, 0, 3, 0, 3, 0)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size == 1, s"expected 1, got ${is.size}: ${is.mkString(", ")}")
    assert(math.hypot(is(0)._1 - 0.0, is(0)._2 - 0.0) < 1e-4, s"expected (0,0), got ${is(0)}")
  }

  test("cubic intersectCubic 04") {
    // A: (0,0) -> (4,2)  with horizontal start/end tangents (a cubic arc).
    // B: (0,1) -> (4,1)  horizontal line y = 1.
    // A starts below y=1 and ends above it; by construction (horizontal start
    // tangent, monotone rise) it crosses y=1 exactly once, at x = 2 (by
    // symmetry of the Hermite arc about its midpoint). So one point: (2, 1).
    val a = CubicInterpolator(0, 0, 4, 0, 4, 2, 4, 0)
    val b = CubicInterpolator(0, 1, 4, 0, 4, 1, 4, 0)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size == 1, s"expected 1, got ${is.size}: ${is.mkString(", ")}")
    assert(math.hypot(is(0)._1 - 2.0, is(0)._2 - 1.0) < 1e-3, s"expected (2,1), got ${is(0)}")
  }

  test("cubic intersectCubic 05") {
    // A: (0,0) -> (4,0)  x-axis.
    // B: (0,0) -> (4,0) with start tangent (1,2) and end tangent (1,-2).
    //    B's y-coordinate is y(u) = 2u(1-u) (Hermite basis with p0=p1=0,
    //    m0=2, m1=-2), which is >= 0 on [0,1] and equals 0 only at u=0 and
    //    u=1. Its peak is at (2, 0.5), ABOVE the x-axis. So B touches the
    //    x-axis only at its two endpoints: exactly 2 intersections, (0,0) and
    //    (4,0).
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(0, 0, 1, 2, 4, 0, 1, -2)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size == 2, s"expected 2, got ${is.size}: ${is.mkString(", ")}")
    // both intersections are on the x-axis, at x = 0 and x = 4
    is.foreach { p => assert(p._2.abs < 1e-3, s"point ${p} not on the x-axis") }
    val xs = is.map(_. _1).sorted
    assert(math.abs(xs(0) - 0.0) < 1e-3, s"first crossing not near x=0: ${xs.mkString(",")}")
    assert(math.abs(xs(1) - 4.0) < 1e-3, s"last crossing not near x=4: ${xs.mkString(",")}")
  }

  test("cubic intersectCubic 06") {
    // Tangent (touching) intersection: A is the x-axis (0,0)->(4,0).
    // B has endpoints (0,-1) and (4,-1), start tangent (4,4) and end tangent
    // (-4,-4). It rises from (0,-1) to a peak with a HORIZONTAL tangent exactly
    // at (3, 0) — touching the x-axis there — then descends to (4,-1) (overshooting
    // x to ~4.27 on the way). So B is tangent to the x-axis at (3, 0): a double
    // root with no interior sign changes. (Verified numerically: the peak is at
    // (3,0), y<=0 elsewhere on [0,1].)
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0) // x-axis
    val b = CubicInterpolator(0, -1, 4, 4, 4, -1, -4, -4)
    val is = a.intersectCubic(b, false, 1e-6)
    // A tangency is a double root: exactly one intersection, at (3, 0).
    assert(is.size == 1, s"expected 1 tangency, got ${is.size}: ${is.mkString(", ")}")
    assert(math.hypot(is(0)._1 - 3.0, is(0)._2 - 0.0) < 1e-2, s"tangency not near (3,0): ${is(0)}")
    // the point must lie on B (a real intersection, not a spurious root)
    assert(b.get(is(0)._1, is(0)._2, false, 1e-3).isDefined, s"tangency ${is(0)} not on B")
  }

  test("cubic intersectCubic 07") {
    // Overlap: A and B are the SAME straight segment (0,0)->(4,0). They share
    // the whole segment (a continuum of points). Per the agreed policy, return
    // the overlap endpoints: (0,0) and (4,0).
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size >= 1, s"overlap should return at least one point, got ${is.size}")
    // the endpoints of the shared segment must be present
    val has = (x: Double, y: Double) => is.exists(p => math.hypot(p._1 - x, p._2 - y) < 1e-3)
    assert(has(0.0, 0.0), s"missing overlap endpoint (0,0): ${is.mkString(", ")}")
    assert(has(4.0, 0.0), s"missing overlap endpoint (4,0): ${is.mkString(", ")}")
  }

  test("cubic intersectCubic 08") {
    // ignoreBounds: A is a short segment (0,0)->(2,0) on the x-axis; B is the
    // vertical line x=3 (from (3,-1) to (3,3)). Bounded, the curves do not meet
    // (B is beyond A's end). With ignoreBounds the cubics extend to the whole
    // real line and meet at (3,0).
    val a = CubicInterpolator(0, 0, 2, 0, 2, 0, 2, 0)
    val b = CubicInterpolator(3, -1, 0, 4, 3, 3, 0, 4)
    assert(a.intersectCubic(b, false, 1e-6).isEmpty, "bounded: B beyond A, expect none")
    val is = a.intersectCubic(b, true, 1e-6)
    assert(is.size == 1, s"ignoreBounds: expected 1, got ${is.size}: ${is.mkString(", ")}")
    assert(math.hypot(is(0)._1 - 3.0, is(0)._2 - 0.0) < 1e-4, s"expected (3,0), got ${is(0)}")
  }

  test("cubic intersectCubic 09") {
    // Symmetry / commutativity of the result set: intersecting A with B should
    // yield the same set of points as intersecting B with A (order aside).
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(1, -2, 0, 5, 3, 4, 0, 5)
    val ab = a.intersectCubic(b, false, 1e-6).map(p => (math.round(p._1 * 1e4).toDouble / 1e4, math.round(p._2 * 1e4).toDouble / 1e4)).toSet
    val ba = b.intersectCubic(a, false, 1e-6).map(p => (math.round(p._1 * 1e4).toDouble / 1e4, math.round(p._2 * 1e4).toDouble / 1e4)).toSet
    assert(ab == ba, s"A∩B != B∩A: $ab vs $ba")
  }

  test("cubic intersectCubic 10") {
    // Three genuine crossings (and NOT an overlap): A is the x-axis (0,0)->(4,0).
    // B goes from (0,0) to (4,0) with x(u)=4u and y(u) = u(1-2u)(1-u)
    // (Hermite p0=0, m0=-1, p1=0, m1=-1), which has roots at u = 0, 1/2, 1, i.e.
    // at x = 0, 2, 4. The curve wiggles below the axis on (0, 0.5) and above it
    // on (0.5, 1), so it crosses the x-axis three times. This is a discrete set of
    // intersections, NOT a shared sub-curve — the overlap heuristic must not
    // collapse it. We assert exactly 3, at x = 0, 2, 4.
    val a = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val b = CubicInterpolator(0, 0, 4, -1, 4, 0, 4, -1)
    val is = a.intersectCubic(b, false, 1e-6)
    assert(is.size == 3, s"expected exactly 3, got ${is.size}: ${is.mkString(", ")}")
    // all on the x-axis, at x = 0, 2, 4
    is.foreach { p => assert(p._2.abs < 1e-3, s"point ${p} not on the x-axis") }
    val xs = is.map(_. _1).sorted
    assert(math.abs(xs(0) - 0.0) < 1e-3, s"first crossing not near x=0: ${xs.mkString(",")}")
    assert(math.abs(xs(1) - 2.0) < 1e-3, s"middle crossing not near x=2: ${xs.mkString(",")}")
    assert(math.abs(xs(2) - 4.0) < 1e-3, s"last crossing not near x=4: ${xs.mkString(",")}")
    // each must also lie on B
    is.foreach { p => assert(b.get(p._1, p._2, false, 1e-6).isDefined, s"point ${p} not on B") }
  }

  // --- offset (adaptive Hermite fit of the exact offset curve) ---

  // A 90-degree circular arc of radius 2 centered at the origin, from (2,0) to
  // (0,2), approximated by a single cubic Hermite (the standard Bezier arc
  // approximation, handle length (4/3)tan(22.5deg) ~ 0.5523). It tracks the
  // circle to within ~5.4e-4 in radius, so it is a good stand-in for a circle.
  test("cubic curvature: unit-circle arc has curvature ~ 1/r") {
    // a genuine regression test: curvature = (x' y'' - y' x'') / |p'|^3, so for a
    // circle of radius r it is ~1/r. The old code divided by |p'|^2 (integer
    // 3/2 == 1), giving a value wrong by a factor of |p'|.
    for (u <- Seq(0.0, 0.25, 0.5, 0.75, 1.0)) {
      val k = arcR1.curvature(u)
      assert(math.abs(k - 1.0) < 0.15, s"curvature at u=$u was $k, expected ~1.0")
    }
    // arcR2 (radius 2) should have curvature ~0.5
    for (u <- Seq(0.0, 0.5, 1.0)) {
      val k = arcR2.curvature(u)
      assert(math.abs(k - 0.5) < 0.1, s"curvature at u=$u was $k, expected ~0.5")
    }
  }

  def arcR2 = CubicInterpolator(2, 0, 0, 3.3137084989847603, 0, 2, -3.3137084989847603, 0)
  // A 90-degree arc of radius 1 (same construction as arcR2). Used for the cusp
  // test: its curvature is large enough that a 2.5 inward offset folds back.
  def arcR1 = CubicInterpolator(1, 0, 0, 1.6568542494923802, 0, 1, -1.6568542494923802, 0)
  // A 90-degree arc of radius 0.5 (same construction). Very tight: its curvature
  // is ~2, so an outward offset of ~0.5 (= its radius) folds it into a cusp.
  def arcR05 = CubicInterpolator(0.5, 0, 0, 0.8284271247461901, 0, 0.5, -0.8284271247461901, 0)

  // Sample the offset result and return the max deviation from a circle of the
  // given radius centered at the origin.
  def maxRadiusError(c: AbsCurve, radius: Double, n: Int = 200): Double = {
    var err = 0.0
    for (i <- 0 to n) {
      val (x, y) = c(i / n.toDouble)
      err = math.max(err, math.abs(math.hypot(x, y) - radius))
    }
    err
  }

  test("cubic offset 01: line is offset exactly to a parallel line") {
    // the x-axis segment (0,0)-(4,0); a line's offset is exactly a parallel line
    val c = CubicInterpolator(0, 0, 4, 0, 4, 0, 4, 0)
    val off = c.offset(1.0, 1e-6)
    // should be the single segment (0,1)-(4,1)
    assert(off.isInstanceOf[CubicInterpolator],
      s"a line offset should be a single cubic, got ${off.getClass}")
    assert(goodEnough(off(0.0), (0.0, 1.0)), s"start ${off(0.0)} != (0,1)")
    assert(goodEnough(off(1.0), (4.0, 1.0)), s"end ${off(1.0)} != (4,1)")
    // every sample lies on y = 1
    for (i <- 0 to 20) {
      val (x, y) = off(i / 20.0)
      assert(math.abs(y - 1.0) < 1e-6, s"sample ${i}: y=$y != 1")
    }
  }

  // NOTE on sign convention (matches the existing code): offset(x) = p(u) + x * normal(u)
  // where normal is the LEFT normal of the direction. For the CCW arcR2 that points
  // INWARD, so a POSITIVE x is an inward (shrinking) offset and a NEGATIVE x is an
  // outward (growing) one. The tests below follow that convention.

  test("cubic offset 02: gentle inward arc offset stays concentric within tol") {
    // positive x -> inward -> radius ~ 2 - 0.3 = 1.7
    val off = arcR2.offset(0.3, 1e-6)
    val err = maxRadiusError(off, 1.7)
    assert(err < 1e-3, s"max radius error $err exceeds tol")
  }

  test("cubic offset 03: larger inward arc offset stays concentric (may split)") {
    // a larger inward offset (radius ~ 2 - 1.5 = 0.5) where a single cubic is less
    // likely to meet the tolerance; the adaptive loop may split, but the result
    // must stay concentric within tol regardless of segment count.
    val off = arcR2.offset(1.5, 1e-5)
    val err = maxRadiusError(off, 0.5)
    assert(err < 1e-3, s"max radius error $err exceeds tol")
  }

  test("cubic offset 04: tolerance is respected (coarser -> no more segments)") {
    val coarse = arcR2.offset(1.5, 1e-2)
    val fine   = arcR2.offset(1.5, 1e-5)
    def segs(c: AbsCurve) = c match {
      case p: Path => p.children.size
      case _       => 1
    }
    assert(segs(coarse) <= segs(fine),
      s"coarse tol used MORE segments (${segs(coarse)}) than fine (${segs(fine)})")
    // Note: arcR2 is itself only a ~5.4e-4 approximation to a true circle, so
    // we cannot hold the fit to a tolerance tighter than that input error. Both
    // results must be accurate to within the arc's own approximation error.
    assert(maxRadiusError(coarse, 0.5) < 1e-2, s"coarse not accurate to 1e-2")
    assert(maxRadiusError(fine, 0.5) < 1e-3, s"fine not accurate to 1e-3")
  }

  test("cubic offset 05: offset beyond the radius of curvature throws (cusp)") {
    // arcR05 (radius ~0.5, curvature ~2) offset INWARD by 0.5 (= its radius)
    // pushes the offset past its center of curvature, so 1 - x*curvature = 0 and
    // the offset curve folds back on itself. This is a genuine geometric
    // singularity that must throw a clear error rather than silently emit a bad
    // curve.
    assertThrows[Exception] { arcR05.offset(0.5, 1e-6) }
  }

  test("cubic offset 07: splits are error-driven, not uniform midpoint bisection") {
    // A curve that is nearly straight in the first half and bends sharply in the
    // last quarter. Its offset's Hermite-fit deviation is concentrated away from
    // the midpoint, so an error-driven split places its breakpoints in that
    // region. In contrast, uniform midpoint bisection would ALWAYS put the
    // top-level (first) breakpoint at exactly u = 0.5. So: for a result that
    // needs multiple segments, u = 0.5 must NOT be a breakpoint.
    val bend = CubicInterpolator(0, 0, 1.5, 0, 3, 0.5, 0.5, 1.0)
    val off  = bend.offset(-0.1, 1e-4)
    off match {
      case p: Path =>
        val n   = p.children.size
        assert(n >= 3, s"expected the offset to split into >=3 segments, got $n")
        val breakpoints = (1 until n).map(i => i.toDouble / n)
        assert(!breakpoints.exists(b => math.abs(b - 0.5) < 1e-9),
          s"u=0.5 is a breakpoint, which is what uniform midpoint bisection would produce: ${breakpoints.mkString(", ")}")
      case _ => fail("expected the offset to split into multiple segments")
    }
  }

  test("cubic offset 06: negative (outward) offset is concentric on the other side") {
    // negative x -> outward -> radius ~ 2 + 0.3 = 2.3
    val off = arcR2.offset(-0.3, 1e-6)
    val err = maxRadiusError(off, 2.3)
    assert(err < 1e-3, s"max radius error $err exceeds tol")
  }

  // ---- toGCode (approximation by G1 lines and G2/G3 arcs) ----

  def confTol(tol: Double): Config = {
    val conf = new Config
    conf.tolerance = tol
    conf
  }

  // Reconstruct the Line/Arc path that the emitted commands trace, starting
  // from `start`. The Path constructor asserts the children connect, which
  // doubles as a continuity check of the command sequence.
  def pathOf(cmds: Seq[Command], start: (Double, Double)): Path = {
    var pos  = start
    val segs = scala.collection.mutable.ArrayBuffer.empty[AbsCurve]
    for (c <- cmds) {
      c match {
        case Command(CmdType.G, Seq(1), params, _, _) =>
          val x = params.collectFirst { case X(v) => v }.get
          val y = params.collectFirst { case Y(v) => v }.get
          if (distance(pos._1, pos._2, x, y) > 1e-9) segs += Line(pos._1, pos._2, x, y)
          pos = (x, y)
        case Command(CmdType.G, Seq(2 | 3), params, _, _) =>
          val x = params.collectFirst { case X(v) => v }.get
          val y = params.collectFirst { case Y(v) => v }.get
          val i = params.collectFirst { case I(v) => v }.get
          val j = params.collectFirst { case J(v) => v }.get
          val cxa = pos._1 + i
          val cya = pos._2 + j
          val r   = math.hypot(pos._1 - cxa, pos._2 - cya)
          val a0  = math.atan2(pos._2 - cya, pos._1 - cxa)
          val b0  = math.atan2(y - cya, x - cxa)
          // G2 sweeps clockwise, G3 counter-clockwise
          val beta =
            if (c.code(0) == 2) { if (b0 >= a0) b0 - 2 * math.Pi else b0 }
            else                { if (b0 <= a0) b0 + 2 * math.Pi else b0 }
          if (beta != a0) segs += Arc(cxa, cya, r, a0, beta)
          pos = (x, y)
        case other => fail(s"unexpected command: $other")
      }
    }
    Path(segs.toIndexedSeq)
  }

  // Distance from a point to a curve: exact orthogonal projection for a Line,
  // radial projection (or the nearer endpoint) for an Arc. `pathOf` only
  // builds Lines and Arcs, so this covers every child of the approximation.
  def distTo(c: AbsCurve, x: Double, y: Double): Double = c match {
    case l: Line =>
      val (xa, ya) = l(0.0)
      val (xb, yb) = l(1.0)
      val dx       = xb - xa
      val dy       = yb - ya
      val t        = ((x - xa) * dx + (y - ya) * dy) / (dx * dx + dy * dy)
      math.hypot(x - (xa + t * dx), y - (ya + t * dy))
    case a: Arc =>
      val d = math.hypot(x - a.a, y - a.b)
      if (d < 1e-12) a.r // the point is the center
      else {
        // same angle-range test as Arc.get (handles the angle wrap)
        val th = math.atan2(y - a.b, x - a.a)
        val k  = if (a.alpha < a.beta) ((a.alpha - th) / (2 * math.Pi)).ceil
                 else                   ((a.alpha - th) / (2 * math.Pi)).floor
        val u  = (th + k * 2 * math.Pi - a.alpha) / (a.beta - a.alpha)
        if (u >= 0 && u <= 1) {
          (d - a.r).abs
        } else {
          val sa = (a.a + a.r * math.cos(a.alpha), a.b + a.r * math.sin(a.alpha))
          val sb = (a.a + a.r * math.cos(a.beta), a.b + a.r * math.sin(a.beta))
          math.min(math.hypot(x - sa._1, y - sa._2), math.hypot(x - sb._1, y - sb._2))
        }
      }
    case other => sys.error(s"distTo: unsupported curve type ${other.getClass}")
  }

  // Max distance from dense samples of `curve` to the approximation `path`
  // (each sample is projected onto every child, the min distance is taken).
  // This is the error of the g-code approximation.
  def maxDeviation(curve: AbsCurve, path: Path, n: Int = 1000): Double = {
    var max = 0.0
    for (i <- 0 to n) {
      val (x, y) = curve(i / n.toDouble)
      var d = Double.PositiveInfinity
      for (c <- path.children) d = math.min(d, distTo(c, x, y))
      max = math.max(max, d)
    }
    max
  }

  test("cubic toGCode 01: a line cubic becomes a single G1") {
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    val cmds = c.toGCode(confTol(1e-6))
    assert(cmds.size == 1, s"expected 1 command, got ${cmds.size}")
    cmds(0) match {
      case Command(CmdType.G, Seq(1), Seq(X(x), Y(y)), _, _) =>
        assert((x - 3.0).abs < 1e-9 && (y - 4.0).abs < 1e-9, s"G1 to ($x, $y), expected (3, 4)")
      case other => fail(s"expected a G1 command, got: $other")
    }
  }

  test("cubic toGCode 02: quarter circle is a single G3 with the right center") {
    // the standard Bezier quarter circle (Hermite tangent 4*tan(22.5deg) =
    // 3 * (4/3)tan(22.5deg), the same construction as arcR1): it tracks the
    // unit circle to within ~5.4e-4, well below the tolerance, so the whole
    // curve must fit in one arc whose center is (to within the cubic's own
    // approximation error) the origin.
    val k = 4.0 * math.tan(math.Pi / 8) // = 1.6568542494923802
    val q = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val cmds = q.toGCode(confTol(1e-3))
    assert(cmds.size == 1, s"expected a single arc, got ${cmds.size}: $cmds")
    cmds(0) match {
      case Command(CmdType.G, Seq(3), Seq(X(x), Y(y), I(i), J(j)), _, _) =>
        // from (1,0) to (0,1), counter-clockwise, center at the origin
        assert((x - 0.0).abs < 1e-9 && (y - 1.0).abs < 1e-9, s"end ($x, $y) != (0, 1)")
        assert((i + 1.0).abs < 1e-4 && j.abs < 1e-4, s"I,J = ($i, $j) != (-1, 0)")
      case other => fail(s"expected a G3 command, got: $other")
    }
  }

  test("cubic toGCode 03: approximation stays within the tolerance (arc-ish and S-curve)") {
    for (c <- Seq(
      // quarter circle (one arc)
      CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0),
      // S-curve: horizontal-ish start and end tangents, wiggles below and above
      // the chord (y(u) = -u(1-2u)(1-u))
      CubicInterpolator(0, 0, 4, -1, 4, 0, 4, -1),
      // a loop-ish pretzel that crosses the y-axis at u = 0.5
      CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1)
    )) {
      val tol  = 1e-2
      val cmds = c.toGCode(confTol(tol))
      assert(cmds.nonEmpty, s"no commands for $c")
      val path = pathOf(cmds, c(0.0))
      assert(goodEnough(path(0.0), c(0.0)), s"path start ${path(0.0)} != ${c(0.0)}")
      assert(goodEnough(path(1.0), c(1.0)), s"path end ${path(1.0)} != ${c(1.0)}")
      val dev = maxDeviation(c, path, 2000)
      assert(dev <= tol + 1e-6, s"max deviation $dev exceeds tolerance $tol for $c")
    }
  }

  test("cubic toGCode 04: sagging half-circle cubic is approximated within tolerance") {
    // cubic Hermite from (1,0) to (-1,0) with vertical tangents: it is NOT a
    // good semicircle (it sags to y = 0.25 at u = 0.5), so the approximation
    // must subdivide heavily to track the cubic (not the true circle).
    val c = CubicInterpolator(1, 0, 0, 1, -1, 0, 0, -1)
    val tol  = 1e-2
    val cmds = c.toGCode(confTol(tol))
    assert(cmds.size >= 2, s"expected multiple commands, got ${cmds.size}")
    val path = pathOf(cmds, c(0.0))
    assert(goodEnough(path(1.0), c(1.0)), s"path end ${path(1.0)} != ${c(1.0)}")
    val dev = maxDeviation(c, path, 2000)
    assert(dev <= tol + 1e-6, s"max deviation $dev exceeds tolerance $tol")
  }

  test("cubic toGCode 05: closed loop cubic terminates and returns to the start") {
    // p(0) = p(1) = (0,0): the root chord is a point and the root arc fit
    // degenerates (r = 0), so the subdivision must handle both.
    val c = CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1)
    val tol  = 1e-2
    val cmds = c.toGCode(confTol(tol))
    assert(cmds.nonEmpty, "no commands")
    val path = pathOf(cmds, c(0.0))
    assert(goodEnough(path(1.0), (0.0, 0.0)), s"path end ${path(1.0)} != (0, 0)")
    val dev = maxDeviation(c, path, 1000)
    assert(dev <= tol + 1e-6, s"max deviation $dev exceeds tolerance $tol")
  }

  test("cubic toGCode 06: generated code drives the machine to the curve's end") {
    val q = CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0)
    val cmds = q.toGCode(confTol(1e-3))
    val m = new AbstractMachine
    m.run(Empty(F(200)))
    m.run(G(0, X(1), Y(0))) // position at the curve's start
    for (c <- cmds) m.run(c)
    assert((m.x - 0.0).abs < 1e-6 && (m.y - 1.0).abs < 1e-6, s"machine at (${m.x}, ${m.y}), expected (0, 1)")
  }

  test("cubic toPath 07: a line cubic becomes a single-segment path of one Line") {
    val c = CubicInterpolator(0, 0, 3, 4, 3, 4, 3, 4)
    val p = c.toPath(1e-6)
    assert(p.children.size == 1, s"expected 1 segment, got ${p.children.size}")
    p.children.head match {
      case l: Line =>
        assert(goodEnough(l.apply(0.0), (0.0, 0.0)), s"line start ${l.apply(0.0)} != (0, 0)")
        assert(goodEnough(l.apply(1.0), (3.0, 4.0)), s"line end ${l.apply(1.0)} != (3, 4)")
      case other => fail(s"expected a Line, got: $other")
    }
  }

  test("cubic toPath 08: a quarter circle becomes a single-segment path of one Arc") {
    val k = 4.0 * math.tan(math.Pi / 8) // Bezier quarter-circle tangent (see toGCode 02)
    val q = CubicInterpolator(1, 0, 0, k, 0, 1, -k, 0)
    val p = q.toPath(1e-3)
    assert(p.children.size == 1, s"expected 1 segment, got ${p.children.size}")
    p.children.head match {
      case a: Arc =>
        // counter-clockwise from (1,0) to (0,1); center and radius are correct
        // to within the cubic's own approximation error of the unit circle
        assert(a.ccw, "expected a counter-clockwise arc")
        assert(a.a.abs < 1e-4 && a.b.abs < 1e-4, s"center (${a.a}, ${a.b}) != (0, 0)")
        assert((a.r - 1.0).abs < 1e-4, s"radius ${a.r} != 1")
        assert(goodEnough(a.apply(0.0), (1.0, 0.0)), s"arc start ${a.apply(0.0)} != (1, 0)")
        assert(goodEnough(a.apply(1.0), (0.0, 1.0)), s"arc end ${a.apply(1.0)} != (0, 1)")
      case other => fail(s"expected an Arc, got: $other")
    }
  }

  test("cubic toPath 09: segments are only Line/Arc, connected, and within tolerance") {
    for (c <- Seq(
      CubicInterpolator(1, 0, 0, 4.0 / 3.0, 0, 1, -4.0 / 3.0, 0), // quarter circle
      CubicInterpolator(0, 0, 4, -1, 4, 0, 4, -1),                // S-curve
      CubicInterpolator(0, 0, 1, 1, 0, 0, 1, -1),                 // closed loop
      CubicInterpolator(1, 0, 0, 1, -1, 0, 0, -1)                 // sagging half-circle
    )) {
      val tol = 1e-2
      val p   = c.toPath(tol)
      assert(p.children.nonEmpty, s"no segments for $c")
      assert(p.children.forall(seg => seg.isInstanceOf[Line] || seg.isInstanceOf[Arc]),
        s"non Line/Arc segment in ${p.children}")
      // connected: each segment starts where the previous one ends
      for (i <- 1 until p.children.size) {
        assert(goodEnough(p.children(i).apply(0.0), p.children(i - 1).apply(1.0)),
          s"segment $i starts at ${p.children(i).apply(0.0)} != end of segment ${i - 1} ${p.children(i - 1).apply(1.0)}")
      }
      assert(goodEnough(p.children.head.apply(0.0), c(0.0)), s"path start ${p.children.head.apply(0.0)} != curve start ${c(0.0)}")
      assert(goodEnough(p.children.last.apply(1.0), c(1.0)), s"path end ${p.children.last.apply(1.0)} != curve end ${c(1.0)}")
      val dev = maxDeviation(c, p, 2000)
      assert(dev <= tol + 1e-6, s"max deviation $dev exceeds tolerance $tol for $c")
    }
  }

}
