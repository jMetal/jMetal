package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem DTLZ4 Minus (DTLZ4 where all the objective vales are multiplied by -1)
 */
@SuppressWarnings("serial")
public class DTLZ4Minus extends DTLZ4 {
  /**
   * Creates a default DTLZ4 minus problem (12 variables and 3 objectives)
   */
  public DTLZ4Minus() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ4Minus problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ4Minus(Integer numberOfVariables, Integer numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;
    name("DTLZ4Minus");
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
