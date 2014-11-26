package org.uma.jmetal.algorithm.multiobjective.nsgaii;

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

/**
 * Created by ajnebro on 16/11/14.
 */
public class NSGAIIBuilder {
  /** NSGAIIBuilder class */
  public final Problem problem ;
  public int maxIterations ;
  public int populationSize ;
  public CrossoverOperator crossoverOperator ;
  public MutationOperator mutationOperator ;
  public SelectionOperator selectionOperator ;
  public SolutionListEvaluator evaluator ;

  /** NSGAIIBuilder constructor */
  public NSGAIIBuilder(Problem problem) {
    this.problem = problem ;
    maxIterations = 250 ;
    populationSize = 100 ;
    crossoverOperator = new SBXCrossover(0.9, 20.0) ;
    mutationOperator = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    selectionOperator = new BinaryTournamentSelection() ;
    evaluator = new SequentialSolutionListEvaluator() ;
  }

  public NSGAIIBuilder setMaxIterations(int maxIterations) {
    if (maxIterations < 0) {
      throw new JMetalException("maxIterations is negative: " + maxIterations) ;
    }
    this.maxIterations = maxIterations ;

    return this ;
  }

  public NSGAIIBuilder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: "  + populationSize) ;
    }

    this.populationSize = populationSize ;

    return this ;
  }

  public NSGAIIBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    if (crossoverOperator == null) {
      throw new JMetalException("crossoverOperator is null") ;
    }
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public NSGAIIBuilder setMutationOperator(MutationOperator mutationOperator) {
    if (mutationOperator == null) {
      throw new JMetalException("mutationOperator is null") ;
    }

    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public NSGAIIBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    if (selectionOperator == null) {
      throw new JMetalException("selectionOperator is null") ;
    }
    this.selectionOperator = selectionOperator ;

    return this ;
  }

  public NSGAIIBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null") ;
    }
    this.evaluator = evaluator ;

    return this ;
  }

  public NSGAII build() {
    return new NSGAII(problem, maxIterations, populationSize, crossoverOperator, mutationOperator,
            selectionOperator, evaluator) ;
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
