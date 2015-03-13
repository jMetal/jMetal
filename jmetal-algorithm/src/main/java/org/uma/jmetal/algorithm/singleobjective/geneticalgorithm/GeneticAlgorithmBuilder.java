package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.Algorithm;
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
 * Created by ajnebro on 10/12/14.
 */
public class GeneticAlgorithmBuilder<S extends Solution> {
  public enum GeneticAlgorithmVariant {GENERATIONAL, STEADY_STATE}
  /**
   * Builder class
   */
  private Problem<S> problem;
  private int maxEvaluations;
  private int populationSize;
  private CrossoverOperator<List<S>, List<S>> crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;

  private GeneticAlgorithmVariant variant ;
  private SelectionOperator defaultSelectionOperator = new BinaryTournamentSelection() ;

  /**
   * Builder constructor
   */
  public GeneticAlgorithmBuilder(Problem<S> problem,
      CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator, GeneticAlgorithmVariant variant) {
    this.problem = problem;
    maxEvaluations = 25000;
    populationSize = 100;
    this.mutationOperator = mutationOperator ;
    this.crossoverOperator = crossoverOperator ;
    this.selectionOperator = defaultSelectionOperator ;

    evaluator = new SequentialSolutionListEvaluator();

    this.variant = variant ;
  }

  public GeneticAlgorithmBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public GeneticAlgorithmBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public GeneticAlgorithmBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    this.selectionOperator = selectionOperator;

    return this;
  }

  public GeneticAlgorithmBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public Algorithm<S> build() {
    if (variant == GeneticAlgorithmVariant.GENERATIONAL) {
      return new GenerationalGeneticAlgorithm<S>(problem, maxEvaluations, populationSize,
          crossoverOperator, mutationOperator, selectionOperator, evaluator);
    } else if (variant == GeneticAlgorithmVariant.STEADY_STATE) {
      return new SteadyStateGeneticAlgorithm<S>(problem, maxEvaluations, populationSize,
          crossoverOperator, mutationOperator, selectionOperator);
    } else {
      throw new JMetalException("Unknown variant: " + variant) ;
    }
  }
}
