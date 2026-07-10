package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem DistributedGenerationSizingPhaseBalanceActiveReactiveLossProblem (RCM39)
 *
 * Optimal Sizing of Single Phase Distributed Generation with reactive power support for Phase
 * Balancing at Main Transformer/Grid and active and reactive Power loss.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM39DistributedGenerationSizingPhaseBalanceActiveReactiveLossProblem
    extends AbstractRCMDistributedGenerationSizing {

  public RCM39DistributedGenerationSizingPhaseBalanceActiveReactiveLossProblem() {
    super(3, "DistributedGenerationSizingPhaseBalanceActiveReactiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.phaseBalance;
    solution.objectives()[1] = sum(result.psp);
    solution.objectives()[2] = sum(result.qsp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
