package libgcode.utils.geometry

import scala.math

/** parameters
 * - (a, b): center of rotation
 * - r: radius
 * - alpha: start angle
 * - beta: end angle
 */
class Arc(val a: Double, val b: Double, val r: Double, val alpha: Double, val beta: Double) extends Curve2D[Arc] {

  assert(r > 0 && alpha != beta && (alpha - beta).abs <= 2 * math.Pi)

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val angle = linearInterpolation(alpha, beta, u)
    (a + r*math.cos(angle), b + r*math.sin(angle))
  }

  def get(a: Double, b: Double,
          ignoreBounds: Boolean = false,
          tolerance: Double = 1e-6) = {
    if (compare( distance(a,b,this.a,this.b), r, tolerance) == 0) {
      val angle = math.atan2(b - this.b, a - this.a)
      val u = linearInterpolationCoeff(alpha, beta, angle)
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
    new Arc(a, b, r+x, alpha, beta)
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
    if (compare(a, a1, tolerance) == 0 && compare(b, b1, tolerance) == 0) {
      Seq()
    } else {
      val l = Line(a, b, a1, b1)
      val d = l.length
      val c = compare(d, r, tolerance)
      if (c == 0) { // 1 tangent
        val (na, nb) = l.normal(0.0)
        Seq(Line(a - na/2, b - nb/2, a + na/2, b + nb/2))
      } else if (c > 0) { // 2 tangents
        ???
      } else {
        Seq()
      }
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
    if (compare(a, arc.a, tolerance) == 0 && compare(b, arc.b, tolerance) == 0) { //degenerate case
      Seq()
    } else if (r > arc.r) {
      arc.tangents2Arc(this, ignoreBounds, tolerance).map(_.flip) //this has the smaller radius
    } else {
      var ts = Seq[Line]()
      val d = Line(a, b, arc.a, arc.b).length
      def onCircle(l: Line) = {
        ???
      }
      def putOnCircle(l: Line) = {
        val l1 = l.offset(r)
        if (onCircle(l1)) {
          l1
        } else {
          val l2 = l.offset(-r)
          assert(onCircle(l2))
          l2
        }
      }
      def outer = {
        if (compare(r, arc.r, tolerance) == 0) {
          val l = Line(a, b, arc.a, arc.b)
          Seq(l.offset(r), l.offset(-r))
        } else {
          val os = Arc(arc.a, arc.b, arc.r - r, 0, 2*math.Pi).tangents2Point(a, b)
          os.map(putOnCircle)
        }
      }
      def inner = {
        val is = Arc(arc.a, arc.b, arc.r + r, 0, 2*math.Pi).tangents2Point(a, b)
        if (is.length == 1) {
          ??? //TODO special case
        } else {
          is.map(putOnCircle)
        }
      }
      val c1 = compare(d, r + arc.r, tolerance)
      val c2 = compare(d, r - arc.r, tolerance)
      if (c1 >= 0) { // 4 or 3 tangents
        ts ++= outer
        ts ++= inner
      } else if (c1 < 0 && c2 > 0) { // 2 tangents
        ts ++= outer
      } else if (c1 < 0 && c2 == 0) { // 1 tangent
        ???
      } // else no tangent
      //TODO filter the ones that match the angles
      def inBounds(l: Line) = {
        ???
      }
      ts.filter(inBounds)
    }
    //inner for tangent: is the crossing point ?
    //outer tangent: reduce to point and smaller circle then offset
    //degenerate cases: reduce to point and larger circle then offset
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
