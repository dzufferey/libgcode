package libgcode.abstractmachine

import libgcode.{Command, Param, ParamT, CmdType, ParamType}
import libgcode.extractor.*
import scala.math.*
import Plane.*

class AbstractMachine {

  val INCH = 25.4 // mm per inch

  // All internal state is stored in millimeters (and feedrates in mm/min).
  // Command values (from the program) are in command units (mm under G21,
  // inches under G20) and must be converted with cmdToMm before touching
  // state; state is read back in command units with mmToCmd. The motion
  // primitives (linearMotion/circularMotion/findCenter) are unit-agnostic and
  // operate purely in mm.
  val maxFeed = 2500.0 // rapid feedrate, mm/min (machine specific) // for G0

  // position (mm)
  var x = 0.0
  var y = 0.0
  var z = 0.0

  // orientation (degrees, unit-independent)
  var a = 0.0
  var b = 0.0
  var c = 0.0

  var time = 0.0 // ms

  var mode = 0 // valid: 0, 1, 2, 3, 81, 82, 83

  var absoluteCoordinates = true

  var useMillimeters = true

  var feedrate = 1.0 // mm/min (internal); see setFeed for the command-unit entry point

  var plane = XY

  var selectedTool = 0

  var spindleRPM = 0.0

  var toolLengthOffset = 0.0 // use that to compute the motion

  // for drilling operation
  var initialLevelReturn           = true // G98
  var zInitial: Option[Double]     = None
  var zRetract: Option[Double]     = None
  var peckDistance: Option[Double] = None
  var dwellTime: Option[Int]       = None
  val peckStartOffset = 1.0 // machine specific, in mm: how close to the peck start the tool may dive from (hover level)

  // TODO: ?
  // cooling
  // cutter radius compensation
  // other axes position (e.g. Extruder)

  protected def isEq(a: Double, b: Double) = (a - b).abs < 1e-5

  // the only place the unit conversion and its direction live
  protected def cmdToMm(cmd: Double): Double = if (useMillimeters) cmd else cmd * INCH
  protected def mmToCmd(mm: Double): Double  = if (useMillimeters) mm else mm / INCH

  // f is a command-unit feedrate (mm/min under G21, in/min under G20);
  // feedrate is stored internally in mm/min
  protected def setFeed(f: Double) = feedrate = cmdToMm(f)

  protected def linearMotion(x: Double, y: Double, z: Double, a: Double, b: Double, c: Double, f: Double) = {
    // the distance (and therefore the time) only accounts for the X/Y/Z axes:
    // the rotary A/B/C axes are not supported yet
    assert(isEq(a, 0) && isEq(b, 0) && isEq(c, 0), s"linearMotion does not support rotary axes: a=$a, b=$b, c=$c")
    // displacement from the current position (in mm)
    val dx = if (absoluteCoordinates) x - this.x else x
    val dy = if (absoluteCoordinates) y - this.y else y
    val dz = if (absoluteCoordinates) z - this.z else z
    if (absoluteCoordinates) {
      this.x = x
      this.y = y
      this.z = z
      this.a = a
      this.b = b
      this.c = c
    } else {
      this.x += x
      this.y += y
      this.z += z
      this.a += a
      this.b += b
      this.c += c
    }
    // TODO: account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    val distance = math.sqrt(dx * dx + dy * dy + dz * dz)
    // f is the feedrate in mm/minutes, distance in mm:
    // time (minutes) = distance / f, converted to ms
    time += (distance / f) * 60000 // from mm/minutes to ms
  }

  protected def getRotationAngle(
      cx: Double,
      cy: Double, // center of rotation
      x1: Double,
      y1: Double, // first point
      x2: Double,
      y2: Double, // second point
      clockwise: Boolean,
      turns: Int
  ): Double = {
    val part1 = atan2(y1 - cy, x1 - cx)
    val part2 = atan2(y2 - cy, x2 - cx)
    val a0    = part2 - part1
    val a =
      if (clockwise && a0 <= 0) a0
      else if (clockwise && a0 > 0) a0 - 2 * Pi
      else if (!clockwise && a0 >= 0) a0
      else a0 + 2 * Pi
    val turnAngle = if (clockwise) -2 * turns * Pi else 2 * turns * Pi
    a + turnAngle
  }

