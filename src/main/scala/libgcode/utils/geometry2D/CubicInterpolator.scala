package libgcode.utils.geometry2D

import libgcode.utils.*
import libgcode.generator.Config
import libgcode.Command
import libgcode.extractor.*
import scala.math

/** A planar cubic Hermite spline on `u ∈ [0, 1]`: the unique cubic `p(u) = (x(u), y(u))` matching given
  * endpoint positions `p(0)`, `p(1)` and tangents `p'(0)`, `p'(1)`. Usually built via the companion `apply`.
  * Stored as the two cubics `x(u) = m1·u³ + n1·u² + o1·u + p1`, `y(u) = m2·u³ + n2·u² + o2·u + p2` (so `p1`/`p2`
  * are the start point, `o1`/`o2` the start derivative, etc.).
  */
class CubicInterpolator(
      val m1: Double,
      val n1: Double,
      val o1: Double,
      val p1: Double,
      val m2: Double,
      val n2: Double,
      val o2: Double,
      val p2: Double
) extends Curve[CubicInterpolator] {

  def apply(u: Double) = {
    assert(u >= 0 && u <= 1)
    eval(u)
  }

  /** Evaluates the cubics at any real `u` (no bounds check). `apply` is the public bounds-checked view;
    * this is used when Newton wanders outside `[0,1]` or `ignoreBounds` extends the curve to the whole line.
    */
  private def eval(u: Double) = {
    val u2 = u * u
    val u3 = u2 * u
    val a  = m1 * u3 + n1 * u2 + o1 * u + p1
    val b  = m2 * u3 + n2 * u2 + o2 * u + p2
    (a, b)
  }

  def get(a: Double, b: Double, ignoreBounds: Boolean = false, tolerance: Double = 1e-6) = {
    // Closest point to (a,b) via multi-start Newton on the squared distance
    // f(u) = |p(u) − q|² (a smooth degree-6 polynomial; its minimum per "lobe" of
    // the curve is the answer, so we seed Newton from a grid to cover each lobe).
    // The residual check f < tolerance² separates points truly on the curve from
    // the closest points of an off-curve query. `eval` is unbounded so Newton can
    // wander; ignoreBounds extends the curve to the whole line, otherwise the
    // distance is to the curve restricted to [0,1] (flat outside).
    def point(u: Double) = eval(if (ignoreBounds) u else clamp(0, 1, u))
    def f(u: Double): Double = {
      val (ua, ub) = point(u)
      val dx = ua - a
      val dy = ub - b
      dx * dx + dy * dy
    }
    def fp(u: Double): Double = {
      val flat = !ignoreBounds && (u < 0 || u > 1) // f is flat outside [0,1]
      if (flat) 0.0
      else {
        val (da, db) = derivative(u)
        val (ua, ub) = point(u)
        2 * ((ua - a) * da + (ub - b) * db) // f'(u) = 2 (p(u) − q) · p'(u)
      }
    }
    val starts = (0 to 10).map(i => 0.1 * i)
    // f is squared distance, so newton's |f| < tol means distance < tolerance.
    val tightTol = tolerance * tolerance
    starts.iterator.flatMap(s => newton(f, fp, s, tightTol).filter(x => f(x) < tightTol)).toSeq.headOption
  }

  /** Arc length, computed by adaptive Simpson quadrature of |p'(u)| (no closed form).
    */
  override def length: Double = {
    def speed(u: Double) = {
      val (a, b) = derivative(u)
      math.hypot(a, b)
    }
    def simpson(u0: Double, u1: Double): Double = {
      val h = (u1 - u0) / 2
      (h / 3) * (speed(u0) + 4 * speed((u0 + u1) / 2) + speed(u1))
    }
    def integrate(u0: Double, u1: Double): Double = {
      val mid   = (u0 + u1) / 2
      val whole = simpson(u0, u1)
      val half  = simpson(u0, mid) + simpson(mid, u1)
      if ((whole - half).abs < 1e-8) {
        half + (half - whole) / 15 // Richardson extrapolation (Simpson error is O(h⁴))
      } else integrate(u0, mid) + integrate(mid, u1)
    }
    integrate(0, 1)
  }

  def derivative(u: Double) = {
    val u2 = u * u
    val a  = 3 * m1 * u2 + 2 * n1 * u + o1
    val b  = 3 * m2 * u2 + 2 * n2 * u + o2
    (a, b)
  }

  def curvature(u: Double) = {
    val (d1a, d1b) = derivative(u)
    val d2a        = 6 * m1 * u + 2 * n1
    val d2b        = 6 * m2 * u + 2 * n2
    // signed curvature (x' y'' - y' x'') / |p'|³. The 1.5 exponent is a Double
    // on purpose: integer `3/2` would be 1 and divide by |p'|² instead.
    (d1a * d2b - d1b * d2a) / math.pow(d1a * d1a + d1b * d1b, 1.5)
  }

  /** Drop near-coincident intersection candidates (keeps the first of each cluster
    * closer than `tolerance`). Independent root finders can report the same
    * intersection more than once; shared by the `intersect` methods.
    */
  private def deduplicate(pts: Seq[(Double, Double)], tolerance: Double): Seq[(Double, Double)] = {
    var result = Seq.empty[(Double, Double)]
    for (p <- pts) {
      if (!result.exists(q => distance(p._1, p._2, q._1, q._2) < tolerance)) result = result :+ p
    }
    result
  }

  /** Deterministic order for intersection points (by x, then y); shared by the
    * `intersect` methods.
    */
  private def sortPoints(pts: Seq[(Double, Double)]): Seq[(Double, Double)] =
    pts.sortWith((a, b) => if (a._1 < b._1) true else if (a._1 > b._1) false else a._2 < b._2)

  /** Active parameter domain: the curve's span `[0,1]`, or a wide window when
    * `ignoreBounds` extends the curve to the whole real line.
    */
  private def domain(ignoreBounds: Boolean): (Double, Double) =
    if (ignoreBounds) (-100.0, 100.0) else (0.0, 1.0)

  /** Find where `f` is (near) zero on `[lo, hi]`, clustered to one representative
    * per geometric zero. Shared 1-D solver for the `intersect` methods.
    *
    * Runs a multi-start Newton sweep plus a scan that depends on `signed`:
    *   - `signed = true`: a dense sign-change scan + bisection (simple roots;
    *     `intersectArc`, where f = |p−center|² − r²).
    *   - `signed = false`: f ≥ 0 always (a squared distance), so zeros are local
    *     MINIMA; a dense local-minima scan catches the sharp crossings Newton
    *     misses (`intersectCubic`).
    *
    * `accept` is the residual test a candidate must pass (rejects Newton iterates
    * that drift off the curve). `newtonTol` is Newton's |f| tolerance (pass
    * `tolerance` for a signed f, `tolerance²` for a squared f). Candidates within
    * `(hi-lo)/200` are clustered: for a signed f the cluster keeps its smallest-|f|
    * member, for a non-negative f it keeps the first.
    */
  private def findZeros(
      f: Double => Double,
      df: Double => Double,
      lo: Double,
      hi: Double,
      signed: Boolean,
      accept: Double => Boolean,
      newtonTol: Double
  ): Seq[Double] = {
    // 1. Multi-start Newton: a dense grid covers the basin of every zero.
    val nStarts = 200
    val newtonRoots = (0 to nStarts).iterator
      .map(i => lo + (hi - lo) * i / nStarts)
      .flatMap(s => newton(f, df, s, newtonTol))
      .filter(accept)
      .toSeq

    // 2. The scan.
    val scanRoots: Seq[Double] =
      if (signed) {
        // Sign-change scan + bisection: catches every simple root.
        def bisect(a: Double, b: Double, fa: Double): Double = {
          var l = a; var rr = b; var fl = fa
          (0 until 60).foreach { _ =>
            val m = (l + rr) / 2; val fm = f(m)
            if (fl * fm <= 0) rr = m else { l = m; fl = fm }
          }
          (l + rr) / 2
        }
        val nScan = 2000
        (0 until nScan).iterator.flatMap { i =>
          val a  = lo + (hi - lo) * i / nScan
          val b  = lo + (hi - lo) * (i + 1) / nScan
          val fa = f(a); val fb = f(b)
          if (fa * fb < 0) Some(bisect(a, b, fa)).filter(accept) else None
        }.toSeq
      } else {
        // Local-minima scan: f >= 0, so zeros are minima. A grid point below both
        // neighbours and a loose threshold is refined with improving-only Newton
        // steps (a sharp V makes a plain finite-difference Newton diverge).
        val nMin = 400
        (1 until nMin).iterator.flatMap { i =>
          val u  = lo + (hi - lo) * i / nMin
          val gm = f(u)
          val gl = f(lo + (hi - lo) * (i - 1) / nMin)
          val gr = f(lo + (hi - lo) * (i + 1) / nMin)
          if (gm < gl && gm < gr && gm < newtonTol * 1e3) {
            var cur = u
            (0 until 40).foreach { _ =>
              val fl = f(cur); val dfl = df(cur)
              if (dfl.abs > 1e-15) {
                val nxt = cur - fl / dfl
                if (nxt >= lo && nxt <= hi && f(nxt) < fl) cur = nxt
              }
            }
            Some(cur).filter(accept)
          } else None
        }.toSeq
      }

    // 3. Cluster: several passes converge to the same zero (especially near a
    //    tangent). Keep one representative per cluster.
    val raw = (newtonRoots ++ scanRoots).toSeq
    val clusterGap = (hi - lo) / 200.0
    raw.sorted.foldLeft(Seq.empty[Double]) { (acc, u) =>
      if (acc.nonEmpty && (u - acc.last).abs < clusterGap) {
        if (signed && f(u).abs < f(acc.last).abs) acc.init :+ u else acc
      } else acc :+ u
    }
  }

  /** Length of the longest run of parameters where each is within `gap` of the
    * previous. A gap-free band (a genuine sub-curve overlap) gives a long run;
    * isolated intersections (even several, spread apart) give short runs. Used
    * to distinguish an overlap from discrete intersections.
    */
  private def longestContiguousRun(us: Seq[Double], gap: Double): Double = {
    var longest  = 0.0
    var runStart = 0.0
    var prevU: Double = 0.0
    var first = true
    for (u <- us) {
      if (first || (u - prevU) > gap) {
        if (!first) longest = math.max(longest, prevU - runStart)
        runStart = u
        first = false
      }
      prevU = u
    }
    if (!first) longest = math.max(longest, prevU - runStart)
    longest
  }

  // Offset (parallel) curve, approximated adaptively. The exact offset of a cubic
  // is not a cubic, but the exact offset function q(u) = p(u) + x*n(u) is smooth
  // and known (q and q' = p'*(1 - x*curvature) are free to evaluate). So we fit
  // a Hermite cubic on each interval and split only where the fit's deviation
  // from q exceeds `tolerance`. Splits are ERROR-DRIVEN: each breaks the interval
  // at the argmax of the deviation (where the fit is worst), not the midpoint, so
  // gentle curves stay a single cubic and splits concentrate where needed. A cusp
  // (1 - x*curvature = 0, i.e. |x| = local radius of curvature) is a real
  // singularity we detect and throw on. Returns a single cubic or a Path of
  // sub-cubics. See Klass (1983), "An offset spline approximation for plane
  // cubic splines".
  def offset(x: Double, tolerance: Double = 1e-6): AbsCurve = {
    def qPos(u: Double): (Double, Double) = {
      val (a, b)   = apply(u)
      val (na, nb) = normal(u)
      (a + na * x, b + nb * x)
    }
    def qDeriv(u: Double): (Double, Double) = {
      val (da, db) = derivative(u)
      // q' = p' * (1 - x*kappa) for the left-normal offset. The minus sign is
      // required: it gives the true d/du of q. Using + feeds the Hermite fit the
      // wrong endpoint tangents and breaks the O(h⁴) convergence.
      val c        = 1 - x * curvature(u)
      (da * c, db * c)
    }

    // Hermite cubic fitting the exact offset on [lb, ub], reparameterized to [0,1];
    // endpoint derivatives are scaled by (ub - lb) = du/dv.
    def fit(lb: Double, ub: Double): CubicInterpolator = {
      val (a1, b1)   = qPos(lb)
      val (a2, b2)   = qPos(ub)
      val s          = ub - lb
      val (da1, db1) = qDeriv(lb)
      val (da2, db2) = qDeriv(ub)
      CubicInterpolator(a1, b1, da1 * s, db1 * s, a2, b2, da2 * s, db2 * s)
    }

    // Max |fit(u) - q(u)| over [lb, ub] and the argmax u* where it occurs, by
    // coarse-grid sampling (cheap, and tracks the true max for these smooth curves).
    // The argmax is where the caller splits (error-driven subdivision).
    def maxDevAt(f: CubicInterpolator, lb: Double, ub: Double): (Double, Double) = {
      val cells    = 16
      var max      = 0.0
      var argmaxU  = (lb + ub) / 2
      for (i <- 0 until cells) {
        val u = lb + (ub - lb) * (i + 0.5) / cells
        val v = (i + 0.5) / cells // position of u within the reparameterized fit
        val (fx, fy) = f(v)
        val (qx, qy) = qPos(u)
        val d = math.hypot(fx - qx, fy - qy)
        if (d > max) { max = d; argmaxU = u }
      }
      (max, argmaxU)
    }

    // Cusp check: 1 - x*curvature(u) must not change sign (or hit zero) on [lb,ub]
    // -- a sign change means the offset tangent vanishes and the curve folds back.
    def hasCusp(lb: Double, ub: Double): Boolean = {
      val n    = 64
      var prev = 1 - x * curvature(lb)
      if (prev.abs <= 1e-12) return true
      for (i <- 1 to n) {
        val u = lb + (ub - lb) * i / n
        val v = 1 - x * curvature(u)
        if (v.abs <= 1e-12) return true
        if (v * prev < 0) return true
        prev = v
      }
      false
    }

    val maxDepth = 10 // safety net: at most 2^maxDepth = 1024 sub-cubics
    def subdivide(lb: Double, ub: Double, depth: Int): List[CubicInterpolator] = {
      if (hasCusp(lb, ub)) {
        sys.error(s"offset($x) of this curve has a cusp near u in [$lb, $ub] " +
          "(1 - x*curvature = 0); the offset curve folds back on itself.")
      }
      val f = fit(lb, ub)
      val (dev, uStar) = maxDevAt(f, lb, ub)
      if (dev <= tolerance || depth >= maxDepth) {
        List(f)
      } else {
        // Error-driven split at the argmax u*, falling back to the midpoint if u*
        // lands on an endpoint (which would give a degenerate sub-interval).
        val split = if (uStar <= lb + 1e-6 || uStar >= ub - 1e-6) (lb + ub) / 2 else uStar
        subdivide(lb, split, depth + 1) ++ subdivide(split, ub, depth + 1)
      }
    }

    val segs = subdivide(0.0, 1.0, 0)
    if (segs.size == 1) segs.head else Path(segs)
  }

  def translate(ta: Double, tb: Double) = {
    val (a1, b1)   = apply(0)
    val (a2, b2)   = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    CubicInterpolator(a1 + ta, b1 + tb, da1, db1, a2 + ta, b2 + tb, da2, db2)
  }

  def rotate(a: Double, b: Double, alpha: Double) = {
    val (a1, b1)   = apply(0)
    val (a2, b2)   = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    val (a3, b3)   = rotateAround(a, b, alpha, a1, b1)
    val (a4, b4)   = rotateAround(a, b, alpha, a2, b2)
    val (da3, db3) = rotateAround(0, 0, alpha, da1, db1)
    val (da4, db4) = rotateAround(0, 0, alpha, da2, db2)
    CubicInterpolator(a3, b3, da3, db3, a4, b4, da4, db4)
  }

  def flip = {
    val (a1, b1)   = apply(0)
    val (a2, b2)   = apply(1)
    val (da1, db1) = derivative(0)
    val (da2, db2) = derivative(1)
    CubicInterpolator(a2, b2, -da2, -db2, a1, b1, -da1, -db1)
  }

  /** Restrict the curve to the sub-interval `[lb, ub]` and reparameterize it over `[0, 1]`.
    *
    * The result is the (unique) cubic Hermite curve `q` on `v ∈ [0, 1]` such that
    * `q(v) = p(lb + v·(ub − lb))`. This is exact: a restriction of a cubic
    * polynomial is a cubic polynomial, and a cubic is determined by its
    * endpoint positions and tangents, so it suffices to compute the four Hermite
    * values of the sub-curve and rebuild with [[CubicInterpolator.apply]].
    *
    * The endpoint derivatives are scaled by `ub − lb` (the reparameterization
    * Jacobian `du/dv`), by the chain rule: `q'(v) = p'(lb + v·(ub−lb))·(ub−lb)`.
    */
  def restrict(lb: Double, ub: Double): CubicInterpolator = {
    assert(lb >= 0 && lb < ub && ub <= 1, s"invalid bounds [$lb, $ub]")
    val (a1, b1)   = apply(lb)
    val (a2, b2)   = apply(ub)
    val (da1, db1) = derivative(lb)
    val (da2, db2) = derivative(ub)
    val s          = ub - lb // reparameterization factor du/dv
    CubicInterpolator(a1, b1, da1 * s, db1 * s, a2, b2, da2 * s, db2 * s)
  }

  /** Intersects this curve with the (infinite) line `l`.
    *
    * Substituting `p(u) = (x(u), y(u))` into the line's Cartesian equation
    * `nx·x + ny·y + c = 0` gives a single cubic `f(u) = nx·x(u) + ny·y(u) + c`;
    * the intersections are exactly the real roots of `f` (at most 3, via
    * [[roots]]). Points are kept only if they lie on BOTH curves (when
    * `ignoreBounds` is false, also within the segment's bounds via `l.get`).
    *
    * Degenerate: if the curve is parallel to the line, `f` is constant. If it is
    * zero the curve is collinear with the line and a representative point is
    * returned; otherwise there is no intersection. Consistent with
    * [[Arc.intersectArc]] / [[Line.intersectLine]].
    */
  def intersectLine(l: Line, ignoreBounds: Boolean = false, tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    val (nx, ny, c) = l.cartesianCoeff
    // f(u) = nx·x(u) + ny·y(u) + c, a cubic in u built from the raw coefficients
    val f3 = nx * m1 + ny * m2
    val f2 = nx * n1 + ny * n2
    val f1 = nx * o1 + ny * o2
    val f0 = nx * p1 + ny * p2 + c
    if (f3.abs < tolerance && f2.abs < tolerance && f1.abs < tolerance) {
      // f is constant: the curve is parallel to the line
      if (f0.abs < tolerance) {
        // collinear: the curve lies on the line. Consistent with
        // Line.intersectLine, return a point in the overlap of the curve's span
        // [0,1] and the line segment, or empty if they are disjoint.
        if (ignoreBounds) {
          Seq(apply(0))
        } else {
          // project the line segment's endpoints onto this curve's parameter
          // (get with ignoreBounds gives the parameter on the supporting line,
          // which is valid here since the curve lies on that line). This mirrors
          // Line.intersectLine's collinear branch.
          val (la1, lb1) = l(0.0)
          val c1         = this.get(la1, lb1, true, tolerance).get
          val (la2, lb2) = l(1.0)
          val c2         = this.get(la2, lb2, true, tolerance).get
          val min        = math.min(c1, c2)
          val max        = math.max(c1, c2)
          if (max < -tolerance || min > 1.0 + tolerance) {
            // disjoint: the segment does not reach the curve's span [0,1]
            Seq.empty
          } else {
            // the overlap [max(0, min), min(1, max)] is non-empty; return its start
            Seq(apply(clamp(0.0, 1.0, min)))
          }
        }
      } else {
        Seq.empty
      }
    } else {
      val us = roots(f3, f2, f1, f0, tolerance)
      val filtered =
        if (ignoreBounds) us
        else us.filter(u => u >= -tolerance && u <= 1.0 + tolerance)
      // map to points on the cubic, keeping only those also on the segment, and dedupe.
      val pts = filtered.flatMap { u =>
        val p = eval(if (ignoreBounds) u else clamp(0, 1, u))
        if (l.get(p._1, p._2, ignoreBounds, tolerance).isDefined) Some(p) else None
      }
      deduplicate(pts, tolerance)
    }
  }

  /** Intersects this curve with the given arc (part of a circle).
    *
    * `p(u)` lies on the circle of center `(cx, cy)`, radius `r` iff
    * `(x-cx)² + (y-cy)² = r²`. Since x, y are cubic in u, the squared distance is
    * a degree-6 polynomial `f(u)`; the intersections are its real roots. Points
    * are kept only if on BOTH curves (when `ignoreBounds` is false, u is clamped
    * to `[0,1]` and the point must lie in the arc's angle range via `arc.get`).
    *
    * `f` has no closed-form roots, so it is solved numerically over the active
    * domain (see [[findZeros]]), then the candidates are mapped to points, filtered
    * to those on the arc, deduplicated and sorted — mirroring [[intersectLine]].
    */
  def intersectArc(arc: Arc, ignoreBounds: Boolean = false, tolerance: Double = 1e-6): Seq[(Double, Double)] = {
    val cx = arc.a
    val cy = arc.b
    val r  = arc.r

    // f(u) = (x-cx)² + (y-cy)² - r², degree 6 in u, ascending powers. The
    // squared part is the self-convolution of each coordinate cubic (Poly6.square);
    // the linear shift -2cx·x / -2cy·y and constant (cx²+cy²-r²) live in deg 0..3.
    val xC  = Poly6(Array(p1, o1, n1, m1))
    val yC  = Poly6(Array(p2, o2, n2, m2))
    val shift = Poly6(Array(
      -2 * cx * p1 - 2 * cy * p2 + cx * cx + cy * cy - r * r,
      -2 * cx * o1 - 2 * cy * o2,
      -2 * cx * n1 - 2 * cy * n2,
      -2 * cx * m1 - 2 * cy * m2
    ))
    val f6 = xC.square + yC.square + shift
    def f(u: Double): Double  = f6.eval(u)
    def fp(u: Double): Double = f6.deriv(u)

    val (lo, hi) = domain(ignoreBounds)

    // A candidate is a valid intersection only if it maps to a point on the circle,
    // which rejects Newton iterates that drift off the curve.
    def onCircle(u: Double): Boolean = f(u).abs < tolerance

    // Parameters where the curve meets the circle (signed f -> sign-scan + Newton, clustered).
    val best = findZeros(f, fp, lo, hi, signed = true, accept = onCircle, newtonTol = tolerance)

    // Map each parameter to a point on the curve and keep only those on the arc
    // (arc.get checks the angle range when ignoreBounds is false).
    val pts = best.flatMap { u =>
      val uu = if (ignoreBounds) u else clamp(0.0, 1.0, u)
      val p  = eval(uu)
      if (arc.get(p._1, p._2, ignoreBounds, tolerance).isDefined) Some(p) else None
    }

    // Dedupe (a tangent can be hit from both sides of its flat region) and sort.
    // Merge scale is a small fraction of the radius: true intersections are far apart.
    val merge = math.max(10 * tolerance, 5e-3 * r)
    sortPoints(deduplicate(pts, merge))
  }

  /** Intersects this curve with another cubic Hermite curve.
    *
    * No closed form (unlike [[intersectLine]] / [[intersectArc]]), so we reduce it
    * to a 1-D root find, mirroring [[intersectArc]]:
    *   1. For `u` on this curve, the squared distance from `p(u)` to the other
    *      curve `c` is `g(u) = min_s |p(u) − q(s)|²` (the inner min is the same
    *      degree-6 root problem as [[get]]).
    *   2. `g(u) ≈ 0` exactly where the curves meet. But `g` is a squared distance
    *      (always ≥ 0), so the intersections are LOCAL MINIMA, not sign changes:
    *      [[findZeros]]'s non-signed mode (Newton sweep + local-minima scan) finds
    *      them.
    *   3. Candidates are clustered, mapped to points, kept only if on BOTH curves
    *      (a residual check), deduplicated and sorted.
    *
    * With `ignoreBounds` each curve is extended to the whole line; otherwise each
    * is restricted to its `[0,1]` span. If the curves share a sub-curve (a
    * continuum of intersections), the overlap's endpoints are returned instead
    * (consistent with [[intersectLine]]'s collinear branch).
    */
  def intersectCubic(
      c: CubicInterpolator,
      ignoreBounds: Boolean = false,
      tolerance: Double = 1e-6
  ): Seq[(Double, Double)] = {
    val (loA, hiA) = domain(ignoreBounds)
    val (loB, hiB) = domain(ignoreBounds)

    // The other curve's coordinate cubics (ascending), reused for every distance poly.
    val cXC = Poly6(Array(c.p1, c.o1, c.n1, c.m1))
    val cYC = Poly6(Array(c.p2, c.o2, c.n2, c.m2))
    val cSelfSq = cXC.square + cYC.square

    // Squared distance from a fixed point to the other curve c, as a degree-6 poly
    // in s (same structure as `f` in `get`), ascending powers.
    def distPoly(qx: Double, qy: Double): Poly6 = {
      val shift = Poly6(Array(
        -2 * qx * c.p1 - 2 * qy * c.p2 + qx * qx + qy * qy,
        -2 * qx * c.o1 - 2 * qy * c.o2,
        -2 * qx * c.n1 - 2 * qy * c.n2,
        -2 * qx * c.m1 - 2 * qy * c.m2
      ))
      cSelfSq + shift
    }

    // Squared distance from a point to c, i.e. the min over s of the degree-6 poly,
    // restricted to c's domain [loB, hiB]. Newton alone is unreliable (non-convex:
    // several starts converge to the same wrong local min), so we take the min over
    // a dense grid (which brackets the global min) and then polish with
    // improving-only Newton. Called once per candidate u, so it stays closed-form.
    def dist2(qx: Double, qy: Double): Double = {
      val p = distPoly(qx, qy)
      // 1. coarse dense minimum (brackets the global min)
      val n = 200
      var best = Double.PositiveInfinity
      var bi   = 0
      var i    = 0
      while (i <= n) {
        val s = loB + (hiB - loB) * i / n
        val d = p.eval(s)
        if (d < best) { best = d; bi = i }
        i += 1
      }
      // 2. Newton-polish from the best grid point, accepting only improving, in-domain steps
      var cur = loB + (hiB - loB) * bi / n
      (0 until 30).foreach { _ =>
        val f  = p.eval(cur)
        val fp = p.deriv(cur)
        if (fp.abs > 1e-15) {
          val nxt = cur - f / fp
          if (nxt >= loB && nxt <= hiB) {
            val d = p.eval(nxt)
            if (d < best) { best = d; cur = nxt }
          }
        }
      }
      math.max(0.0, best)
    }

    // g(u) = squared distance from p(u) to the other curve; its (near-)zeros are
    // the intersections. Use the RAW (unclamped) distance: clamping flattens the
    // minima into wide zero bands and hides the sharpness Newton needs.
    val distTol = tolerance * tolerance
    def g(u: Double): Double = {
      val (px, py) = eval(u)
      dist2(px, py)
    }
    // g is piecewise smooth (a closest-point lobe switch creates a kink); a
    // central difference is a robust derivative for Newton.
    def gp(u: Double): Double = {
      val h = (hiA - loA) * 1e-5
      (g(u + h) - g(u - h)) / (2 * h)
    }

    // A candidate is a real intersection only if the point lies on BOTH curves.
    def onBoth(u: Double): Boolean = {
      val (px, py) = eval(u)
      dist2(px, py) < distTol
    }

    // Parameters where the curves meet. g is a squared distance (>= 0), so its
    // zeros are minima, not sign changes: the non-signed mode (Newton + minima
    // scan) applies, stopping at |g| < tolerance².
    val best = findZeros(g, gp, loA, hiA, signed = false, accept = onBoth, newtonTol = distTol)

    // Map each parameter to a point on THIS curve, keeping only those on the other curve.
    val pts = best.flatMap { u =>
      val uu = if (ignoreBounds) clamp(-100.0, 100.0, u) else clamp(0.0, 1.0, u)
      val p  = eval(uu)
      if (onBoth(uu)) Some(p) else None
    }

    // Distinguish a genuine OVERLAP (the curves share a sub-curve, so g is ~0 over
    // a whole interval of u) from isolated intersections. The point count can't
    // decide it (bidegree (3,3) can meet at up to 9 points); the signal is a
    // CONTIGUOUS, gap-free band of candidates. Isolated points (even several) have
    // gaps, so no run is long enough — only a shared sub-curve makes a dense band.
    val dedup = deduplicate(pts, math.max(10 * tolerance, 1e-3 * math.max(hiA, hiB)))
    val us    = best
    val bandThreshold = (hiA - loA) * 0.05 // 5% of the domain counts as an overlap
    // us are already clustered by findZeros at gap (hiA-loA)/200; reuse that gap.
    val overlap = longestContiguousRun(us, (hiA - loA) / 200.0) > bandThreshold
    if (overlap) {
      // return the endpoints of the overlap band (min and max candidate u)
      val pLo = eval(if (ignoreBounds) clamp(-100.0, 100.0, us.min) else clamp(0.0, 1.0, us.min))
      val pHi = eval(if (ignoreBounds) clamp(-100.0, 100.0, us.max) else clamp(0.0, 1.0, us.max))
      Seq(pLo, pHi)
    } else {
      sortPoints(dedup)
    }
  }

  def intersect(c: AbsCurve, ignoreBounds: Boolean, tolerance: Double): Seq[(Double, Double)] = {
    if (c.isInstanceOf[Arc]) {
      this.intersectArc(c.asInstanceOf[Arc], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Line]) {
      this.intersectLine(c.asInstanceOf[Line], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[CubicInterpolator]) {
      this.intersectCubic(c.asInstanceOf[CubicInterpolator], ignoreBounds, tolerance)
    } else if (c.isInstanceOf[Path]) {
      c.intersect(this, ignoreBounds, tolerance)
    } else {
      sys.error(s"does not know how to intersect $this and $c")
    }
  }

  def toGCode(config: Config): Seq[Command] = {
    // TODO: approximate by lines and arcs to create a path and then covert that to g-code
    // try bi arc, check max deviation and refine if needed
    ???
  }

}

object CubicInterpolator {

  /** Build the cubic Hermite curve starting at `(a1, b1)` with tangent `(da1, db1)`
    * and ending at `(a2, b2)` with tangent `(da2, db2)`. For each coordinate this
    * solves `c(0)=a1, c'(0)=da1, c(1)=a2, c'(1)=da2` (likewise for b).
    *
    * Beware: the eight arguments are positional doubles; the wrong order silently
    * builds a different (possibly self-intersecting) curve.
    */
  def apply(
      a1: Double,
      b1: Double, // start point
      da1: Double,
      db1: Double, // start derivative
      a2: Double,
      b2: Double, // end point
      da2: Double,
      db2: Double
  ) = { // end derivative
    // TODO sanity check for the parameters
    // m x³ + n x² + o x + p
    val m1 = 2 * a1 + da1 - 2 * a2 + da2
    val n1 = -3 * a1 - 2 * da1 + 3 * a2 - da2
    val o1 = da1
    val p1 = a1
    val m2 = 2 * b1 + db1 - 2 * b2 + db2
    val n2 = -3 * b1 - 2 * db1 + 3 * b2 - db2
    val o2 = db1
    val p2 = b1
    new CubicInterpolator(m1, n1, o1, p1, m2, n2, o2, p2)
  }
}
