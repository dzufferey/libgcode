package libgcode.abstractmachine

import libgcode.{Command, Param, ParamT, CmdType, ParamType}
import libgcode.extractor._
import scala.math._
import Plane._

class AbstractMachine {

  val maxFeed = 2500.0 // for G0

  // position
  var x = 0.0
  var y = 0.0
  var z = 0.0

  // orientation
  var a = 0.0
  var b = 0.0
  var c = 0.0

  var time = 0.0

  var mode = 0 //valid: 0, 1, 2, 3, 81, 82, 83

  var absoluteCoordinates = true

  var useMillimeters = true

  var feedrate = 1.0

  var plane = XY

  var selectedTool = 0

  var spindleRPM = 0.0

  var toolLengthOffset = 0.0 // use that to compute the motion

  //for drilling operation
  var initialLevelReturn = true // G98
  var zInitial: Option[Double] = None
  var zRetract: Option[Double] = None
  var peckDistance: Option[Double] = None
  var dwellTime: Option[Int] = None
  val peckStartOffset = 1.0 // machine specific

  // TODO ?
  // cooling
  // cutter radius compensation
  // other axes position (e.g. Extruder)

  protected def isEq(a: Double, b: Double) = (a-b).abs < 1e-5

  protected def setFeed(f: Double) = {
      if (useMillimeters) {
          feedrate = f
      } else {
          feedrate = f * 25.4
      }
  }


  protected def linearMotion(x: Double, y: Double, z: Double,
                             a: Double, b: Double, c: Double,
                             f: Double) = {
    val coeff = if (useMillimeters) 1 else 25.4
    val x2 = coeff * x
    val y2 = coeff * y
    val z2 = coeff * z
    if (absoluteCoordinates) {
      this.x = x2
      this.y = y2
      this.z = z2
      this.a = a
      this.b = b
      this.c = c
    } else {
      this.x += x2
      this.y += y2
      this.z += z2
      this.a += a
      this.b += b
      this.c += c
    }
    //TODO account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    val distance = math.sqrt(x2*x2 + y2*y2 + z2*z2)
    time += f * distance * 60000 // from mm/minutes to ms
  }


  protected def getRotationAngle( cx: Double, cy: Double, //center of rotation
                                  x1: Double, y1: Double, //first point
                                  x2: Double, y2: Double, //second point
                                  clockwise: Boolean, turns: Int): Double = {
    val part1 = atan2(y1 - cy, x1 - cx)
    val part2 = atan2(y2 - cy, x2 - cx)
    val a0 = part2 - part1
    val a = if (clockwise && a0 <= 0) a0
            else if (clockwise && a0 > 0) a0 - 2*Pi
            else if (!clockwise && a0 >= 0) a0
            else a0 + 2*Pi
    val turnAngle = if (clockwise) -2*turns*Pi else 2*turns*Pi
    a + turnAngle
  }

