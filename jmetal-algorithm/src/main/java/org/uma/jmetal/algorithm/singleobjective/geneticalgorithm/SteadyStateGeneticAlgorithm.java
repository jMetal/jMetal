package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class SteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
  private Comparator<S> comparator;
  private int maxEvaluations;
  private int populationSize;
  private int evaluations;

  private Problem<S> problem;

  /**
   * Constructor
   */
  public SteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator) {
    this.problem = problem;
    this.maxEvaluations = maxEvaluations;
    this.populationSize = populationSize;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    comparator = new ObjectiveComparator<S>(0);
  }

  @Override protected boolean isStoppingConditionReached() {
    return (evaluations >= maxEvaluations);
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    Collections.sort(population, comparator) ;
    int worstSolutionIndex = population.size() - 1;
    if (comparator.compare(population.get(worstSolutionIndex), offspringPopulation.get(0)) > 0) {
      population.remove(worstSolutionIndex);
      population.add(offspringPopulation.get(0));
    }

    return population;
  }

  @Override protected List<S> reproduction(List<S> matingPopulation) {
    List<S> offspringPopulation = new ArrayList<>(1);

    List<S> parents = new ArrayList<>(2);
    parents.add(matingPopulation.get(0));
    parents.add(matingPopulation.get(1));

    List<S> offspring = crossoverOperator.execute(parents);
    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(2);
    for (int i = 0; i < 2; i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add(solution);
    }

    return matingPopulation;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    for (S solution : population) {
      problem.evaluate(solution);
    }

    return population;
  }

  @Override public S getResult() {
    Collections.sort(getPopulation(), comparator) ;
    return getPopulation().get(0);
  }

  @Override public void initProgress() {
    evaluations = 1;
  }

  @Override public void updateProgress() {
    evaluations++;
  }
}
