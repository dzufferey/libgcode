package libgcode.generator

import org.scalatest.funsuite.AnyFunSuite
import libgcode.utils.geometry2D._
import scala.math

class ContourTest extends AnyFunSuite {

  test("contour 01") {
    val path = Contour.path(Seq((0.0,0.0,1.0),(2.0,0.0,1.0)))
    assert(path.children.size == 4)
    assert(path.children(0).isInstanceOf[Line])
    assert(path.children(1).isInstanceOf[Arc])
    assert(path.children(2).isInstanceOf[Line])
    assert(path.children(3).isInstanceOf[Arc])
    //Console.println(path.children)
  }

  test("contour 02") {
    val path = Contour.path(Seq((0.0,0.0,-1.0),(2.0,0.0,-1.0)))
    assert(path.children.size == 4)
    assert(path.children(0).isInstanceOf[Line])
    assert(path.children(1).isInstanceOf[Arc])
    assert(path.children(2).isInstanceOf[Line])
    assert(path.children(3).isInstanceOf[Arc])
    //Console.println(path.children)
  }

  test("contour 03") {
    val path = Contour.path(Seq((-2.0,0.0,1.0),(2.0,0.0,-1.0)))
    assert(path.children.size == 4)
    //Console.println(path.children)
  }

  test("contour 04") {
    val path = Contour.path(Seq((0.0,0.0,1.0),(2.0,0.0,-1.0)))
    assert(path.children.size == 2)
    assert(path.children(0).isInstanceOf[Arc])
    assert(path.children(1).isInstanceOf[Arc])
    //Console.println(path.children)
  }

}
