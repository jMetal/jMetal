package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 30/10/14.
 */
public class NSGAII<S extends  Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected final int maxIterations;
  protected final int populationSize;

  protected final Problem<S> problem;

  protected final SolutionListEvaluator<S> evaluator;

  protected int iterations;

  /**
   * Constructor
   */
  public NSGAII(Problem<S> problem, int maxIterations, int populationSize,
      CrossoverOperator<List<S>, List<S>> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super() ;
    this.problem = problem;
    this.maxIterations = maxIterations;
    this.populationSize = populationSize;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
  }

  @Override protected void initProgress() {
    iterations = 1;
  }

  @Override protected void updateProgress() {
    iterations++;
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, problem);

    return population;
  }

  @Override protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(population.size());
    for (int i = 0; i < populationSize; i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add(solution);
    }

    return matingPopulation;
  }

  @Override protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i += 2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(i + 1));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    Ranking<S> ranking = computeRanking(jointPopulation);
    List<S> pop = crowdingDistanceSelection(ranking);

    return pop;
  }

  @Override public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation());
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<S>();
    ranking.computeRanking(solutionList);

    return ranking;
  }

  protected List<S> crowdingDistanceSelection(Ranking<S> ranking) {
    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    List<S> population = new ArrayList<>(populationSize);
    int rankingIndex = 0;
    while (populationIsNotFull(population)) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population;
  }

  protected boolean populationIsNotFull(List<S> population) {
    return population.size() < populationSize;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
    return ranking.getSubfront(rank).size() < (populationSize - population.size());
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front;

    front = ranking.getSubfront(rank);

    for (int i = 0; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> currentRankedFront = ranking.getSubfront(rank);

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator());

    int i = 0;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i));
      i++;
    }
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList);
  }
}
