package libgcode.abstractmachine

import org.scalatest.funsuite.AnyFunSuite
import libgcode.extractor.*

class DrillingCycleTest extends AnyFunSuite {

  private def isEq(a: Double, b: Double) = (a - b).abs < 1e-5

  // G82 in mm: R=0, Z=-2, F=1000, P=5, peckStartOffset=1mm.
  // hover at -1 (1mm above target), feed -1→-2 (1mm), dwell 5ms, retract -2→0 (2mm).
  test("G82 (mm): dive, dwell, and retract to the R level") {
    val m = new AbstractMachine
    m.run(Empty(F(1000))) // feedrate 1000 mm/min
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    m.run(G(82, Z(-2.0), R(0.0), P(5)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // rapid 0→-1 (1mm) + feed -1→-2 (1mm) + dwell 5ms + rapid -2→0 (2mm)
    val expected = (1.0 + 2.0) / 2500.0 * 60000 + 1.0 / 1000.0 * 60000 + 5.0
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  // The same physical cycle in inch mode: distances and feedrates are the
  // same physical values (2 mm, 1000 mm/min), so the time must equal the mm
  // case. Regression for unit mixing: the rapid feedrate (maxFeed) and the
  // hover offset (peckStartOffset) are machine constants in mm and must not
  // be interpreted in command units.
  test("G82 (inches): same physical motion as the mm case") {
    val m = new AbstractMachine
    m.run(G(20)) // inch mode
    // 1000 mm/min = (1000/25.4) in/min; setFeed multiplies by 25.4 to get mm/min
    m.run(Empty(F(1000.0 / 25.4)))
    m.run(G(0, X(10.0 / 25.4), Y(0)))
    val t0 = m.time
    // 2 mm = (2/25.4) inches
    m.run(G(82, Z(-2.0 / 25.4), R(0.0), P(5)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // same physical motion as the mm case: rapid 1mm + feed 1mm + dwell 5ms + rapid 2mm
    val expected = (1.0 + 2.0) / 2500.0 * 60000 + 1.0 / 1000.0 * 60000 + 5.0
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  // G83 peck drilling in mm: R=0, Z=-3, Q=1, peckStartOffset=1mm.
  // Peck 1: target=-1, hover=0 (skip), feed 0→-1, retract -1→0.
  // Peck 2: target=-2, hover=-1, rapid 0→-1, feed -1→-2, retract -2→0.
  // Peck 3: target=-3, hover=-2, rapid 0→-2, feed -2→-3, retract -3→0.
  test("G83 (mm): pecks advance one Q at a time, hovering 1mm above each peck") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    m.run(G(83, Z(-3.0), R(0.0), Q(1.0)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // hovers (rapid): 1 + 2 = 3mm; retractions (rapid): 1 + 2 + 3 = 6mm
    // dives (feed): 3 × 1mm
    val expected = (3.0 + 6.0) / 2500.0 * 60000 + 3.0 / 1000.0 * 60000
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  // Q not dividing the depth: the last peck must be clamped at Z, and the
  // loop must terminate (regression for lastZ > z with rounding).
  test("G83 (mm): last peck is clamped at Z when Q does not divide the depth") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    m.run(G(83, Z(-2.5), R(0.0), Q(1.0)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // peck 1: target=-1, hover=0 (skip), feed 1mm, retract 1mm
    // peck 2: target=-2, hover=-1, rapid 1mm, feed 1mm, retract 2mm
    // peck 3: target=-2.5 (clamped), hover=-1.5, rapid 1.5mm, feed 1mm, retract 2.5mm
    val expected = (1.0 + 1.5 + 1.0 + 2.0 + 2.5) / 2500.0 * 60000 + 3.0 / 1000.0 * 60000
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  // A depth that is a repeating binary fraction: with Q = 0.1 the running sum
  // 0.1 + 0.1 + ... never lands exactly on -0.3, so the old `lastZ == z`
  // check skipped the dwell and `lastZ > z` relied on max() to terminate.
  test("G83 (mm): terminates and dwells with repeating-fraction Q (0.1)") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    // G82 with the same depth and a dwell: lastZ is assigned from z, so the
    // dwell must fire even though -0.3 is a repeating binary fraction
    m.run(G(82, Z(-0.3), R(0.0), P(7)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // hover=0.7 (above retract, skip), feed 0→-0.3 (0.3mm), dwell 7ms, retract 0.3mm
    val expected =
      0.3 / 1000.0 * 60000 + 7.0 + 0.3 / 2500.0 * 60000
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  test("G83 (mm): terminates with repeating-fraction Q (0.1) not dividing the depth") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    // 0.1 + 0.1 + 0.1 = 0.30000000000000004 != 0.3, so the last peck must be
    // clamped at z and the loop must terminate via isEq, not ==
    m.run(G(83, Z(-0.3), R(0.0), Q(0.1)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // all hovers are above retract (0.9, 0.8, 0.7), so no rapid hovers
    // each feed starts from R: 0.1 + 0.2 + 0.3 = 0.6mm feed; 0.6mm retract
    val expected =
      0.6 / 2500.0 * 60000 + 0.6 / 1000.0 * 60000
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  // Sign handling: retract level below the hole (positive Z is the hole).
  // The old `while (lastZ > z)` never entered the loop; the cycle now works
  // in either sign convention.
  test("G82: works when the hole is above the retract level (positive depth)") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(82, Z(2.0), R(0.0), P(5)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // rapid 0→1 (1mm) + feed 1→2 (1mm) + dwell 5ms + rapid 2→0 (2mm)
    val expected = (1.0 + 2.0) / 2500.0 * 60000 + 1.0 / 1000.0 * 60000 + 5.0
    assert(isEq(m.time, expected), s"time = ${m.time}, expected $expected")
  }

  test("G83: pecks work when the hole is above the retract level") {
    val m = new AbstractMachine
    m.run(Empty(F(1000)))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    m.run(G(83, Z(3.0), R(0.0), Q(1.0)))
    assert(isEq(m.z, 0.0), s"must end retracted at R: z = ${m.z}")
    // mirror of the negative-depth G83 case:
    // peck 1: target=1, hover=0 (skip), feed 1mm, retract 1mm
    // peck 2: target=2, hover=1, rapid 1mm, feed 1mm, retract 2mm
    // peck 3: target=3, hover=2, rapid 2mm, feed 1mm, retract 3mm
    val expected = (3.0 + 6.0) / 2500.0 * 60000 + 3.0 / 1000.0 * 60000
    assert(isEq(m.time - t0, expected), s"time = ${m.time - t0}, expected $expected")
  }

  test("G82 without R fails with a clear error (no None.get)") {
    val m = new AbstractMachine
    val e = intercept[Exception] {
      m.run(G(82, Z(-2.0)))
    }
    assert(e.getMessage.contains("requires R"), s"unexpected message: ${e.getMessage}")
  }

  test("G83 without Q fails with a clear error (no None.get)") {
    val m = new AbstractMachine
    m.run(G(82, R(0.0), Z(-1.0))) // set modal R
    val e = intercept[Exception] {
      m.run(G(83, Z(-2.0)))
    }
    assert(e.getMessage.contains("requires Q"), s"unexpected message: ${e.getMessage}")
  }

  test("G83 with Q <= 0 is rejected") {
    val m = new AbstractMachine
    val e = intercept[AssertionError] {
      m.run(G(83, Z(-2.0), R(0.0), Q(0.0)))
    }
    assert(e.getMessage.contains("Q > 0"), s"unexpected message: ${e.getMessage}")
  }

  test("cycle with R == Z is rejected") {
    val m = new AbstractMachine
    val e = intercept[AssertionError] {
      m.run(G(82, Z(-2.0), R(-2.0)))
    }
    assert(e.getMessage.contains("R and Z are equal"), s"unexpected message: ${e.getMessage}")
  }

}
