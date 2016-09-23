package libgcode

object CmdType extends Enumeration {
  type CmdType = Value
  val G, M, O, T, Empty = Value
}

object ParamType extends Enumeration {
  type ParamType = Value
  val A, B, C, D, E, F, H, I, J, K, L, P, Q, R, S, T, X, Y, Z = Value
}

import CmdType._
import ParamType.{T => PT, _}

// each line is a command
case class Command( ctype: CmdType,             // Empty means it is a comment only
                    code: Seq[Int],             // code X.Y corresponds to Seq(X, Y)
                    parameters: Seq[Param],     // parameters
                    line: Option[Int],          // line number (optional)
                    comment: Option[String])    // trailing comment

sealed abstract class Param
case class RealParam(ptype: ParamType, value: Double) extends Param {
  assert(RealParam.is(ptype), ptype + " is not a real valued parameter")
  override def toString = ptype.toString + value
}
case class IntParam(ptype: ParamType, value: Int) extends Param {
  assert(IntParam.is(ptype), ptype + " is not an integer valued parameter")
  override def toString = ptype.toString + value
}

object RealParam {
  val types = Set(A, B, C, D, E, F, H, I, J, K, P, Q, R, X, Y, Z)
  def is(t: ParamType) = types(t)
}

object IntParam {
  val types = Set(L, S, PT)
  def is(t: ParamType) = types(t)
}
