package libgcode

import fastparse._
import SingleLineWhitespace._
import CmdType.CmdType
import ParamType.ParamType

object Parser {

  private def eol[_: P] = P( "\n\r" | "\n" | "\r" )

  private def integral[_: P] = P( CharIn("0-9").rep(1) )
  private def fractional[_: P] = P( "." ~ integral )

  def integer[_: P]: P[Int] = P( "-".? ~ integral ).!.map(_.toInt)
  def real[_: P]: P[Double] = P( "-".? ~ integral ~ fractional.? ).!.map(_.toDouble)

  private def isParam(c: Char) = {
    try { ParamType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  private def isCommand(c: Char) = {
    try { CmdType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  def paramType[_: P]: P[ParamType] = P( CharPred( isParam ).!.map(c => ParamType.withName(c.toUpperCase)) )

  def realParam[_: P]: P[RealParam] = P( paramType.filter(RealParam.is) ~ real ).map{ case (a, b) => RealParam(a, b) }
  def intParam[_: P]: P[IntParam] = P( paramType.filter(IntParam.is) ~ integer ).map{ case (a, b) => IntParam(a, b) }
  def paramT[_: P]: P[ParamT] = P( paramType ).map( a => ParamT(a) )
  def param[_: P]: P[Param] = P( realParam | intParam | paramT )

  def cmdType[_: P]: P[CmdType] = P( CharPred( isCommand ).!.map(c => CmdType.withName(c.toUpperCase)) )

  def line[_: P]: P[Int] = P( ("N" | "n") ~ integer )

  def lineComment[_: P]: P[String] = P( Pass ~ ";" ~/ CharsWhile(c => c != '\n' && c != '\r').?.! )
  def blockComment[_: P]: P[String] = P( "(" ~/ CharsWhile(_ != ')').! ~ ")" )
  def comment[_: P]: P[String] = P( lineComment | blockComment )

  def code[_: P]: P[(CmdType,Seq[Int])] = P( cmdType ~ integer.rep(min=1,sep=".") ) 

  def cmdNonEmpty[_: P]: P[Command] = P( (line ).? ~
                                   code ~/
                                   (param.rep()) ~/
                                   (comment.?) ).map{ case (l, (c, i), ps, cmt) => Command(c, i, ps, l, cmt) }
  def cmdEmpty[_: P]: P[Command] = P( comment ).map( c => Command(CmdType.Empty, Nil, Nil, None, Some(c)) )
  def emptyLine[_: P]: P[Command] = Pass.map(_ => Command(CmdType.Empty, Nil, Nil, None, None))
  def cmdNoEOL[_: P]: P[Command] = P( cmdNonEmpty | cmdEmpty )
  def cmd[_: P]: P[Command] = P( ((cmdNonEmpty | cmdEmpty) ~/ eol) | (emptyLine ~ eol) )
  def cmds[_: P]: P[Seq[Command]] = P( cmd.rep ~ End )

  def apply(str: String): Seq[Command] = parse(str, cmds(_)) match {
    case Parsed.Success(cs, _) => cs
    case f @ Parsed.Failure(parser, _, _) =>
      sys.error("parsing failure: " + parser.toString + " with " + f.trace().longMsg)
  }

  //TODO parse from reader

}
