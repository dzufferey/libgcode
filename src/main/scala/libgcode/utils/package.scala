package libgcode

package object utils {

  def newton(f: Double => Double,
             fp: Double => Double,
             x0: Double,
             progress: Double = 1e-10,
             tolerance: Double = 1e-10) = {
    var x = x0
    var curr = f(x)
    var old = curr
    do {
      old = curr
      x = x - curr/fp(x)
      curr = f(x)
    } while (curr.abs < tolerance &&
             (curr - old).abs > progress &&
             curr.abs < old.abs)
    if (curr.abs < tolerance) {
      Some(x)
    } else {
      None
    }
  }

}
