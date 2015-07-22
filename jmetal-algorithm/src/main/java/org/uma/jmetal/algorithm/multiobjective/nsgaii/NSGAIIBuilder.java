package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGAII<S>> {
  public enum NSGAIIVariant {NSGAII, SteadyStateNSGAII, Measures}

  /**
   * NSGAIIBuilder class
   */
  private final Problem<S> problem;
  private int maxIterations;
  private int populationSize;
  private CrossoverOperator<S>  crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator<List<S>, S> selectionOperator;
  private SolutionListEvaluator<S> evaluator;

  private NSGAIIVariant variant;

  /**
   * NSGAIIBuilder constructor
   */
  public NSGAIIBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem;
    maxIterations = 250;
    populationSize = 100;
    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    selectionOperator = new BinaryTournamentSelection<S>();
    evaluator = new SequentialSolutionListEvaluator<S>();

    this.variant = NSGAIIVariant.NSGAII ;
  }

  public NSGAIIBuilder<S> setMaxIterations(int maxIterations) {
    if (maxIterations < 0) {
      throw new JMetalException("maxIterations is negative: " + maxIterations);
    }
    this.maxIterations = maxIterations;

    return this;
  }

  public NSGAIIBuilder<S> setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public NSGAIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    if (selectionOperator == null) {
      throw new JMetalException("selectionOperator is null");
    }
    this.selectionOperator = selectionOperator;

    return this;
  }

  public NSGAIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null");
    }
    this.evaluator = evaluator;

    return this;
  }


  public NSGAIIBuilder<S> setVariant(NSGAIIVariant variant) {
    this.variant = variant;

    return this;
  }

  public NSGAII<S> build() {
    NSGAII<S> algorithm = null ;
    if (variant.equals(NSGAIIVariant.NSGAII)) {
      algorithm = new NSGAII<S>(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    } else if (variant.equals(NSGAIIVariant.SteadyStateNSGAII)) {
      algorithm = new SteadyStateNSGAII<S>(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    } else if (variant.equals(NSGAIIVariant.Measures)) {
      algorithm = new NSGAIIMeasures<S>(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    }

    return algorithm ;
  }

  /* Getters */
  public Problem<S> getProblem() {
    return problem;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public int getPopulationSize() {
    return populationSize;
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

  public SolutionListEvaluator<S> getSolutionListEvaluator() {
    return evaluator;
  }
}
