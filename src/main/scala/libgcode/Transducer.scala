package libgcode

import java.io._
import fastparse._
import SingleLineWhitespace._

trait Transducer {

  /** (re)initialize the transducer internal state */
  protected def init: Seq[Command] = Seq()

  /** clean-up */
  protected def finish: Seq[Command] = Seq()

  /** read a command and produce a (possibly empty) sequence of commands */
  protected def transform(cmd: Command): Seq[Command]
  
  def transduce(cmds: Seq[Command]): Seq[Command] = {
    init ++ cmds.flatMap(transform) ++ finish
  }

  def transduce(input: BufferedReader, output: BufferedWriter) = {
    var read = 0
    var written = 0
    val printer = new Printer(output)
    val header = init
    written += header.length
    printer(header)
    while(input.ready()) {
      val line = input.readLine.trim
      read += 1
      var cmd: Command = null
      if (line == "") {
        cmd = Command(CmdType.Empty, Nil, Nil, None, None)
      } else {
        parse(line, Parser.cmd(_)) match {
          case Parsed.Success(c, _) =>
            cmd = c
          case f @ Parsed.Failure(parser, _, _) =>
            sys.error("parsing failure: " + parser.toString + " with " + f.trace().longMsg)
        }
      }
      val cs = transform(cmd)
      written += cs.length
      printer(cs)
    }
    val footer = finish
    written += footer.length
    printer(footer)
    output.flush
    (read, written)
  }

}
