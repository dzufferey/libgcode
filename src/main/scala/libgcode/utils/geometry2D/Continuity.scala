package libgcode.utils.geometry2D

object Continuity extends Enumeration {

  type Continuity = Value
  /** Continuity as in https://dev.opencascade.org/doc/overview/html/occt_user_guides__modeling_data.html#occt_modat_4_2a
   * - C0: continuous
   * - G1: C0 + colinear derivative
   * - C1: C0 + same derivative
   * - G2: G1 + same centerOfCurvature
   * - C2: C1 + same centerOfCurvature
   */
  val C0, G1, C1, G2, C2 = Value

  def level(c: Continuity): Int = c match {
    case C0 => 0
    case C1 | G1 => 1
    case C2 | G2 => 2
  }

  def parametricContinuity(c: Continuity): Boolean = c match {
    case C1 | C2 => true
    case _ => false
  }

  def min(c1: Continuity, c2: Continuity): Continuity = {
    val lvl = math.min(level(c1), level(c2))
    val pc = parametricContinuity(c1) && parametricContinuity(c2)
    if (lvl == 0) {
      C0
    } else if (lvl == 1) {
      if (pc) C1 else G1
    } else {
      if (pc) C2 else G2
    }
  }

  def max(c1: Continuity, c2: Continuity): Continuity = {
    val lvl = math.max(level(c1), level(c2))
    val pc = parametricContinuity(c1) || parametricContinuity(c2)
    if (lvl == 0) {
      C0
    } else if (lvl == 1) {
      if (pc) C1 else G1
    } else {
      if (pc) C2 else G2
    }
  }

}
