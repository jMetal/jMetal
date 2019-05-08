package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;
import java.util.List;

/**
 * Abstract class representing a local search algorithm
 *
 * @param <S> Solution
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class DefaultLocalSearch<S extends Solution<?>> implements Algorithm<S> {
  private Problem<S> problem;
  public Problem<S> getProblem() {
    return problem;
  }

  private MutationOperator<S> mutationOperator;
  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  private Comparator<S> comparator;
  public Comparator<S> getComparator() {
    return comparator;
  }

  private int evaluations ;
  public int getEvaluations() {
    return evaluations ;
  }

  private S bestSolution;

  private long startComputingTime ;
  private long currentComputingTime ;

  public DefaultLocalSearch(
      Problem<S> problem, S solutionToImprove, MutationOperator<S> mutationOperator, Comparator<S> comparator) {
    this.problem = problem;
    this.mutationOperator = mutationOperator;
    this.comparator = comparator ;
    this.bestSolution = solutionToImprove ;
  }


  protected void initProgress() {
    startComputingTime = System.currentTimeMillis() ;
    evaluations = 0 ;
  }

  protected void updateProgress() {
    evaluations ++ ;
    currentComputingTime = System.currentTimeMillis() - startComputingTime ;
  }

  public long getCurrentComputingTime() {
    return currentComputingTime ;
  }

  protected abstract boolean isStoppingConditionReached();

  @Override
  public S getResult() {
    return bestSolution ;
  }

  @Override
  public void run() {
    initProgress();
    while (!isStoppingConditionReached()) {
      S mutatedSolution = mutationOperator.execute((S)bestSolution.copy());
      problem.evaluate(mutatedSolution);
      int result = comparator.compare(bestSolution, mutatedSolution) ;
      if (result == 1) {
        bestSolution = mutatedSolution ;
      } else if (result == 0) {
          if (JMetalRandom.getInstance().nextDouble() < 0.5) {
            bestSolution = mutatedSolution ;
        }
      }
      updateProgress();
    }
  }
}
