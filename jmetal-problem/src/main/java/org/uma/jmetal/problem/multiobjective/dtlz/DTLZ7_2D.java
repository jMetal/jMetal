package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ7
 */
@SuppressWarnings("serial")
public class DTLZ7_2D extends DTLZ7 {
  /**
   * Creates a DTLZ7 instance with two objectives (22 variables and 2 objectives)
   */
  public DTLZ7_2D() {
    super(22, 3);
  }
}
