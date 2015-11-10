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

package org.uma.jmetal.algorithm.multiobjective.paes;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 *
 * This class implements the PAES algorithm.
 */
public class PAES<S extends Solution<?>> extends AbstractEvolutionStrategy<S, List<S>> {
  protected int archiveSize;
  protected int maxEvaluations;
  protected int biSections;
  protected int evaluations;

  protected AdaptiveGridArchive<S> archive;
  protected Comparator<S> comparator;

  /**
   * Constructor
   */
  public PAES(Problem<S> problem, int archiveSize, int maxEvaluations, int biSections,
      MutationOperator<S> mutationOperator) {
    super(problem);
    setProblem(problem);
    this.archiveSize = archiveSize;
    this.maxEvaluations = maxEvaluations;
    this.biSections = biSections;
    this.mutationOperator = mutationOperator;

    archive = new AdaptiveGridArchive<S>(archiveSize, biSections, problem.getNumberOfObjectives());
    comparator = new DominanceComparator<S>();
  }

  /* Getters */
  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getBiSections() {
    return biSections;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  @Override protected void initProgress() {
    evaluations = 0;
  }

  @Override protected void updateProgress() {
    evaluations++;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> solutionList = new ArrayList<>(1);
    solutionList.add(getProblem().createSolution());
    return solutionList;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    getProblem().evaluate(population.get(0));
    return population;
  }

  @Override protected List<S> selection(List<S> population) {
    return population;
  }

  @SuppressWarnings("unchecked")
  @Override protected List<S> reproduction(List<S> population) {
    S mutatedSolution = (S)population.get(0).copy();
    mutationOperator.execute(mutatedSolution);

    List<S> mutationSolutionList = new ArrayList<>(1);
    mutationSolutionList.add(mutatedSolution);
    return mutationSolutionList;
  }

  @SuppressWarnings("unchecked")
  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    S current = population.get(0);
    S mutatedSolution = offspringPopulation.get(0);

    int flag = comparator.compare(current, mutatedSolution);
    if (flag == 1) {
      current = (S)mutatedSolution.copy();
      archive.add(mutatedSolution);
    } else if (flag == 0) {
      if (archive.add(mutatedSolution)) {
        population.set(0, test(current, mutatedSolution, archive));
      }
    }

    population.set(0, current);
    return population;
  }

  @Override public List<S> getResult() {
    return archive.getSolutionList();
  }

  /**
   * Tests two solutions to determine which one becomes be the guide of PAES
   * algorithm
   *
   * @param solution        The actual guide of PAES
   * @param mutatedSolution A candidate guide
   */
  @SuppressWarnings("unchecked")
  public S test(S solution, S mutatedSolution, AdaptiveGridArchive<S> archive) {
    int originalLocation = archive.getGrid().location(solution);
    int mutatedLocation = archive.getGrid().location(mutatedSolution);

    if (originalLocation == -1) {
      return (S)mutatedSolution.copy();
    }

    if (mutatedLocation == -1) {
      return (S)solution.copy();
    }

    if (archive.getGrid().getLocationDensity(mutatedLocation) < archive.getGrid()
        .getLocationDensity(originalLocation)) {
      return (S)mutatedSolution.copy();
    }

    return (S)solution.copy();
  }

  @Override public String getName() {
    return "PAES" ;
  }

  @Override public String getDescription() {
    return "Pareto-Archived Evolution Strategy" ;
  }

}
