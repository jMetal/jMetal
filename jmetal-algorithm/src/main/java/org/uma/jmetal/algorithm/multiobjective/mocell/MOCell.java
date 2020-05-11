package org.uma.jmetal.algorithm.multiobjective.mocell;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author JuanJo Durillo
 * @param <S>
 */
@SuppressWarnings("serial")
public class MOCell<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int evaluations;
  protected int maxEvaluations;
  protected final SolutionListEvaluator<S> evaluator;

  protected Neighborhood<S> neighborhood;
  protected int currentIndividual;
  protected List<S> currentNeighbors;

  protected BoundedArchive<S> archive;

  protected Comparator<S> dominanceComparator;

  /**
   * Constructor
   *
   * @param problem
   * @param maxEvaluations
   * @param populationSize
   * @param neighborhood
   * @param crossoverOperator
   * @param mutationOperator
   * @param selectionOperator
   * @param evaluator
   */
  public MOCell(
      Problem<S> problem,
      int maxEvaluations,
      int populationSize,
      BoundedArchive<S> archive,
      Neighborhood<S> neighborhood,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize);
    this.archive = archive;
    this.neighborhood = neighborhood;
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
    this.dominanceComparator = new DominanceComparator<S>();

    this.evaluator = evaluator;
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
    currentIndividual = 0;
    for (S solution : population) {
      archive.add((S) solution.copy());
    }
  }

  @Override
  protected void updateProgress() {
    evaluations++;
    currentIndividual = (currentIndividual + 1) % getMaxPopulationSize();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return (evaluations == maxEvaluations);
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem());

    return population;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> parents = new ArrayList<>(2);
    currentNeighbors = neighborhood.getNeighbors(population, currentIndividual);
    currentNeighbors.add(population.get(currentIndividual));

    parents.add(selectionOperator.execute(currentNeighbors));
    if (archive.size() > 0) { // TODO. REVISAR EN EL CASO DE TAMAÑO 1
      parents.add(selectionOperator.execute(archive.getSolutionList()));
    } else {
      parents.add(selectionOperator.execute(currentNeighbors));
    }
    return parents;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> result = new ArrayList<>(1);
    List<S> offspring = crossoverOperator.execute(population);
    mutationOperator.execute(offspring.get(0));
    result.add(offspring.get(0));
    return result;
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    int flag =
        dominanceComparator.compare(population.get(currentIndividual), offspringPopulation.get(0));

    if (flag == 1) { // The new individual dominates
      population = insertNewIndividualWhenItDominatesTheCurrentOne(population, offspringPopulation);
    } else if (flag == 0) { // The new individual is non-dominated
      population =
          insertNewIndividualWhenItAndTheCurrentOneAreNonDominated(population, offspringPopulation);
    }
    return population;
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList();
  }

  private List<S> insertNewIndividualWhenItDominatesTheCurrentOne(
      List<S> population, List<S> offspringPopulation) {
    population.set(currentIndividual, offspringPopulation.get(0));
    archive.add(offspringPopulation.get(0));
    return population;
  }

  private List<S> insertNewIndividualWhenItAndTheCurrentOneAreNonDominated(
      List<S> population, List<S> offspringPopulation) {
    currentNeighbors.add(offspringPopulation.get(0));

    Ranking<S> rank = new DominanceRanking<S>();
    rank.computeRanking(currentNeighbors);

    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    for (int j = 0; j < rank.getNumberOfSubFronts(); j++) {
      crowdingDistance.computeDensityEstimator(rank.getSubFront(j));
    }

    Collections.sort(this.currentNeighbors, new RankingAndCrowdingDistanceComparator<S>());
    S worst = this.currentNeighbors.get(this.currentNeighbors.size() - 1);

    archive.add(offspringPopulation.get(0));

    if (worst != offspringPopulation.get(0)) {
      population.set(currentIndividual, offspringPopulation.get(0));
    }
    return population;
  }

  @Override
  public String getName() {
    return "MOCell";
  }

  @Override
  public String getDescription() {
    return "Multi-Objective Cellular evolutionary algorithm";
  }
}
