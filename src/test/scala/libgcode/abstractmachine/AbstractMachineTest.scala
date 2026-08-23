package libgcode.abstractmachine

import org.scalatest.funsuite.AnyFunSuite
import libgcode.extractor._

class AbstractMachineTest extends AnyFunSuite {

  test("linear feed time 001") {
    val m = new AbstractMachine
    // set the feedrate (modal, no motion)
    m.run(Empty(F(6000)))
    // switch to feed (G1) mode (the machine starts in rapid mode)
    m.run(G(1))
    // move 10mm at 6000mm/min = 1/600 min = 100ms
    val t0 = m.time
    m.run(G(1, X(10)))
    assert((m.time - t0 - 100.0).abs < 1e-9)
    // position was updated
    assert(m.x == 10)
  }

  test("rapid (G0) uses maxFeed") {
    val m = new AbstractMachine
    val t0 = m.time
    // move 10mm rapidly at maxFeed = 2500 mm/min => 10/2500 min = 240 ms
    m.run(G(0, X(10)))
    assert((m.time - t0 - 240.0).abs < 1e-9)
  }

  test("dwell G4 adds milliseconds") {
    val m = new AbstractMachine
    val t0 = m.time
    m.run(G(4, P(1500)))
    assert((m.time - t0 - 1500).abs < 1e-9)
  }

  test("feed set on the motion command applies") {
    val m = new AbstractMachine
    val t0 = m.time
    // 5mm at 3000mm/min = 100ms
    m.run(G(1, X(5), F(3000)))
    assert((m.time - t0 - 100.0).abs < 1e-9)
  }

  test("circular feed time 001") {
    // flat circular arc of radius 5 in the XY plane (no Z move):
    // start (5,0), center (0,0) (I=-5 J=0), end (0,5).
    // The time must equal the true path length / feedrate, with no NaN.
    val m = new AbstractMachine
    m.run(Empty(F(1000))) // 1000 mm/min
    m.run(G(2))
    m.run(G(0, X(5), Y(0)))
    val t0 = m.time
    m.run(G(2, X(0), Y(5), I(-5), J(0)))
    assert(!m.time.isNaN, "time is NaN (division by zero in helix pitch)")
    // the path is an arc of radius 5; whichever way the machine sweeps
    // (short or long way around), the length is 5 * (sweep angle).
    // Compute the sweep angle from the actual start/end positions.
    val startA = math.atan2(0.0 - 0.0, 5.0 - 0.0)   // 0
    val endA   = math.atan2(5.0 - 0.0, 0.0 - 0.0)   // π/2
    // G2 is clockwise; the machine may take the short (π/2) or long (3π/2) way.
    // Accept either, but the time must match the length that was actually cut.
    val shortLen = 5 * math.Pi / 2
    val longLen  = 5 * 3 * math.Pi / 2
    val expectedShort = shortLen / 1000 * 60000
    val expectedLong  = longLen  / 1000 * 60000
    val ok = (m.time - t0 - expectedShort).abs < 1e-6 * expectedShort ||
             (m.time - t0 - expectedLong).abs  < 1e-6 * expectedLong
    assert(ok, s"time ${m.time - t0} matches neither the short arc (${expectedShort}) nor the long arc (${expectedLong})")
  }

  test("zero-sweep arc does not corrupt the time") {
    // end position == start position: the sweep angle is 0, so the old code
    // computed pitchOver2Pi = Δz / angle = 0/0 = NaN and time became NaN.
    val m = new AbstractMachine
    m.run(G(2))
    m.run(G(0, X(10), Y(0)))
    val t0 = m.time
    m.run(G(2, X(10), Y(0), I(5), J(0)))
    assert(!m.time.isNaN, s"time is NaN after a zero-sweep arc: ${m.time}")
    assert(m.time == t0, "a zero-sweep arc should not add any time")
  }

  test("rotary axes are rejected in linear motion") {
    val m = new AbstractMachine
    m.run(G(1))
    val e = intercept[AssertionError] {
      m.run(G(1, X(1), A(10)))
    }
    assert(e.getMessage.contains("rotary"))
  }

}
