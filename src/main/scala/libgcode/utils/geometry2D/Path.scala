package libgcode.utils.geometry2D

import libgcode.utils._
import libgcode.generator.Config
import libgcode.Command
import scala.math

class Path(val children: IndexedSeq[AbsCurve]) extends Curve[Path] {

  assert(isConntected(), s"Path is not connected: $children")
  assert(children.forall( c => !c.isInstanceOf[Path]), "Path should be flattened. Use the companion object to construct a Path.")

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

  def rotate(a: Double, b: Double, alpha: Double) = {
    val c2 = children.map( c => c.rotate(a, b, alpha) )
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
      Continuity.C0
    }
  }

  def isClosed(tolerance: Double = 1e-6): Boolean = {
    compare(apply(0), apply(1), tolerance)
  }

  protected def selfIntersections(tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    // intersect all pairs and filter out intersections of neighbouring segments at their connecting ends
    var intersecions = Seq[(Double,Double)]()
    val n = children.length
    for (i <- 0 until n) {
      val ci = children(i)
      // FIXME cubic curves can intersect with themselves !
      for (j <- i+1 until n) {
        val cj = children(j)
        var inter = ci.intersect(cj, tolerance = tolerance)
        if (i == 0 && j == n-1 && isClosed(tolerance)) {
          inter = inter.filterNot( point => compare(point, ci(0.0), tolerance) )
        } else if (j == i+1) {
          inter = inter.filterNot( point => compare(point, ci(1.0), tolerance) )
        }
        intersecions ++= inter
      }
    }
    intersecions
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

  protected def pureOffset(x: Double, tolerance: Double = 1e-6): Path = {
    // the offset may result in degenerate curves
    // TODO if the path is open, should we add arc at the beginning and the end ?
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
    new Path(revSeq.reverse)
  }

  // Reference: https://github.com/jbuckmccready/CavalierContours and https://github.com/jbuckmccready/cavalier_contours
  // 1. create the offset of all the children and stich them together (joining curves)
  // 2. if the path is open, do the same with -x as offset
  // 3. find all self-intersections and, if applicable, intersections with the negative offset curve
  // 4. cut into segments starting and finishing at the intersections
  // 5. discard the intersections that are closer than the offsets
  // 6. close the loops including the final one if the path was originially closed
  def offsetAll(x: Double, tolerance: Double = 1e-6): Seq[Path] = {
    // 1. create the offset of all the children and stich them together (joining curves)
    val p = pureOffset(x, tolerance)
    // 2. if the path is open, do the same with -x as offset
    val closed = p.isClosed(tolerance)
    val p2 = if (!closed) Some(pureOffset(-x, tolerance)) else None
    // 3. find all self-intersections and, if applicable, the negative offset line
    val intersections = p.selfIntersections(tolerance) ++ p2.map( p.intersect(_, tolerance = tolerance) ).getOrElse(Seq())
    if (intersections.isEmpty) {
      return p
    }
    // 4. cut into segments starting and finishing at the intersections
    // starting from the 1st child of a closed path, we may be in the middle of one of the segments
    // need to be careful if an intersection happens precisely at the start/end points
    val segments = scala.collection.mutable.Buffer[AbsCurve]()
    var currentSegment: Option[AbsCurve] = None
    for (c <- p.children) {
      val splitPoints = intersections.flatMap{ case (a,b) => c.get(a, b, tolerance = tolerance) }.sorted
      val lb = splitPoints.foldLeft(0.0)( (lb, ub) => {
        assert(lb <= ub)
        if (ub < tolerance) {
          if (currentSegment.isDefined) {
            segments += currentSegment.get
            currentSegment = None
          }
          lb
        } else {
          val c2 = c.restrict(lb, ub)
          segments += currentSegment.map( _.append(c2, tolerance) ).getOrElse(c2)
          currentSegment = None
          ub
        }
      })
      if (lb < 1.0 - tolerance) {
        val c2 = c.restrict(lb, 1.0)
        currentSegment = currentSegment.map( _.append(c2, tolerance) ).orElse(Some(c2))
      }
    }
    if (currentSegment.isDefined) {
      val cs = currentSegment.get
      val end = cs(1.0)
      if (closed &&
          intersections.forall( i => !compare(i, end, tolerance) ) &&
          segments.size > 0) {
        segments(0) = cs.append(segments(0))
      } else {
        segments += cs
      }
    }
    // 5. discard the intersections that are closer than the offsets
    val goodSegments = segments.toSeq.filter( approximateDistance(_) <= x - tolerance )
    // 6. close the loops including the final one if the path was originially closed
    def findClosestIntersection(c : AbsCurve, u: Double): (Double, Double) = {
      intersections.minBy{ case (ia, ib) => 
        val (ca, cb) = c(u)
        distance(ia, ib, ca, cb)
      }
    }
    def findStart(c: AbsCurve) = findClosestIntersection(c, 0.0)
    def findEnd(c: AbsCurve) = findClosestIntersection(c, 1.0)
    // starts and ends are an implicit adjacency matrix
    val starts = goodSegments.groupBy(findStart)
    val ends = goodSegments.groupBy(findEnd)
    // dome marks the edges that have arleady benn processed
    val done = scala.collection.mutable.Set[AbsCurve]()
    def findLoop(start: (Double, Double)): Seq[AbsCurve] = {
      ???
    }
    // TODO traverse with a stack and a set of points along the path
    // invariant is that the segments are connected
    // TODO build a directed labelled graph with intersecions and segments ?
    // tarjan's algo
    // bridges of Koenigsberg
    // - at most two nodes with an odd number of edges (start and end)
    // - all other nodes with 
    // corner cases:
    // - pan-handle paths
    // - paths with self-intersections but no crossing (generalization of pan-handle)
    // 7. sanity checks
    // assert(!isClosed(tolerance) || res.forall(_.isClosed(tolerance)))
    // not closed mean at most 2 unclosed path in res
    ???
  }

  def offset(x: Double, tolerance: Double = 1e-6): Path = {
    val ps = offsetAll(x, tolerance)
    if (ps.size > 1) {
      sys.error("offset is composed of multiple paths!")
    } else {
      ps.head
    }
  }

  def toGCode(config: Config): Seq[Command] = {
    children.flatMap(_.toGCode(config))
  }

  //is convex / convex hull ?

}

object Path {

  def apply(children: Iterable[AbsCurve]): Path = {
    if (children.size == 0) {
      sys.error("empty path")
    } else if (children.size == 1 && children.head.isInstanceOf[Path]) {
      children.head.asInstanceOf[Path]
    } else if (children.isInstanceOf[IndexedSeq[AbsCurve]] && children.forall( c => !c.isInstanceOf[Path] ) ){
      new Path(children.asInstanceOf[IndexedSeq[AbsCurve]])
    } else {
      val buffer = IndexedSeq.newBuilder[AbsCurve]
      for ( c <- children ) {
        if (c.isInstanceOf[Path]) {
          buffer ++= c.asInstanceOf[Path].children
        } else {
          buffer += c
        }
      }
      new Path(buffer.result())
    }
  } 

}
