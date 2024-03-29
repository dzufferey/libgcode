package libgcode.utils.geometry2D

import libgcode.utils._
import libgcode.generator.Config
import libgcode.Command
import scala.math

//TODO param to make the param depend on the length
class Path(val children: IndexedSeq[AbsCurve]) extends Curve[Path] {

  assert(isConntected(), s"not connected $children") //TODO

  protected def expand(u: Double): (Int, Double) = {
    assert(u >= 0 && u <= 1)
    val u2 = u * children.size
    val i = u2.floor.toInt
    if (i == children.size) {
      (i-1, 1)
    } else {
      (i, u2-i)
    }
  }

  protected def compress(i: Int, u2: Double) = {
    (i + u2) / children.size
  }

  def apply(u: Double): (Double, Double) = {
    val (i, u2) = expand(u)
    children(i)(u2)
  }

  /** Returns the parameter for a point on the curve */
  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6): Option[Double] = {
    for (i <- 0 until children.size;
         u <- children(i).get(a,b,ignoreBounds,tolerance)) {
      return Some(compress(i, u))
    }
    return None
  }

  def length: Double = {
    children.foldLeft(0.0)( (acc, c) => acc + c.length)
  }

  def derivative(u: Double): (Double, Double) = {
    val (i, u2) = expand(u)
    val (a, b) = children(i).derivative(u2)
    (a / children.size, b / children.size)
  }

  // signed
  def curvature(u: Double): Double = {
    val (i, u2) = expand(u)
    children(i).curvature(u2)
  }

  def translate(a: Double, b: Double): Path = {
    val c2 = children.map( c => c.translate(a, b) )
    new Path(c2)
  }

  def flip: Path = {
    val c2 = children.map( c => c.flip )
    new Path(c2.reverse)
  }

  def restrict(lb: Double, ub: Double): Path = {
    val (li, lc) = expand(lb)
    val (ui, uc) = expand(ub)
    assert(li <= ui)
    val start = children(li).restrict(lc, 1.0)
    val middle = children.slice(li+1, ui-1)
    val end = children(ui).restrict(0.0, uc)
    val newChildren = start +: middle :+ end
    new Path(newChildren)
  }

  def intersect(c: AbsCurve,
                ignoreBounds: Boolean = false,
                tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    children.flatMap( c2 => c2.intersect(c, ignoreBounds, tolerance) )
  }

  protected def isConntected(tolerance: Double = 1e-6): Boolean = {
    (0 until children.size -1).forall( i => {
      val (a0, b0) = children(i)(1)
      val (a1, b1) = children(i+1)(0)
      compare(a0, a1, tolerance) == 0 && compare(b0, b1, tolerance) == 0
    })
  }

  override def continuity(tolerance: Double = 1e-6): Continuity.Continuity = {
    //TODO child could be a Path ...
    val upper = children.size + (if (isClosed(tolerance)) 0 else -1)
    var g1 = true
    var c1 = true
    var sameRoC = true
    (0 until upper).foreach( i => {
      children(i).continuity(tolerance) match {
        case Continuity.C0 =>
          g1 = false
          c1 = false
          sameRoC = false
        case Continuity.G1 =>
          c1 = false
          sameRoC = false
        case Continuity.C1 =>
          sameRoC = false
        case Continuity.G2 =>
          c1 = false
        case _ => ()
      }
      if (g1) {
        val ch0 = children(i)
        val ch1 = children((i+1) % children.size)
        val colinear = compare(ch0.direction(1), ch1.direction(0), tolerance)
        if (!colinear) {
          g1 = false
          c1 = false
        } else if (c1) {
          c1 = compare(ch0.derivative(1), ch1.derivative(0), tolerance)
        }
        if (sameRoC) {
          sameRoC = g1 && compare(ch0.curvature(1), ch1.curvature(0), tolerance) == 0
        }
      }
    })
    if (c1) {
      if (sameRoC) Continuity.C2 else Continuity.C1
    } else if (g1) {
      if (sameRoC) Continuity.G2 else Continuity.G1
    } else {
      // assert isConntected => C0
      Continuity.C0
    }
  }

  def isClosed(tolerance: Double = 1e-6): Boolean = {
    compare(apply(0), apply(1), tolerance)
  }

  def selfIntersections(tolerance: Double = 1e-6): Int = {
    //careful as cubic curves can intersect with themselves ?!
    ???
  }

  protected def connectingArc(center: (Double, Double), startsAfter: AbsCurve, stopsBefore: AbsCurve) = {
    val (startA, startB) = startsAfter(1)
    val (centerA, centerB) = center
    val (endA, endB) = stopsBefore(0)
    Arc(startA, startB, centerA, centerB, endA, endB)
  }

  protected def connectSegments(c: (Double, Double), c1: AbsCurve, c2: AbsCurve, tolerance: Double = 1e-6): Seq[AbsCurve] = {
    val intersections = c1.intersect(c2, tolerance = tolerance)
    intersections.size match {
      case 0 =>
        // connects the two path with an arc
        Seq(c1, connectingArc(c, c1, c2), c2)
      case 1 =>
        // restrict the two paths to meet at the intersection
        val (a, b) = intersections.head
        val u = c1.get(a, b, tolerance = tolerance).get
        val l = c2.get(a, b, tolerance = tolerance).get
        Seq(c1.restrict(0, u), c2.restrict(l, 1))
      case _ =>
        sys.error("offset does not yet handle multiple intersection")
    }
  }

  // Could use https://github.com/jbuckmccready/CavalierContours for reference
  // TODO in some cases, it could return more than one path (think of an height)
  def offset(x: Double, tolerance: Double = 1e-6): Path = {
    // the offset may result in degenerate curves
    val stack = scala.collection.mutable.Stack[AbsCurve]()
    stack.push(children.head.offset(x))
    for (c <- children.tail) {
      val c2 = c.offset(x) //this may fails if it is circle that become a negative radius
      val previous = stack.pop()
      stack.pushAll(connectSegments(c(0), previous, c2, tolerance))
    }
    val revSeq = if (isClosed(tolerance)) {
        val center = children.head(0)
        val start = stack.pop()
        val end = stack.last
        val result = connectSegments(center, start, end, tolerance)
        result.size match {
          case 2 =>
            stack.push(result(0))
            stack.toIndexedSeq.dropRight(1) :+ result(1)
          case 3 =>
            stack.push(result(0))
            stack.push(result(1))
            stack.toIndexedSeq
          case _ =>
            sys.error("connection should result in 2 or 3 segments")
        }
      } else {
        stack.toIndexedSeq
      }
    val p = new Path(revSeq.reverse)
    assert(!isClosed(tolerance) || p.isClosed(tolerance)) // small sanity check
    p
  }

  def toGCode(config: Config): Seq[Command] = {
    children.flatMap(_.toGCode(config))
  }

  //is convex / convex hull ?

}

object Path {

  def apply(children: IndexedSeq[AbsCurve]) = new Path(children)

}