  // Unit-agnostic: all lengths in mm (already converted by the caller), f in mm/min.
  protected def circularMotion(
      x: Double,
      y: Double,
      z: Double, // end position
      a: Double,
      b: Double,
      c: Double, // end orientation
      i: Double,
      j: Double,
      k: Double, // center of rotation (relative coord)
      clockwise: Boolean,
      p: Int,
      f: Double
  ) = { // number of turns and feedrate
    // equation of an helix:
    // x(t) = a * cos(t)
    // y(t) = a * sin(t)
    // z(t) = b * t
    // a is the radius
    // b is the pitch / 2π

    // get the center
    val cx = this.x + i
    val cy = this.y + j
    val cz = this.z + k

    // compute the radius, the angle, and pitch of the helix
    val radius: Double = plane match {
      case XY => hypot(i, j)
      case ZX => hypot(k, i)
      case YZ => hypot(j, k)
    }
    val radiusCheck = plane match {
      case XY => hypot(x - cx, y - cy)
      case ZX => hypot(z - cz, x - cx)
      case YZ => hypot(y - cy, z - cz)
    }
    assert(
      isEq(radius, radiusCheck),
      "start and end radius of the circle do not agree: " + radius + " and " + radiusCheck
    )

    val arc = plane match {
      case XY => getRotationAngle(cx, cy, this.x, this.y, x, y, clockwise, p)
      case ZX => getRotationAngle(cz, cx, this.z, this.x, z, x, clockwise, p)
      case YZ => getRotationAngle(cy, cz, this.y, this.z, y, z, clockwise, p)
    }
    val angle = arc.abs
    // rise (orthogonal to the working plane) over the sweep
    val rise: Double = plane match {
      case XY => z - this.z
      case ZX => y - this.y
      case YZ => x - this.x
    }

    // update the coordinate
    if (absoluteCoordinates) {
      this.x = x
      this.y = y
      this.z = z
      this.a = a
      this.b = b
      this.c = c
    } else {
      this.x += x
      this.y += y
      this.z += z
      this.a += a
      this.b += b
      this.c += c
    }
    // TODO: account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    // helix arc length: angle * hypot(radius, rise/angle) = hypot(radius*angle, rise)
    // (the second form avoids a division by angle, which is 0 for a zero-sweep arc)
    val distance = hypot(radius * angle, rise)
    // f is the feedrate in mm/minutes, distance in mm:
    // time (minutes) = distance / f, converted to ms
    time += (distance / f) * 60000 // from mm/minutes to ms
  }

  // Unit-agnostic: all lengths in mm (already converted by the caller).
  protected def findCenter(
      x1: Double,
      y1: Double, // start position (machine state, mm)
      x2: Double,
      y2: Double, // end position (mm)
      r: Double,  // radius (mm)
      clockwise: Boolean
  ): (Double, Double) = {
    assert(!isEq(x1, x2) || !isEq(y1, y2), "ill-formed command " + (if (clockwise) "G2" else "G3"))
    // middle point
    val mx = (x1 + x2) / 2
    val my = (y1 + y2) / 2
    // vector from p₁ to p₂
    val dx = x2 - x1
    val dy = y2 - y1
    // how far from m should we go ???
    val toM2 = (mx - x1) * (mx - x1) + (my - y1) * (my - y1)
    val k    = sqrt(r * r - toM2)
    assert(!k.isNaN, s"could not find center: $x1 → $x2, $y1 → $y2, radius $r")
    // normal to d (take the direction into account)
    val nx = (if (clockwise) dy else -dy) / hypot(dx, dy)
    val ny = (if (clockwise) -dx else dx) / hypot(dx, dy)
    val px = mx + nx * k
    val py = my + ny * k
    (px, py)
  }

  // helper to compute the end position of a motion: default value of the arguments.
  // Returns command units (the unit the program is written in).
  protected def getX = if (absoluteCoordinates) mmToCmd(x) else 0.0
  protected def getY = if (absoluteCoordinates) mmToCmd(y) else 0.0
  protected def getZ = if (absoluteCoordinates) mmToCmd(z) else 0.0
  protected def getA = if absoluteCoordinates then a else 0.0
  protected def getB = if absoluteCoordinates then b else 0.0
  protected def getC = if absoluteCoordinates then c else 0.0

  def handleLinear(params: Seq[Param]) = {
    // default end position in command units
    var x = getX
    var y = getY
    var z = getZ
    var a = getA
    var b = getB
    var c = getC
    params.foreach {
      case X(v)  => x = v
      case Y(v)  => y = v
      case Z(v)  => z = v
      case A(v)  => a = v
      case B(v)  => b = v
      case C(v)  => c = v
      case F(v)  => setFeed(v)
      case other => sys.error("in G0 or G1, unexpected param: " + other)
    }
    // normalize the end position to mm (the unit-agnostic boundary)
    val xm = cmdToMm(x)
    val ym = cmdToMm(y)
    val zm = cmdToMm(z)
    // feedrate and maxFeed are both internal mm/min
    val f = if (mode == 1) feedrate else maxFeed
    linearMotion(xm, ym, zm, a, b, c, f)
  }

