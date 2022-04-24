package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
import scala.math

class LineTest extends AnyFunSuite {

  test("line 01") {
    val l = Line(0, 0, 0, 1)
    assert(l(0) == (0.0, 0.0))
    assert(l(0.5) == (0.0, 0.5))
    assert(l(1) == (0.0, 1.0))
    assert(l.normal(0) == (-1.0, 0.0))
    assert(l.normal(0.5) == (-1.0, 0.0))
    assert(l.normal(1) == (-1.0, 0.0))
    assert(l.curvature(0) == 0)
  }

  test("line cart eq") {
    assert(Line(0, 0, 0, 1).cartesianCoeff == (-1.0,0.0,0.0))
    assert(Line(0, 0, 1, 0).cartesianCoeff == (0.0,1.0,0.0))
  }

  test("line insersection") {
    assert(Line(0, 0, 0, 1).intersectLine(Line(0, 0, 1, 0)).isDefined)
    assert(Line(0, 0, 1, 1).intersectLine(Line(0, 0, 0, 1)).isDefined)
    assert(Line(0, 0, 0, 1).intersectLine(Line(0, 0, 0, 1)).isDefined)
    assert(Line(0, 0, 1, 1).intersectLine(Line(1, 0, 0, 1)).isDefined)
    assert(Line(0, 0, 1, 1).intersectLine(Line(0, 1, 1, 0)).isDefined)
    assert(Line(0.0, 0.2, 1.0, 0.2).intersect(Line(0.8, 0.0, 0.8, 1.0)).length == 1)
  }

  test("arc insersection") {
    assert(Line(0, 0, 0, 1).intersectArc(Arc(0, 0, 1, 0, 2*math.Pi)) == Seq((0.0, 1.0)))
    assert(Line(0, 0, 0, 1).intersectArc(Arc(0, 0, 1, 0, 2*math.Pi), true) == Seq((0.0,-1.0), (0.0,1.0)))
    assert(Line(1, -1, 1, 1).intersectArc(Arc(0, 0, 1, 0, 2*math.Pi)) == Seq((1.0,0.0)))
    assert(Line(1, -1, 1, 1).intersectArc(Arc(0, 0, 1, 0, 2*math.Pi), true) == Seq((1.0,0.0)))
    assert(Line(-1, -1, 1, 1).intersectArc(Arc(0, 0, 1, 0, 2*math.Pi)).size == 2)
    assert(Line(-1, -1, 1, 1).intersectArc(Arc(0, 0, 1, 0, math.Pi)).size == 1)
    assert(Line(-1, -1, 1, 1).intersectArc(Arc(0, 0, 1, math.Pi/2, math.Pi)).size == 0)
    assert(Line(-1, -1, -1, 1).intersectArc(Arc(0, 0, 0.9, 0, 2*math.Pi)).size == 0)
  }

}
