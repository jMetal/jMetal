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

package org.uma.jmetal.algorithm.multiobjective.pesa2;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.pesa2.util.PESA2Selection;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PESA2<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, AdaptiveGridArchive<S>, List<S>> {
  private int maxEvaluations ;
  private int archiveSize ;
  private int populationSize ;
  private int biSections ;

  private int evaluations ;

  protected final Problem<S> problem;

  private AdaptiveGridArchive<S> archive;
  protected final SolutionListEvaluator<S> evaluator;

  public PESA2(Problem<S> problem, int maxEvaluations, int populationSize, int archiveSize,
      int biSections, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator, SolutionListEvaluator<S> evaluator) {
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    this.populationSize = populationSize ;
    this.archiveSize = archiveSize ;
    this.biSections = biSections ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = new PESA2Selection<S>();

    this.evaluator = evaluator ;
  }

  @Override protected void initProgress() {
    evaluations = populationSize ;
  }

  @Override protected void updateProgress() {
    evaluations += populationSize ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override protected AdaptiveGridArchive<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    population = evaluatePopulation(population);
    archive = new AdaptiveGridArchive<>(this.archiveSize, this.biSections, this.problem.getNumberOfObjectives());
    for (S solution : population) {
      archive.add(solution);
    }
    return archive;
  }

  protected List<S> evaluatePopulation(List<S> population) {
	return evaluator.evaluate(population, problem);
  }

  @Override protected List<S> selection(AdaptiveGridArchive<S> archive) {
    List<S> matingPopulation = new ArrayList<>(populationSize) ;

    while (matingPopulation.size() < populationSize) {
      S solution = selectionOperator.execute(archive) ;

      matingPopulation.add(solution);
    }

    return matingPopulation ;
  }

  @Override protected List<S> reproduction(List<S> selectedIndividuals) {
    List<S> offspringPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i+=2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(selectedIndividuals.get(i));
      parents.add(selectedIndividuals.get(i + 1));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));

      offspringPopulation.add(offspring.get(0));
    }
    return offspringPopulation;
  }

  @Override protected AdaptiveGridArchive<S> replacement(AdaptiveGridArchive<S> archive, List<S> offsprings) {
    offsprings = evaluatePopulation(offsprings);
    for (S solution : offsprings) {
      archive.add(solution) ;
    }

    return archive;
  }

  @Override public List<S> getResult() {
    return archive.getSolutionList();
  }
}
