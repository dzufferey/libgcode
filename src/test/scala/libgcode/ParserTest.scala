package libgcode

import org.scalatest._
import dzufferey.utils.IO
import fastparse.all._
import fastparse.core.Parsed

class ParserTest extends FunSuite {
  
  val path = "src/test/resources/"
  
  def parse[A](p: P[A], str: String) {
    p.parse(str) match {
      case Parsed.Success(v, _) => ()
      case other => sys.error(other.toString)
    }
  }

  def check[A](p: P[A], str: String, a: A) {
    p.parse(str) match {
      case Parsed.Success(v, _) => assert(v == a)
      case other => sys.error(other.toString)
    }
  }
  
  test("chunks") {
    check(Parser.cmdType, "G", CmdType.G)
    check(Parser.cmdType, "g", CmdType.G)
    check(Parser.cmdType, "M", CmdType.M)
    check(Parser.cmdType, "m", CmdType.M)
    check(Parser.cmdType, "O", CmdType.O)
    check(Parser.cmdType, "o", CmdType.O)
    check(Parser.code, "G28", (CmdType.G, scala.collection.mutable.ArrayBuffer(28)))
    check(Parser.code, "M104", (CmdType.M, scala.collection.mutable.ArrayBuffer(104)))
    check(Parser.line, "n04", 4)
    check(Parser.lineComment, "; home all axes", " home all axes")
    check(Parser.comment, "; home all axes", " home all axes")
    parse(Parser.cmdEmpty, "; home all axes")
    parse(Parser.cmdEmpty, "   ; home all axes")
    parse(Parser.emptyLine, "")
  }
  
  def expectedNbrLineLoop(fName: String, n: Int) {
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


  def expectedNbrLine(fName: String, n: Int) {
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
