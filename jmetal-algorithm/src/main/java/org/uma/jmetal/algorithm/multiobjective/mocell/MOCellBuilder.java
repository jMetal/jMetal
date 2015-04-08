package org.uma.jmetal.algorithm.multiobjective.mocell;

import org.uma.jmetal.algorithm.Algorithm;
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
 * Created by juanjo
 *  */
public class MOCellBuilder<S extends Solution> implements AlgorithmBuilder {
  public enum MOCellVariant {MOCell, SteadyStateMOCell, Measures}

  /**
   * MOCellBuilder class
   */
  private final Problem<S> problem;
  private int maxIterations;
  private int populationSize;
  private CrossoverOperator<List<S>, List<S>>  crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;

  /**
   * MOCellBuilder constructor
   */
  public MOCellBuilder(Problem<S> problem, CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem;
    maxIterations = 250;
    populationSize = 100;
    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    selectionOperator = new BinaryTournamentSelection();
    evaluator = new SequentialSolutionListEvaluator();
  }

  public MOCellBuilder setMaxIterations(int maxIterations) {
    if (maxIterations < 0) {
      throw new JMetalException("maxIterations is negative: " + maxIterations);
    }
    this.maxIterations = maxIterations;

    return this;
  }

  public MOCellBuilder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public MOCellBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    if (selectionOperator == null) {
      throw new JMetalException("selectionOperator is null");
    }
    this.selectionOperator = selectionOperator;

    return this;
  }

  public MOCellBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null");
    }
    this.evaluator = evaluator;

    return this;
  }

  public Algorithm<List<S>> build() {
    Algorithm<List<S>> algorithm = new MOCell<S>(problem, maxIterations, populationSize, crossoverOperator,
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
