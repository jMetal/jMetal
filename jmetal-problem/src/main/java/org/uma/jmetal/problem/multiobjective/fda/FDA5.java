package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem FDA5 of Farina, Deb and Amato (see {@link FDA}): type II, nonconvex fronts whose radius
 * and density of solutions change with t. With M objectives:
 *
 * <pre>
 * f_1 = (1 + g) prod_{i=1..M-1} cos(y_i pi / 2)
 * f_k = (1 + g) prod_{i=1..M-k} cos(y_i pi / 2) sin(y_{M-k+1} pi / 2),  k = 2, ..., M - 1
 * f_M = (1 + g) sin(y_1 pi / 2)
 * g = G + sum_{i=M..n} (x_i - G)^2,  y_i = x_i^F,  G = |sin(0.5 pi t)|,  F = 1 + 100 sin^4(0.5 pi t)
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The paper suggests n = M + 9; the default is M = 3 and n = 12. Up to jMetal 7.11 the class
 * always had three objectives, whatever the number given.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA5 extends FDA {

  /** Creates the problem with 3 objectives and 12 variables. */
  public FDA5() {
    this(12, 3);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   */
  public FDA5(Integer numberOfVariables, Integer numberOfObjectives) {
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
  public FDA5(int numberOfVariables, int numberOfObjectives, int tauT, int nT) {
    super(tauT, nT);
    FDA4.checkDimensions("FDA5", numberOfVariables, numberOfObjectives);
    numberOfObjectives(numberOfObjectives);
    name("FDA5");
    setVariableBounds(numberOfVariables, numberOfVariables, 0.0, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int m = solution.objectives().length;
    double bigG = Math.abs(Math.sin(0.5 * Math.PI * time));
    double bigF = 1.0 + 100.0 * Math.pow(Math.sin(0.5 * Math.PI * time), 4.0);
    double g = bigG;
    for (int i = m - 1; i < solution.variables().size(); i++) {
      g += Math.pow(solution.variables().get(i) - bigG, 2.0);
    }
    double[] y = new double[m - 1];
    for (int i = 0; i < m - 1; i++) {
      y[i] = Math.pow(solution.variables().get(i), bigF);
    }
    FDA4.sphericalObjectives(y, g, solution.objectives());
    return solution;
  }
}
