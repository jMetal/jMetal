package org.uma.jmetal.algorithm.multiobjective.mochc;

/**
 * Created by ajnebro on 21/11/14.
 */

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Builder class
 */
public class MOCHCBuilder implements AlgorithmBuilder<MOCHC> {
  BinaryProblem problem;
  SolutionListEvaluator<BinarySolution> evaluator;
  int populationSize;
  int maxEvaluations;
  int convergenceValue;
  double preservedPopulation;
  double initialConvergenceCount;
  CrossoverOperator<BinarySolution> crossoverOperator;
  MutationOperator<BinarySolution> cataclysmicMutation;
  SelectionOperator<List<BinarySolution>, BinarySolution> parentSelection;
  SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;

  public MOCHCBuilder(BinaryProblem problem) {
    this.problem = problem;
    evaluator = new SequentialSolutionListEvaluator<BinarySolution>() ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;
    convergenceValue = 3 ;
    preservedPopulation = 0.05 ;
    initialConvergenceCount = 0.25 ;
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

  public CrossoverOperator<BinarySolution> getCrossover() {
    return crossoverOperator;
  }

  public MutationOperator<BinarySolution> getCataclysmicMutation() {
    return cataclysmicMutation;
  }

  public SelectionOperator<List<BinarySolution>,BinarySolution> getParentSelection() {
    return parentSelection;
  }

  public SelectionOperator<List<BinarySolution>, List<BinarySolution>> getNewGenerationSelection() {
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

  public MOCHCBuilder setCrossover(CrossoverOperator<BinarySolution> crossover) {
    this.crossoverOperator = crossover;

    return this;
  }

  public MOCHCBuilder setCataclysmicMutation(MutationOperator<BinarySolution> cataclysmicMutation) {
    this.cataclysmicMutation = cataclysmicMutation;

    return this;
  }

  public MOCHCBuilder setParentSelection(SelectionOperator<List<BinarySolution>, BinarySolution> parentSelection) {
    this.parentSelection = parentSelection;

    return this;
  }

  public MOCHCBuilder setNewGenerationSelection(SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection) {
    this.newGenerationSelection = newGenerationSelection;

    return this;
  }

  public MOCHCBuilder setEvaluator(SolutionListEvaluator<BinarySolution> evaluator) {
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
