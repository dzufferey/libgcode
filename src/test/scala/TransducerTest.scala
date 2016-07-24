package libgcode

import org.scalatest._
import java.io._
import java.nio.file._

class TransducerTest extends FunSuite {
  
  val path = "src/test/resources/"

  val files = List(
    ("sample001.gcode", 1),
    ("sample002.gcode", 1),
    ("sample003.gcode", 17),
    ("sample004.gcode", 17),
    ("sample005.gcode", 3),
    ("slic3r01.gco", 215668),
    ("gcodetools01.ngc", 29)
  )

  // a transducer that does nothing
  class ID extends Transducer {
    protected def transform(c: Command) = List(c)
  }

  
  test("ID") {
    val id = new ID
    files.foreach{ case (fName, nbrLines) =>
      val file = Paths.get(path, fName)
      val reader = Files.newBufferedReader(file)
      val writer = new BufferedWriter(new StringWriter)
      val (read, written) = id(reader, writer)
      assert(nbrLines == read)
      assert(nbrLines == written)
    }
  }

}
