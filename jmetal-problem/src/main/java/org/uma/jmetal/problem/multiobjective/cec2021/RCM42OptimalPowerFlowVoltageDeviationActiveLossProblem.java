package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowVoltageDeviationActiveLossProblem (RCM42)
 *
 * Optimal Power flow (Minimization of voltage deviation, Active and Reactive Power Loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM42OptimalPowerFlowVoltageDeviationActiveLossProblem
    extends AbstractRCMOptimalPowerFlow {

  public RCM42OptimalPowerFlowVoltageDeviationActiveLossProblem() {
    super(2, "OptimalPowerFlowVoltageDeviationActiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = sum(result.psp);
    solution.objectives()[1] = result.voltageDeviation;

    setEqualityConstraints(solution, result);

    return solution;
  }
}
