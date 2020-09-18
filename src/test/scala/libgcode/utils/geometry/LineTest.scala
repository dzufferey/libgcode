package libgcode.utils.geometry

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

}
