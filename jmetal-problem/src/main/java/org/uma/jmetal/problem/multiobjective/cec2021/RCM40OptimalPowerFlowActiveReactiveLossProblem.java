package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem OptimalPowerFlowActiveReactiveLossProblem (RCM40)
 *
 * Optimal Power flow (Minimization of Active and Reactive Power Loss).
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM40OptimalPowerFlowActiveReactiveLossProblem extends AbstractRCMOptimalPowerFlow {

  public RCM40OptimalPowerFlowActiveReactiveLossProblem() {
    super(2, "OptimalPowerFlowActiveReactiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = sum(result.psp);
    solution.objectives()[1] = result.quirkyReactiveTerm + (sum(result.qsp) - result.qsp[0]);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
