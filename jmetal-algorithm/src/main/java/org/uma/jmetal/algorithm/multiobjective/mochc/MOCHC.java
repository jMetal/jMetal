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

package org.uma.jmetal.algorithm.multiobjective.mochc;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

/**
 * This class executes the MOCHC algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHC extends AbstractEvolutionaryAlgorithm<BinarySolution, List<BinarySolution>> {
  private BinaryProblem problem ;

  private int populationSize ;
  private int maxEvaluations ;
  private int convergenceValue ;
  private double preservedPopulation ;
  private double initialConvergenceCount ;
  private CrossoverOperator crossover ;
  private MutationOperator cataclysmicMutation ;
  private SelectionOperator newGenerationSelection ;
  private SelectionOperator parentSelection ;
  private int evaluations ;
  private int minimumDistance;

  private SolutionListEvaluator evaluator ;

  /** Constructor */
  public MOCHC(BinaryProblem problem, int populationSize, int maxEvaluations, int convergenceValue,
               double preservedPopulation, double initialConvergenceCount, CrossoverOperator crossoverOperator,
               MutationOperator cataclysmicMutation, SelectionOperator newGenerationSelection,
               SelectionOperator parentSelection, SolutionListEvaluator evaluator) {
    super() ;
    this.problem = problem ;
    this.populationSize = populationSize ;
    this.maxEvaluations = maxEvaluations ;
    this.convergenceValue = convergenceValue ;
    this.preservedPopulation = preservedPopulation ;
    this.initialConvergenceCount = initialConvergenceCount ;
    this.crossover = crossoverOperator ;
    this.cataclysmicMutation = cataclysmicMutation ;
    this.newGenerationSelection = newGenerationSelection ;
    this.parentSelection = parentSelection ;
    this.evaluator = evaluator ;

    int size = 0 ;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      size += problem.getNumberOfBits(i) ;
    }
    minimumDistance = (int) Math.floor(initialConvergenceCount * size);
  }

  /* Getters */
  public BinaryProblem getProblem() {
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

  public CrossoverOperator getCrossover() {
    return crossover ;
  }

  public MutationOperator getCataclysmicMutation() {
    return cataclysmicMutation ;
  }

  public SelectionOperator getParentSelection() {
    return parentSelection ;
  }

  public SelectionOperator getNewGenerationSelection() {
    return newGenerationSelection ;
  }

  public double getPreservedPopulation() {
    return preservedPopulation ;
  }


  @Override
  protected void initProgress() {
    evaluations = 0 ;
  }

  @Override
  protected void updateProgress() {
    evaluations += populationSize ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override
  protected List<BinarySolution> createInitialPopulation() {
    List<BinarySolution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      BinarySolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<BinarySolution> evaluatePopulation(List<BinarySolution> population) {
    population = evaluator.evaluate(population, problem) ;

    return population ;
  }

  @Override
  protected List<BinarySolution> selection(List<BinarySolution> population) {
    List<BinarySolution> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < populationSize; i+=2) {
      List<BinarySolution> solution = (List<BinarySolution>) parentSelection.execute(population);
      matingPopulation.add(solution.get(0)) ;
      matingPopulation.add(solution.get(1)) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<BinarySolution> reproduction(List<BinarySolution> matingPopulation) {
    List<BinarySolution> offspringPopulation = new ArrayList<>() ;
    for (int i = 0; i < matingPopulation.size(); i += 2) {
      List<BinarySolution> parents = new ArrayList<>(2);
      parents.add(matingPopulation.get(i));
      parents.add(matingPopulation.get(i + 1));
      //Equality condition between solutions
      if (hammingDistance(parents.get(0), parents.get(1)) >= (minimumDistance)) {
        List<BinarySolution> offspring = (List<BinarySolution>) crossover.execute(parents);
        offspringPopulation.add(offspring.get(0));
        offspringPopulation.add(offspring.get(1));
      }
    }
    return offspringPopulation ;
  }

  @Override
  protected List<BinarySolution> replacement(List<BinarySolution> population, List<BinarySolution> offspringPopulation) {
    List<BinarySolution> union = new ArrayList<>() ;
    union.addAll(population) ;
    union.addAll(offspringPopulation) ;

    List<BinarySolution> newPopulation = (List<BinarySolution>) newGenerationSelection.execute(union);

    if (solutionSetsAreEquals(population, newPopulation)) {
      minimumDistance--;
    }
/*
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
*/
    return union;
  }

  @Override
  public List<BinarySolution> getResult() {
    return SolutionListUtils.getNondominatedSolutions(getPopulation()) ;
  }



  /** Execute() method */
  /*
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
*/
  /**
   * Compares two solutionSets to determine if both are equals
   *
   * @param solutionSet    A <code>SolutionSet</code>
   * @param newSolutionSet A <code>SolutionSet</code>
   * @return true if both are contains the same solutions, false in other case
   */
  public boolean solutionSetsAreEquals(List<BinarySolution> solutionSet, List<BinarySolution> newSolutionSet) {
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

  private int hammingDistance(BinarySolution solutionOne, BinarySolution solutionTwo) {
    int distance = 0;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      distance += hammingDistance(solutionOne.getVariableValue(i), solutionTwo.getVariableValue(2)) ;
    }

    return distance;
  }


  private int hammingDistance(BitSet bitSet1, BitSet bitSet2) {
    int distance = 0;
    int i = 0;
    while (i < bitSet1.size()) {
      if (bitSet1.get(i) != bitSet2.get(i)) {
        distance++;
      }
      i++;
    }
    return distance;
  }
}
