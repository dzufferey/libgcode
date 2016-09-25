package libgcode.extractor

import libgcode.{Command, Param, CmdType}

class CmdExtractor(cmdType: CmdType.Value) {

  def unapply(c: Command): Option[(Int,Int,Seq[Param])] = c match {
    case Command(cmdT, Seq(code1), parameters, _, _) if cmdT == cmdType => Some((code1, 0, parameters))
    case Command(cmdT, Seq(code1,code2), parameters, _, _) if cmdT == cmdType => Some((code1, code2, parameters))
    case _ => None
  }
}

object G extends CmdExtractor(CmdType.G) { }

object M extends CmdExtractor(CmdType.M) { }

object O extends CmdExtractor(CmdType.O) { }

object T extends CmdExtractor(CmdType.T) { }
