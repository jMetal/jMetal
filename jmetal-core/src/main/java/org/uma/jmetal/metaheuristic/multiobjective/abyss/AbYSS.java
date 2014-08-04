//  AbYSS.java
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

package org.uma.jmetal.metaheuristic.multiobjective.abyss;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.localSearch.LocalSearch;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Spea2Fitness;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XReal;

import java.util.Comparator;

/**
 * This class implements the AbYSS algorithm. This algorithm is an adaptation
 * of the single-objective scatter search template defined by F. Glover in:
 * F. Glover. "A template for scatter search and path relinking", Lecture Notes
 * in Computer Science, Springer Verlag, 1997. AbYSS is described in:
 * A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 * "AbYSS: Adapting Scatter Search to Multiobjective Optimization."
 * IEEE Transactions on Evolutionary Computation. Vol. 12,
 * No. 4 (August 2008), pp. 439-457
 */
public class AbYSS extends Algorithm {
  private static final long serialVersionUID = 1682316167611498510L;

  int numberOfSubranges;
  int[] sumOfFrequencyValues;
  int[] sumOfReverseFrequencyValues;
  int[][] frequency;
  int[][] reverseFrequency;

  private SolutionSet solutionSet;
  private int solutionSetSize;

  private CrowdingArchive archive;
  private int refSet1Size;

  private SolutionSet refSet1;
  private SolutionSet refSet2;
  private int archiveSize;
  private int refSet2Size;

  private SolutionSet subSet;

  private int maxEvaluations;
  private int evaluations;

  private Comparator<Solution> dominance;
  private Comparator<Solution> equal;
  private Comparator<Solution> fitness;
  private Comparator<Solution> crowdingDistance;

  private Operator crossoverOperator;
  private LocalSearch improvementOperator;

  private Distance distance;

  /** Constructor */
  public AbYSS() {
    solutionSet = null;
    archive = null;
    refSet1 = null;
    refSet2 = null;
    subSet = null;
  } 

  /**
   * Reads the parameter from the parameter list using the
   * <code>getInputParameter</code> method.
   */
  public void initParam() {
    //Read the parameters
    solutionSetSize = (Integer) getInputParameter("populationSize");
    refSet1Size = (Integer) getInputParameter("refSet1Size");
    refSet2Size = (Integer) getInputParameter("refSet2Size");
    archiveSize = (Integer) getInputParameter("archiveSize");
    maxEvaluations = (Integer) getInputParameter("maxEvaluations");

    //Initialize the variables
    solutionSet = new SolutionSet(solutionSetSize);
    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives());
    refSet1 = new SolutionSet(refSet1Size);
    refSet2 = new SolutionSet(refSet2Size);
    subSet = new SolutionSet(solutionSetSize * 1000);
    evaluations = 0;

    numberOfSubranges = 4;

