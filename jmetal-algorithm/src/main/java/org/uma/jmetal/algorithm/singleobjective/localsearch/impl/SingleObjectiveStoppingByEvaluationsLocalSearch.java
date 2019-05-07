package org.uma.jmetal.algorithm.singleobjective.localsearch.impl;

import org.uma.jmetal.algorithm.singleobjective.localsearch.AbstractSingleObjectiveLocalSearch;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class SingleObjectiveStoppingByEvaluationsLocalSearch<S extends Solution<?>>
    extends AbstractSingleObjectiveLocalSearch<S> {
  private int maxEvaluations;

  public SingleObjectiveStoppingByEvaluationsLocalSearch(Problem<S> problem, int maxEvaluations) {
    super(problem) ;
    this.maxEvaluations = maxEvaluations ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }
}
