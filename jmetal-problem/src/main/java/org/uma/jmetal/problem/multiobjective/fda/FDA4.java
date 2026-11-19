package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Problem FDA4 of Farina, Deb and Amato (see {@link FDA}): type I, nonconvex front (the spherical
 * front of DTLZ2) whose Pareto set moves with t. With M objectives:
 *
 * <pre>
 * f_1 = (1 + g) prod_{i=1..M-1} cos(x_i pi / 2)
 * f_k = (1 + g) prod_{i=1..M-k} cos(x_i pi / 2) sin(x_{M-k+1} pi / 2),  k = 2, ..., M - 1
 * f_M = (1 + g) sin(x_1 pi / 2)
 * g = sum_{i=M..n} (x_i - G)^2,  G = |sin(0.5 pi t)|,  x in [0, 1]^n
 * </pre>
 *
 * <p>The paper suggests n = M + 9; the default is M = 3 and n = 12. Up to jMetal 7.11 the class
 * always had three objectives, whatever the number given.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA4 extends FDA {

  /** Creates the problem with 3 objectives and 12 variables. */
  public FDA4() {
    this(12, 3);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   */
  public FDA4(Integer numberOfVariables, Integer numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, DEFAULT_TAU_T, DEFAULT_N_T);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   * @param tauT the number of values of the counter for which t remains fixed
   * @param nT the number of distinct steps in t
   */
  public FDA4(int numberOfVariables, int numberOfObjectives, int tauT, int nT) {
    super(tauT, nT);
    checkDimensions("FDA4", numberOfVariables, numberOfObjectives);
    numberOfObjectives(numberOfObjectives);
    name("FDA4");
    setVariableBounds(numberOfVariables, numberOfVariables, 0.0, 1.0);
  }

  static void checkDimensions(String name, int numberOfVariables, int numberOfObjectives) {
    Check.that(
        numberOfObjectives >= 2,
        () -> name + " needs at least 2 objectives (" + numberOfObjectives + ")");
    Check.that(
        numberOfVariables >= numberOfObjectives,
        () ->
            name
                + " needs at least as many variables ("
                + numberOfVariables
                + ") as objectives ("
                + numberOfObjectives
                + ")");
  }

  /**
   * The objectives of the spherical front of DTLZ2 for the position values y_1, ..., y_{M-1} and
   * the distance g.
   */
  static void sphericalObjectives(double[] y, double g, double[] f) {
    int m = f.length;
    for (int k = 1; k <= m; k++) {
      double value = 1.0 + g;
      for (int i = 1; i <= m - k; i++) {
        value *= Math.cos(y[i - 1] * Math.PI / 2.0);
      }
      if (k > 1) {
        value *= Math.sin(y[m - k] * Math.PI / 2.0);
      }
      f[k - 1] = value;
    }
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int m = solution.objectives().length;
    double bigG = Math.abs(Math.sin(0.5 * Math.PI * time));
    double g = 0.0;
    for (int i = m - 1; i < solution.variables().size(); i++) {
      g += Math.pow(solution.variables().get(i) - bigG, 2.0);
    }
    double[] y = new double[m - 1];
    for (int i = 0; i < m - 1; i++) {
      y[i] = solution.variables().get(i);
    }
    sphericalObjectives(y, g, solution.objectives());
    return solution;
  }
}
