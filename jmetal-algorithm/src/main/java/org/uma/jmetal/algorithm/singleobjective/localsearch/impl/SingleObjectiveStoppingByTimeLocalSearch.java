package org.uma.jmetal.algorithm.singleobjective.localsearch.impl;

import org.uma.jmetal.algorithm.singleobjective.localsearch.AbstractSingleObjectiveLocalSearch;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class SingleObjectiveStoppingByTimeLocalSearch<S extends Solution<?>>
    extends AbstractSingleObjectiveLocalSearch<S> {
  private long maxComputingTime;

  public SingleObjectiveStoppingByTimeLocalSearch(Problem<S> problem, long maxComputingTime) {
    super(problem) ;
    this.maxComputingTime = maxComputingTime;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return currentComputingTime >= maxComputingTime;
  }
}
