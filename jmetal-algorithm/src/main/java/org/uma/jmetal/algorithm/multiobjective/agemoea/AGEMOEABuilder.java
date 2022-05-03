package org.uma.jmetal.algorithm.multiobjective.agemoea;

import java.util.List;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.agemoea.util.SurvivalScoreComparator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;


/** Builder class */
public class AGEMOEABuilder<S extends Solution<?>> implements AlgorithmBuilder<AGEMOEA<S>>{
  // no access modifier means access from classes within the same package
  private Problem<S> problem ;
  private int maxIterations ;
  private int populationSize ;
  private CrossoverOperator<S> crossoverOperator ;
  private MutationOperator<S> mutationOperator ;
  private SelectionOperator<List<S>, S> selectionOperator ;

  private SolutionListEvaluator<S> evaluator ;

  /** Builder constructor */
  public AGEMOEABuilder(Problem<S> problem) {
    this.problem = problem ;
    maxIterations = 250 ;
    populationSize = 100 ;
    evaluator = new SequentialSolutionListEvaluator<S>() ;
    selectionOperator = new BinaryTournamentSelection<>(new SurvivalScoreComparator<>());
  }

  public AGEMOEABuilder<S> setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public AGEMOEABuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public AGEMOEABuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public AGEMOEABuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public AGEMOEABuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator ;

    return this ;
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

  public AGEMOEA<S> build() {
    return new AGEMOEA<>(this) ;
  }
}
