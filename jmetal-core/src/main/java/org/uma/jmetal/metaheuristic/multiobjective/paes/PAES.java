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

package org.uma.jmetal.metaheuristic.multiobjective.paes;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.AdaptiveGridArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * This class implements the PAES algorithm.
 */
public class PAES extends Algorithm {
  private static final long serialVersionUID = 264140016408197977L;

  private int archiveSize_;
  private int maxEvaluations_;
  private int biSections_ ;

  private Operator mutationOperator_;

  @Deprecated
  public PAES() {
    super();
  }

  private PAES(Builder builder) {
    super();

    problem_ = builder.problem_ ;
    archiveSize_  = builder.archiveSize_ ;
    maxEvaluations_ = builder.maxEvaluations_ ;
    biSections_ = builder.biSections_ ;
    mutationOperator_ = builder.mutationOperator_ ;
  }

  public int getArchiveSize() {
    return archiveSize_ ;
  }

  public int getMaxEvaluations() {
    return maxEvaluations_ ;
  }

  public int getBiSections() {
    return biSections_ ;
  }

  public Operator getMutationOperator() {
    return mutationOperator_ ;
  }

  /**
   * Runs of the Paes algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations;
    AdaptiveGridArchive archive;
    Comparator<Solution> dominance;

    //Read the params
    //biSections_ = (Integer) this.getInputParameter("biSections");
    //archiveSize = (Integer) this.getInputParameter("archiveSize");
    //maxEvaluations = (Integer) this.getInputParameter("maxEvaluations");

    //Read the operator
    //mutationOperator = this.operators_.get("mutation");

    //Initialize the variables                
    evaluations = 0;
    archive = new AdaptiveGridArchive(archiveSize_, biSections_, problem_.getNumberOfObjectives());
    dominance = new DominanceComparator();

    //-> Create the initial solutiontype and evaluate it and his constraints
    Solution solution = new Solution(problem_);
    problem_.evaluate(solution);
    problem_.evaluateConstraints(solution);
    evaluations++;

    // Add it to the archive
    archive.add(new Solution(solution));

    //Iterations....
    do {
      // Create the mutate one
      Solution mutatedIndividual = new Solution(solution);
      mutationOperator_.execute(mutatedIndividual);

      problem_.evaluate(mutatedIndividual);
      problem_.evaluateConstraints(mutatedIndividual);
      evaluations++;
      //<-

      // Check dominance
      int flag = dominance.compare(solution, mutatedIndividual);

      if (flag == 1) { //If mutate solutiontype dominate
        solution = new Solution(mutatedIndividual);
        archive.add(mutatedIndividual);
      } else if (flag == 0) { //If none dominate the other                               
        if (archive.add(mutatedIndividual)) {
          solution = test(solution, mutatedIndividual, archive);
        }
      }
    } while (evaluations < maxEvaluations_);

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

  public static class Builder {
    protected Problem problem_ ;

    protected int archiveSize_;
    protected int maxEvaluations_;
    protected int biSections_ ;

    protected Operator mutationOperator_;

    public Builder(Problem problem) {
      problem_ = problem ;
    }

    public Builder archiveSize(int archiveSize) {
      archiveSize_ = archiveSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      maxEvaluations_ = maxEvaluations ;

      return this ;
    }

    public Builder biSections(int biSections) {
      biSections_ = biSections ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator_ = mutation ;

      return this ;
    }

    public PAES build() {
      return new PAES(this) ;
    }
  }
}
