//  PAES.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the PAES algorithm.
 */
public class PAES extends AbstractEvolutionStrategy<Solution, List<Solution>> {

  private Problem problem ;

  private int archiveSize;
  private int maxEvaluations;
  private int biSections;
  private int evaluations ;

  private MutationOperator mutationOperator;

  private AdaptiveGridArchive archive;
  private Comparator comparator ;

  /** Constructor */
  public PAES(PAESBuilder builder) {
    super();

    problem = builder.problem;
    archiveSize = builder.archiveSize;
    maxEvaluations = builder.maxEvaluations;
    biSections = builder.biSections;
    mutationOperator = builder.mutationOperator;

    archive = new AdaptiveGridArchive(archiveSize, biSections, problem.getNumberOfObjectives());
    comparator = new DominanceComparator();

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

  public MutationOperator getMutationOperator() {
    return mutationOperator;
  }

  @Override
  protected void initProgress() {
    evaluations = 0 ;
  }

  @Override
  protected void updateProgress() {
    evaluations ++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<Solution> createInitialPopulation() {
    List<Solution> solutionList = new ArrayList<>(1) ;
    solutionList.add(problem.createSolution()) ;
    return solutionList ;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    problem.evaluate(population.get(0));
    return population ;
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    return population;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> mutationSolutionList = new ArrayList<>(1) ;
    mutationOperator.execute(population.get(0)) ;
    mutationSolutionList.add(population.get(0)) ;
    return mutationSolutionList;
  }

  @Override
  protected List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation) {
    int flag = comparator.compare(population.get(0), offspringPopulation.get(0)) ;

    if (flag == 1) {
      population.set(0, offspringPopulation.get(0)) ;
      archive.add(offspringPopulation.get(0));
    } else if (flag == 0) { //If none dominate the other
      if (archive.add(offspringPopulation.get(0))) {
        population.set(0, test(population.get(0), offspringPopulation.get(0), archive));
      }
    }

     return population;
  }

  @Override
  public List<Solution> getResult() {
    return null;
  }

  /** run() method */
  /*
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations;
    Comparator<Solution> dominance;

    evaluations = 0;
    archive = new AdaptiveGridArchive(archiveSize, biSections, problem.getNumberOfObjectives());
    dominance = new DominanceComparator();

    //-> Create the initial solutiontype and evaluate it and his constraints
    Solution solution = new Solution(problem);
    problem.evaluate(solution);
    problem.evaluateConstraints(solution);
    evaluations++;

    // Add it to the setArchive
    archive.add(new Solution(solution));

    //Iterations....
    do {
      Solution mutatedIndividual = new Solution(solution);
      mutationOperator.execute(mutatedIndividual);

      problem.evaluate(mutatedIndividual);
      problem.evaluateConstraints(mutatedIndividual);
      evaluations++;

      int flag = dominance.compare(solution, mutatedIndividual);

      if (flag == 1) { //If mutate solutiontype dominate
        solution = new Solution(mutatedIndividual);
        archive.add(mutatedIndividual);
      } else if (flag == 0) { //If none dominate the other                               
        if (archive.add(mutatedIndividual)) {
          solution = test(solution, mutatedIndividual, archive);
        }
      }
    } while (evaluations < maxEvaluations);

    return archive;
  }
  */

  /**
   * Tests two solutions to determine which one becomes be the guide of PAES
   * algorithm
   *
   * @param solution        The actual guide of PAES
   * @param mutatedSolution A candidate guide
   */
  public Solution test(Solution solution,
    Solution mutatedSolution,
    AdaptiveGridArchive archive) {

    int originalLocation = archive.getGrid().location(solution);
    int mutatedLocation = archive.getGrid().location(mutatedSolution);

    if (originalLocation == -1) {
      return mutatedSolution.copy();
    }

    if (mutatedLocation == -1) {
      return solution.copy();
    }

    if (archive.getGrid().getLocationDensity(mutatedLocation) <
      archive.getGrid().getLocationDensity(originalLocation)) {
      return mutatedSolution.copy();
    }

    return solution.copy();
  }
}
