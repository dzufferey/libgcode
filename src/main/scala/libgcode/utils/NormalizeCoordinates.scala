package libgcode.utils

import libgcode.abstractmachine.*
import libgcode.{Command, Param, CmdType, ParamType, Transducer}
import libgcode.extractor.*
import scala.collection.mutable.Map
import scala.math.*
import Plane.*

//only works for G0/1/2/3
class NormalizeCoordinates(
    initX: Double,
    initY: Double,
    initZ: Double,
    initA: Double,
    initB: Double,
    initC: Double,
    initP: Plane.Plane
) extends AbstractMachine
    with Transducer {

  override protected def init: Seq[Command] = {
    x = initX
    y = initY
    z = initZ
    a = initA
    b = initB
    c = initC
    plane = initP
    val cmdP = initP match {
      case XY => G(17)
      case ZX => G(18)
      case YZ => G(19)
    }
    Seq(
      cmdP,
      G(21), // use milimeters
      G(90)
    ) // use absolute coordinates
  }

  protected def transform(cmd: Command): Seq[Command] = {
    run(cmd)
    val oldX             = x
    val oldY             = y
    val oldZ             = z
    def toMil(d: Double) = if (useMillimeters) d else 25.4 * d
    def mapParam(p: Param) = p match {
      case X(_)  => X(x)
      case Y(_)  => Y(y)
      case Z(_)  => Z(z)
      case A(_)  => A(a)
      case B(_)  => B(b)
      case C(_)  => C(c)
      case I(v)  => I(if (absoluteCoordinates) toMil(v) else oldX + toMil(v))
      case J(v)  => J(if (absoluteCoordinates) toMil(v) else oldY + toMil(v))
      case K(v)  => K(if (absoluteCoordinates) toMil(v) else oldZ + toMil(v))
      case other => other
    }
    cmd match {
      case G(motion @ (0 | 1 | 2 | 3 | 92), 0, params) =>
        Seq(G(motion, params.map(mapParam): _*).replaceComment(cmd.comment))
      case G(20 | 21 | 90 | 91, 0, Seq()) =>
        if (cmd.comment.isEmpty) Seq()
        else Seq(Command(CmdType.Empty, Seq(), Seq(), None, cmd.comment))
      case other => Seq(other)
    }
  }

}
