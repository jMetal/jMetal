package org.uma.jmetal.algorithm.multiobjective.ctaea;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Builder of {@link CTAEA}. The defaults are a population size of 100 (the requested number of
 * weight vectors, as the default population size of PlatEMO; the actual population size is the
 * number of vectors generated, e.g. 91 with three objectives), 25000 evaluations and a {@link
 * SequentialSolutionListEvaluator}.
 *
 * @param <S> the type of the solutions
 */
public class CTAEABuilder<S extends Solution<?>> implements AlgorithmBuilder<CTAEA<S>> {
  /** Default requested population size (number of weight vectors). */
  public static final int DEFAULT_POPULATION_SIZE = 100;

  /** Default maximum number of evaluations. */
  public static final int DEFAULT_MAX_EVALUATIONS = 25000;

  private final Problem<S> problem;
  private final CrossoverOperator<S> crossoverOperator;
  private final MutationOperator<S> mutationOperator;
  private int populationSize;
  private int maxEvaluations;
  private SolutionListEvaluator<S> evaluator;

  /**
   * Creates the builder.
   *
   * @param problem the problem to solve (at least two objectives)
   * @param crossoverOperator a crossover of two parents; only its first child is used
   * @param mutationOperator the mutation applied to the child
   */
  public CTAEABuilder(
      Problem<S> problem,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    Check.notNull(problem, "problem");
    Check.notNull(crossoverOperator, "crossoverOperator");
    Check.notNull(mutationOperator, "mutationOperator");
    Check.that(
        problem.numberOfObjectives() >= 2, "The problem must have at least two objectives");
    Check.that(
        crossoverOperator.numberOfRequiredParents() == 2,
        "The crossover must require two parents");

    this.problem = problem;
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.populationSize = DEFAULT_POPULATION_SIZE;
    this.maxEvaluations = DEFAULT_MAX_EVALUATIONS;
    this.evaluator = new SequentialSolutionListEvaluator<>();
  }

  /**
   * Sets the requested population size, which is the requested number of weight vectors.
   *
   * @param populationSize the requested population size (at least 1)
   * @return this builder
   */
  public CTAEABuilder<S> setPopulationSize(int populationSize) {
    Check.that(populationSize >= 1, "The population size must be at least 1: " + populationSize);
    this.populationSize = populationSize;
    return this;
  }

  /**
   * Sets the maximum number of evaluations.
   *
   * @param maxEvaluations the maximum number of evaluations (at least 1)
   * @return this builder
   */
  public CTAEABuilder<S> setMaxEvaluations(int maxEvaluations) {
    Check.that(
        maxEvaluations >= 1,
        "The maximum number of evaluations must be at least 1: " + maxEvaluations);
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  /**
   * Sets the evaluator of the solutions.
   *
   * @param evaluator the evaluator
   * @return this builder
   */
  public CTAEABuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    Check.notNull(evaluator, "evaluator");
    this.evaluator = evaluator;
    return this;
  }

  @Override
  public CTAEA<S> build() {
    return new CTAEA<>(
        problem, populationSize, maxEvaluations, crossoverOperator, mutationOperator, evaluator);
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public SolutionListEvaluator<S> getSolutionListEvaluator() {
    return evaluator;
  }
}
