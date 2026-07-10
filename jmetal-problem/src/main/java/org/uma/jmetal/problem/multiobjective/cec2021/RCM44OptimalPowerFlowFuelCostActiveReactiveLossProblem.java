package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowFuelCostActiveReactiveLossProblem (RCM44)
 *
 * Optimal Power flow (Minimization of Fuel Cost, active and reactive power loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM44OptimalPowerFlowFuelCostActiveReactiveLossProblem
    extends AbstractRCMOptimalPowerFlow {

  public RCM44OptimalPowerFlowFuelCostActiveReactiveLossProblem() {
    super(3, "OptimalPowerFlowFuelCostActiveReactiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.fuelCost;
    solution.objectives()[1] = sum(result.psp);
    solution.objectives()[2] = sum(result.qsp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
