package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Builder class
 */
public class NSGAIIIBuilder implements AlgorithmBuilder {

  // no access modifier means access from classes within the same package
  Problem problem;
  int maxEvaluations;
  int populationSize;
  CrossoverOperator crossoverOperator;
  MutationOperator mutationOperator;
  SelectionOperator selectionOperator;
  SolutionListEvaluator evaluator;
  int divisions;

  /**
   * Builder constructor
   */
  public NSGAIIIBuilder(Problem problem) {
    this.problem = problem;
    maxEvaluations = 25000;
    populationSize = 100;
    evaluator = new SequentialSolutionListEvaluator();
  }

  public NSGAIIIBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public NSGAIIIBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public NSGAIIIBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
    this.crossoverOperator = crossoverOperator;

    return this;
  }

  public NSGAIIIBuilder setMutationOperator(MutationOperator mutationOperator) {
    this.mutationOperator = mutationOperator;

    return this;
  }

  public NSGAIIIBuilder setSelectionOperator(SelectionOperator selectionOperator) {
    this.selectionOperator = selectionOperator;

    return this;
  }

  public NSGAIIIBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public NSGAIIIBuilder setDivisions(int div) {
    this.divisions = div;
    return this;
  }

  public int getDivisions() {
    return this.divisions;
  }

  public NSGAIII build() {
    return new NSGAIII(this);
  }
}
