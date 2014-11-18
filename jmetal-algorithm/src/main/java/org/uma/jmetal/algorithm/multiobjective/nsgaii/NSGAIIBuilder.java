package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by ajnebro on 16/11/14.
 */
public class NSGAIIBuilder {
  /** NSGAIIBuilder class */
  public Problem problem ;
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
    evaluator = new SequentialSolutionListEvaluator() ;
  }

  public NSGAIIBuilder setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public NSGAIIBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public NSGAIIBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public NSGAIIBuilder setMutationOperator(MutationOperator mutationOperator) {
    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public NSGAIIBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    this.selectionOperator = selectionOperator ;

    return this ;
  }

  public NSGAIIBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public NSGAII build() {
    return new NSGAII(this) ;
  }
}
