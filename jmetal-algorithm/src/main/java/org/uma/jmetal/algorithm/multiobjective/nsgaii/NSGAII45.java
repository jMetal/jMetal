package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Implementation of NSGA-II following the scheme used in jMetal4.5 and former versions, i.e,
 * without implementing the {@link AbstractGeneticAlgorithm} interface.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAII45<S extends Solution<?>> implements Algorithm<List<S>> {
  protected List<S> population;
  protected final int maxEvaluations;
  protected final int populationSize;

  protected final Problem<S> problem;

  protected final SolutionListEvaluator<S> evaluator;

  protected int evaluations;

  protected SelectionOperator<List<S>, S> selectionOperator;
  protected CrossoverOperator<S> crossoverOperator;
  protected MutationOperator<S> mutationOperator;

  /** Constructor */
  public NSGAII45(
      Problem<S> problem,
      int maxEvaluations,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator) {
    super();
    this.problem = problem;
    this.maxEvaluations = maxEvaluations;
    this.populationSize = populationSize;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
  }

  /** Run method */
  @Override
  public void run() {
    population = createInitialPopulation();
    evaluatePopulation(population);

    evaluations = populationSize;

    while (evaluations < maxEvaluations) {
      List<S> offspringPopulation = new ArrayList<>(populationSize);
      for (int i = 0; i < populationSize; i += 2) {
        List<S> parents = new ArrayList<>(2);
        parents.add(selectionOperator.execute(population));
        parents.add(selectionOperator.execute(population));

        List<S> offspring = crossoverOperator.execute(parents);

        mutationOperator.execute(offspring.get(0));
        mutationOperator.execute(offspring.get(1));

        offspringPopulation.add(offspring.get(0));
        offspringPopulation.add(offspring.get(1));
      }

      evaluatePopulation(offspringPopulation);

      List<S> jointPopulation = new ArrayList<>();
      jointPopulation.addAll(population);
      jointPopulation.addAll(offspringPopulation);

      Ranking<S> ranking = new FastNonDominatedSortRanking<>();
      ranking.compute(jointPopulation);

      RankingAndCrowdingSelection<S> rankingAndCrowdingSelection;
      rankingAndCrowdingSelection = new RankingAndCrowdingSelection<>(populationSize);

      population = rankingAndCrowdingSelection.execute(jointPopulation);

      evaluations += populationSize;
    }
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(population);
  }

  protected List<S> createInitialPopulation() {
      List<S> population = new ArrayList<>(populationSize);
      int bound = populationSize;
      for (int i = 0; i < bound; i++) {
          S solution = problem.createSolution();
          population.add(solution);
      }
      return population;
  }

  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, problem);

    return population;
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    @NotNull Ranking<S> ranking = new FastNonDominatedSortRanking<>();
    ranking.compute(solutionList);

    return ranking;
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNonDominatedSolutions(solutionList);
  }

  @Override
  public String getName() {
    return "NSGAII45";
  }

  @Override
  public @NotNull String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II. Version not using the AbstractGeneticAlgorithm template";
  }
}
