//  SPEA2.java
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

package org.uma.jmetal.metaheuristic.multiobjective.spea2;

import org.uma.jmetal.core.*;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.SteadyStateNSGAII;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * This class representing the SPEA2 algorithm
 */
public class SPEA2 extends Algorithm {
  private static final long serialVersionUID = -6552554169817006100L;

  private static final int TOURNAMENTS_ROUNDS = 1;

  private int populationSize ;
  private int archiveSize ;
  private int maxEvaluations;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  /** Constructor  */
  @Deprecated
  public SPEA2() {
    super();
  }

  /** Constructor */
  private SPEA2(Builder builder) {
    problem = builder.problem;

    populationSize = builder.populationSize;
    archiveSize = builder.archiveSize;
    maxEvaluations = builder.maxEvaluations;
    mutationOperator = builder.mutationOperator;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
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

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  public Operator getCrossoverOperator() {
    return crossoverOperator;
  }

  public Operator getSelectionOperator() {
    return selectionOperator;
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    SolutionSet solutionSet ;
    SolutionSet archive ;
    SolutionSet offSpringSolutionSet;
    int evaluations;

    //Initialize the variables
    solutionSet = new SolutionSet(populationSize);
    archive = new SolutionSet(archiveSize);
    evaluations = 0;

    //-> Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      problem.evaluate(newSolution);
      problem.evaluateConstraints(newSolution);
      evaluations++;
      solutionSet.add(newSolution);
    }

    while (evaluations < maxEvaluations) {
      SolutionSet union = ((SolutionSet) solutionSet).union(archive);
      Spea2Fitness spea = new Spea2Fitness(union);
      spea.fitnessAssign();
      archive = spea.environmentalSelection(archiveSize);
      // Create a new offspringPopulation
      offSpringSolutionSet = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      while (offSpringSolutionSet.size() < populationSize) {
        int j = 0;
        do {
          j++;
          parents[0] = (Solution) selectionOperator.execute(archive);
        } while (j < SPEA2.TOURNAMENTS_ROUNDS);
        int k = 0;
        do {
          k++;
          parents[1] = (Solution) selectionOperator.execute(archive);
        } while (k < SPEA2.TOURNAMENTS_ROUNDS);

        //make the crossover 
        Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
        mutationOperator.execute(offSpring[0]);
        problem.evaluate(offSpring[0]);
        problem.evaluateConstraints(offSpring[0]);
        offSpringSolutionSet.add(offSpring[0]);
        evaluations++;
      }
      // End Create a offSpring solutionSet
      solutionSet = offSpringSolutionSet;
    }

    Ranking ranking = new Ranking(archive);
    return ranking.getSubfront(0);
  }

  /** Builder class */
  public static class Builder {
    private Problem problem;

    private int populationSize;
    private int archiveSize;

    private int maxEvaluations;

    private Operator mutationOperator;
    private Operator crossoverOperator;
    private Operator selectionOperator;

    public Builder(Problem problem) {
      this.problem = problem ;
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

    public Builder crossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public SPEA2 build() {
      return new SPEA2(this) ;
    }
  }
} 
