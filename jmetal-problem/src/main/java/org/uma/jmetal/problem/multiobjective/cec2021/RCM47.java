package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem IslandedMicrogridDroopControlActiveReactiveLossProblem (RCM47)
 *
 * Optimal Setting of Droop Controller for Minimization of Active Power Loss in Islanded
 * Microgrids.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM47
    extends AbstractRCMIslandedMicrogridDroopControl {

  public RCM47() {
    super(2, "RCM47");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = sum(result.psp);
    solution.objectives()[1] = sum(result.qsp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
