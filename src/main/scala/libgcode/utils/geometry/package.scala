package libgcode.utils

import scala.math

package object geometry {

  def compare(x: Double, y: Double, tolerance: Double) = {
    if ((x-y).abs <= tolerance) 0
    else if (x < y) -1
    else 1
  }

  def distance(x1: Double, y1: Double,
               x2: Double, y2: Double) = {
    val dx = x1 - x2
    val dy = y1 - y2
    math.sqrt(dx*dx + dy*dy)
  }

  def clamp(min: Double, max: Double, value: Double) = {
    math.min(max, math.max(min, value))
  }

  def linearInterpolation(x: Double, y: Double, u: Double) = {
    x + u * (y - x)
  }

  def linearInterpolationCoeff(x: Double, y: Double, a: Double) = {
    (a - x) / (y - x)
  }

  def evenSteps(start: Double, stop: Double, maxStep: Double) = {
    val delta = stop - start
    val nSteps = math.ceil(delta.abs /  maxStep).toInt
    val stepSize = delta / nSteps
    (nSteps, stepSize)
  }

}