  def handleRotate(params: Seq[Param]) = {
    val clockwise = (mode == 2)
    // end position
    var x = getX
    var y = getY
    var z = getZ
    var a = getA
    var b = getB
    var c = getC
    // circle given either by R (radius), or IJK (center relative to the start)
    var i = 0.0
    var j = 0.0
    var k = 0.0
    var r = 0.0
    // P is the number of turn
    var p = 0
    // parse parameters
    params.foreach {
      case X(v)  => x = v
      case Y(v)  => y = v
      case Z(v)  => z = v
      case A(v)  => a = v
      case B(v)  => b = v
      case C(v)  => c = v
      case I(v)  => i = v
      case J(v)  => j = v
      case K(v)  => k = v
      case R(v)  => r = v
      case F(v)  => setFeed(v)
      case P(v)  => p = v
      case other => sys.error("in G2 or G3, unexpected param: " + other)
    }
    assert(r >= 0, "ill-formed G2/3 command: radius = " + r)
    // normalize to mm (the unit-agnostic boundary for findCenter/circularMotion)
    val xm = cmdToMm(x)
    val ym = cmdToMm(y)
    val zm = cmdToMm(z)
    val im = cmdToMm(i)
    val jm = cmdToMm(j)
    val km = cmdToMm(k)
    val rm = cmdToMm(r)
    if (rm > 0) {
      plane match {
        case XY =>
          val (c1, c2) = findCenter(this.x, this.y, xm, ym, rm, clockwise)
          i = c1 - this.x
          j = c2 - this.y
        case ZX =>
          val (c1, c2) = findCenter(this.z, this.x, zm, xm, rm, clockwise)
          k = c1 - this.z
          i = c2 - this.x
        case YZ =>
          val (c1, c2) = findCenter(this.y, this.z, ym, zm, rm, clockwise)
          j = c1 - this.y
          k = c2 - this.z
      }
    }
    circularMotion(xm, ym, zm, a, b, c, im, jm, km, clockwise, p, feedrate)
  }

  def handleDrillingCycle(params: Seq[Param]) = {
    assert(plane == XY, "only drill along Z for the moment")
    var x = getX
    var y = getY
    var z = getZ
    if (zInitial.isEmpty) {
      zInitial = Some(z)
    }
    params.foreach {
      case X(v)  => x = v
      case Y(v)  => y = v
      case Z(v)  => z = v
      case R(v)  => zRetract = Some(v)
      case Q(v)  => peckDistance = Some(v)
      case F(v)  => setFeed(v)
      case P(v)  => dwellTime = Some(v)
      case other => sys.error("in G81-3 unexpected param: " + other)
    }
    // validate the cycle parameters (they persist as modal state, so a bare
    // G81 Z.. without a prior R must fail loudly rather than throw None.get)
    val retract = zRetract.getOrElse(sys.error("G81-83 requires R (retract level)"))
    val q =
      if (mode == 83) {
        val q0 = peckDistance.getOrElse(sys.error("G83 requires Q (peck distance)"))
        assert(q0 > 0, "G83 requires Q > 0, got " + q0)
        q0
      } else 0.0
    // drill direction in Z: from the retract level toward the hole depth
    assert(!isEq(retract, z), "ill-formed cycle: R and Z are equal (" + retract + ")")
    val direction = if (z < retract) -1.0 else 1.0
    // Normalize the command-unit values to mm: the cycle geometry (direction,
    // peck stepping, hover) is computed in mm, matching the unit-agnostic
    // linearMotion. peckStartOffset and maxFeed are machine constants already
    // in mm / mm/min, so they need no conversion.
    val xm  = cmdToMm(x)
    val ym  = cmdToMm(y)
    val zm  = cmdToMm(z)
    val rM  = cmdToMm(retract)
    val qM  = cmdToMm(q)
    val z0M = cmdToMm(getZ) // current Z, in mm
    // expand the drilling into a sequence of linear motions
    // 1. get to initial position: XY rapid, then Z rapid to R
    linearMotion(xm, ym, z0M, getA, getB, getC, maxFeed)
    linearMotion(xm, ym, rM, getA, getB, getC, maxFeed)
    // 2. drill / peck. The tool always retracts to R between pecks, so each
    // feed starts from R and advances one Q toward the hole (clamped at z).
    var depth = 0.0 // cumulative depth from R, in the direction of the hole (mm)
    while (!isEq(rM + direction * depth, zm)) {
      // peck target: one Q deeper, clamped at the hole depth
      depth = if (mode == 83) min(depth + qM, (zm - rM) * direction) else (zm - rM) * direction
      val target = rM + direction * depth
      // rapid to a hover level peckStart above the peck target (on the
      // retract side), so the feed move only covers the last peckStart of the
      // peck. If the hover is at or above the retract level, skip it.
      val hover = target - direction * peckStartOffset
      if ((hover - rM) * direction > 0) {
        linearMotion(xm, ym, hover, getA, getB, getC, maxFeed)
      }
      // feed from the hover (or retract) to the peck target
      linearMotion(xm, ym, target, getA, getB, getC, feedrate)
      // dwell at the last step (isEq: target and z may differ by rounding)
      if (mode == 82 && isEq(target, zm)) {
        val dwellMs = dwellTime.getOrElse(0)
        time += dwellMs
      }
      // retract
      linearMotion(xm, ym, rM, getA, getB, getC, maxFeed)
    }
  }

