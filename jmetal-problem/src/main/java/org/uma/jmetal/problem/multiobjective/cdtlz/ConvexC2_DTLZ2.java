package org.uma.jmetal.problem.multiobjective.cdtlz;

import java.util.HashMap;
import java.util.Map;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ConvexC2-DTLZ2, defined in: Jain, H. and K. Deb. "An Evolutionary Many-Objective
 * Optimization Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part II:
 * Handling Constraints and Extending to an Adaptive Approach." EEE Transactions on Evolutionary
 * Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class ConvexC2_DTLZ2 extends DTLZ2 {
  /** Creates the problem with 3 objectives and 12 variables. */
  public ConvexC2_DTLZ2() {
    this(12, 3);
  }

  private static Map<Integer, Double> rValue;

  static {
    rValue = new HashMap<Integer, Double>();
    rValue.put(3, 0.225);
    rValue.put(5, 0.225);
    rValue.put(8, 0.26);
    rValue.put(10, 0.26);
    rValue.put(15, 0.27);
  }

  /**
   * Constructor
   *
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public ConvexC2_DTLZ2(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives);

    numberOfConstraints(1);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    convexify(solution);
    this.evaluateConstraints(solution);

    return solution ;
  }

  /**
   * Turns the objectives of DTLZ2 into those of the convex DTLZ2 problem on which Jain and Deb
   * define this one: f_i^4 for i < M and f_M^2.
   */
  private static void convexify(DoubleSolution solution) {
    double[] f = solution.objectives();
    for (int i = 0; i < f.length - 1; i++) {
      f[i] = Math.pow(f[i], 4.0);
    }
    f[f.length - 1] = Math.pow(f[f.length - 1], 2.0);
  }

  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[numberOfConstraints()];

    double sum = 0;
    for (int i = 0; i < solution.objectives().length; i++) {
      sum += solution.objectives()[i];
    }

    double lambda = sum / solution.objectives().length;

    sum = 0;
    for (int i = 0; i < solution.objectives().length; i++) {
      sum += Math.pow(solution.objectives()[i] - lambda, 2.0);
    }

    constraint[0] = sum - Math.pow(rValue.get(solution.objectives().length), 2.0);

    for (int i = 0; i < numberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
