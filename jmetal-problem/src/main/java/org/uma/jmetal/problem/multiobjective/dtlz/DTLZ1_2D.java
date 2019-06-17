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
public class DTLZ1_2D extends DTLZ1 {
  /**
   * Creates a DTLZ1 instance with two objectives (7 variables and 2 objectives)
   */
  public DTLZ1_2D() {
    super(7, 2);
  }
}

