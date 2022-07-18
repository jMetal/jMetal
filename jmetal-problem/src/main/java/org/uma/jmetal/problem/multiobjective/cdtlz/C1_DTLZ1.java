package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.stream.IntStream;

/**
 * Problem C1-DTLZ1, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C1_DTLZ1 extends DTLZ1 {
  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C1_DTLZ1(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    setNumberOfConstraints(1);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    evaluateConstraints(solution);

    return solution ;
  }

  public void evaluateConstraints(DoubleSolution solution) {
    var sum = 0.0;
    var bound = solution.objectives().length - 2;
    for (var i = 0; i < bound; i++) {
      var v = solution.objectives()[i] / 0.5;
      sum += v;
    }

    solution.constraints()[0] = 1.0 - solution.objectives()[(solution.objectives().length-1)] - sum ;
  }
}
