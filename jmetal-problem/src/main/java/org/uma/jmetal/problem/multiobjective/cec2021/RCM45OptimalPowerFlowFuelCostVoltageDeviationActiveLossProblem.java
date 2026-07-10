package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowFuelCostVoltageDeviationActiveLossProblem (RCM45)
 *
 * Optimal Power flow (Minimization of Fuel Cost, voltage deviation and active power loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM45OptimalPowerFlowFuelCostVoltageDeviationActiveLossProblem
    extends AbstractRCMOptimalPowerFlow {

  public RCM45OptimalPowerFlowFuelCostVoltageDeviationActiveLossProblem() {
    super(3, "OptimalPowerFlowFuelCostVoltageDeviationActiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.fuelCost;
    solution.objectives()[1] = sum(result.psp);
    solution.objectives()[2] = result.voltageDeviation;

    setEqualityConstraints(solution, result);

    return solution;
  }
}
