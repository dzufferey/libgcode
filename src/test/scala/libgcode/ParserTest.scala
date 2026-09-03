package libgcode

import org.scalatest.funsuite.AnyFunSuite
import java.nio.file.*
import fastparse.*

class ParserTest extends AnyFunSuite {

  val path = "src/test/resources/"

  def ok(p: P[?] => P[Any], str: String) = {
    parse(str, p) match {
      case Parsed.Success(v, _) => ()
      case other                => sys.error(other.toString)
    }
  }

  def check[A](p: P[?] => P[A], str: String, a: A) = {
    parse(str, p) match {
      case Parsed.Success(v, _) => assert(v == a)
      case other                => sys.error(other.toString)
    }
  }

  test("chunks") {
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "G", CmdType.G)
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "g", CmdType.G)
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "M", CmdType.M)
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "m", CmdType.M)
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "O", CmdType.O)
    check((c: ParsingRun[?]) => Parser.cmdType(using c), "o", CmdType.O)
    check((c: ParsingRun[?]) => Parser.code(using c), "G28", (CmdType.G, scala.collection.mutable.ArrayBuffer(28)))
    check((c: ParsingRun[?]) => Parser.code(using c), "M104", (CmdType.M, scala.collection.mutable.ArrayBuffer(104)))
    check((c: ParsingRun[?]) => Parser.line(using c), "n04", 4)
    check((c: ParsingRun[?]) => Parser.lineComment(using c), "; home all axes", "home all axes")
    check((c: ParsingRun[?]) => Parser.comment(using c), "; home all axes", "home all axes")
    ok((c: ParsingRun[?]) => Parser.comment(using c), "; home all axes")
    ok((c: ParsingRun[?]) => Parser.comment(using c), "   ; home all axes")
    ok((c: ParsingRun[?]) => Parser.cmd(using c), "")
  }

  test("eol variants") {
    // same commands parsed from LF, CRLF and CR line endings
    val lf   = Parser("G21\nG90\nG1 X10\n")
    val crlf = Parser("G21\r\nG90\r\nG1 X10\r\n")
    val cr   = Parser("G21\rG90\rG1 X10\r")
    assert(lf == crlf)
    assert(lf == cr)
  }

  def expectedNbrLineLoop(fName: String, n: Int, compareReparsed: Boolean = true) = {
    val raw      = Files.readString(Path.of(path + fName))
    val commands = Parser(raw)
    assert(commands.size == n)
    val printed = Printer(commands)
    // Files.writeString(Path.of("temp.txt"), printed)
    val reparsed = Parser(printed)
    assert(reparsed.size == n)
    if (compareReparsed) {
      for (i <- 0 until n) {
        assert(commands(i) == reparsed(i), "at " + i)
      }
    }
  }

  def expectedNbrLine(fName: String, n: Int) = {
    val raw      = Files.readString(Path.of(path + fName))
    val commands = Parser(raw)
    assert(commands.size == n)
  }

  test("sample 001") {
    expectedNbrLineLoop("sample001.gcode", 1)
  }

  test("sample 002") {
    expectedNbrLineLoop("sample002.gcode", 1)
  }

  test("sample 003") {
    expectedNbrLineLoop("sample003.gcode", 17)
  }

  test("sample 004") {
    expectedNbrLineLoop("sample004.gcode", 17)
  }

  test("sample 005") {
    expectedNbrLineLoop("sample005.gcode", 3)
  }

  test("sample 006") {
    expectedNbrLineLoop("sample006.gcode", 2)
  }

  test("sample 007") {
    expectedNbrLineLoop("sample007.gcode", 94)
  }

  test("sample 008") {
    expectedNbrLineLoop("sample008.gcode", 1)
  }

  test("slic3r 01") {
    // TODO: not quite sure why the reparsing fails
    // printing on disk and parsing works fine ...
    expectedNbrLine("slic3r01.gco", 215668)
  }

  test("gcodetools 01") {
    expectedNbrLineLoop("gcodetools01.ngc", 29, false)
  }

  test("cura 01") {
    expectedNbrLineLoop("cura01.gco", 2488)
  }

  test("cura 02") {
    expectedNbrLineLoop("cura02.gco", 2491)
  }

  test("kisslicer 01") {
    expectedNbrLineLoop("kisslicer01.gcode", 3228)
  }

}
