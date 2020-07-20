package libgcode.extractor

import libgcode.{Command, Param, CmdType}

class CmdExtractor(cmdType: CmdType.Value) {

  def unapply(c: Command): Option[(Int,Int,Seq[Param])] = c match {
    case Command(cmdT, Seq(code1), parameters, _, _) if cmdT == cmdType => Some((code1, 0, parameters))
    case Command(cmdT, Seq(code1,code2), parameters, _, _) if cmdT == cmdType => Some((code1, code2, parameters))
    case _ => None
  }

  def apply(code: Int, args: Param*) = Command(cmdType, Seq(code), args, None, None)

  def apply(code1: Int, code2: Int, args: Param*) = Command(cmdType, Seq(code1, code2), args, None, None)

}

object G extends CmdExtractor(CmdType.G) { }

object M extends CmdExtractor(CmdType.M) { }

object O extends CmdExtractor(CmdType.O) { }

object Empty {

  def unapply(c: Command): Option[Seq[Param]] = c match {
    case Command(CmdType.Empty, Nil, parameters, _, _) => Some(parameters)
    case _ => None
  }

  def apply(args: Param*) = Command(CmdType.Empty, Nil, args, None, None)

}
