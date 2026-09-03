package libgcode

import java.io.*
import fastparse.*
import SingleLineWhitespace.*

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

  def transduce(
      input: BufferedReader,
      output: BufferedWriter,
      mkPrinter: BufferedWriter => Printer = b => new DefaultPrinter(b)
  ) = {
    var read    = 0
    var written = 0
    val printer = mkPrinter(output)
    val header  = init
    written += header.length
    printer.header
    printer(header)
    // readLine() is the documented way to detect EOF (it returns null); the
    // older while (input.ready()) { readLine() } pattern could observe ready()
    // true while readLine() yielded null (an unguarded null .trim). Blank
    // lines need no special-casing: Parser.cmd already parses them to an
    // Empty command.
    var line: String = input.readLine()
    while (line != null) {
      read += 1
      parse(line.trim, (c: ParsingRun[?]) => Parser.cmd(using c)) match {
        case Parsed.Success(c, _) =>
          val cs = transform(c)
          written += cs.length
          printer(cs)
        case f @ Parsed.Failure(parser, _, _) =>
          sys.error("parsing failure: " + parser.toString + " with " + f.trace().longMsg)
      }
      line = input.readLine()
    }
    val footer = finish
    written += footer.length
    printer(footer)
    printer.footer
    output.flush
    (read, written)
  }

}
