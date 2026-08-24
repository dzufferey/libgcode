package libgcode

import org.scalatest.funsuite.AnyFunSuite
import java.io.*
import java.nio.file.*

class TransducerTest extends AnyFunSuite {

  val path = "src/test/resources/"

  val files = List(
    ("sample001.gcode", 1),
    ("sample002.gcode", 1),
    ("sample003.gcode", 17),
    ("sample004.gcode", 17),
    ("sample005.gcode", 3),
    ("sample006.gcode", 2),
    ("slic3r01.gco", 215668),
    ("gcodetools01.ngc", 29)
  )

  // a transducer that does nothing
  class ID extends Transducer {
    protected def transform(c: Command) = List(c)
  }

  test("ID") {
    val id = new ID
    files.foreach { case (fName, nbrLines) =>
      val file            = Paths.get(path, fName)
      val reader          = Files.newBufferedReader(file)
      val writer          = new BufferedWriter(new StringWriter)
      val (read, written) = id.transduce(reader, writer)
      assert(nbrLines == read)
      assert(nbrLines == written)
    }
  }

  // Regression: the input must be read line-by-line to EOF regardless of
  // whether the last line carries a trailing newline, and must not trip on the
  // ready()/readLine()==null EOF boundary. (In-memory reader, no resource file.)
  test("ID handles a final line without a trailing newline") {
    def run(body: String) = {
      val id     = new ID
      val reader = new BufferedReader(new StringReader(body))
      val writer = new BufferedWriter(new StringWriter)
      id.transduce(reader, writer)
    }
    assert(run("G0 X1\nG1 Y2") == (2, 2))        // no trailing newline
    assert(run("G0 X1\nG1 Y2\n") == (2, 2))      // trailing newline
    assert(run("G0 X1") == (1, 1))               // single line, no newline
    assert(run("G0 X1\n\nG1 Y2\n") == (3, 3))    // blank middle line counted
    assert(run("") == (0, 0))                    // empty input
  }

}
