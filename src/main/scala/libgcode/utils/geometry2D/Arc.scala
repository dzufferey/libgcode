package libgcode.utils.geometry2D

import libgcode.utils._
import libgcode.generator.Config
import libgcode.Command
import libgcode.extractor._
import scala.math

/** parameters
 * - (a, b): center of rotation
 * - r: radius
 * - alpha: start angle
 * - beta: end angle
 */
class Arc(val a: Double, val b: Double, val r: Double, val alpha: Double, val beta: Double) extends Curve[Arc] {

  assert(r > 0 && alpha != beta && (alpha - beta).abs <= 2 * math.Pi, s"invalid $this")

  override def toString = {
    s"Arc($a, $b, $r, $alpha, $beta)"
  }

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val angle = linearInterpolation(alpha, beta, u)
    (a + r*math.cos(angle), b + r*math.sin(angle))
  }

  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6) = {
    if (compare( distance(a,b,this.a,this.b), r, tolerance) == 0) {
      val angle = math.atan2(b - this.b, a - this.a) // angle ∈ [-π,π]
      // alpha, beta not necessarily in the same interval as angle
      val kAlpha = (alpha - angle) / (2 * math.Pi)
      //val kBeta = (beta - angle) / (2 * math.Pi)
      val k = if (alpha < beta) kAlpha.ceil else kAlpha.floor
      val u = linearInterpolationCoeff(alpha, beta, angle + k*2*math.Pi)
      if (ignoreBounds) {
        Some(u)
      } else if (u >= - tolerance && u <= 1.0 + tolerance) {
        Some(clamp(0.0, 1.0, u))
      } else {
        None
      }
    } else {
      None
    }
  }

  def length = {
    r * (alpha - beta).abs
  }

  def derivative(u: Double) = {
    val angle = linearInterpolation(alpha, beta, u)
    (-r*math.sin(angle), r*math.cos(angle))
  }

  def curvature(u: Double) = {
    (beta - alpha).sign / r
  }

  def offset(x: Double) = {
    if (alpha < beta) {
      new Arc(a, b, r-x, alpha, beta)
    } else {
      new Arc(a, b, r+x, alpha, beta)
    }
  }

  def translate(ta: Double, tb: Double) = {
    new Arc(a+ta, b+tb, r, alpha, beta)
  }

  def flip = {
    new Arc(a, b, r, beta, alpha)
  }

  def centerOfRotation = {
    (a,b)
  }

  def radius = r

  def ccw = alpha <= beta

  def cw = beta <= alpha

  /** Returns the tangents between the given point and the arc.
   *  The method first computes the tangents to the two circle and
   *  then keeps the ones which falls within the arc's angles.
   *  The method return between 0 and 2 lines.
   *  When there are 2 tangents, they start at (a,b) and end on the circle.
   *  When there is only 1 tangent, it has length 1 and (a,b) is the midpoint.
   */
  def tangents2Point(a1: Double, b1: Double,
                     ignoreBounds: Boolean = false,
                     tolerance: Double = 1e-6): Seq[Line] = {
    if (compare((a, b), (a1, b1), tolerance)) {
      Seq()
    } else {
      val l = Line(a, b, a1, b1)
      val d = l.length
      val c = compare(d, r, tolerance)
      if (c == 0) { // 1 tangent
        val (na, nb) = l.normal(0.0)
        Seq(Line(a - na/2, b - nb/2, a + na/2, b + nb/2))
      } else if (c > 0) { // 2 tangents
        // solution from https://mathworld.wolfram.com/CircleTangentLine.html
        val a0 = a - a1
        val b0 = b - b1
        val denom = a0*a0 + b0*b0
        val num0 = -r*a0 + b0 * math.sqrt(denom - r*r)
        val num1 = -r*a0 - b0 * math.sqrt(denom - r*r)
        val angles = Seq( math.acos(num0 / denom),
                         -math.acos(num0 / denom),
                          math.acos(num1 / denom),
                         -math.acos(num1 / denom))
        val sortedAngles = angles.sorted
        val (uniqueAngles, _) = sortedAngles.foldLeft(Seq.empty[Double], 10.0)( (acc, t) => {
          val (seq, t0) = acc
          if (compare(t, t0, tolerance) == 0) {
            acc
          } else {
            (seq :+ t, t)
          }
        })
        val tangents = for (t <- uniqueAngles;
                            a2 <- Some(a + r*math.cos(t));
                            b2 <- Some(b + r*math.sin(t))
                            if compare((a2 - a1) * (a2 - a) + (b2 - b1) * (b2 - b), 0, tolerance) == 0)
                       yield Line(a1, b1, a2, b2)
        tangents.filter(tangentInBounds(_, ignoreBounds, tolerance))
        // alternative
        // let (x,y) be the point of the tangence
        // we can find the two solutions by solving
        //   (x-a1,y-b2)·(x-a,y-b) = 0
        //   |(x-a,y-b)| = r
        //   |(x-a1,y-b1)|² = r² + |(a-a1,b-b1)|²
        // this is 3 equations with up to quadratic terms over x,y.
      } else {
        Seq()
      }
    }
  }

  // checks that there is an intersection with the line and the arc
  protected def onCircle(l: Line, tolerance: Double) = {
    val pts = l.intersectArc(this, true, tolerance)
    pts.size > 0 //TODO check derivative for tangent
  }

  protected def putOnCircles(l: Line, a: Arc, tolerance: Double) = {
    val l1 = l.offset(r)
    if (onCircle(l1, tolerance) && a.onCircle(l1, tolerance)) {
      l1
    } else {
      val l2 = l.offset(-r)
      assert(onCircle(l2, tolerance) && a.onCircle(l2, tolerance))
      l2
    }
  }

  protected def tangentInBounds(l: Line, ignoreBounds: Boolean, tolerance: Double) = {
    l.intersectArc(this, ignoreBounds, tolerance).size > 0
  }

  def innerTangents(arc: Arc,
                    ignoreBounds: Boolean = false,
                    tolerance: Double = 1e-6): Seq[Line] = {
    val is = Arc(arc.a, arc.b, arc.r + r, 0, 2*math.Pi).tangents2Point(a, b, true, tolerance)
    is.map(putOnCircles(_, arc, tolerance)).filter( tangentInBounds(_, ignoreBounds, tolerance) )
  }

  def outerTangents(arc: Arc,
                    ignoreBounds: Boolean = false,
                    tolerance: Double = 1e-6): Seq[Line] = {
    if (compare(r, arc.r, tolerance) == 0) {
      val l = Line(a, b, arc.a, arc.b)
      Seq(l.offset(r), l.offset(-r)).filter( tangentInBounds(_, ignoreBounds, tolerance) )
    } else {
      val os = Arc(arc.a, arc.b, arc.r - r, 0, 2*math.Pi).tangents2Point(a, b, true, tolerance)
      os.map(putOnCircles(_, arc, tolerance)).filter( tangentInBounds(_, ignoreBounds, tolerance) )
    }
  }

  /** Returns the tangents to the two arcs.
   *  The method first computes the tangents to the two full circles and
   *  then keeps the ones which falls within the arc's angles.
   *  The method return between 0 and 4 lines.
   *  If the two circles are equal then 0 tangents are returned.
   */
  def tangents2Arc(arc: Arc,
                   ignoreBounds: Boolean = false,
                   tolerance: Double = 1e-6): Seq[Line] = {
    if (compare(centerOfRotation, arc.centerOfRotation, tolerance)) { //degenerate case
      Seq()
    } else if (r > arc.r) {
      arc.tangents2Arc(this, ignoreBounds, tolerance).map(_.flip) //this has the smaller radius
    } else {
      var ts = Seq[Line]()
      val d = Line(a, b, arc.a, arc.b).length
      val c1 = compare(d, r + arc.r, tolerance)
      val c2 = compare(d, r - arc.r, tolerance)
      if (c1 >= 0) { // 4 or 3 tangents
        ts ++= outerTangents(arc, ignoreBounds, tolerance)
        ts ++= innerTangents(arc, ignoreBounds, tolerance)
      } else if (c1 < 0 && c2 > 0) { // 2 tangents
        ts ++= outerTangents(arc, ignoreBounds, tolerance)
      } else if (c1 < 0 && c2 == 0) { // 1 tangent
        ts ++= innerTangents(arc, ignoreBounds, tolerance)
      } // else no tangent
      ts
    }
  }

  def restrict(lb: Double, ub: Double): Arc = {
    val newAlpha = linearInterpolation(alpha, beta, lb)
    val newBeta = linearInterpolation(alpha, beta, ub)
    new Arc(a, b, r, newAlpha, newBeta)
  }

  def intersectLine(l: Line,
                    ignoreBounds: Boolean = false,
                    tolerance: Double = 1e-6) = {
    l.intersectArc(this, ignoreBounds, tolerance)
  }

  def intersectArc(arc: Arc,
                   ignoreBounds: Boolean = false,
                   tolerance: Double = 1e-6): Seq[(Double,Double)] = {
    val d = distance(a, b, arc.a, arc.b)
    val dr = (radius - arc.radius).abs
    val sr = radius + arc.radius
    val cdr = compare(d, dr, tolerance)
    val csr = compare(d, sr, tolerance)
    def inBounds(p: (Double, Double)) = {
      val (a,b) = p
      get(a,b,false,tolerance).isDefined &&
      arc.get(a,b,false,tolerance).isDefined
    }
    if (d < tolerance) {
      // corner case: same center
      if (ignoreBounds) {
        Seq(apply(0))
      } else {
        val candidates = Seq(apply(0), apply(1), arc(0), arc(1))
        candidates.find(inBounds) match {
          case Some(pt) => Seq(pt)
          case None => Seq.empty
        }
      }
    } else if (cdr == 0 || csr == 0) {
      // 1 intersection point
      val pt = if (csr == 0) {
          val l = Line(a, b, arc.a, arc.b)
          l(r / sr)
        } else {
          // cannot use Line as u ∉ [0,1]
          val u = if (radius - arc.radius > 0) radius / d else -radius / d
          linearInterpolation(centerOfRotation, arc.centerOfRotation, u)
        }
      if (ignoreBounds) {
        Seq(pt)
      } else {
        if (inBounds(pt)) {
          Seq(pt)
        } else {
          Seq.empty
        }
      }
    } else if (cdr > 0 && csr < 0) {
      // 2 intersection points
      // reduce to line intersection, https://mathworld.wolfram.com/Circle-CircleIntersection.html
      val x = (d*d - arc.radius*arc.radius + radius*radius) / (2*d)
      val l = Line(a, b, arc.a, arc.b)
      val (da,db) = l.direction(0)
      val l2 = Line(a, b, a + da*x, b + db*x)
      val (ma,mb) = l2(1)
      val (na,nb) = l2.normal(1)
      val l3 = Line(ma,mb,ma+na,mb+nb)
      val i0 = intersectLine(l3, true, tolerance)
      val i = i0.filter(inBounds)
      assert(i.length == 2)
      i
    } else {
      // no intersection
      Seq.empty
    }
  }

  def intersect(c: AbsCurve,
                ignoreBounds: Boolean = false,
                tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    if (c.isInstanceOf[Arc]) {
      this.intersectArc(c.asInstanceOf[Arc], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Line]) {
      this.intersectLine(c.asInstanceOf[Line], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[CubicInterpolator]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Path]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else {
      sys.error(s"does not know how to intersect $this and $c")
    }
  }

  def toGCode(config: Config): Seq[Command] = {
    val (a0,b0) = apply(0.0)
    val (a1,b1) = apply(1.0)
    val dir = if (cw) 2 else 3
    Seq(G(dir, config.x(a1), config.y(b1), config.i(a0 - a), config.j(b0 - b)))
  }

}

object Arc {

  def apply(a: Double, b: Double, r: Double, alpha: Double, beta: Double) = {
    new Arc(a, b, r, alpha, beta)
  }

  def apply(a0: Double, b0: Double, ca: Double, cb: Double, a1: Double, b1: Double) = {
    val r0 = math.hypot(a0 - ca, b0 - cb)
    val r1 = math.hypot(a1 - ca, b1 - cb)
    assert((r0 - r1). abs < 1e-5)
    val alpha = math.atan2(b0 - cb, a0 - ca)
    val beta = math.atan2(b1 - cb, a1 - ca)
    new Arc(ca, cb, r0, alpha, beta)
  }

}