  def run(cmd: Command): String = {
    try {
      cmd match {
        case G(m @ (0 | 1), 0, Seq()) => mode = m
        case G(m @ (0 | 1), 0, params) =>
          mode = m
          handleLinear(params)
        case G(dir @ (2 | 3), 0, Seq()) => mode = dir
        case G(dir @ (2 | 3), 0, params) =>
          mode = dir
          handleRotate(params)
        case G(4, 0, Seq(P(ms))) => time += ms
        case G(4, 0, Seq(X(s)))  => time += 1000 * s
        case G(4, 0, Seq(S(s)))  => time += 1000 * s
        case G(17, 0, Seq())     => plane = XY
        case G(18, 0, Seq())     => plane = ZX
        case G(19, 0, Seq())     => plane = YZ
        case G(20, 0, Seq())     => useMillimeters = false
        case G(21, 0, Seq())     => useMillimeters = true
        case G(28, 0, params) =>
          if (params.isEmpty) {
            x = 0
            y = 0
            z = 0
            a = 0
            b = 0
            c = 0
          } else {
            params.foreach {
              case ParamT(ParamType.X) => x = 0
              case ParamT(ParamType.Y) => y = 0
              case ParamT(ParamType.Z) => z = 0
              case ParamT(ParamType.A) => a = 0
              case ParamT(ParamType.B) => b = 0
              case ParamT(ParamType.C) => c = 0
              case other               => sys.error("in G28, unexpected axis: " + other)
            }
          }
        case G(80, 0, Seq()) =>
          val z = if (initialLevelReturn) zInitial.get else zRetract.get
          zInitial = None
          zRetract = None
          peckDistance = None
          dwellTime = None
          run(G(0, Z(z)))
        case G(cycleKind @ (81 | 82 | 83), 0, params) =>
          mode = cycleKind
          handleDrillingCycle(params)
        case G(90, 0, Seq()) => absoluteCoordinates = true
        case G(91, 0, Seq()) => absoluteCoordinates = false
        case G(92, 0, params) =>
          assert(params.nonEmpty)
          params.foreach {
            case X(v)  => x = cmdToMm(v)
            case Y(v)  => y = cmdToMm(v)
            case Z(v)  => z = cmdToMm(v)
            case A(v)  => a = v
            case B(v)  => b = v
            case C(v)  => c = v
            case other => sys.error("in G92 unexpected axis: " + other)
          }
        case G(98, 0, Seq())             => initialLevelReturn = true
        case G(99, 0, Seq())             => initialLevelReturn = false
        case M(3, 0, Seq(S(clockwise)))  => spindleRPM = clockwise
        case M(4, 0, Seq(S(cclockwise))) => spindleRPM = -cclockwise
        case M(5, 0, Seq())              => spindleRPM = 0
        case M(114, 0, Seq()) =>
          return f"X:${mmToCmd(x)} Y:${mmToCmd(y)} Z:${mmToCmd(z)} A:${a} B:${b} C:${c}"
        case Empty(Seq(F(f))) =>
          setFeed(f)
        case Empty(Seq(T(i))) =>
          selectedTool = i
        case Empty(params) =>
          if (mode == 0 || mode == 1) handleLinear(params)
          else if (mode == 2 || mode == 3) handleRotate(params)
          else if (mode == 81 || mode == 82 || mode == 83) handleDrillingCycle(params)
          else sys.error("Command not supported or ill-formed: " + cmd)
        case M(2, 0, Seq()) =>
          () // end of program, not much to do
        // XXX more commands
        case _ =>
          sys.error("Command not supported or ill-formed: " + cmd)
      }
    } catch {
      case e: Throwable =>
        Console.err.println(s"error processing: $cmd")
        Console.err.println(s"internal state: x = $x, y = $y, z = $z")
        throw e
    }
    ""
  }

}
