package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C3-DTLZ4, defined in: Jain, H. and K. Deb. "An Evolutionary Many-Objective Optimization
 * Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part II: Handling
 * Constraints and Extending to an Adaptive Approach." EEE Transactions on Evolutionary Computation,
 * 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C3_DTLZ4 extends DTLZ4 {
  /**
   * Constructor
   *
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C3_DTLZ4(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    super(numberOfVariables, numberOfObjectives);

    setNumberOfConstraints(numberOfConstraints);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    this.evaluateConstraints(solution);
  }

  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];

    for (int j = 0; j < getNumberOfConstraints(); j++) {
      double sum = 0;
      constraint[j] = Math.pow(solution.getObjective(j), 2.0) / 4.0 - 1.0;
      for (int i = 0; i < getNumberOfObjectives(); i++) {
        if (i != j) {
          sum += Math.pow(solution.getObjective(j), 2.0);
        }
        constraint[j] += sum;
      }
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }
  }
}
