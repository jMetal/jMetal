//  MOCHC.java
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

package org.uma.jmetal.metaheuristic.multiobjective.mochc;

import org.uma.jmetal.core.*;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Comparator;

/**
 * This class executes the MOCHC algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHC extends Algorithm {
  private static final long serialVersionUID = -2880293437154543456L;

  int populationSize ;
  int maxEvaluations ;
  int convergenceValue ;
  double preservedPopulation ;
  double initialConvergenceCount ;
  Crossover crossover ;
  Mutation cataclysmicMutation ;
  Selection newGenerationSelection ;
  Selection parentSelection ;

  @Deprecated
  public MOCHC() {
    super();
  }

  /** Constructor */
  private MOCHC(Builder builder) {
    super() ;
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.convergenceValue = builder.convergenceValue ;
    this.preservedPopulation = builder.preservedPopulation ;
    this.initialConvergenceCount = builder.initialConvergenceCount ;
    this.crossover = builder.crossover ;
    this.cataclysmicMutation = builder.cataclysmicMutation ;
    this.newGenerationSelection = builder.newGenerationSelection ;
    this.parentSelection = builder.parentSelection ;
  }

  public Problem getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize ;
  }

  public int getMaxEvaluation() {
    return maxEvaluations ;
  }

  public double getInitialConvergenceCount() {
    return initialConvergenceCount ;
  }

  public int getConvergenceValue() {
    return convergenceValue ;
  }

  public Crossover getCrossover() {
    return crossover ;
  }

  public Mutation getCataclysmicMutation() {
    return cataclysmicMutation ;
  }

  public Selection getParentSelection() {
    return parentSelection ;
  }

  public Selection getNewGenerationSelection() {
    return newGenerationSelection ;
  }

  public double getPreservedPopulation() {
    return preservedPopulation ;
  }

  /**
   * Compares two solutionSets to determine if both are equals
   *
   * @param solutionSet    A <code>SolutionSet</code>
   * @param newSolutionSet A <code>SolutionSet</code>
   * @return true if both are contains the same solutions, false in other case
   */
  public boolean solutionSetsAreEquals(SolutionSet solutionSet, SolutionSet newSolutionSet) {
    boolean found;
    for (int i = 0; i < solutionSet.size(); i++) {

      int j = 0;
      found = false;
      while (j < newSolutionSet.size()) {

        if (solutionSet.get(i).equals(newSolutionSet.get(j))) {
          found = true;
        }
        j++;
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  /**
   * Calculate the hamming distance between two solutions
   *
   * @param solutionOne A <code>Solution</code>
   * @param solutionTwo A <code>Solution</code>
   * @return the hamming distance between solutions
   */
  public int hammingDistance(Solution solutionOne, Solution solutionTwo) {
    int distance = 0;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      distance +=
        ((Binary) solutionOne.getDecisionVariables()[i]).
          hammingDistance((Binary) solutionTwo.getDecisionVariables()[i]);
    }

    return distance;
  }

  /** execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int iterations;

    int minimumDistance;
    int evaluations;

    Comparator crowdingComparator = new CrowdingComparator();

    boolean condition = false;
    SolutionSet solutionSet, offspringPopulation, newPopulation;

    iterations = 0;
    evaluations = 0;

    //Calculate the maximum problem sizes
    Solution aux = new Solution(problem);
    int size = 0;
    for (int var = 0; var < problem.getNumberOfVariables(); var++) {
      size += ((Binary) aux.getDecisionVariables()[var]).getNumberOfBits();
    }
    minimumDistance = (int) Math.floor(initialConvergenceCount * size);

    solutionSet = new SolutionSet(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem);
      problem.evaluate(solution);
      problem.evaluateConstraints(solution);
      evaluations++;
      solutionSet.add(solution);
    }

    while (!condition) {
      offspringPopulation = new SolutionSet(populationSize);
      for (int i = 0; i < solutionSet.size() / 2; i++) {
        Solution[] parents = (Solution[]) parentSelection.execute(solutionSet);

        //Equality condition between solutions
        if (hammingDistance(parents[0], parents[1]) >= (minimumDistance)) {
          Solution[] offspring = (Solution[]) crossover.execute(parents);
          problem.evaluate(offspring[0]);
          problem.evaluateConstraints(offspring[0]);
          problem.evaluate(offspring[1]);
          problem.evaluateConstraints(offspring[1]);
          evaluations += 2;
          offspringPopulation.add(offspring[0]);
          offspringPopulation.add(offspring[1]);
        }
      }
      SolutionSet union = solutionSet.union(offspringPopulation);
      //newGenerationSelection.setParameter("populationSize", populationSize);
      newPopulation = (SolutionSet) newGenerationSelection.execute(union);

      if (solutionSetsAreEquals(solutionSet, newPopulation)) {
        minimumDistance--;
      }
      if (minimumDistance <= -convergenceValue) {

        minimumDistance = (int) (1.0 / size * (1 - 1.0 / size) * size);

        int preserve = (int) Math.floor(preservedPopulation * populationSize);
        newPopulation = new SolutionSet(populationSize);
        solutionSet.sort(crowdingComparator);
        for (int i = 0; i < preserve; i++) {
          newPopulation.add(new Solution(solutionSet.get(i)));
        }
        for (int i = preserve; i < populationSize; i++) {
          Solution solution = new Solution(solutionSet.get(i));
          cataclysmicMutation.execute(solution);
          problem.evaluate(solution);
          problem.evaluateConstraints(solution);
          newPopulation.add(solution);
        }
      }
      iterations++;

      solutionSet = newPopulation;
      if (evaluations >= maxEvaluations) {
        condition = true;
      }
    }


    CrowdingArchive archive;
    archive = new CrowdingArchive(populationSize, problem.getNumberOfObjectives());
    for (int i = 0; i < solutionSet.size(); i++) {
      archive.add(solutionSet.get(i));
    }

    return archive;
  }

  /** Builder class */
  static public class Builder {
    Problem problem ;
    SolutionSetEvaluator evaluator ;
    int populationSize ;
    int maxEvaluations ;
    int convergenceValue ;
    double preservedPopulation ;
    double initialConvergenceCount ;
    Crossover crossover ;
    Mutation cataclysmicMutation;
    Selection parentSelection ;
    Selection newGenerationSelection ;

    public Builder(Problem problem) {
      this.problem = problem ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder convergenceValue(int convergenceValue) {
      this.convergenceValue = convergenceValue ;

      return this ;
    }

    public Builder initialConvergenceCount(double initialConvergenceCount) {
      this.initialConvergenceCount = initialConvergenceCount ;

      return this ;
    }

    public Builder preservedPopulation(double preservedPopulation) {
      this.preservedPopulation = preservedPopulation ;

      return this ;
    }

    public Builder crossover(Crossover crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public Builder cataclysmicMutation(Mutation cataclysmicMutation) {
      this.cataclysmicMutation = cataclysmicMutation ;

      return this ;
    }

    public Builder parentSelection(Selection parentSelection) {
      this.parentSelection = parentSelection ;

      return this ;
    }

    public Builder newGenerationSelection(Selection newGenerationSelection) {
      this.newGenerationSelection = newGenerationSelection ;

      return this ;
    }

    public MOCHC build() {
      MOCHC algorithm =  new MOCHC(this) ;

      return algorithm ;
    }
  }
}
