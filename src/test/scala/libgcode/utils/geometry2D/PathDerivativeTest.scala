package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
import scala.math

class PathDerivativeTest extends AnyFunSuite {

  def goodEnough(t1: (Double, Double), t2: (Double, Double)) =
    math.hypot(t1._1 - t2._1, t1._2 - t2._2) < 1e-10

  /**
    * Convention (established from Line/Arc):
    *   - derivative(u) returns dP/du w.r.t. the curve's LOCAL u in [0,1],
    *     so |derivative(u)| == length for a Line/Arc.
    *   - curvature(u) is the signed geometric curvature (1/R), independent
    *     of parameterization.
    *
    * A Path re-parameterizes with a single GLOBAL u in [0,1] via
    * expand: u2 = u * n (equal-weight, each child owns [i/n, (i+1)/n]).
    * By the chain rule the global derivative must be:
    *   dP/du = children(i).derivative(u2) * n
    * i.e. |dP/du| = n * (child length). The buggy code divided by n instead.
    */

  // Two collinear unit lines: total path length 2, n = 2.
  val twoLines = Path(IndexedSeq(
    Line(0, 0, 1, 0),
    Line(1, 0, 2, 0)
  ))

  test("derivative magnitude follows the chain rule (equal-weight expand)") {
    // n = 2, each child length = 1, so |dP/du| must be 2 * 1 = 2 at interior points.
    for (u <- Seq(0.25, 0.5, 0.75)) {
      val (dx, dy) = twoLines.derivative(u)
      val mag      = math.hypot(dx, dy)
      assert((mag - 2.0).abs < 1e-12, s"|dP/du| at u=$u was $mag, expected 2.0")
    }
  }

  test("derivative direction is the true unit tangent") {
    // The path runs along +X, so the unit tangent is (1, 0) everywhere interior.
    for (u <- Seq(0.25, 0.5, 0.75)) {
      val (dx, dy) = twoLines.derivative(u)
      val mag      = math.hypot(dx, dy)
      assert(goodEnough((dx / mag, dy / mag), (1.0, 0.0)),
        s"tangent at u=$u was (${dx / mag}, ${dy / mag}), expected (1,0)")
    }
  }

  test("derivative magnitude scales with child length (unequal children)") {
    // n = 2, child 0 length = 1, child 1 length = 3.
    // |dP/du| = n * (child length): 2*1 = 2 in the first half, 2*3 = 6 in the second.
    val p = Path(IndexedSeq(
      Line(0, 0, 1, 0),
      Line(1, 0, 4, 0)
    ))
    val (dxa, _) = p.derivative(0.25)
    val (dxb, _) = p.derivative(0.75)
    assert((dxa - 2.0).abs < 1e-12, s"first-half |dP/du| was $dxa, expected 2.0")
    assert((dxb - 6.0).abs < 1e-12, s"second-half |dP/du| was $dxb, expected 6.0")
  }

  // Curvature is a geometric invariant (parameterization-independent).
  // A straight-line path has zero curvature everywhere.
  test("curvature is parameterization-invariant (zero for a straight path)") {
    for (u <- Seq(0.0, 0.25, 0.5, 0.75, 1.0)) {
      assert((twoLines.curvature(u) - 0.0).abs < 1e-12,
        s"curvature at u=$u was ${twoLines.curvature(u)}, expected 0")
    }
  }

  // A path whose single child is an Arc: Path.curvature must equal the
  // child's curvature (no scaling), and direction must be the unit tangent.
  test("curvature of a single-child Path equals the child's curvature") {
    // Arc of radius 2, quarter turn: curvature = sign(beta-alpha)/r = 1/2
    val arc   = Arc(0, 0, 2, 0.0, math.Pi / 2)
    val p     = Path(IndexedSeq(arc))
    for (u <- Seq(0.0, 0.25, 0.5, 0.75, 1.0)) {
      assert((p.curvature(u) - (1.0 / 2.0)).abs < 1e-12,
        s"Path curvature at u=$u was ${p.curvature(u)}, expected 0.5")
    }
  }

}
