package libgcode

import org.scalatest.funsuite.AnyFunSuite
import libgcode.extractor.*

class PrinterTest extends AnyFunSuite {

  test("default printer 001") {
    val program = Seq(
      G(0, X(0), Y(0), Z(0)),
      Empty(T(2)),
      M(6),
      M(2)
    )
    val expected = """G0 X0.00000 Y0.00000 Z0.00000
                     |T2
                     |M6
                     |M2
                     |""".stripMargin
    assert(Printer(program) == expected)
  }

  test("rolangDG printer 001") {
    val program = Seq(
      G(0, X(0), Y(0), Z(0)),
      M(6, T(2)),
      M(2)
    )
    val expected = """%
                     |G00 X0.00000 Y0.00000 Z0.00000
                     |M06 T2
                     |M02
                     |%
                     |""".stripMargin
    assert(Printer.rolandDG(program) == expected)
  }

  test("replaceComment 001") {
    val cmd = Command(CmdType.G, Seq(1), Seq(X(1.0)), None, Some("old"))
    //replace with a different comment
    val cmd2 = cmd.replaceComment(Some("new"))
    assert(cmd2.comment == Some("new"))
    assert(cmd2.parameters == Seq(X(1.0)))
    //replacing by the same comment returns the same command
    assert(cmd2.replaceComment(Some("new")) eq cmd2)
    //remove the comment
    val cmd3 = cmd.replaceComment(None)
    assert(cmd3.comment.isEmpty)
  }
}
