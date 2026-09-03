package examples

import java.nio.file.*

object Examples {

  /** path to `out/name`, creating the directory if needed */
  def out(name: String): String = {
    Files.createDirectories(Path.of("out"))
    s"out/$name"
  }
}
