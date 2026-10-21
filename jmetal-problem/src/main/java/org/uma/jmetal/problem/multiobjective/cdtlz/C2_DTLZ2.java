package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem C2-DTLZ2, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class C2_DTLZ2 extends DTLZ2 {
  /** Creates the problem with 3 objectives and 12 variables. */
  public C2_DTLZ2() {
    this(12, 3);
  }

  private double rValue ;
  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C2_DTLZ2(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    numberOfConstraints(1);
    name("C2_DTLZ2");

    if (numberOfObjectives() == 3) {
      rValue = 0.4 ;
    } else {
      rValue = 0.5 ;
    }
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Evaluates the constraint c(x) = -min(min_i [(f_i - 1)^2 + sum_{j != i} f_j^2 - r^2],
   * sum_i (f_i - 1/sqrt(M))^2 - r^2) >= 0: a solution is feasible inside a sphere of radius r
   * centered at a corner of the front or at its center.
   *
   * @param solution the solution, with its objectives already evaluated
   */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] f = solution.objectives();
    int m = f.length;
    double squaredNorm = 0.0;
    for (double value : f) {
      squaredNorm += value * value;
    }
    double r2 = rValue * rValue;
    double cornerSpheres = Double.POSITIVE_INFINITY;
    double centerSphere = -r2;
    for (int i = 0; i < m; i++) {
      double corner = Math.pow(f[i] - 1.0, 2.0) + squaredNorm - f[i] * f[i] - r2;
      cornerSpheres = Math.min(cornerSpheres, corner);
      centerSphere += Math.pow(f[i] - 1.0 / Math.sqrt(m), 2.0);
    }
    solution.constraints()[0] = -Math.min(cornerSpheres, centerSphere);
  }
}