    dominance = new DominanceComparator();
    equal = new EqualSolutions();
    fitness = new FitnessComparator();
    crowdingDistance = new CrowdingDistanceComparator();
    distance = new Distance();
    sumOfFrequencyValues = new int[problem.getNumberOfVariables()];
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()];
    frequency = new int[numberOfSubranges][problem.getNumberOfVariables()];
    reverseFrequency = new int[numberOfSubranges][problem.getNumberOfVariables()];

    //Read the operator of crossover and improvement
    crossoverOperator = operators.get("crossover");
    improvementOperator = (LocalSearch) operators.get("improvement");
    improvementOperator.setParameter("archive", archive);
  } 

  /**
   * Returns a <code>Solution</code> using the diversification generation method
   * described in the scatter search template.
   *
   * @throws org.uma.jmetal.util.JMetalException
   * @throws ClassNotFoundException
   */
  public Solution diversificationGeneration() throws JMetalException, ClassNotFoundException {
    Solution solution;
    solution = new Solution(problem);
    XReal wrapperSolution = new XReal(solution);

    double value;
    int range;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubranges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      } // for

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = PseudoRandom.randInt(0, numberOfSubranges - 1);
      } else {
        value = PseudoRandom.randInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        } 
      }             

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      double low = problem.getLowerLimit(i) + range * (problem.getUpperLimit(i) -
        problem.getLowerLimit(i)) / numberOfSubranges;
      double high = low + (problem.getUpperLimit(i) -
        problem.getLowerLimit(i)) / numberOfSubranges;
      value = PseudoRandom.randDouble(low, high);
      wrapperSolution.setValue(i, value);
    }       
    return solution;
  } 


  /**
   * Implements the referenceSetUpdate method.
   *
   * @param build if true, indicates that the reference has to be build for the
   *              first time; if false, indicates that the reference set has to be
   *              updated with new solutions
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void referenceSetUpdate(boolean build) throws JMetalException {
    if (build) { // Build a new reference set
      // STEP 1. Select the p best individuals of P, where p is refSet1Size.
      //         Selection Criterium: Spea2Fitness
      Solution individual;
      (new Spea2Fitness(solutionSet)).fitnessAssign();
      solutionSet.sort(fitness);

      // STEP 2. Build the RefSet1 with these p individuals            
      for (int i = 0; i < refSet1Size; i++) {
        individual = solutionSet.get(0);
        solutionSet.remove(0);
        individual.unMarked();
        refSet1.add(individual);
      }

      // STEP 3. Compute Euclidean distances in SolutionSet to obtain q 
      //         individuals, where q is refSet2Size
      for (int i = 0; i < solutionSet.size(); i++) {
        individual = solutionSet.get(i);
        individual.setDistanceToSolutionSet(
          distance.distanceToSolutionSetInSolutionSpace(individual, refSet1));
      }

      int size = refSet2Size;
      if (solutionSet.size() < refSet2Size) {
        size = solutionSet.size();
      }

      // STEP 4. Build the RefSet2 with these q individuals
      for (int i = 0; i < size; i++) {
        // Find the maximumMinimunDistanceToPopulation
        double maxMinimum = 0.0;
        int index = 0;
        for (int j = 0; j < solutionSet.size(); j++) {
          if (solutionSet.get(j).getDistanceToSolutionSet() > maxMinimum) {
            maxMinimum = solutionSet.get(j).getDistanceToSolutionSet();
            index = j;
          }
        }
        individual = solutionSet.get(index);
        solutionSet.remove(index);

        // Update distances to REFSET in population
        for (int j = 0; j < solutionSet.size(); j++) {
          double aux = distance.distanceBetweenSolutions(solutionSet.get(j), individual);
          if (aux < individual.getDistanceToSolutionSet()) {
            solutionSet.get(j).setDistanceToSolutionSet(aux);
          }
        }

        // Insert the individual into REFSET2
        refSet2.add(individual);

        // Update distances in REFSET2
        for (int j = 0; j < refSet2.size(); j++) {
          for (int k = 0; k < refSet2.size(); k++) {
            if (i != j) {
              double aux = distance.distanceBetweenSolutions(refSet2.get(j), refSet2.get(k));
              if (aux < refSet2.get(j).getDistanceToSolutionSet()) {
                refSet2.get(j).setDistanceToSolutionSet(aux);
              } 
            } 
          } 
        }    
      }                        

    } else { 
      Solution individual;
      for (int i = 0; i < subSet.size(); i++) {
        individual = (Solution) improvementOperator.execute(subSet.get(i));
        evaluations += improvementOperator.getEvaluations();

        if (refSet1Test(individual)) { 
          for (int indSet2 = 0; indSet2 < refSet2.size(); indSet2++) {
            double aux = distance.distanceBetweenSolutions(individual,
              refSet2.get(indSet2));
            if (aux < refSet2.get(indSet2).getDistanceToSolutionSet()) {
              refSet2.get(indSet2).setDistanceToSolutionSet(aux);
            } 
          }                     
        } else {
          refSet2Test(individual);
        }  
      }
      subSet.clear();
    }
  } 

  /**
   * Tries to update the reference set 2 with a <code>Solution</code>
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   * @throws org.uma.jmetal.util.JMetalException
   */
  public boolean refSet2Test(Solution solution) throws JMetalException {
    if (refSet2.size() < refSet2Size) {
      solution.setDistanceToSolutionSet(
        distance.distanceToSolutionSetInSolutionSpace(solution, refSet1));
      double aux = distance.distanceToSolutionSetInSolutionSpace(solution, refSet2);
      if (aux < solution.getDistanceToSolutionSet()) {
        solution.setDistanceToSolutionSet(aux);
      }
      refSet2.add(solution);
      return true;
    }

    solution
      .setDistanceToSolutionSet(distance.distanceToSolutionSetInSolutionSpace(solution, refSet1));
    double aux = distance.distanceToSolutionSetInSolutionSpace(solution, refSet2);
    if (aux < solution.getDistanceToSolutionSet()) {
      solution.setDistanceToSolutionSet(aux);
    }

    double peor = 0.0;
    int index = 0;
    for (int i = 0; i < refSet2.size(); i++) {
      aux = refSet2.get(i).getDistanceToSolutionSet();
      if (aux > peor) {
        peor = aux;
        index = i;
      }
    }

    if (solution.getDistanceToSolutionSet() < peor) {
      refSet2.remove(index);
      //Update distances in REFSET2
      for (int j = 0; j < refSet2.size(); j++) {
        aux = distance.distanceBetweenSolutions(refSet2.get(j), solution);
        if (aux < refSet2.get(j).getDistanceToSolutionSet()) {
          refSet2.get(j).setDistanceToSolutionSet(aux);
        }
      }
      solution.unMarked();
      refSet2.add(solution);
      return true;
    }
    return false;
  }

  /**
   * Tries to update the reference set one with a <code>Solution</code>.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   * @throws org.uma.jmetal.util.JMetalException
   */
  public boolean refSet1Test(Solution solution) throws JMetalException {
    boolean dominated = false;
    int flag;
    int i = 0;
    while (i < refSet1.size()) {
      flag = dominance.compare(solution, refSet1.get(i));
      if (flag == -1) { //This is: solutiontype dominates
        refSet1.remove(i);
      } else if (flag == 1) {
        dominated = true;
        i++;
      } else {
        flag = equal.compare(solution, refSet1.get(i));
        if (flag == 0) {
          return true;
        } 
        i++;
      }  
    } 

    if (!dominated) {
      solution.unMarked();
      if (refSet1.size() < refSet1Size) {
        refSet1.add(solution);
      } else {
        archive.add(solution);
      } 
    } else {
      return false;
    } 
    return true;
  } 
  
  /**
   * Implements the subset generation method described in the scatter search
   * template
   *
   * @return Number of solutions created by the method
   * @throws org.uma.jmetal.util.JMetalException
   */
  public int subSetGeneration() throws JMetalException {
    Solution[] parents = new Solution[2];
    Solution[] offSpring;

    subSet.clear();

    //All pairs from refSet1
    for (int i = 0; i < refSet1.size(); i++) {
      parents[0] = refSet1.get(i);
      for (int j = i + 1; j < refSet1.size(); j++) {
        parents[1] = refSet1.get(j);
        if (!parents[0].isMarked() || !parents[1].isMarked()) {
          offSpring = (Solution[]) crossoverOperator.execute(parents);
          problem.evaluate(offSpring[0]);
          problem.evaluate(offSpring[1]);
          problem.evaluateConstraints(offSpring[0]);
          problem.evaluateConstraints(offSpring[1]);
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subSet.add(offSpring[0]);
            subSet.add(offSpring[1]);
          }
          parents[0].marked();
          parents[1].marked();
        }
      }
    }

    // All pairs from refSet2
    for (int i = 0; i < refSet2.size(); i++) {
      parents[0] = refSet2.get(i);
      for (int j = i + 1; j < refSet2.size(); j++) {
        parents[1] = refSet2.get(j);
        if (!parents[0].isMarked() || !parents[1].isMarked()) {
          offSpring = (Solution[]) crossoverOperator.execute(parents);
          problem.evaluateConstraints(offSpring[0]);
          problem.evaluateConstraints(offSpring[1]);
          problem.evaluate(offSpring[0]);
          problem.evaluate(offSpring[1]);
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subSet.add(offSpring[0]);
            subSet.add(offSpring[1]);
          }
          parents[0].marked();
          parents[1].marked();
        }
      }
    }

    return subSet.size();
  }

  /**
   * Runs of the AbYSS algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    // STEP 1. Initialize parameters
    initParam();

    // STEP 2. Build the initial solutionSet
    Solution solution;
    for (int i = 0; i < solutionSetSize; i++) {
      solution = diversificationGeneration();
      problem.evaluate(solution);
      problem.evaluateConstraints(solution);
      evaluations++;
      solution = (Solution) improvementOperator.execute(solution);
      evaluations += improvementOperator.getEvaluations();
      solutionSet.add(solution);
    } 

    // STEP 3. Main loop
    int newSolutions;
    while (evaluations < maxEvaluations) {
      referenceSetUpdate(true);
      newSolutions = subSetGeneration();
      while (newSolutions > 0) {           
        referenceSetUpdate(false);
        if (evaluations >= maxEvaluations) {
          return archive;
        }
        newSolutions = subSetGeneration();
      } 

      // RE-START
      if (evaluations < maxEvaluations) {
        solutionSet.clear();
        // Add refSet1 to SolutionSet
        for (int i = 0; i < refSet1.size(); i++) {
          solution = refSet1.get(i);
          solution.unMarked();
          solution = (Solution) improvementOperator.execute(solution);
          evaluations += improvementOperator.getEvaluations();
          solutionSet.add(solution);
        }
        // Remove refSet1 and refSet2
        refSet1.clear();
        refSet2.clear();

        // Sort the archive and insert the best solutions
        distance.crowdingDistanceAssignment(archive);
        archive.sort(crowdingDistance);

        int insert = solutionSetSize / 2;
        if (insert > archive.size()) {
          insert = archive.size();
        }

        if (insert > (solutionSetSize - solutionSet.size())) {
          insert = solutionSetSize - solutionSet.size();
        }

        // Insert solutions 
        for (int i = 0; i < insert; i++) {
          solution = new Solution(archive.get(i));
          solution.unMarked();
          solutionSet.add(solution);
        }

        // Create the rest of solutions randomly
        while (solutionSet.size() < solutionSetSize) {
          solution = diversificationGeneration();
          problem.evaluateConstraints(solution);
          problem.evaluate(solution);
          evaluations++;
          solution = (Solution) improvementOperator.execute(solution);
          evaluations += improvementOperator.getEvaluations();
          solution.unMarked();
          solutionSet.add(solution);
        } 
      }    
    }       

    // STEP 4. Return the archive
    return archive;
  } 
} 
