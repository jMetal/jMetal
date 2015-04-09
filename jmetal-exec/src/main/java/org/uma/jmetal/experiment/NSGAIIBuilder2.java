package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.SteadyStateNSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Created by ajnebro on 16/11/14.
 */
public class NSGAIIBuilder2<S extends Solution, P extends Problem<S>> implements AlgorithmBuilder2<S,P> {

  public enum NSGAIIVariant2 {NSGAII, SteadyStateNSGAII, Measures}

  /**
   * NSGAIIBuilder class
   */
  private P problem;
  private int maxIterations;
  private int populationSize;
  private CrossoverOperator<List<S>, List<S>>  crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;

  private NSGAIIVariant2 variant;

  /**
   * NSGAIIBuilder constructor
   */
  public NSGAIIBuilder2(CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator, NSGAIIVariant2 variant) {
    this.problem = null ;
    maxIterations = 250;
    populationSize = 100;
    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    selectionOperator = new BinaryTournamentSelection();
    evaluator = new SequentialSolutionListEvaluator();

    this.variant = variant ;
  }

  public NSGAIIBuilder2 setMaxIterations(int maxIterations) {
    if (maxIterations < 0) {
      throw new JMetalException("maxIterations is negative: " + maxIterations);
    }
    this.maxIterations = maxIterations;

    return this;
  }

  public NSGAIIBuilder2 setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public NSGAIIBuilder2 setSelectionOperator(SelectionOperator selectionOperator) {
    if (selectionOperator == null) {
      throw new JMetalException("selectionOperator is null");
    }
    this.selectionOperator = selectionOperator;

    return this;
  }

  public NSGAIIBuilder2 setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null");
    }
    this.evaluator = evaluator;

    return this;
  }

  public Algorithm<List<S>> build(P problem) {
    this.problem = problem ;
    Algorithm<List<S>> algorithm = null ;
    if (variant.equals(NSGAIIVariant2.NSGAII)) {
      algorithm = new NSGAII<S>(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    } else if (variant.equals(NSGAIIVariant2.SteadyStateNSGAII)) {
      algorithm = new SteadyStateNSGAII<S>(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    } else if (variant.equals(NSGAIIVariant2.Measures)) {
      algorithm = new NSGAIIMeasures(problem, maxIterations, populationSize, crossoverOperator,
          mutationOperator, selectionOperator, evaluator);
    }

    return algorithm ;
  }

  /* Getters */
  public P getProblem() {
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
