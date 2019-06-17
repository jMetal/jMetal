package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ5
 */
@SuppressWarnings("serial")
public class DTLZ5_2D extends DTLZ5 {
  /**
   * Creates a DTLZ5 instance with two objective (12 variables and 2 objectives)
   */
  public DTLZ5_2D() {
    super(12, 3);
  }
}
