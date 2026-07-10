package org.uma.jmetal.problem.multiobjective.cec2021;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem DistributedGenerationSizingActiveReactiveLossProblem (RCM38)
 *
 * Optimal Sizing of Single Phase Distributed Generation with reactive power support for active
 * and reactive Power loss.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM38DistributedGenerationSizingActiveReactiveLossProblem
    extends AbstractRCMDistributedGenerationSizing {

  public RCM38DistributedGenerationSizingActiveReactiveLossProblem() {
    super(2, "DistributedGenerationSizingActiveReactiveLossProblem");
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    PowerFlowResult result = computePowerFlow(solution);

    solution.objectives()[0] = sum(result.qsp);
    solution.objectives()[1] = sum(result.psp);

    setEqualityConstraints(solution, result);

    return solution;
  }
}
