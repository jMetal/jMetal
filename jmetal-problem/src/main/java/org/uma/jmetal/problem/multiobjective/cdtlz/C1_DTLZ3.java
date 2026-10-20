package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C1-DTLZ3, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class C1_DTLZ3 extends DTLZ3 {
  /** Creates the problem with 3 objectives and 12 variables. */
  public C1_DTLZ3() {
    this(12, 3);
  }

  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C1_DTLZ3(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    numberOfConstraints(1);
  }

  /**
   * The radius r of the constraint: 9 for 3 objectives, 12.5 for 5 and 8, and 15 for 10 and 15
   * (Jain and Deb); 6 for 2 objectives, and the next listed value for the others, as PlatEMO.
   */
  static double radius(int numberOfObjectives) {
    if (numberOfObjectives == 2) {
      return 6.0;
    } else if (numberOfObjectives <= 3) {
      return 9.0;
    } else if (numberOfObjectives <= 8) {
      return 12.5;
    }
    return 15.0;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    this.evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Evaluates the constraint c(x) = (sum f_i^2 - 16) (sum f_i^2 - r^2) >= 0.
   *
   * @param solution the solution, with its objectives already evaluated
   */
  public void evaluateConstraints(DoubleSolution solution) {
    double squaredNorm = 0.0;
    for (double f : solution.objectives()) {
      squaredNorm += f * f;
    }
    double r = radius(solution.objectives().length);
    solution.constraints()[0] = (squaredNorm - 16.0) * (squaredNorm - r * r);
  }
}
