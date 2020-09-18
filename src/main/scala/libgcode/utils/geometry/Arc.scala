package libgcode.utils.geometry

import scala.math

/** parameters
 * - (a, b): center of rotation
 * - r: radius
 * - alpha: start angle
 * - beta: end angle
 */
class Arc(a: Double, b: Double, r: Double, alpha: Double, beta: Double) extends Curve2D[Arc] {

  assert(r > 0 && alpha != beta && (alpha - beta).abs <= 2 * math.Pi)

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    val angle = alpha + (beta-alpha) * u
    (a + r*math.cos(angle), b + r*math.sin(angle))
  }

  def length = {
    r * (alpha - beta).abs
  }

  def derivative(u: Double) = {
    val angle = alpha + (beta-alpha) * u
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
