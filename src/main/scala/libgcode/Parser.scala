package libgcode

import fastparse._
import SingleLineWhitespace._
import CmdType.CmdType
import ParamType.ParamType

object Parser {

  private def eol[$: P] = P( "\n\r" | "\n" | "\r" )

  private def integral[$: P] = P( CharIn("0-9").rep(1) )
  private def fractional[$: P] = P( "." ~ integral )

  def integer[$: P]: P[Int] = P( "-".? ~ integral ).!.map(_.toInt)
  def real[$: P]: P[Double] = P( "-".? ~ integral ~ fractional.? ).!.map(_.toDouble)

  private def isParam(c: Char) = {
    try { ParamType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  private def isCommand(c: Char) = {
    try { CmdType.withName(c.toUpper.toString); true
    } catch { case _: NoSuchElementException => false }
  }

  def paramType[$: P]: P[ParamType] = P( CharPred( isParam ).!.map(c => ParamType.withName(c.toUpperCase)) )

  def realParam[$: P]: P[RealParam] = P( paramType.filter(RealParam.is) ~ real ).map{ case (a, b) => RealParam(a, b) }
  def intParam[$: P]: P[IntParam] = P( paramType.filter(IntParam.is) ~ integer ).map{ case (a, b) => IntParam(a, b) }
  def paramT[$: P]: P[ParamT] = P( paramType ).map( a => ParamT(a) )
  def param[$: P]: P[Param] = P( realParam | intParam | paramT )

  def cmdType[$: P]: P[CmdType] = P( CharPred( isCommand ).!.map(c => CmdType.withName(c.toUpperCase)) )

  def line[$: P]: P[Int] = P( ("N" | "n") ~ integer )

  def lineComment[$: P]: P[String] = P( Pass ~ ";" ~/ CharsWhile(c => c != '\n' && c != '\r').?.! )
  def blockComment[$: P]: P[String] = P( "(" ~/ CharsWhile(_ != ')').! ~ ")" )
  def comment[$: P]: P[String] = P( lineComment | blockComment )

  def code[$: P]: P[(CmdType,Seq[Int])] = P( cmdType ~ integer.rep(min=1,sep=".") ) 

  def cmd[$: P]: P[Command] = P( line.? ~
                                 code.? ~/
                                 (param.rep()) ~/
                                 (comment.?)).map{
                                   case (l, Some((c, i)), ps, cmt) => Command(c, i, ps, l, cmt)
                                   case (l, None, ps, cmt) => Command(CmdType.Empty, Nil, ps, l, cmt)
                                 }
  def cmds[$: P]: P[Seq[Command]] = P( cmd.rep(sep=eol).map(_.dropRight(1)) ~ End )

  def apply(str: String): Seq[Command] = parse(str, cmds(_)) match {
    case Parsed.Success(cs, _) => cs
    case f @ Parsed.Failure(parser, _, _) =>
      sys.error("parsing failure: " + parser.toString + " with " + f.trace().longMsg)
  }

  //TODO parse from reader

}
