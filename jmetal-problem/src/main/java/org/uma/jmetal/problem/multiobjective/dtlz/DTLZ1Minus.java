package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ1
 */
@SuppressWarnings("serial")
public class DTLZ1Minus extends DTLZ1 {
  /**
   * Creates a default DTLZ1 problem (7 variables and 3 objectives)
   */
  public DTLZ1Minus() {
    this(7, 3);
  }

  /**
   * Creates a DTLZ1 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ1Minus(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    super(numberOfVariables, numberOfObjectives) ;
    name("DTLZ1Minus");
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = -1.0 * solution.objectives()[i];
    }
    return solution ;
  }
}

