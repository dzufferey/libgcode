package libgcode

import java.io._
import fastparse.core.Parsed

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
        Parser.cmdNoEOL.parse(line) match {
          case Parsed.Success(c, _) =>
            cmd = c
          case Parsed.Failure(parser, _, extra) =>
            sys.error("parsing failure: " + parser.toString + " with " + extra.traced.trace)
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
