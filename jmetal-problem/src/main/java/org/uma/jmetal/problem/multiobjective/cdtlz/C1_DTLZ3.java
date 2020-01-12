package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem C1-DTLZ3, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C1_DTLZ3 extends DTLZ3 {
  private static Map<Integer, Double> rValue;

  static {
    rValue = new HashMap<Integer, Double>() ;
    rValue.put(3, 9.0) ;
    rValue.put(5, 12.5) ;
    rValue.put(8, 12.5) ;
    rValue.put(10, 15.0) ;
    rValue.put(15, 15.0) ;
  }

  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C1_DTLZ3(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    setNumberOfConstraints(1);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    this.evaluateConstraints(solution);
  }

  public void evaluateConstraints(DoubleSolution solution) {

    double sum1 = 0 ;
    double sum2 = 0 ;
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      double v = Math.pow(solution.getObjective(i), 2) ;
      sum1 += v - 16.0 ;
      sum2 += v - Math.pow(rValue.get(getNumberOfObjectives()), 2.0) ;
    }

    solution.setConstraint(0, sum1 * sum2);
  }
}
