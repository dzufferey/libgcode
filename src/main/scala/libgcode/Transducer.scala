package libgcode

import java.io._
import fastparse.core.Parsed

abstract class Transducer {

  /** reinitialize the transducer internal state */
  protected def reset { }

  /** read a command and produce a (possibly empty) sequence of commands */
  protected def transform(cmd: Command): Seq[Command]

  def apply(input: BufferedReader, output: BufferedWriter) = {
    var read = 0
    var written = 0
    reset
    val printer = new Printer(output)
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
      Printer(cs)
    }
    output.flush
    (read, written)
  }

}
