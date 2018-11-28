package org.uma.jmetal.algorithm.multiobjective.ibea;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;

import java.util.List;

/**
 * This class implements the IBEA algorithm
 */
public class IBEABuilder implements AlgorithmBuilder<IBEA<DoubleSolution>> {
  private Problem<DoubleSolution> problem;
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;

  private CrossoverOperator<DoubleSolution> crossover;
  private MutationOperator<DoubleSolution> mutation;
  private SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

  /**
   * Constructor
   * @param problem
   */
  public IBEABuilder(Problem<DoubleSolution> problem) {
    this.problem = problem;
    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    selection = new BinaryTournamentSelection<DoubleSolution>();
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

  public CrossoverOperator<DoubleSolution> getCrossover() {
    return crossover;
  }

  public MutationOperator<DoubleSolution> getMutation() {
    return mutation;
  }

  public SelectionOperator<List<DoubleSolution>, DoubleSolution> getSelection() {
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

  public IBEABuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
    this.crossover = crossover;

    return this;
  }

  public IBEABuilder setMutation(MutationOperator<DoubleSolution> mutation) {
    this.mutation = mutation;

    return this;
  }

  public IBEABuilder setSelection(SelectionOperator<List<DoubleSolution>, DoubleSolution> selection) {
    this.selection = selection;

    return this;
  }

  public IBEA<DoubleSolution> build() {
    return new IBEA<DoubleSolution>(problem, populationSize, archiveSize, maxEvaluations, selection, crossover,
        mutation);
  }
}
