package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;

/**
 * Created by ajnebro on 26/10/14.
 */
public class SteadyStateGeneticAlgorithmBuilder {
  private Problem problem;
  private int maxEvaluations;
  private int populationSize;
  private CrossoverOperator crossoverOperator;
  private MutationOperator mutationOperator;
  private SelectionOperator selectionOperator;

  /**
   * Builder constructor
   */
  public SteadyStateGeneticAlgorithmBuilder(Problem problem) {
    this.problem = problem;
    maxEvaluations = 250;
    populationSize = 100;
  }

  public SteadyStateGeneticAlgorithmBuilder setMaxEvaluations(int maxIterations) {
    this.maxEvaluations = maxIterations;

    return this;
  }

  public SteadyStateGeneticAlgorithmBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public SteadyStateGeneticAlgorithmBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    this.crossoverOperator = crossoverOperator;

    return this;
  }

  public SteadyStateGeneticAlgorithmBuilder setMutationOperator(MutationOperator mutationOperator) {
    this.mutationOperator = mutationOperator;

    return this;
  }

  public SteadyStateGeneticAlgorithmBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    this.selectionOperator = selectionOperator;

    return this;
  }

  public SteadyStateGeneticAlgorithm build() {
    return new SteadyStateGeneticAlgorithm(problem, maxEvaluations, populationSize,
        crossoverOperator, mutationOperator, selectionOperator);
  }
}
