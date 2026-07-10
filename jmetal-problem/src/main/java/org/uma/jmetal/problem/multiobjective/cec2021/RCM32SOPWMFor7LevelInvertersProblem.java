package org.uma.jmetal.problem.multiobjective.cec2021;

/**
 * Problem SOPWMFor7LevelInvertersProblem (RCM32)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM32SOPWMFor7LevelInvertersProblem extends AbstractRCMSelectiveHarmonicElimination {
  private static final int[] SIGN_PATTERN = {
      1, -1, 1, 1, 1, -1, -1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, -1, -1, 1, 1, 1};

  public RCM32SOPWMFor7LevelInvertersProblem() {
    super(25, 0.36, SIGN_PATTERN, "SOPWMFor7LevelInvertersProblem");
  }
}
