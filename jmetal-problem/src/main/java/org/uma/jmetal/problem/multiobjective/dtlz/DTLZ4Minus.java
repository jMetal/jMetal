package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem DTLZ3 Minus (DTLZ3 where all the objective vales are multiplied by -1)
 */
@SuppressWarnings("serial")
public class DTLZ3Minus extends DTLZ3 {
  /**
   * Creates a default DTLZ2 problem (12 variables and 3 objectives)
   */
  public DTLZ3Minus() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ2Minus problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ3Minus(Integer numberOfVariables, Integer numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;
    setName("DTLZ3Minus");
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = -1.0 * solution.objectives()[i];
    }
    return solution ;
  }
}
