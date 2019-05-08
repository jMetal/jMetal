package org.uma.jmetal.algorithm.singleobjective.localsearch;

import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class SingleObjectiveStoppingByEvaluationsLocalSearch<S extends Solution<?>>
    extends DefaultLocalSearch<S> {
  private int maxEvaluations;

  public SingleObjectiveStoppingByEvaluationsLocalSearch(
      Problem<S> problem, S solutionToImprove, MutationOperator<S> mutation, int maxEvaluations) {
    super(problem, solutionToImprove, mutation, new ObjectiveComparator<S>(0));
    this.maxEvaluations = maxEvaluations;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return getEvaluations() >= maxEvaluations;
  }

  @Override
  public String getName() {
    return "Single objective local search";
  }

  @Override
  public String getDescription() {
    return "Single objective local search";
  }
}
