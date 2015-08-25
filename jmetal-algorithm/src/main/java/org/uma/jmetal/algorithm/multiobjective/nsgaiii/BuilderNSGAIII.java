package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;


/** Builder class */
public class BuilderNSGAIII <S extends Solution<?>> implements AlgorithmBuilder<NSGAIII<S>>{
  
  // no access modifier means access from classes within the same package
  Problem<S> problem ;
  int maxIterations ;
  int populationSize ;
  CrossoverOperator<S> crossoverOperator ;
  MutationOperator<S> mutationOperator ;
  SelectionOperator<List<S>, S> selectionOperator ;
  SolutionListEvaluator<S> evaluator ;
  
  /** Builder constructor */
  public BuilderNSGAIII(Problem<S> problem) {
    this.problem = problem ;
    maxIterations = 250 ;
    populationSize = 100 ;
    evaluator = new SequentialSolutionListEvaluator<S>() ;
  }

  public BuilderNSGAIII<S> setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public BuilderNSGAIII<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public BuilderNSGAIII<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public BuilderNSGAIII<S> setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public BuilderNSGAIII<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    this.selectionOperator = selectionOperator ;

    return this ;
  }

  public BuilderNSGAIII<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public NSGAIII<S> build() {
    return new NSGAIII<>(this) ;
  }
}
