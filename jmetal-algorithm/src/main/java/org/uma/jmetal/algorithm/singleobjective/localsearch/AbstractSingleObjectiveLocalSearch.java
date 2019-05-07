package org.uma.jmetal.algorithm.singleobjective.localsearch;

import org.uma.jmetal.algorithm.impl.AbstractLocalSearch;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;

public abstract class AbstractSingleObjectiveLocalSearch<S extends Solution<?>> extends AbstractLocalSearch<S> {
  protected int evaluations ;
  protected long initTime ;
  protected long currentComputingTime ;
  protected Comparator<S> comparator ;

  public AbstractSingleObjectiveLocalSearch(Problem<S> problem) {
    super(problem) ;
  }

  @Override
  protected void initProgress() {
    initTime = System.currentTimeMillis() ;
    evaluations = 0 ;
  }

  @Override
  protected void updateProgress() {
    currentComputingTime = System.currentTimeMillis() - initTime ;
    evaluations ++ ;
  }

  @Override
  protected S improve(S solution) {
    S  mutatedSolution = (S) bestSolution.copy();
    getProblem().evaluate(mutatedSolution);

    int result = comparator.compare(bestSolution, mutatedSolution) ;
    if (result > 0) {
      bestSolution = mutatedSolution ;
    } else if (result == 0) {
      if (JMetalRandom.getInstance().nextDouble() < 0.5) {
        bestSolution = mutatedSolution ;
      }
    }

    return bestSolution;
  }

  @Override
  public S getResult() {
    return bestSolution ;
  }

  @Override
  public String getName() {
    return "Single objective local search";
  }

  @Override
  public String getDescription() {
    return "Single objective local search";
  }

  @Override
  public long getComputingTime() {
    return currentComputingTime ;
  }

  @Override
  public int getNumberOfEvaluations() {
    return evaluations ;
  }
}
