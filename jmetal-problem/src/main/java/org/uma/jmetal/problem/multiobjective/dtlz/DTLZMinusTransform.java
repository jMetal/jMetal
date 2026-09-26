package org.uma.jmetal.problem.multiobjective.dtlz;

/**
 * Shared support for the "minus" DTLZ variants of Ishibuchi, Setoguchi, Masuda and Nojima,
 * "Performance of Decomposition-Based Many-Objective Algorithms Strongly Depends on Pareto Front
 * Shapes," IEEE Transactions on Evolutionary Computation, 21(2), 169-190, 2017,
 * doi:10.1109/TEVC.2016.2587749.
 *
 * <p>Every standard DTLZ1-DTLZ4 objective has the form {@code f_j(x) = (1 + g(x_m)) * h_j(x_pos)},
 * with {@code g, h_j >= 0}. Simply negating every objective is not enough to reproduce the
 * published problems: since both factors are nonnegative, negating turns "minimize g and h" into
 * "maximize g and h", which moves the Pareto-optimal solutions from {@code g(x_m) = 0} to whatever
 * decision variables maximize {@code g(x_m)} instead. Eq. (9) of the paper above fixes this by
 * rescaling the negated objectives so that the ideal point is the origin and the nadir point is
 * {@code (1, ..., 1)}, which keeps {@code g(x_m) = 0} the Pareto-optimal condition:
 *
 * <pre>{@code f_j(x) = 1 - (1 - g(x_m) / (1 + gMax)) * h_j(x_pos)}</pre>
 *
 * <p>Both transformations below recover {@code g(x_m)} from the objectives already computed by
 * the standard DTLZ evaluate() method, using the identity that the standard front satisfies either
 * {@code sum(h) = frontSum} (DTLZ1, a hyperplane) or {@code sum(h^2) = 1} (DTLZ2-DTLZ4, a
 * hypersphere) for every value of {@code g}, not only at the front.
 */
final class DTLZMinusTransform {
  /** The single-dimension term maximized by DTLZ1/DTLZ3's distance function, found once. */
  private static final double SEPARABLE_TERM_MAXIMUM = computeSeparableTermMaximum();

  private DTLZMinusTransform() {}

  /**
   * The maximum of DTLZ1/DTLZ3's multimodal distance function {@code g(x_m) = 100 * (k +
   * sum((x_i - 0.5)^2 - cos(20*pi*(x_i - 0.5))))} over {@code k} variables in {@code [0, 1]}. The
   * sum is separable, so the maximum is {@code k} times the maximum of a single term.
   */
  static double multimodalDistanceMaximum(int k) {
    return 100.0 * k * (1.0 + SEPARABLE_TERM_MAXIMUM);
  }

  /**
   * The maximum of DTLZ2/DTLZ4's quadratic distance function {@code g(x_m) = sum((x_i -
   * 0.5)^2)} over {@code k} variables in {@code [0, 1]}, reached at the box corners.
   */
  static double quadraticDistanceMaximum(int k) {
    return 0.25 * k;
  }

  /**
   * Applies Eq. (9) in place to objectives already computed by DTLZ1, whose standard front
   * satisfies {@code sum(h) = frontSum} (0.5) for every {@code g}.
   */
  static void negateSumFront(double[] objectives, double frontSum, double gMax) {
    double sum = 0.0;
    for (double value : objectives) {
      sum += value;
    }
    double onePlusG = sum / frontSum;
    rescale(objectives, onePlusG, gMax);
  }

  /**
   * Applies Eq. (9) in place to objectives already computed by DTLZ2, DTLZ3 or DTLZ4, whose
   * standard front satisfies {@code sum(h^2) = 1} for every {@code g}.
   */
  static void negateSphereFront(double[] objectives, double gMax) {
    double sumOfSquares = 0.0;
    for (double value : objectives) {
      sumOfSquares += value * value;
    }
    double onePlusG = Math.sqrt(sumOfSquares);
    rescale(objectives, onePlusG, gMax);
  }

  private static void rescale(double[] objectives, double onePlusG, double gMax) {
    double g = onePlusG - 1.0;
    double weight = 1.0 - g / (1.0 + gMax);
    for (int i = 0; i < objectives.length; i++) {
      objectives[i] = 1.0 - weight * (objectives[i] / onePlusG);
    }
  }

  private static double computeSeparableTermMaximum() {
    double max = Double.NEGATIVE_INFINITY;
    int samples = 2_000_000;
    for (int i = 0; i <= samples; i++) {
      double s = -0.5 + (double) i / samples;
      double value = s * s - Math.cos(20.0 * Math.PI * s);
      max = Math.max(max, value);
    }
    return max;
  }
}
