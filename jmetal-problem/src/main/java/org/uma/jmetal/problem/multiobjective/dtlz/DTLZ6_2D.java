package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ6
 */
@SuppressWarnings("serial")
public class DTLZ6_2D extends DTLZ6 {
  /**
   * Creates a DTLZ6 instance with two objectives (12 variables and 2 objectives)
   */
  public DTLZ6_2D() {
    super(12, 2);
  }
}
