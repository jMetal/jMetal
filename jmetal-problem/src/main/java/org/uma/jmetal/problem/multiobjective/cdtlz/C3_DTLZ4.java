package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C3-DTLZ4, defined in: Jain, H. and K. Deb. "An Evolutionary Many-Objective Optimization
 * Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part II: Handling
 * Constraints and Extending to an Adaptive Approach." EEE Transactions on Evolutionary Computation,
 * 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class C3_DTLZ4 extends DTLZ4 {
  /** Creates the problem with 3 objectives, 12 variables and 3 constraints. */
  public C3_DTLZ4() {
    this(12, 3, 3);
  }

  /**
   * Constructor
   *
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C3_DTLZ4(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    super(numberOfVariables, numberOfObjectives);

    numberOfConstraints(numberOfConstraints);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    this.evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Evaluates the constraints c_j(x) = f_j^2 / 4 + sum_{i != j} f_i^2 - 1 >= 0, for j = 1..M.
   *
   * @param solution the solution, with its objectives already evaluated
   */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] f = solution.objectives();
    double squaredNorm = 0.0;
    for (double value : f) {
      squaredNorm += value * value;
    }
    for (int j = 0; j < numberOfConstraints(); j++) {
      solution.constraints()[j] = f[j] * f[j] / 4.0 + squaredNorm - f[j] * f[j] - 1.0;
    }
  }
}
