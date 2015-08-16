package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Builder class
 */
public class NSGAIIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGAIII<S>> {

  // no access modifier means access from classes within the same package
  Problem<S> problem;
  int maxEvaluations;
  int populationSize;
  CrossoverOperator<S> crossoverOperator;
  MutationOperator<S> mutationOperator;
  SelectionOperator<List<S>, S> selectionOperator;
  SolutionListEvaluator<S> evaluator;
  int divisions;

  /**
   * Builder constructor
   */
  public NSGAIIIBuilder(Problem<S> problem) {
    this.problem = problem;
    maxEvaluations = 25000;
    populationSize = 100;
    evaluator = new SequentialSolutionListEvaluator<S>();
  }

  public NSGAIIIBuilder<S> setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public NSGAIIIBuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public NSGAIIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;

    return this;
  }

  public NSGAIIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator;

    return this;
  }

  public NSGAIIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    this.selectionOperator = selectionOperator;

    return this;
  }

  public NSGAIIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public NSGAIIIBuilder<S> setDivisions(int div) {
    this.divisions = div;
    return this;
  }

  public int getDivisions() {
    return this.divisions;
  }

  public NSGAIII<S> build() {
    return new NSGAIII<S>(this);
  }
}
