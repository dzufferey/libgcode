package libgcode

import fastparse.all._
import fastparse.core.Parsed
import CmdType.CmdType
import ParamType.ParamType

object Parser {

  private val eol = P( "\n\r" | "\n" | "\r" )
  private val space = P( CharsWhile(" \t".contains(_: Char)).? )

  private val integral = P( CharIn('0' to '9').rep(1) )
  private val fractional = P( "." ~ integral )

  val integer: P[Int] = P( "-".? ~ integral ).!.map(_.toInt)
  val real: P[Double] = P( "-".? ~ integral ~ fractional.? ).!.map(_.toDouble)

  private def isParam(c: Char) = {
    try { ParamType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  private def isCommand(c: Char) = {
    try { CmdType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  val paramType: P[ParamType] = P( CharPred( isParam ).!.map(c => ParamType.withName(c.toUpperCase)) )

  val realParam: P[RealParam] = P( paramType.filter(RealParam.is) ~ real ).map{ case (a, b) => RealParam(a, b) }
  val intParam: P[IntParam] = P( paramType.filter(IntParam.is) ~ integer ).map{ case (a, b) => IntParam(a, b) }
  val paramT: P[ParamT] = P( paramType ).map( a => ParamT(a) )
  val param: P[Param] = P( realParam | intParam | paramT )

  val cmdType: P[CmdType] = P( CharPred( isCommand ).!.map(c => CmdType.withName(c.toUpperCase)) )

  val line: P[Int] = P( ("N" | "n") ~ integer )

  val lineComment: P[String] = P( ";" ~/ CharsWhile(c => c != '\n' && c != '\r').?.! )
  val blockComment: P[String] = P( "(" ~/ CharsWhile(_ != ')').! ~ ")" )
  val comment: P[String] = P( lineComment | blockComment )

  val code: P[(CmdType,Seq[Int])] = P( cmdType ~ integer.rep(min=1,sep=".") ) 

  val cmdNonEmpty: P[Command] = P( (line ~ space).? ~
                                   code ~/
                                   (space ~ param.rep(sep=space)) ~/
                                   (space ~ comment.?) ).map{ case (l, (c, i), ps, cmt) => Command(c, i, ps, l, cmt) }
  val cmdEmpty: P[Command] = P( space ~ comment ).map( c => Command(CmdType.Empty, Nil, Nil, None, Some(c)) )
  val emptyLine: P[Command] = Pass.map(_ => Command(CmdType.Empty, Nil, Nil, None, None))
  val cmdNoEOL: P[Command] = P( cmdNonEmpty | cmdEmpty )
  val cmd: P[Command] = P( ((cmdNonEmpty | cmdEmpty) ~/ eol) | (emptyLine ~ eol) )
  val cmds: P[Seq[Command]] = P( cmd.rep ~ End )

  def apply(str: String): Seq[Command] = cmds.parse(str) match {
    case Parsed.Success(cs, _) => cs
    case Parsed.Failure(parser, _, extra) =>
      sys.error("parsing failure: " + parser.toString + " with " + extra.traced.trace)
  }

  //TODO parse from reader

}
