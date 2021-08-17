package libgcode.generator

import libgcode._
import libgcode.extractor._
import libgcode.utils.Viewer
import libgcode.abstractmachine.Plane
import java.io._
import java.nio.file._

abstract class Program(val conf: Config) {

  def header = Seq(
    conf.workingPlane match {
      case Plane.XY => G(17)
      case Plane.ZX => G(18)
      case Plane.YZ => G(19)
    },
    Program.mm,
    Program.absolute,
    Empty(F(conf.feed)),
    G(0, conf.z(conf.safeHeight))
  )

  def body: Seq[Command] // FILL INSTRUCTIONS HERE

  def footer = Seq(
    G(0, conf.z(conf.safeHeight)),
    M(2)
  )

  def program = header ++ body ++ footer

  def save(fileName: String, printer: (Seq[Command], String) => Unit = Printer.apply) = {
    printer(program, fileName)
  }

  def display = Viewer.display(program)

  override def toString = {
    val writer = new StringWriter
    Printer(program, new BufferedWriter(writer))
    writer.toString
  }

}


object Program {

  val absolute = G(90)
  val incremental = G(91)
  val inches = G(20)
  val mm = G(21)

}
