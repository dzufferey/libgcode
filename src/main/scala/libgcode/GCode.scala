package libgcode

import java.text.{DecimalFormat,DecimalFormatSymbols}
import java.util.Locale

object CmdType extends Enumeration {
  type CmdType = Value
  val G, M, O, Empty = Value // Empty means it is a comment or changing some parameter like the feed without moving
}

object ParamType extends Enumeration {
  type ParamType = Value
  val A, B, C, D, E, F, H, I, J, K, L, P, Q, R, S, T, X, Y, Z = Value
}

import CmdType._
import ParamType._

// each line is a command
case class Command( ctype: CmdType,
                    code: Seq[Int],             // code X.Y corresponds to Seq(X, Y), ignored for Empty CmdType
                    parameters: Seq[Param],     // parameters
                    line: Option[Int],          // line number (optional)
                    comment: Option[String])  { // trailing comment

  def replaceComment(c: Option[String]) = {
    if (c == comment) this
    else Command(ctype, code, parameters, line, comment)
  }

}

sealed abstract class Param
case class ParamT(ptype: ParamType) extends Param {
  override def toString = ptype.toString
}
case class RealParam(ptype: ParamType, value: Double) extends Param {
  assert(RealParam.is(ptype), ptype.toString + " is not a real valued parameter")
  override def toString = ptype.toString + RealParam.format(value)
  def format(f: Double => String) = ptype.toString + f(value)
}
case class IntParam(ptype: ParamType, value: Int) extends Param {
  assert(IntParam.is(ptype), ptype.toString + " is not an integer valued parameter")
  override def toString = ptype.toString + value
}

object RealParam {
  val types = Set(A, B, C, D, E, F, H, I, J, K, Q, R, X, Y, Z)
  def is(t: ParamType) = types(t)

  protected val df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
  df.setMaximumFractionDigits(340)
  def format(d: Double) = df.format(d)
}

object IntParam {
  val types = Set(L, P, S, T)
  def is(t: ParamType) = types(t)
}
