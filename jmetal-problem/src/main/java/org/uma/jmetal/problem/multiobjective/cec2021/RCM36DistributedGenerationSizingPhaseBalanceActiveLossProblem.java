package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem DistributedGenerationSizingPhaseBalanceActiveLossProblem (RCM36)
 *
 * Optimal Sizing of Single Phase Distributed Generation with reactive power support for Phase
 * Balancing at Main Transformer/Grid and active Power loss.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM36DistributedGenerationSizingPhaseBalanceActiveLossProblem
    extends AbstractRCMDistributedGenerationSizing {

  public RCM36DistributedGenerationSizingPhaseBalanceActiveLossProblem() {
    super(2, "DistributedGenerationSizingPhaseBalanceActiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = result.phaseBalance;
    solution.objectives()[1] = sum(result.psp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
