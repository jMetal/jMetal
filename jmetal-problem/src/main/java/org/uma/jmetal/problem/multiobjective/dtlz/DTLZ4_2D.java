package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ4
 */
@SuppressWarnings("serial")
public class DTLZ4_2D extends DTLZ4 {
  /**
   * Creates a DTLZ4 instance with two objectives (12 variables and 2 objectives)
   */
  public DTLZ4_2D() {
    super(12, 2);
  }
}
