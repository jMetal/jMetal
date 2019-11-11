package org.uma.jmetal.algorithm.multiobjective.ibea;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/** This class implements the IBEA algorithm */
public class IBEABuilder<S extends Solution<?>> implements AlgorithmBuilder<IBEA<S>> {
  private Problem<S> problem;
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;

  private CrossoverOperator<S> crossover;
  private MutationOperator<S> mutation;
  private SelectionOperator<List<S>, S> selection;

  /**
   * Constructor
   *
   * @param problem
   */
  public IBEABuilder(
      Problem<S> problem, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    this.problem = problem;
    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;

    this.crossover = crossover;
    this.mutation = mutation;

    selection = new BinaryTournamentSelection<S>();
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator<S> getCrossover() {
    return crossover;
  }

  public MutationOperator<S> getMutation() {
    return mutation;
  }

  public SelectionOperator<List<S>, S> getSelection() {
    return selection;
  }

  /* Setters */
  public IBEABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public IBEABuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;

    return this;
  }

  public IBEABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IBEABuilder setCrossover(CrossoverOperator<S> crossover) {
    this.crossover = crossover;

    return this;
  }

  public IBEABuilder setMutation(MutationOperator<S> mutation) {
    this.mutation = mutation;

    return this;
  }

  public IBEABuilder setSelection(SelectionOperator<List<S>, S> selection) {
    this.selection = selection;

    return this;
  }

  public Problem<S> getProblem() {
    return problem ;
  }

  public IBEA<S> build() {
    return new IBEA<S>(
        problem, populationSize, archiveSize, maxEvaluations, selection, crossover, mutation);
  }
}
