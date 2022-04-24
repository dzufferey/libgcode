package libgcode.utils.geometry2D

import org.scalatest.funsuite.AnyFunSuite
import scala.math

class PathTest extends AnyFunSuite {

  def goodEnough(t1: (Double,Double), t2: (Double,Double)) = {
    math.hypot(t1._1 - t2._1, t1._2 - t2._2) < 1e-10
  }

  test("path not connected") {
    assertThrows[java.lang.AssertionError] {
      Path(IndexedSeq(
        Line(0, 0, 1, 0),
        Line(0, 1, 0, 0)
      ))
    }
  }

  def cube = Path(IndexedSeq(
      Line(0, 0, 1, 0),
      Line(1, 0, 1, 1),
      Line(1, 1, 0, 1),
      Line(0, 1, 0, 0)
    ))

  test("path apply 01") {
    assert(goodEnough(cube(0), (0.0, 0.0)))
    assert(goodEnough(cube(1), (0.0, 0.0)))
    assert(goodEnough(cube(0.5), (1.0, 1.0)))
    assert(goodEnough(cube(0.125), (0.5, 0.0)))
  }

  test("path offset 01") {
    val smallCube = cube.offset(0.2)
    assert(smallCube.children.size == 4)
    val largeCube = cube.offset(-0.2)
    assert(largeCube.children.size == 8)
  }


}
