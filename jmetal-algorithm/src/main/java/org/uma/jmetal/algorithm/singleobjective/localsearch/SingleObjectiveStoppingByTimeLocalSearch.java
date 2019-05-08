package org.uma.jmetal.algorithm.singleobjective.localsearch;

import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class SingleObjectiveStoppingByTimeLocalSearch<S extends Solution<?>>
    extends DefaultLocalSearch<S> {
  private long maxComputingTime;

  public SingleObjectiveStoppingByTimeLocalSearch(Problem<S> problem, S solutionToImprove, MutationOperator<S> mutation, long maxComputingTime) {
    super(problem, solutionToImprove, mutation, new ObjectiveComparator<>(0)) ;
    this.maxComputingTime = maxComputingTime;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return getCurrentComputingTime() >= maxComputingTime;
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
