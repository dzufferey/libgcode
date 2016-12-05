package libgcode.extractor

import libgcode.{Command, Param, ParamType, IntParam, RealParam}

class IntParamExtractor(paramType: ParamType.Value) {

  def unapply(p: Param): Option[Int] = p match {
    case IntParam(pt, v) if pt == paramType => Some(v)
    case _ => None
  }

  def apply(i: Int) = IntParam(paramType, i)

}

class RealParamExtractor(paramType: ParamType.Value) {

  def unapply(p: Param): Option[Double] = p match {
    case RealParam(pt, v) if pt == paramType => Some(v)
    case _ => None
  }

  def apply(d: Double) = RealParam(paramType, d)
}


// position
object X extends RealParamExtractor(ParamType.X) { }
object Y extends RealParamExtractor(ParamType.Y) { }
object Z extends RealParamExtractor(ParamType.Z) { }

// orientation
object A extends RealParamExtractor(ParamType.A) { }
object B extends RealParamExtractor(ParamType.B) { }
object C extends RealParamExtractor(ParamType.C) { }

// 
object D extends RealParamExtractor(ParamType.D) { }
object E extends RealParamExtractor(ParamType.E) { }
object F extends RealParamExtractor(ParamType.F) { }
object I extends RealParamExtractor(ParamType.I) { }
object J extends RealParamExtractor(ParamType.J) { }
object K extends RealParamExtractor(ParamType.K) { }
object Q extends RealParamExtractor(ParamType.Q) { }
object R extends RealParamExtractor(ParamType.R) { }

object L extends IntParamExtractor(ParamType.L) { }
object P extends IntParamExtractor(ParamType.P) { }
object S extends IntParamExtractor(ParamType.S) { }
object Pt extends IntParamExtractor(ParamType.T) { }
