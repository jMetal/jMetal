package org.uma.jmetal.algorithm.multiobjective.ccmo;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMO.VariationType;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Builder of {@link CCMO}. The defaults are a population size of 100 (for each of the two
 * populations), 25000 evaluations, the {@link VariationType#GA} variation and a {@link
 * SequentialSolutionListEvaluator}.
 *
 * @param <S> the type of the solutions
 */
public class CCMOBuilder<S extends Solution<?>> implements AlgorithmBuilder<CCMO<S>> {
  private final Problem<S> problem;
  private final CrossoverOperator<S> crossover;
  private final MutationOperator<S> mutation;
  private int populationSize;
  private int maxEvaluations;
  private VariationType variationType;
  private SolutionListEvaluator<S> evaluator;

  /**
   * Creates a builder.
   *
   * @param problem the problem to solve
   * @param crossover the crossover operator (a {@link
   *     org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover} with {@link
   *     VariationType#DE})
   * @param mutation the mutation operator
   */
  public CCMOBuilder(
      Problem<S> problem, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    Check.notNull(problem, "problem");
    Check.notNull(crossover, "crossover");
    Check.notNull(mutation, "mutation");
    this.problem = problem;
    this.crossover = crossover;
    this.mutation = mutation;
    this.populationSize = 100;
    this.maxEvaluations = 25000;
    this.variationType = VariationType.GA;
    this.evaluator = new SequentialSolutionListEvaluator<>();
  }

  /** Sets the size of each of the two populations (at least 2). */
  public CCMOBuilder<S> setPopulationSize(int populationSize) {
    Check.that(populationSize >= 2, "The population size must be at least 2: " + populationSize);
    this.populationSize = populationSize;
    return this;
  }

  /** Sets the maximum number of evaluations (positive). */
  public CCMOBuilder<S> setMaxEvaluations(int maxEvaluations) {
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  /**
   * Sets the variation of the populations. {@link VariationType#DE} needs a {@link
   * org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover} as crossover, which is
   * checked when the algorithm is built.
   */
  public CCMOBuilder<S> setVariationType(VariationType variationType) {
    Check.notNull(variationType, "variationType");
    this.variationType = variationType;
    return this;
  }

  public CCMOBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    Check.notNull(evaluator, "evaluator");
    this.evaluator = evaluator;
    return this;
  }

  @Override
  public CCMO<S> build() {
    return new CCMO<>(
        problem, populationSize, maxEvaluations, crossover, mutation, variationType, evaluator);
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossover;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutation;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public VariationType getVariationType() {
    return variationType;
  }

  public SolutionListEvaluator<S> getSolutionListEvaluator() {
    return evaluator;
  }
}
