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

package org.uma.jmetal45.metaheuristic.multiobjective.paes;

import org.uma.jmetal45.core.*;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.archive.AdaptiveGridArchive;
import org.uma.jmetal45.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * This class implements the PAES algorithm.
 */
public class PAES implements Algorithm {
  private static final long serialVersionUID = 264140016408197977L;

  private Problem problem ;

  private int archiveSize;
  private int maxEvaluations;
  private int biSections;

  private Operator mutationOperator;

  @Deprecated
  public PAES() {
    super();
  }

  /** Constructor */
  private PAES(Builder builder) {
    super();

    problem = builder.problem;
    archiveSize = builder.archiveSize;
    maxEvaluations = builder.maxEvaluations;
    biSections = builder.biSections;
    mutationOperator = builder.mutationOperator;
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

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem;

    private int archiveSize;
    private int maxEvaluations;
    private int biSections;

    private Operator mutationOperator;

    public Builder(Problem problem) {
      this.problem = problem ;
    }

    public Builder setArchiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setBiSections(int biSections) {
      this.biSections = biSections ;

      return this ;
    }

    public Builder setMutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public PAES build() {
      return new PAES(this) ;
    }
  }

  /** run() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations;
    AdaptiveGridArchive archive;
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
      return new Solution(mutatedSolution);
    }

    if (mutatedLocation == -1) {
      return new Solution(solution);
    }

    if (archive.getGrid().getLocationDensity(mutatedLocation) <
      archive.getGrid().getLocationDensity(originalLocation)) {
      return new Solution(mutatedSolution);
    }

    return new Solution(solution);
  }
}
