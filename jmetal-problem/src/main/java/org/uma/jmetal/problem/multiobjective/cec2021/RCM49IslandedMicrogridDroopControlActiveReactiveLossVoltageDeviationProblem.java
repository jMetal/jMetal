package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem (RCM49)
 *
 * Optimal Setting of Droop Controller for Minimization of Active Power Loss in Islanded
 * Microgrids.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem
    extends AbstractRCMIslandedMicrogridDroopControl {

  public RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem() {
    super(3, "IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = sum(result.psp);
    solution.objectives()[1] = sum(result.qsp);
    solution.objectives()[2] = result.voltageDeviation;

    setEqualityConstraints(solution, result);

    return solution;
  }
}
