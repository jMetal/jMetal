package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C1-DTLZ1, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class C1_DTLZ1 extends DTLZ1 {
  /** Creates the problem with 3 objectives and 7 variables. */
  public C1_DTLZ1() {
    this(7, 3);
  }

  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C1_DTLZ1(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    numberOfConstraints(1);
    name("C1_DTLZ1");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Evaluates the constraint c(x) = 1 - f_M / 0.6 - sum_{i < M} f_i / 0.5 >= 0.
   *
   * @param solution the solution, with its objectives already evaluated
   */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] f = solution.objectives();
    int m = f.length;
    double sum = 0.0;
    for (int i = 0; i < m - 1; i++) {
      sum += f[i] / 0.5;
    }
    solution.constraints()[0] = 1.0 - f[m - 1] / 0.6 - sum;
  }
}
