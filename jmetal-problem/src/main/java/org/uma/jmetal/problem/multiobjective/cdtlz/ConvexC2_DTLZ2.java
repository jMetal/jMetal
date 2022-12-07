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
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ConvexC2_DTLZ2 extends DTLZ2 {
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
    this.evaluateConstraints(solution);

    return solution ;
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
