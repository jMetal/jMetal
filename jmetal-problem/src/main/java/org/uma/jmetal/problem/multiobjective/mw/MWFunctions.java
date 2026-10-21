package org.uma.jmetal.problem.multiobjective.mw;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * The distance functions of the MW problems (Z. Ma and Y. Wang, "Evolutionary Constrained
 * Multiobjective Optimization: Test Suite Construction and Performance Comparisons", IEEE
 * Transactions on Evolutionary Computation 23(6):972-986, 2019, Eqs. 12-14), and the set-up shared
 * by the problems.
 *
 * <p>With m objectives and n variables, the position variables are x_1, ..., x_{m-1} and the
 * distance variables x_m, ..., x_n. The three distance functions have a minimum of 1.
 */
final class MWFunctions {

  private MWFunctions() {}

  /**
   * Checks the dimensions of a problem and sets its name, dimensions and bounds: every variable in
   * [0, upperBound].
   *
   * @param problem the problem
   * @param name its name
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least two
   * @param numberOfConstraints the number of constraints
   * @param upperBound the upper bound of every variable
   */
  static void setUp(
      AbstractDoubleProblem problem,
      String name,
      int numberOfVariables,
      int numberOfObjectives,
      int numberOfConstraints,
      double upperBound) {
    Check.that(
        numberOfObjectives >= 2,
        () -> name + " needs at least two objectives (" + numberOfObjectives + ")");
    Check.that(
        numberOfVariables >= numberOfObjectives,
        () ->
            name
                + " needs at least as many variables ("
                + numberOfVariables
                + ") as objectives ("
                + numberOfObjectives
                + ")");
    problem.name(name);
    problem.numberOfObjectives(numberOfObjectives);
    problem.numberOfConstraints(numberOfConstraints);
    problem.variableBounds(
        Collections.nCopies(numberOfVariables, 0.0),
        Collections.nCopies(numberOfVariables, upperBound));
  }

  /** The variables of a solution as an array. */
  static double[] variables(DoubleSolution solution) {
    double[] x = new double[solution.variables().size()];
    for (int i = 0; i < x.length; i++) {
      x[i] = solution.variables().get(i);
    }
    return x;
  }

  /**
   * The biased distance function g1 (Eq. 12):
   *
   * <pre>
   * g1 = 1 + sum_{i=m..n} (1 - exp(-10 (z_i - 0.5 - (i - 1) / (2 n))^2)),  z_i = x_i^(n - m)
   * </pre>
   *
   * <p>Its minimum, 1, is reached with z_i = 0.5 + (i - 1) / (2 n).
   *
   * @param x the variables
   * @param m the number of objectives
   * @return g1
   */
  static double g1(double[] x, int m) {
    int n = x.length;
    double sum = 0.0;
    for (int i = m; i <= n; i++) {
      double z = Math.pow(x[i - 1], n - m);
      double d = z - 0.5 - (i - 1) / (2.0 * n);
      sum += 1.0 - Math.exp(-10.0 * d * d);
    }
    return 1.0 + sum;
  }

  /**
   * The multimodal distance function g2 (Eq. 13):
   *
   * <pre>
   * g2 = 1 + sum_{i=m..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),
   * z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
   * </pre>
   *
   * <p>Its minimum, 1, is reached with x_i = (i - 1) / n.
   *
   * @param x the variables
   * @param m the number of objectives
   * @return g2
   */
  static double g2(double[] x, int m) {
    int n = x.length;
    double sum = 0.0;
    for (int i = m; i <= n; i++) {
      double d = x[i - 1] - (i - 1) / (double) n;
      double z = 1.0 - Math.exp(-10.0 * d * d);
      sum += 1.5 + 0.1 / n * z * z - 1.5 * Math.cos(2.0 * Math.PI * z);
    }
    return 1.0 + sum;
  }

  /**
   * The distance function with variable linkages g3 (Eq. 14):
   *
   * <pre>
   * g3 = 1 + sum_{i=m..n} 2 (x_i + (x_{i-1} - 0.5)^2 - 1)^2
   * </pre>
   *
   * <p>Its minimum, 1, is reached with x_i = 1 - (x_{i-1} - 0.5)^2.
   *
   * @param x the variables
   * @param m the number of objectives
   * @return g3
   */
  static double g3(double[] x, int m) {
    double sum = 0.0;
    for (int i = m; i <= x.length; i++) {
      double previous = x[i - 2] - 0.5;
      double d = x[i - 1] + previous * previous - 1.0;
      sum += 2.0 * d * d;
    }
    return 1.0 + sum;
  }

  /** sqrt(max(0, value)): the square root of a value that is negative only by rounding. */
  static double sqrtOfNonNegative(double value) {
    return Math.sqrt(Math.max(0.0, value));
  }
}
