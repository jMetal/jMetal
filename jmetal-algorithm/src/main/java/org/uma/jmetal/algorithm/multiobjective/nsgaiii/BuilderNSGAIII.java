package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/** Builder class */
public class BuilderNSGAIII {
  
  // no access modifier means access from classes within the same package
  Problem problem ;
  int maxIterations ;
  int populationSize ;
  CrossoverOperator crossoverOperator ;
  MutationOperator mutationOperator ;
  SelectionOperator selectionOperator ;
  SolutionListEvaluator evaluator ;
  int divisions;
  
  /** Builder constructor */
  public BuilderNSGAIII(Problem problem) {
    this.problem = problem ;
    maxIterations = 250 ;
    populationSize = 100 ;
    evaluator = new SequentialSolutionListEvaluator() ;
  }

  public BuilderNSGAIII setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public BuilderNSGAIII setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public BuilderNSGAIII setCrossoverOperator(CrossoverOperator crossoverOperator) {
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public BuilderNSGAIII setMutationOperator(MutationOperator mutationOperator) {
    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public BuilderNSGAIII setSelectionOperator(SelectionOperator selectionOperator) {
    this.selectionOperator = selectionOperator ;

    return this ;
  }

  public BuilderNSGAIII setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public BuilderNSGAIII setDivisions(int div) {
	  this.divisions = div;
	  return this;
  }
  
  public int getDivisions() {
	  return this.divisions;
  }
  
  public NSGAIII build() {
    return new NSGAIII(this) ;
  }
}
