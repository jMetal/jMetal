package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem DTLZ2 Minus (DTLZ2 where all the objective vales are multiplied by -1
 */
@SuppressWarnings("serial")
public class DTLZ2Minus extends DTLZ2 {
  /**
   * Creates a default DTLZ2 problem (12 variables and 3 objectives)
   */
  public DTLZ2Minus() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ2Minus problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ2Minus(Integer numberOfVariables, Integer numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;
    setName("DTLZ2Minus");
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.setObjective(i, -1.0 * solution.getObjective(i));
    }
  }
}
