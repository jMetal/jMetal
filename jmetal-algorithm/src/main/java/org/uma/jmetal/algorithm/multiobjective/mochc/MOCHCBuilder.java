package org.uma.jmetal.algorithm.multiobjective.mochc;

/**
 * Created by ajnebro on 21/11/14.
 */

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Builder class
 */
public class MOCHCBuilder implements AlgorithmBuilder {
  BinaryProblem problem;
  SolutionListEvaluator evaluator;
  int populationSize;
  int maxEvaluations;
  int convergenceValue;
  double preservedPopulation;
  double initialConvergenceCount;
  CrossoverOperator crossoverOperator;
  MutationOperator cataclysmicMutation;
  SelectionOperator parentSelection;
  SelectionOperator newGenerationSelection;

  public MOCHCBuilder(BinaryProblem problem) {
    this.problem = problem;
    evaluator = new SequentialSolutionListEvaluator() ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;
    convergenceValue = 3 ;
    preservedPopulation = 0.05 ;
  }

  /* Getters */
  public BinaryProblem getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluation() {
    return maxEvaluations;
  }

  public double getInitialConvergenceCount() {
    return initialConvergenceCount;
  }

  public int getConvergenceValue() {
    return convergenceValue;
  }

  public CrossoverOperator getCrossover() {
    return crossoverOperator;
  }

  public MutationOperator getCataclysmicMutation() {
    return cataclysmicMutation;
  }

  public SelectionOperator getParentSelection() {
    return parentSelection;
  }

  public SelectionOperator getNewGenerationSelection() {
    return newGenerationSelection;
  }

  public double getPreservedPopulation() {
    return preservedPopulation;
  }

  /* Setters */
  public MOCHCBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public MOCHCBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public MOCHCBuilder setConvergenceValue(int convergenceValue) {
    this.convergenceValue = convergenceValue;

    return this;
  }

  public MOCHCBuilder setInitialConvergenceCount(double initialConvergenceCount) {
    this.initialConvergenceCount = initialConvergenceCount;

    return this;
  }

  public MOCHCBuilder setPreservedPopulation(double preservedPopulation) {
    this.preservedPopulation = preservedPopulation;

    return this;
  }

  public MOCHCBuilder setCrossover(CrossoverOperator crossover) {
    this.crossoverOperator = crossover;

    return this;
  }

  public MOCHCBuilder setCataclysmicMutation(MutationOperator cataclysmicMutation) {
    this.cataclysmicMutation = cataclysmicMutation;

    return this;
  }

  public MOCHCBuilder setParentSelection(SelectionOperator parentSelection) {
    this.parentSelection = parentSelection;

    return this;
  }

  public MOCHCBuilder setNewGenerationSelection(SelectionOperator newGenerationSelection) {
    this.newGenerationSelection = newGenerationSelection;

    return this;
  }

  public MOCHCBuilder setEvaluator(SolutionListEvaluator evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public MOCHC build() {
    MOCHC algorithm =
        new MOCHC(problem, populationSize, maxEvaluations, convergenceValue, preservedPopulation,
            initialConvergenceCount, crossoverOperator, cataclysmicMutation, newGenerationSelection,
            parentSelection, evaluator);

    return algorithm;
  }
}
