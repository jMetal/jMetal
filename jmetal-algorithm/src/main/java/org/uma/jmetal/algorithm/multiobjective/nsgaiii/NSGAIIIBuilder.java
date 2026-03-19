package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import java.util.List;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Builder class for NSGA-III algorithm.
 * 
 * <p>
 * Supports both single-layer and two-layer reference point generation.
 * For problems with many objectives (>5), two-layer reference points are
 * recommended.
 */
public class NSGAIIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGAIII<S>> {
  // no access modifier means access from classes within the same package
  private Problem<S> problem;
  private int maxIterations;
  private int numberOfDivisions;
  private int secondLayerDivisions; // For two-layer reference points (0 = disabled)
  private CrossoverOperator<S> crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator<List<S>, S> selectionOperator;

  private SolutionListEvaluator<S> evaluator;

  /** Builder constructor */
  public NSGAIIIBuilder(Problem<S> problem) {
    this.problem = problem;
    maxIterations = 250;
    numberOfDivisions = 12;
    secondLayerDivisions = 0; // Disabled by default
    evaluator = new SequentialSolutionListEvaluator<S>();
  }

  public NSGAIIIBuilder<S> setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
    return this;
  }

  public NSGAIIIBuilder<S> setNumberOfDivisions(int numberOfDivisions) {
    this.numberOfDivisions = numberOfDivisions;
    return this;
  }

  /**
   * Sets the number of divisions for the second layer of reference points.
   * Use this for problems with many objectives (>5) as recommended in the
   * NSGA-III paper.
   * Set to 0 (default) to use single-layer reference points.
   *
   * @param secondLayerDivisions number of divisions for the inner layer (0 to
   *                             disable)
   * @return the builder instance
   */
  public NSGAIIIBuilder<S> setSecondLayerDivisions(int secondLayerDivisions) {
    this.secondLayerDivisions = secondLayerDivisions;
    return this;
  }

  public NSGAIIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;
    return this;
  }

  public NSGAIIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator;
    return this;
  }

  public NSGAIIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    this.selectionOperator = selectionOperator;
    return this;
  }

  public NSGAIIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator;
    return this;
  }

  public SolutionListEvaluator<S> getEvaluator() {
    return evaluator;
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public int getNumberOfDivisions() {
    return numberOfDivisions;
  }

  public int getSecondLayerDivisions() {
    return secondLayerDivisions;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }

  public NSGAIII<S> build() {
    if (crossoverOperator == null) {
      throw new JMetalException("Crossover operator is not set");
    }
    if (mutationOperator == null) {
      throw new JMetalException("Mutation operator is not set");
    }
    if (selectionOperator == null) {
      throw new JMetalException("Selection operator is not set");
    }
    return new NSGAIII<>(this);
  }
}
