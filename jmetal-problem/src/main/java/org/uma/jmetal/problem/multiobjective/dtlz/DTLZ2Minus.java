package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ2-1
 */
@SuppressWarnings("serial")
public class DTLZ2Inverted extends DTLZ2 {
  /**
   * Creates a default DTLZ2 problem (12 variables and 3 objectives)
   */
  public DTLZ2Inverted() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ2 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ2Inverted(Integer numberOfVariables, Integer numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;
    setName("DTLZ2_inverted");
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.setObjective(i, -1.0 * solution.getObjective(i));
    }
  }
}
