package org.uma.jmetal.algorithm.multiobjective.ibea;

import java.util.List;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Builder for the IBEA family of algorithms.
 *
 * <p>The default configuration builds the plain {@link IBEA} variant and uses a fitness-based
 * binary tournament selection. Use {@link #setVariant(IBEAVariant)} to opt in to {@link mIBEA}.
 */
public class IBEABuilder implements AlgorithmBuilder<IBEA<DoubleSolution>> {
  public enum IBEAVariant {IBEA, MIBEA}

  private Problem<DoubleSolution> problem;
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;

  private CrossoverOperator<DoubleSolution> crossover;
  private MutationOperator<DoubleSolution> mutation;
  private SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
  private IBEAVariant variant;

  /**
   * Constructor.
   *
   * @param problem the problem to solve
   */
  public IBEABuilder(Problem<DoubleSolution> problem) {
    this.problem = problem;
    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    selection = new BinaryTournamentSelection<>(new FitnessComparator<>());
    variant = IBEAVariant.IBEA;
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator<DoubleSolution> getCrossover() {
    return crossover;
  }

  public MutationOperator<DoubleSolution> getMutation() {
    return mutation;
  }

  public SelectionOperator<List<DoubleSolution>, DoubleSolution> getSelection() {
    return selection;
  }

  /**
   * Returns the configured IBEA variant.
   *
   * @return the configured variant
   */
  public IBEAVariant getVariant() {
    return variant;
  }

  /* Setters */
  public IBEABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public IBEABuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;

    return this;
  }

  public IBEABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IBEABuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
    this.crossover = crossover;

    return this;
  }

  public IBEABuilder setMutation(MutationOperator<DoubleSolution> mutation) {
    this.mutation = mutation;

    return this;
  }

  public IBEABuilder setSelection(SelectionOperator<List<DoubleSolution>, DoubleSolution> selection) {
    this.selection = selection;

    return this;
  }

  /**
   * Sets the IBEA variant to build.
   *
   * @param variant the variant to build
   * @return this builder
   */
  public IBEABuilder setVariant(IBEAVariant variant) {
    this.variant = variant;

    return this;
  }

  /**
   * Builds the configured IBEA variant.
   *
   * @return the configured algorithm instance
   */
  public IBEA<DoubleSolution> build() {
    if (variant == null) {
      throw new JMetalException("Unknown variant: null");
    }

    return switch (variant) {
      case IBEA -> new IBEA<>(
          problem, populationSize, archiveSize, maxEvaluations, selection, crossover, mutation);
      case MIBEA -> new mIBEA<>(
          problem, populationSize, archiveSize, maxEvaluations, selection, crossover, mutation);
      default -> throw new JMetalException("Unknown variant: " + variant);
    };
  }
}
