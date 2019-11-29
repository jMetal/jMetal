package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.localsearch.impl.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * Abstract class representing a local search algorithm
 *
 * @param <S> Solution
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultLocalSearch<S extends Solution<?>> implements Algorithm<S> {
  private Problem<S> problem;
  private int maxEvaluations;

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

  private S bestSolution;

  public DefaultLocalSearch(
      int maxEvaluations,
      Problem<S> problem,
      MutationOperator<S> mutationOperator,
      Comparator<S> comparator) {
    this.problem = problem;
    this.mutationOperator = mutationOperator;
    this.comparator = comparator;
    this.maxEvaluations = maxEvaluations;
  }

  @Override
  public S getResult() {
    return bestSolution;
  }

  @Override
  public void run() {
    bestSolution = problem.createSolution();
    problem.evaluate(bestSolution);

    LocalSearchOperator<S> localSearch =
        new BasicLocalSearch<S>(
            maxEvaluations, mutationOperator, new DominanceComparator<S>(), problem);

    bestSolution = localSearch.execute(bestSolution);
  }

  @Override
  public String getName() {
    return "Default local search";
  }

  @Override
  public String getDescription() {
    return "Default local search";
  }
}
