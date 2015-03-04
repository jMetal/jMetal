package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Created by ajnebro on 16/11/14.
 */
public class NSGAIIMeasuresBuilder implements AlgorithmBuilder {
  public enum NSGAIIVariant {NSGAII, SteadyStateNSGAII}

  /**
   * NSGAIIBuilder class
   */
  private final Problem problem;
  private int maxIterations;
  private int populationSize;
  private CrossoverOperator crossoverOperator;
  private MutationOperator mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;

  /**
   * NSGAIIBuilder constructor
   */
  public NSGAIIMeasuresBuilder(Problem problem, NSGAIIVariant variant) {
    this.problem = problem;
    maxIterations = 250;
    populationSize = 100;
    crossoverOperator = new SBXCrossover(0.9, 20.0);
    mutationOperator = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    selectionOperator = new BinaryTournamentSelection();
    evaluator = new SequentialSolutionListEvaluator();
  }

  /**
   * NSGAIIBuilder constructor
   */
  public NSGAIIMeasuresBuilder(Problem problem) {
    this(problem, NSGAIIVariant.NSGAII) ;
  }

  public NSGAIIMeasuresBuilder setMaxIterations(int maxIterations) {
    if (maxIterations < 0) {
      throw new JMetalException("maxIterations is negative: " + maxIterations);
    }
    this.maxIterations = maxIterations;

    return this;
  }

  public NSGAIIMeasuresBuilder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public NSGAIIMeasuresBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    if (crossoverOperator == null) {
      throw new JMetalException("crossoverOperator is null");
    }
    this.crossoverOperator = crossoverOperator;

    return this;
  }

  public NSGAIIMeasuresBuilder setMutationOperator(MutationOperator mutationOperator) {
    if (mutationOperator == null) {
      throw new JMetalException("mutationOperator is null");
    }

    this.mutationOperator = mutationOperator;

    return this;
  }

  public NSGAIIMeasuresBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    if (selectionOperator == null) {
      throw new JMetalException("selectionOperator is null");
    }
    this.selectionOperator = selectionOperator;

    return this;
  }

  public NSGAIIMeasuresBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null");
    }
    this.evaluator = evaluator;

    return this;
  }

  public Algorithm build() {
    Algorithm algorithm ;
    algorithm = new NSGAIIMeasures(problem, maxIterations, populationSize, crossoverOperator,
        mutationOperator, selectionOperator, evaluator);

    return algorithm ;
  }

  /* Getters */
  public Problem getProblem() {
    return problem;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public CrossoverOperator getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator getMutationOperator() {
    return mutationOperator;
  }

  public SelectionOperator getSelectionOperator() {
    return selectionOperator;
  }

  public SolutionListEvaluator getSolutionListEvaluator() {
    return evaluator;
  }
}
