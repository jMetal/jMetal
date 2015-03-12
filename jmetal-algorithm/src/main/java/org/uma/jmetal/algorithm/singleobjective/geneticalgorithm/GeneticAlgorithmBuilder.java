package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by ajnebro on 10/12/14.
 */
public class GeneticAlgorithmBuilder {
  public enum GeneticAlgorithmVariant {GENERATIONAL, STEADY_STATE}
  /**
   * Builder class
   */
  private Problem problem;
  private int maxEvaluations;
  private int populationSize;
  private CrossoverOperator crossoverOperator;
  private MutationOperator mutationOperator;
  private SelectionOperator selectionOperator;
  private SolutionListEvaluator evaluator;

  private GeneticAlgorithmVariant variant ;

  /**
   * Builder constructor
   */
  public GeneticAlgorithmBuilder(Problem problem, GeneticAlgorithmVariant variant) {
    this.problem = problem;
    maxEvaluations = 25000;
    populationSize = 100;
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

  public GeneticAlgorithmBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    this.crossoverOperator = crossoverOperator;

    return this;
  }

  public GeneticAlgorithmBuilder setMutationOperator(MutationOperator mutationOperator) {
    this.mutationOperator = mutationOperator;

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

  public Algorithm build() {
    if (variant == GeneticAlgorithmVariant.GENERATIONAL) {
      return new GenerationalGeneticAlgorithm(problem, maxEvaluations, populationSize,
          crossoverOperator, mutationOperator, selectionOperator, evaluator);
    } else if (variant == GeneticAlgorithmVariant.STEADY_STATE) {
      return new SteadyStateGeneticAlgorithm(problem, maxEvaluations, populationSize,
          crossoverOperator, mutationOperator, selectionOperator);
    } else {
      throw new JMetalException("Unknown variant: " + variant) ;
    }
  }
}
