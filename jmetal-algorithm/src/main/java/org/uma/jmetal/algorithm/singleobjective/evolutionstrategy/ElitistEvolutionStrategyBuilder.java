package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Created by ajnebro on 10/3/15.
 */
public class ElitistEvolutionStrategyBuilder<S extends Solution> implements AlgorithmBuilder {
  private Problem problem;
  private int mu;
  private int lambda;
  private int maxEvaluations;
  private MutationOperator<S> mutation;

  public ElitistEvolutionStrategyBuilder(Problem problem, MutationOperator<S> mutationOperator) {
    this.problem = problem;
    this.mu = 1;
    this.lambda = 10;
    this.maxEvaluations = 250000;
    this.mutation = mutationOperator;
  }

  public ElitistEvolutionStrategyBuilder setMu(int mu) {
    this.mu = mu;

    return this;
  }

  public ElitistEvolutionStrategyBuilder setLambda(int lambda) {
    this.lambda = lambda;

    return this;
  }

  public ElitistEvolutionStrategyBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public ElitistEvolutionStrategyBuilder setMutationOperator(MutationOperator<S> mutation) {
    this.mutation = mutation;

    return this;
  }

  @Override public Algorithm build() {
    return new ElitistEvolutionStrategy<S>(problem, mu, lambda, maxEvaluations, mutation);
  }

  /* Getters */
  public int getMu() {
    return mu;
  }

  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public MutationOperator getMutation() {
    return mutation;
  }
}
