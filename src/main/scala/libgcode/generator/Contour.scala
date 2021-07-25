package libgcode.generator

import libgcode.Command
import libgcode.extractor._
import libgcode.utils.geometry2D._
import libgcode.utils._
import scala.math

object Contour {


  // find the appropriate tangent
  protected def findTangent(pred: Arc, succ: Arc, tolerance: Double): Line = {
    // if same direction then outer tangent
    if (pred.ccw == succ.ccw) {
      val ot = pred.outerTangents(succ, tolerance = tolerance)
      if (ot.size == 2) {
        if (pred.ccw) ot(0) else ot(1) //TODO check which one
      } else if (ot.size == 1) {
        ot.head
      } else {
        assert(ot.size == 0)
        throw new IllegalArgumentException(s"cannot find outer tangents for $pred and $succ")
      }
    } else { // if opposite direction then inner tangent
      val it = pred.innerTangents(succ, tolerance = tolerance)
      if (it.size == 2) {
        if (pred.ccw) it(0) else it(1) //TODO check which one
      } else if (it.size == 1) {
        it.head
      } else {
        assert(it.size == 0)
        throw new IllegalArgumentException(s"cannot find inner tangents for $pred and $succ")
      }
    }
  }

  // returns the parameter value where the tangent intersect the 1st circle,
  // the tangent line segment, and parameter value of the 2nd intersection
  protected def findTangentWithParameters(pred: Arc, succ: Arc, tolerance: Double): (Double, Line, Double) = {
    val l = findTangent(pred, succ, tolerance)
    val ip = pred.intersectLine(l, tolerance = tolerance)
    assert(ip.size == 1)
    val (a1,b1) = ip.head
    val u1 = pred.get(a1, b1, tolerance = tolerance)
    assert(u1.isDefined)
    val is = succ.intersectLine(l, tolerance = tolerance)
    assert(is.size == 1)
    val (a2,b2) = is.head
    val u2 = pred.get(a2, b2, tolerance = tolerance)
    assert(u2.isDefined)
    (u1.get,l,u2.get)
  }

  protected def makeArc(a: Arc, u1: Double, u2: Double): Arc = {
    val alpha = linearInterpolation(a.alpha, a.beta, u1)
    val beta = linearInterpolation(a.alpha, a.beta, u2)
    Arc(a.a, a.b, a.r, alpha, beta)
  }

  def path(pointsAndRadii: Seq[(Double, Double, Double)], tolerance: Double = 1e-6): Path = {
    val cirles = pointsAndRadii.map{ case (a,b,r) => if (r >= 0) Arc(a,b,r,0,2*math.Pi) else Arc(a,b,-r,2*math.Pi,0) }
    val tgs = for (i <- cirles.indices)
              yield findTangentWithParameters(cirles(i), cirles((i+1) % cirles.size), tolerance)
    // connect the lines with arcs
    val arcs = for (i <- tgs.indices) yield makeArc(cirles((i+1) % tgs.size), tgs(i)._3, tgs((i+1) % tgs.size)._1)
    var ps = Seq[AbsCurve]()
    for (i <- tgs.indices) {
      ps ++= Seq(tgs(i)._2, arcs(i))
    }
    Path(ps.toIndexedSeq)
  }

  def apply(pointsAndRadii: Seq[(Double, Double, Double)],
            c: Double, //z if working in the XY plane
            inside: Boolean = false,
            tolerance: Double = 1e-6
           )(implicit conf: Config): Seq[Command] = {
    val p = path(pointsAndRadii, tolerance)
    val p2 = if (inside) p.offset(-conf.endmillRadius) else p.offset(conf.endmillRadius)
    val p3 = if (inside == conf.climb) p2 else p2.flip
    //move to start, plunge, follow path, retract
    val buffer = scala.collection.mutable.ArrayBuffer.empty[Command]
    buffer += Empty.comment(s"Contour")
    val (a,b) = p3(0)
    buffer += G(0, conf.x(a), conf.y(b))
    buffer += G(1, conf.z(c), F(conf.plungeFeed))
    buffer += Empty(F(conf.feed))
    buffer ++= p3.toGCode(conf)
    buffer += G(1, conf.z(c + conf.travelHeight), F(conf.plungeFeed))
    buffer += Empty(F(conf.feed))
    buffer.toSeq
  }

}
