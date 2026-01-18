package libgcode

import java.io.*
import dzufferey.utils.IO

class Printer(
    prefix: Option[String],
    suffix: Option[String],
    formatCmdType: Int => String,
    formatFloat: Double => String,
    writer: BufferedWriter
) {

  def apply(cmd: Command): Unit = {
    cmd.line.foreach(l => {
      writer.write('N')
      writer.write(l.toString)
      writer.write(' ')
    })
    cmd.ctype match {
      case CmdType.Empty =>
        ()
      case other =>
        writer.write(other.toString)
        writer.write(cmd.code.map(formatCmdType).mkString("."))
        if (cmd.parameters.nonEmpty) writer.write(' ')
    }
    val formatedParams = cmd.parameters.map(p =>
      p match {
        case p @ RealParam(_, _) => p.format(formatFloat)
        case other               => other.toString
      }
    )
    writer.write(formatedParams.mkString(" "))
    if (cmd.comment.nonEmpty && (cmd.ctype != CmdType.Empty || cmd.parameters.nonEmpty)) {
      writer.write(' ')
    }
    cmd.comment.foreach(c => {
      if (c contains '\n') {
        writer.write("( ")
        writer.write(c)
        writer.write(" )")
      } else {
        writer.write("; ")
        writer.write(c)
      }
    })
    writer.newLine
  }

  def header = {
    for (p <- prefix) {
      writer.write(p)
      writer.newLine
    }
  }

  def footer = {
    for (s <- suffix) {
      writer.write(s)
      writer.newLine
    }
  }

  def apply(cmds: Seq[Command]): Unit = {
    header
    cmds.foreach(apply)
    footer
  }

}

class DefaultPrinter(writer: BufferedWriter)
    extends Printer(None, None, (x: Int) => x.toString, (x: Double) => f"$x%.5f", writer)

//not fully complient but better
class RolandDGPrinter(writer: BufferedWriter)
    extends Printer(Some("%"), Some("%"), (x: Int) => f"$x%02d", (x: Double) => f"$x%.5f", writer)

object Printer {

  def apply(cmds: Seq[Command]): String = {
    val s = new StringWriter()
    val b = new BufferedWriter(s)
    apply(cmds, b)
    b.close
    s.toString
  }

  def apply(cmds: Seq[Command], fileName: String): Unit = {
    IO.writeInFile(fileName, (b: BufferedWriter) => apply(cmds, b))
  }

  def apply(cmds: Seq[Command], out: BufferedWriter): Unit = {
    val p = new DefaultPrinter(out)
    p(cmds)
  }

  def rolandDG(cmds: Seq[Command]): String = {
    val s = new StringWriter()
    val b = new BufferedWriter(s)
    rolandDG(cmds, b)
    b.close
    s.toString
  }

  def rolandDG(cmds: Seq[Command], fileName: String): Unit = {
    IO.writeInFile(fileName, (b: BufferedWriter) => rolandDG(cmds, b))
  }

  def rolandDG(cmds: Seq[Command], out: BufferedWriter): Unit = {
    val p = new RolandDGPrinter(out)
    p(cmds)
  }

}
