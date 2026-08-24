package libgcode

import org.scalatest.funsuite.AnyFunSuite
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class RealParamFormatTest extends AnyFunSuite {

  test("format prints integers without a trailing .0") {
    assert(RealParam.format(1.0) == "1")
    assert(RealParam.format(0.0) == "0")
    assert(RealParam.format(-42.0) == "-42")
  }

  test("format does not emit double binary noise (cap of 10 fraction digits)") {
    // 0.3 is not exactly representable; the old 340-digit formatter printed
    // the full binary expansion. Now it must be clean and short.
    val s = RealParam.format(0.3)
    assert(!s.contains("000000000000"), s"unexpected binary noise in $s")
    assert(s.length <= 12, s"format(0.3) too long: $s")
  }

  test("format is deterministic and correct across a range of values") {
    assert(RealParam.format(1.5) == "1.5")
    assert(RealParam.format(0.1) == "0.1")
    assert(RealParam.format(-2.5) == "-2.5")
    assert(RealParam.format(123456.789) == "123456.789")
  }

  test("format is thread-safe (no corruption under concurrent formatting)") {
    // RealParam.df is now a ThreadLocal; hammer it from many threads. Each
    // value has a deterministic correct output, so any mismatch indicates the
    // shared-mutable-formatter bug returning.
    val pairs = Seq(
      0.3 -> "0.3", 1.5 -> "1.5", -2.5 -> "-2.5", 0.1 -> "0.1",
      123456.789 -> "123456.789", 0.0001 -> "0.0001",
      -9999.999 -> "-9999.999", 3.14159 -> "3.14159", 1.0 -> "1", 0.0 -> "0"
    )
    val nThreads = 16
    val iters    = 50000
    val futures  = (0 until nThreads).map { t =>
      Future {
        var bad = 0
        var i   = 0
        while (i < iters) {
          val (v, exp) = pairs((i + t) % pairs.length)
          if (RealParam.format(v) != exp) bad += 1
          i += 1
        }
        bad
      }
    }
    val totalBad = Await.result(Future.sequence(futures), 30.seconds).sum
    assert(totalBad == 0, s"$totalBad corrupted results under concurrent formatting")
  }

}
