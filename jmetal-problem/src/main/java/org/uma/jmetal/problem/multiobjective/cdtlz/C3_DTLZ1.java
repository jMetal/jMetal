package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C3-DTLZ1, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class C3_DTLZ1 extends DTLZ1 {
  /** Creates the problem with 3 objectives, 7 variables and 3 constraints. */
  public C3_DTLZ1() {
    this(7, 3, 3);
  }

  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C3_DTLZ1(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    super(numberOfVariables, numberOfObjectives) ;

    numberOfConstraints(numberOfConstraints);
    name("C3_DTLZ1");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    this.evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Evaluates the constraints c_j(x) = sum_{i != j} f_i + f_j / 0.5 - 1 >= 0, for j = 1..M.
   *
   * @param solution the solution, with its objectives already evaluated
   */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] f = solution.objectives();
    double sum = 0.0;
    for (double value : f) {
      sum += value;
    }
    for (int j = 0; j < numberOfConstraints(); j++) {
      solution.constraints()[j] = sum - f[j] + f[j] / 0.5 - 1.0;
    }
  }
}
