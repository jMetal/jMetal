package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.*;

/**
 * Created by ajnebro on 26/10/14.
 */
public class GenerationalGeneticAlgorithm extends AbstractGeneticAlgorithm<Solution, Solution> {
  private Comparator<Solution> comparator;
  private int maxIterations;
  private int populationSize;
  private int iterations;

  private Problem problem;

  private SolutionListEvaluator evaluator;

  /**
   * Constructor
   */
  public GenerationalGeneticAlgorithm(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    this.problem = problem;
    this.maxIterations = maxIterations;
    this.populationSize = populationSize;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;

    comparator = new ObjectiveComparator(0);
  }

  @Override protected boolean isStoppingConditionReached() {
    return (iterations >= maxIterations);
  }

  @Override protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<Solution> replacement(List population, List offspringPopulation) {
    Collections.sort(population, comparator);
    offspringPopulation.add(population.get(0));
    offspringPopulation.add(population.get(1));
    Collections.sort(offspringPopulation, comparator) ;
    offspringPopulation.remove(offspringPopulation.size() - 1);
    offspringPopulation.remove(offspringPopulation.size() - 1);

    return offspringPopulation;
  }

  @Override protected List<Solution> reproduction(List<Solution> matingPopulation) {
    List<Solution> offspringPopulation = new ArrayList<>(matingPopulation.size() + 2);
    for (int i = 0; i < populationSize; i += 2) {
      List<Solution> parents = new ArrayList<>(2);
      parents.add(matingPopulation.get(i));
      parents.add(matingPopulation.get(i + 1));

      List<Solution> offspring = crossoverOperator.execute(parents);
      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation;
  }

  @Override protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(population.size());
    for (int i = 0; i < populationSize; i++) {
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
    Collections.sort(getPopulation(), comparator) ;
    return getPopulation().get(0);
  }

  @Override public void initProgress() {
    iterations = 1;
  }

  @Override public void updateProgress() {
    iterations++;
  }
}