  protected def circularMotion(_x: Double,_y: Double,_z: Double, // end position
                                a: Double, b: Double, c: Double, // end orientation
                               _i: Double,_j: Double,_k: Double, // center of rotation
                               clockwise: Boolean, p: Int, f: Double) = { // number of turns and feedrate
    // equation of an helix:
    // x(t) = a * cos(t)
    // y(t) = a * sin(t)
    // z(t) = b * t
    // a is the radius
    // b is the pitch / 2π

    // convert to millimeters
    val coeff = if (useMillimeters) 1 else 25.4
    val x = coeff * _x
    val y = coeff * _y
    val z = coeff * _z
    val i = coeff * _i
    val j = coeff * _j
    val k = coeff * _k

    // get the center
    val cx = this.x + i
    val cy = this.y + j
    val cz = this.z + k

    // compute the radius, the angle, and pitch of the helix
    val radius: Double = plane match {
      case XY => hypot( i, j)
      case ZX => hypot( k, i)
      case YZ => hypot( j, k)
    }
    val radiusCheck = plane match {
      case XY => hypot( x - cx, y - cy)
      case ZX => hypot( z - cz, x - cx)
      case YZ => hypot( y - cy, z - cz)
    }
    assert(isEq(radius, radiusCheck), "start and end radius of the circle do not agree: " + radius + " and " + radiusCheck)

    val arc = plane match {
      case XY => getRotationAngle(cx, cy, this.x, this.y, x, y, clockwise, p)
      case ZX => getRotationAngle(cz, cx, this.z, this.x, z, x, clockwise, p)
      case YZ => getRotationAngle(cy, cz, this.y, this.z, y, z, clockwise, p)
    }
    val angle = arc.abs
    val pitchOver2Pi: Double = plane match {
      case XY => (z - this.z) / angle
      case ZX => (y - this.y) / angle
      case YZ => (x - this.x) / angle
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
    //TODO account for the rotation (a/b/c) ? (the position is given at tool tip so rotation should not matter)
    val distance = angle * hypot(radius, pitchOver2Pi)
    time += f * distance * 60000 // from mm/minutes to ms
  }

  protected def findCenter( x1: Double, y1: Double, // start position (machine state)
                            _x2: Double, _y2: Double, // end position (from the command)
                            r: Double, clockwise: Boolean): (Double, Double) = {
    val x2 = if (useMillimeters) _x2 else 25.4 * _x2
    val y2 = if (useMillimeters) _y2 else 25.4 * _y2
    assert(!isEq(x1, x2) || !isEq(y1, y2), "ill-formed command " + (if (clockwise) "G2" else "G3"))
    // middle point
    val mx = (x1 + x2) / 2
    val my = (y1 + y2) / 2
    // vector from p₁ to p₂
    val dx = x2 - x1
    val dy = y2 - y1
    // how far from m should we go ???
    val toM2 = (mx - x1) * (mx - x1) + (my - y1) * (my - y1)
    val k = sqrt(r*r - toM2)
    assert(!k.isNaN, s"could not find center: $x1 → $x2, $y1 → $y2, radius $r")
    // normal to d (take the direction into account)
    val nx = (if (clockwise)  dy else -dy) / hypot(dx, dy)
    val ny = (if (clockwise) -dx else  dx) / hypot(dx, dy)
    val px = mx + nx * k
    val py = my + ny * k
    if (useMillimeters) (px, py)
    else (px / 25.4, py / 25.4)
  }


  // helper to compute the end position of a motion: default value of the arguments
  protected def getX = if (absoluteCoordinates) { if (useMillimeters) this.x else this.x / 25.4 } else 0.0
  protected def getY = if (absoluteCoordinates) { if (useMillimeters) this.y else this.y / 25.4 } else 0.0
  protected def getZ = if (absoluteCoordinates) { if (useMillimeters) this.z else this.z / 25.4 } else 0.0
  protected def getA = if (absoluteCoordinates) this.a else 0.0
  protected def getB = if (absoluteCoordinates) this.b else 0.0
  protected def getC = if (absoluteCoordinates) this.c else 0.0

  def handleLinear(params: Seq[Param]) = {
    var x = getX
    var y = getY
    var z = getZ
    var a = getA
    var b = getB
    var c = getC
    params.foreach{
      case X(v) => x = v
      case Y(v) => y = v
      case Z(v) => z = v
      case A(v) => a = v
      case B(v) => b = v
      case C(v) => c = v
      case F(v) => setFeed(v)
      case other => sys.error("in G0 or G1, unexpected param: " + other)
    }
    var f = if (mode == 1) feedrate else maxFeed
    linearMotion(x, y, z, a, b, c, f)
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
    //circle given either by R (radius), or IJK (center relative to the start)
    var i = x
    var j = y
    var k = z
    var r = 0.0
    // P is the number of turn
    var p = 0
    // parse parameters
    params.foreach{
      case X(v) => x = v
      case Y(v) => y = v
      case Z(v) => z = v
      case A(v) => a = v
      case B(v) => b = v
      case C(v) => c = v
      case I(v) => i = v
      case J(v) => j = v
      case K(v) => k = v
      case R(v) => r = v
      case F(v) => setFeed(v)
      case P(v) => p = v
      case other => sys.error("in G2 or G3, unexpected param: " + other)
    }
    assert(r >= 0, "ill-formed G2/3 command: radius = " + r)
    if (r > 0) {
      plane match {
        case XY =>
          val (c1, c2) = findCenter(this.x, this.y, x, y, r, clockwise)
          i = c1 - getX
          j = c2 - getY
        case ZX =>
          val (c1, c2) = findCenter(this.z, this.x, z, x, r, clockwise)
          k = c1 - getZ
          i = c2 - getX
        case YZ =>
          val (c1, c2) = findCenter(this.y, this.z, y, z, r, clockwise)
          j = c1 - getY
          k = c2 - getZ
      }
    }
    circularMotion(x, y, z, a, b, c, i, j, k, clockwise, p, feedrate)
  }

  def handleDrillingCycle(params: Seq[Param]) = {
    assert(plane == XY, "only drill along Z for the moment")
    var x = getX
    var y = getY
    var z = getZ
    if (zInitial.isEmpty) {
      zInitial = Some(z)
    }
    params.foreach{
      case X(v) => x = v
      case Y(v) => y = v
      case Z(v) => z = v
      case R(v) => zRetract = Some(v)
      case Q(v) => peckDistance = Some(v)
      case F(v) => setFeed(v)
      case P(v) => dwellTime = Some(v)
      case other => sys.error("in G81-3 unexpected param: " + other)
    }
    // expand the drilling int a sequence of linear motions
    //1. get to initial position: XY rapid, then Z rapid to R
    linearMotion(x, y, getZ, getA, getB, getC, maxFeed)
    linearMotion(getX, getY, zRetract.get, getA, getB, getC, maxFeed)
    //2. drill
    var lastZ = zRetract.get
    while (lastZ > z) {
        //move to start of peck
        if (getZ > lastZ + peckStartOffset) {
            linearMotion(getX, getY, lastZ + peckStartOffset, getA, getB, getC, maxFeed)
        }
        //peck/drill
        lastZ = if(mode == 83) max(z, lastZ - peckDistance.get) else z
        linearMotion(getX, getY, lastZ, getA, getB, getC, feedrate)
        //dwell at last step
        if (lastZ == z && mode == 82) {
            run(G(4, P(dwellTime.get)))
        }
        //retract
        linearMotion(getX, getY, zRetract.get, getA, getB, getC, maxFeed)
    }
  }

  def run(cmd: Command): String = {
    try {
      cmd match {
        case G(m @ (0|1), 0, Seq()) =>    mode = m
        case G(m @ (0|1), 0, params) =>
          mode = m
          handleLinear(params)
        case G(dir @ (2|3), 0, Seq()) =>  mode = dir
        case G(dir @ (2|3), 0, params) =>
          mode = dir
          handleRotate(params)
        case G(4, 0, Seq(P(ms))) => time += ms
        case G(4, 0, Seq(X(s))) =>  time += 1000 * s
        case G(4, 0, Seq(S(s))) =>  time += 1000 * s
        case G(17, 0, Seq()) =>   plane = XY
        case G(18, 0, Seq()) =>   plane = ZX
        case G(19, 0, Seq()) =>   plane = YZ
        case G(20, 0, Seq()) =>   useMillimeters = false
        case G(21, 0, Seq()) =>   useMillimeters = true
        case G(28, 0, params) =>
          if (params.isEmpty) {
            x = 0
            y = 0
            z = 0
            a = 0
            b = 0
            c = 0
          } else {
            params.foreach{
              case ParamT(ParamType.X) => x = 0
              case ParamT(ParamType.Y) => y = 0
              case ParamT(ParamType.Z) => z = 0
              case ParamT(ParamType.A) => a = 0
              case ParamT(ParamType.B) => b = 0
              case ParamT(ParamType.C) => c = 0
              case other => sys.error("in G28, unexpected axis: " + other)
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
        case G(90, 0, Seq()) =>   absoluteCoordinates = true
        case G(91, 0, Seq()) =>   absoluteCoordinates = false
        case G(92, 0, params) =>
          assert(params.nonEmpty)
          val coeff = if (useMillimeters) 1 else 25.4
          params.foreach{
            case X(v) => x = coeff * v
            case Y(v) => y = coeff * v
            case Z(v) => z = coeff * v
            case A(v) => a = v
            case B(v) => b = v
            case C(v) => c = v
            case other => sys.error("in G92 unexpected axis: " + other)
          }
        case G(98, 0, Seq()) => initialLevelReturn = true
        case G(99, 0, Seq()) => initialLevelReturn = false
        case M(3, 0, Seq(S(clockwise))) =>    spindleRPM = clockwise
        case M(4, 0, Seq(S(cclockwise))) =>   spindleRPM = -cclockwise
        case M(5, 0, Seq()) =>                spindleRPM = 0
        case M(114, 0, Seq()) =>
          if (useMillimeters) {
            return "X:" + x + " Y:" + y + " Z:" + z + " A:" + a + " B:" + b + " C:" + c
          } else {
            return "X:" + x/25.4 + " Y:" + y/25.4 + " Z:" + z/25.4 + " A:" + a + " B:" + b + " C:" + c
          }
        case Empty( Seq(F(f))) =>
          setFeed(f)
        case Empty( Seq(T(i))) =>
          selectedTool = i
        case Empty(params) =>
          if (mode == 0 || mode == 1) handleLinear(params)
          else if (mode == 2 || mode == 3) handleRotate(params)
          else if (mode == 81 || mode == 82 || mode == 83) handleDrillingCycle(params)
          else sys.error("Command not supported or ill-formed: " + cmd)
        case M(2, 0, Seq()) =>
          () //end of program, not much to do
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
