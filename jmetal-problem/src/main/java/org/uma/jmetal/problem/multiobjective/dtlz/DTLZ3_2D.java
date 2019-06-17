package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ3
 */
@SuppressWarnings("serial")
public class DTLZ3_2D extends DTLZ3 {
  /**
   * Creates a DTLZ3 instance with 2 objectives (12 variables and 2 objectives)
   */
  public DTLZ3_2D() {
    super(12, 2);
  }
}
