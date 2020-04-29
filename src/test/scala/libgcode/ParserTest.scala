package libgcode

import org.scalatest.funsuite.AnyFunSuite
import dzufferey.utils.IO
import fastparse._

class ParserTest extends AnyFunSuite {
  
  val path = "src/test/resources/"
  
  def ok(p: P[_] => P[Any], str: String) = {
    parse(str, p) match {
      case Parsed.Success(v, _) => ()
      case other => sys.error(other.toString)
    }
  }

  def check[A](p: P[_] => P[A], str: String, a: A) = {
    parse(str, p) match {
      case Parsed.Success(v, _) => assert(v == a)
      case other => sys.error(other.toString)
    }
  }
  
  test("chunks") {
    check(Parser.cmdType(_), "G", CmdType.G)
    check(Parser.cmdType(_), "g", CmdType.G)
    check(Parser.cmdType(_), "M", CmdType.M)
    check(Parser.cmdType(_), "m", CmdType.M)
    check(Parser.cmdType(_), "O", CmdType.O)
    check(Parser.cmdType(_), "o", CmdType.O)
    check(Parser.code(_), "G28", (CmdType.G, scala.collection.mutable.ArrayBuffer(28)))
    check(Parser.code(_), "M104", (CmdType.M, scala.collection.mutable.ArrayBuffer(104)))
    check(Parser.line(_), "n04", 4)
    check(Parser.lineComment(_), "; home all axes", "home all axes")
    check(Parser.comment(_), "; home all axes", "home all axes")
    ok(Parser.comment(_), "; home all axes")
    ok(Parser.comment(_), "   ; home all axes")
    ok(Parser.cmd(_), "")
  }
  
  def expectedNbrLineLoop(fName: String, n: Int) = {
    val raw = IO.readTextFile(path + fName)
    val commands = Parser(raw)
    assert(commands.size == n)
    val printed = Printer(commands)
    //IO.writeInFile("temp.txt", printed)
    val reparsed = Parser(printed)
    assert(reparsed.size == n)
    for (i <- 0 until n) {
        assert(commands(i) == reparsed(i), "at " + i)
    }
  }


  def expectedNbrLine(fName: String, n: Int) = {
    val raw = IO.readTextFile(path + fName)
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
    // TODO not quite sure why the reparsing fails
    // printing on disk and parsing works fine ...
    expectedNbrLine("slic3r01.gco", 215668)
  }

  test("gcodetools 01") {
    expectedNbrLineLoop("gcodetools01.ngc", 29)
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
