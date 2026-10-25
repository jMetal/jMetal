package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Builder of {@link CMOEAMS}. The defaults are a population of 100 solutions, 25000 evaluations,
 * {@code lambda} = 0.5 (the value of the paper and of PlatEMO) and a sequential evaluator. The
 * crossover selects the variation of PlatEMO: genetic operators for a crossover with two parents
 * and two children, and differential evolution for a {@link
 * org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover} (see {@link CMOEAMS}).
 *
 * @param <S> the type of the solutions
 */
public class CMOEAMSBuilder<S extends Solution<?>> implements AlgorithmBuilder<CMOEAMS<S>> {
  private final Problem<S> problem;
  private final CrossoverOperator<S> crossover;
  private final MutationOperator<S> mutation;
  private int populationSize;
  private int maxEvaluations;
  private double lambda;
  private SolutionListEvaluator<S> evaluator;

  /**
   * Creates the builder.
   *
   * @param problem the problem to solve
   * @param crossover the crossover, with two parents and two children, or a {@link
   *     org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover} of variant {@code
   *     RAND_1_BIN} or {@code RAND_1_EXP}
   * @param mutation the mutation
   */
  public CMOEAMSBuilder(
      Problem<S> problem, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    Check.notNull(problem, "problem");
    Check.notNull(crossover, "crossover");
    Check.notNull(mutation, "mutation");
    CMOEAMS.checkCrossover(crossover);

    this.problem = problem;
    this.crossover = crossover;
    this.mutation = mutation;
    populationSize = 100;
    maxEvaluations = 25000;
    lambda = 0.5;
    evaluator = new SequentialSolutionListEvaluator<>();
  }

  /**
   * Sets the population size.
   *
   * @param populationSize the population size, at least 2
   * @return this builder
   */
  public CMOEAMSBuilder<S> setPopulationSize(int populationSize) {
    CMOEAMS.checkPopulationSize(populationSize);
    this.populationSize = populationSize;
    return this;
  }

  /**
   * Sets the maximum number of evaluations.
   *
   * @param maxEvaluations the maximum number of evaluations, positive
   * @return this builder
   */
  public CMOEAMSBuilder<S> setMaxEvaluations(int maxEvaluations) {
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  /**
   * Sets {@code lambda}, the ratio of feasible solutions above which the algorithm is in stage B.
   *
   * @param lambda the threshold, in [0, 1]
   * @return this builder
   */
  public CMOEAMSBuilder<S> setLambda(double lambda) {
    CMOEAMS.checkLambda(lambda);
    this.lambda = lambda;
    return this;
  }

  /**
   * Sets the evaluator of the solutions.
   *
   * @param evaluator the evaluator
   * @return this builder
   */
  public CMOEAMSBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    Check.notNull(evaluator, "evaluator");
    this.evaluator = evaluator;
    return this;
  }

  @Override
  public CMOEAMS<S> build() {
    return new CMOEAMS<>(
        problem, populationSize, maxEvaluations, lambda, crossover, mutation, evaluator);
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public CrossoverOperator<S> getCrossover() {
    return crossover;
  }

  public MutationOperator<S> getMutation() {
    return mutation;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public double getLambda() {
    return lambda;
  }

  public SolutionListEvaluator<S> getSolutionListEvaluator() {
    return evaluator;
  }
}
