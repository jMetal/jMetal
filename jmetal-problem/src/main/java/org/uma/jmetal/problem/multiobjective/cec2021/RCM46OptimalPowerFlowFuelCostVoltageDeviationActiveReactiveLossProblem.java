package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowFuelCostVoltageDeviationActiveReactiveLossProblem (RCM46)
 *
 * Optimal Power flow (Minimization of Fuel Cost, voltage deviation, active and reactive power
 * loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM46OptimalPowerFlowFuelCostVoltageDeviationActiveReactiveLossProblem
    extends AbstractRCMOptimalPowerFlow {

  public RCM46OptimalPowerFlowFuelCostVoltageDeviationActiveReactiveLossProblem() {
    super(4, "OptimalPowerFlowFuelCostVoltageDeviationActiveReactiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.fuelCost;
    solution.objectives()[1] = sum(result.psp);
    solution.objectives()[2] = sum(result.qsp);
    solution.objectives()[3] = result.voltageDeviation;

    setEqualityConstraints(solution, result);

    return solution;
  }
}
