package libgcode

import java.math.RoundingMode
import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

// Empty means it is a comment or changing some parameter like the feed without moving
enum CmdType {
  case G, M, O, Empty
}
object CmdType {
  def parse(c: Char): Option[CmdType] = values.find(_.toString.equalsIgnoreCase(c.toString))
}

enum ParamType {
  case A, B, C, D, E, F, H, I, J, K, L, P, Q, R, S, T, X, Y, Z
}
object ParamType {
  def parse(c: Char): Option[ParamType] = values.find(_.toString.equalsIgnoreCase(c.toString))
}

import CmdType.*
import ParamType.*

// each line is a command
case class Command(
    ctype: CmdType,
    code: Seq[Int],         // code X.Y corresponds to Seq(X, Y), ignored for Empty CmdType
    parameters: Seq[Param], // parameters
    line: Option[Int],      // line number (optional)
    comment: Option[String]
) { // trailing comment

  def replaceComment(c: Option[String]) = {
    if (c == comment) this
    else Command(ctype, code, parameters, line, c)
  }

}

sealed abstract class Param
case class ParamT(ptype: ParamType) extends Param {
  override def toString = ptype.toString
}
case class RealParam(ptype: ParamType, value: Double) extends Param {
  assert(RealParam.is(ptype), ptype.toString + " is not a real valued parameter")
  override def toString           = ptype.toString + RealParam.format(value)
  def format(f: Double => String) = ptype.toString + f(value)
}
case class IntParam(ptype: ParamType, value: Int) extends Param {
  assert(IntParam.is(ptype), ptype.toString + " is not an integer valued parameter")
  override def toString = ptype.toString + value
}

object RealParam {
  val types            = Set(A, B, C, D, E, F, H, I, J, K, Q, R, X, Y, Z)
  def is(t: ParamType) = types(t)

  // DecimalFormat is not thread-safe.
  protected val df = new ThreadLocal[DecimalFormat] {
    override def initialValue: DecimalFormat = {
      val f = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
      f.setMaximumFractionDigits(10)
      f.setRoundingMode(RoundingMode.HALF_UP)
      f
    }
  }
  def format(d: Double) = df.get.format(d)
}

object IntParam {
  val types            = Set(L, P, S, T)
  def is(t: ParamType) = types(t)
}
