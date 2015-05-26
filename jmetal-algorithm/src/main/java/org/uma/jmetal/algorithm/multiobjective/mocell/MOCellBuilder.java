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
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;

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
  private int maxEvaluations;
  private int populationSize;
  private int archiveSize ;
  private CrossoverOperator<List<S>, List<S>>  crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;
  private Neighborhood<S> neighborhood ;

  /**
   * MOCellBuilder constructor
   */
  public MOCellBuilder(Problem<S> problem, CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem;
    maxEvaluations = 25000;
    populationSize = 100;
    archiveSize = 100 ;
    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    selectionOperator = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());
    this.neighborhood = new C9((int)Math.sqrt(populationSize), (int)Math.sqrt(populationSize)) ;
    evaluator = new SequentialSolutionListEvaluator();
  }

  public MOCellBuilder setMaxEvaluations(int maxEvaluations) {
    if (maxEvaluations < 0) {
      throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
    }
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public MOCellBuilder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public MOCellBuilder setArchiveSize(int archiveSize) {
    if (archiveSize < 0) {
      throw new JMetalException("archive size is negative: " + populationSize);
    }

    this.archiveSize = archiveSize;

    return this;
  }

  public MOCellBuilder setNeighborhood(Neighborhood<S> neighborhood) {
    this.neighborhood = neighborhood;

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
    Algorithm<List<S>> algorithm = new MOCell<S>(problem, maxEvaluations, populationSize,
        archiveSize, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    
    return algorithm ;
  }

  /* Getters */
  public Problem getProblem() {
    return problem;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize ;
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
