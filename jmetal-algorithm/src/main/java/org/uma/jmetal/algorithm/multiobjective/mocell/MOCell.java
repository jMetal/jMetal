//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.mocell;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
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
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author JuanJo Durillo
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public class MOCell<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int evaluations;
  protected int maxEvaluations;
  protected final SolutionListEvaluator<S> evaluator;

  private Neighborhood<S> neighborhood;
  private int currentIndividual;
  private List<S> currentNeighbors;

  private BoundedArchive<S> archive;

  private Comparator<S> dominanceComparator;
  private LocationAttribute<S> location;

  /**
   * Constructor
   * @param problem
   * @param maxEvaluations
   * @param populationSize
   * @param neighborhood
   * @param crossoverOperator
   * @param mutationOperator
   * @param selectionOperator
   * @param evaluator
   */
  public MOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive,
                Neighborhood<S> neighborhood,
                CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize);
    this.archive = archive ;
    //this.archive = new CrowdingDistanceArchive<>(archiveSize);
    this.neighborhood = neighborhood ;
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
    this.dominanceComparator = new DominanceComparator<S>() ;

    this.evaluator = evaluator ;
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
    currentIndividual=0;
  }

  @Override
  protected void updateProgress() {
    evaluations++;
    currentIndividual=(currentIndividual+1)%getMaxPopulationSize();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return (evaluations==maxEvaluations);
  }

  @Override
  protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      S newIndividual = getProblem().createSolution();
      population.add(newIndividual);
    }
    location = new LocationAttribute<>(population);
    return population;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem());
    for (S solution : population) {
      archive.add((S)solution.copy()) ;
    }

    return population;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> parents = new ArrayList<>(2);
    currentNeighbors = neighborhood.getNeighbors(population, currentIndividual);
    currentNeighbors.add(population.get(currentIndividual));

    parents.add(selectionOperator.execute(currentNeighbors));
    if (archive.size() > 0) {
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
    int flag = dominanceComparator.compare(population.get(currentIndividual),offspringPopulation.get(0));

    if (flag == 1) { //The new individual dominates
      insertNewIndividualWhenDominates(population,offspringPopulation);
    } else if (flag == 0) { //The new individual is non-dominated
      insertNewIndividualWhenNonDominated(population,offspringPopulation);
    }
    return population;
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList();
  }

  private void insertNewIndividualWhenDominates(List<S> population, List<S> offspringPopulation) {
    location.setAttribute(offspringPopulation.get(0),
        location.getAttribute(population.get(currentIndividual)));

    population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));
    archive.add(offspringPopulation.get(0));
  }

  private void insertNewIndividualWhenNonDominated(List<S> population, List<S> offspringPopulation) {
    currentNeighbors.add(offspringPopulation.get(0));
    location.setAttribute(offspringPopulation.get(0), -1);

    Ranking<S> rank = new DominanceRanking<S>();
    rank.computeRanking(currentNeighbors);

    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
      crowdingDistance.computeDensityEstimator(rank.getSubfront(j));
    }

    Collections.sort(this.currentNeighbors,new RankingAndCrowdingDistanceComparator<S>());
    S worst = this.currentNeighbors.get(this.currentNeighbors.size()-1);

    if (location.getAttribute(worst) == -1) { //The worst is the offspring
      archive.add(offspringPopulation.get(0));
    } else {
      location.setAttribute(offspringPopulation.get(0),
          location.getAttribute(worst));
      population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));
      archive.add(offspringPopulation.get(0));
    }
  }

  @Override public String getName() {
    return "MOCell" ;
  }

  @Override public String getDescription() {
    return "Multi-Objective Cellular evolutionry algorithm" ;
  }
}
