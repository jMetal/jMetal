//  PESA2.java
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

package org.uma.jmetal.metaheuristic.multiobjective.pesa2;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.PESA2Selection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.AdaptiveGridArchive;

/**
 * This class implements the PESA2 algorithm.
 */
public class PESA2 extends Algorithm {
  private static final long serialVersionUID = 6904671980699718772L;

  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;
  private int biSections;

  private Crossover crossover ;
  private Mutation mutation ;

  /** Constructor */
  private PESA2(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.archiveSize = builder.archiveSize ;
    this.biSections = builder.biSections ;
    this.maxEvaluations = builder.maxEvaluations ;

    this.crossover = builder.crossover ;
    this.mutation = builder.mutation ;
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getBiSections() {
    return biSections;
  }

  public Crossover getCrossover() {
    return crossover;
  }

  public Mutation getMutation() {
    return mutation;
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations ;
    AdaptiveGridArchive archive;
    SolutionSet solutionSet;
    Operator  selection;

    // Initialize the variables
    evaluations = 0;
    archive = new AdaptiveGridArchive(archiveSize, biSections, problem.getNumberOfObjectives());
    solutionSet = new SolutionSet(populationSize);
    selection = new PESA2Selection.Builder()
            .build() ;

    //-> Create the initial individual and evaluate it and his constraints
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem);
      problem.evaluate(solution);
      problem.evaluateConstraints(solution);
      evaluations++;
      solutionSet.add(solution);
    }               

    // Incorporate non-dominated solutiontype to the archive
    for (int i = 0; i < solutionSet.size(); i++) {
      archive.add(solutionSet.get(i)); // Only non dominated are accepted by the archive
    }

    // Clear the init solutionSet
    solutionSet.clear();

    //Iterations....
    Solution[] parents = new Solution[2];
    do {
      //-> Create the offSpring solutionSet                    
      while (solutionSet.size() < populationSize) {
        parents[0] = (Solution) selection.execute(archive);
        parents[1] = (Solution) selection.execute(archive);

        Solution[] offSpring = (Solution[]) crossover.execute(parents);
        mutation.execute(offSpring[0]);
        problem.evaluate(offSpring[0]);
        problem.evaluateConstraints(offSpring[0]);
        evaluations++;
        solutionSet.add(offSpring[0]);
      }

      for (int i = 0; i < solutionSet.size(); i++) {
        archive.add(solutionSet.get(i));
      }

      // Clear the solutionSet
      solutionSet.clear();

    } while (evaluations < maxEvaluations);
    //Return the  solutionSet of non-dominated individual
    return archive;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem;

    private int populationSize;
    private int archiveSize;
    private int maxEvaluations;
    private int biSections;

    private Crossover crossover ;
    private Mutation mutation ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.populationSize = 10 ;
      this.archiveSize = 100 ;
      this.biSections = 5 ;

      this.crossover = new SBXCrossover.Builder()
              .probability(0.9)
              .distributionIndex(20.0)
              .build() ;

      this.mutation = new PolynomialMutation.Builder()
              .probability(1.0/problem.getNumberOfVariables())
              .distributionIndex(20.0)
              .build() ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder biSections(int biSections) {
      this.biSections = biSections ;

      return this ;
    }

    public Builder mutation(Mutation mutation) {
      this.mutation = mutation ;

      return this ;
    }

    public Builder crossover(Crossover crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public PESA2 build() {
      return new PESA2(this) ;
    }
  }
} 
