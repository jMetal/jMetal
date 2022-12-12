package org.uma.jmetal.algorithm.singleobjective.localsearch;

import java.util.Comparator;
import org.uma.jmetal.algorithm.impl.AbstractLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class BasicLocalSearch<S extends Solution<?>> extends AbstractLocalSearch<S> {

  private S initialSolution ;
  private int evaluations ;
  private int maxEvaluations ;
  private MutationOperator<S> mutationOperator ;
  private Comparator<S> comparator ;
  private Problem<S> problem ;

  public BasicLocalSearch(S initialSolution, int maxEvaluations,
      Problem<S> problem, MutationOperator<S> mutationOperator, Comparator<S> comparator) {
    this.initialSolution = initialSolution ;
    this.maxEvaluations = maxEvaluations ;
    this.mutationOperator = mutationOperator ;
    this.comparator = comparator ;
    this.problem = problem ;
  }

  @Override
  protected S setCurrentSolution() {
    return initialSolution;
  }

  @Override
  protected void initProgress() {
    evaluations = 1 ;
  }

  @Override
  protected void updateProgress() {
    evaluations ++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected S updateCurrentSolution(S currentSolution) {
    S newSolution = mutationOperator.execute((S)currentSolution.copy()) ;
    problem.evaluate(newSolution) ;

    int result = comparator.compare(newSolution, currentSolution) ;
    if ((result == -1) || ((result == 0) && (JMetalRandom.getInstance().getRandomGenerator().nextDouble() < 0.5))) {
      currentSolution = newSolution ;
    }

    return currentSolution ;
  }

  @Override
  public String getName() {
    return "Basic local search";
  }

  @Override
  public String getDescription() {
    return "Basic local search";
  }
}
