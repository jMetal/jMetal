package org.uma.jmetal.problem.multiobjective.cec2021;

/**
 * Problem SOPWMFor9LevelInvertersProblem (RCM33)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM33SOPWMFor9LevelInvertersProblem extends AbstractRCMSelectiveHarmonicElimination {
  private static final int[] SIGN_PATTERN = {
      1, 1, 1, 1, -1, 1, -1, -1, -1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, -1, -1, 1, -1, -1, 1, 1, 1,
      1, -1, 1};

  public RCM33SOPWMFor9LevelInvertersProblem() {
    super(30, 0.32, SIGN_PATTERN, "SOPWMFor9LevelInvertersProblem");
  }
}
