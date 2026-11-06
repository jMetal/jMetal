package org.uma.jmetal.algorithm.multiobjective.top;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Builder of {@link ToP}. The default values are those of the ToP of PlatEMO: a population of 100
 * solutions, CR = 1.0 and F = 0.5 in the differential evolution, and the polynomial mutation with
 * probability 1/n (n being the number of variables) and distribution index 20; the maximum number
 * of evaluations is 25000 and the solutions are evaluated sequentially.
 */
public class ToPBuilder implements AlgorithmBuilder<ToP> {
  /** Default population size. */
  public static final int DEFAULT_POPULATION_SIZE = 100;

  /** Default maximum number of evaluations. */
  public static final int DEFAULT_MAX_EVALUATIONS = 25000;

  /** Default crossover rate of the differential evolution. */
  public static final double DEFAULT_CR = 1.0;

  /** Default scale factor of the differential evolution. */
  public static final double DEFAULT_F = 0.5;

  /** Default distribution index of the polynomial mutation. */
  public static final double DEFAULT_MUTATION_DISTRIBUTION_INDEX = 20.0;

  private final Problem<DoubleSolution> problem;
  private int populationSize;
  private int maxEvaluations;
  private double cr;
  private double f;
  private MutationOperator<DoubleSolution> mutationOperator;
  private SolutionListEvaluator<DoubleSolution> evaluator;

  /**
   * Creates a builder with the default values.
   *
   * @param problem the problem to solve
   */
  public ToPBuilder(Problem<DoubleSolution> problem) {
    Check.notNull(problem, "problem");
    this.problem = problem;
    this.populationSize = DEFAULT_POPULATION_SIZE;
    this.maxEvaluations = DEFAULT_MAX_EVALUATIONS;
    this.cr = DEFAULT_CR;
    this.f = DEFAULT_F;
    this.mutationOperator =
        new PolynomialMutation(
            1.0 / problem.numberOfVariables(), DEFAULT_MUTATION_DISTRIBUTION_INDEX);
    this.evaluator = new SequentialSolutionListEvaluator<>();
  }

  /**
   * Sets the population size.
   *
   * @param populationSize a positive number of solutions
   * @return this builder
   */
  public ToPBuilder setPopulationSize(int populationSize) {
    Check.valueIsPositive(populationSize, "populationSize");
    this.populationSize = populationSize;
    return this;
  }

  /**
   * Sets the maximum number of evaluations. The run ends at the end of the generation in which
   * this number is reached.
   *
   * @param maxEvaluations a positive number of evaluations
   * @return this builder
   */
  public ToPBuilder setMaxEvaluations(int maxEvaluations) {
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  /**
   * Sets the crossover rate CR of the differential evolution of both phases.
   *
   * @param cr a value in [0, 1]
   * @return this builder
   */
  public ToPBuilder setCr(double cr) {
    Check.probabilityIsValid(cr, "cr");
    this.cr = cr;
    return this;
  }

  /**
   * Sets the scale factor F of the differential evolution of both phases.
   *
   * @param f a positive and finite value
   * @return this builder
   */
  public ToPBuilder setF(double f) {
    Check.valueIsPositive(f, "f");
    Check.valueIsFinite(f, "f");
    this.f = f;
    return this;
  }

  /**
   * Sets the mutation applied to each trial vector, after clamping it to the bounds.
   *
   * @param mutationOperator the mutation operator
   * @return this builder
   */
  public ToPBuilder setMutationOperator(MutationOperator<DoubleSolution> mutationOperator) {
    Check.notNull(mutationOperator, "mutationOperator");
    this.mutationOperator = mutationOperator;
    return this;
  }

  /**
   * Sets the evaluator of the solutions.
   *
   * @param evaluator the evaluator
   * @return this builder
   */
  public ToPBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
    Check.notNull(evaluator, "evaluator");
    this.evaluator = evaluator;
    return this;
  }

  @Override
  public ToP build() {
    return new ToP(this);
  }

  public Problem<DoubleSolution> getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public double getCr() {
    return cr;
  }

  public double getF() {
    return f;
  }

  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  public SolutionListEvaluator<DoubleSolution> getSolutionListEvaluator() {
    return evaluator;
  }
}
