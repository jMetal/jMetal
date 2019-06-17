package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ1
 */
@SuppressWarnings("serial")
public class DTLZ2_2D extends DTLZ2 {
  /**
   * Creates a DTLZ2 instance with two objectives (12 variables and 2 objectives)
   */
  public DTLZ2_2D() {
    super(12, 2);
  }
}
