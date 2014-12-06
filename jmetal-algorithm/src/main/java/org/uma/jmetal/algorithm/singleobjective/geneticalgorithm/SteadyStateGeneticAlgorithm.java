package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class SteadyStateGeneticAlgorithm extends AbstractGeneticAlgorithm<Solution, Solution> {
  private Comparator<Solution> comparator;
  private int maxEvaluations;
  private int populationSize;
  private int evaluations;

  private Problem<Solution> problem;

  /**
   * Constructor
   */
  private SteadyStateGeneticAlgorithm(Builder builder) {
    problem = builder.problem;
    maxEvaluations = builder.maxEvaluations;
    populationSize = builder.populationSize;

    crossoverOperator = builder.crossoverOperator;
    mutationOperator = builder.mutationOperator;
    selectionOperator = builder.selectionOperator;

    comparator = new ObjectiveComparator(0);
  }

  /**
   * Builder class
   */
  public static class Builder {
    private Problem problem;
    private int maxEvaluations;
    private int populationSize;
    private CrossoverOperator crossoverOperator;
    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;

    /**
     * Builder constructor
     */
    public Builder(Problem problem) {
      this.problem = problem;
      maxEvaluations = 250;
      populationSize = 100;
    }

    public Builder setMaxEvaluations(int maxIterations) {
      this.maxEvaluations = maxIterations;

      return this;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize;

      return this;
    }

    public Builder setCrossoverOperator(CrossoverOperator crossoverOperator) {
      this.crossoverOperator = crossoverOperator;

      return this;
    }

    public Builder setMutationOperator(MutationOperator mutationOperator) {
      this.mutationOperator = mutationOperator;

      return this;
    }

    public Builder setSelectionOperator(SelectionOperator selectionOperator) {
      this.selectionOperator = selectionOperator;

      return this;
    }

    public SteadyStateGeneticAlgorithm build() {
      return new SteadyStateGeneticAlgorithm(this);
    }

  }

  @Override protected boolean isStoppingConditionReached() {
    return (evaluations >= maxEvaluations);
  }

  @Override protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<Solution> replacement(List<Solution> population,
      List<Solution> offspringPopulation) {
    population.sort(comparator);
    int worstSolutionIndex = population.size() - 1;
    if (comparator.compare(population.get(worstSolutionIndex), offspringPopulation.get(0)) > 0) {
      population.remove(worstSolutionIndex);
      population.add(offspringPopulation.get(0));
    }

    return population;
  }

  @Override protected List<Solution> reproduction(List<Solution> matingPopulation) {
    List<Solution> offspringPopulation = new ArrayList<>(1);

    List<Solution> parents = new ArrayList<>(2);
    parents.add(matingPopulation.get(0));
    parents.add(matingPopulation.get(1));

    List<Solution> offspring = crossoverOperator.execute(parents);
    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(2);
    for (int i = 0; i < 2; i++) {
      Solution solution = selectionOperator.execute(population);
      matingPopulation.add(solution);
    }

    return matingPopulation;
  }

  @Override protected List<Solution> evaluatePopulation(List<Solution> population) {
    for (Solution solution : population) {
      problem.evaluate(solution);
    }

    return population;
  }

  @Override public Solution getResult() {
    getPopulation().sort(comparator);
    return getPopulation().get(0);
  }

  @Override public void initProgress() {
    evaluations = 1;
  }

  @Override public void updateProgress() {
    evaluations++;
  }
}
