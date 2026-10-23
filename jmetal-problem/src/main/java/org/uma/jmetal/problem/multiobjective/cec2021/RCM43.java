package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowFuelCostActiveLossProblem (RCM43)
 *
 * Optimal Power flow (Minimization of Fuel Cost, active power loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM43 extends AbstractRCMOptimalPowerFlow {

  public RCM43() {
    super(2, "RCM43");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.fuelCost;
    solution.objectives()[1] = sum(result.psp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
