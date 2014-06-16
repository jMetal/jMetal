//  CellDE.java
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

package org.uma.jmetal.metaheuristics.cellde;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/**
 * This class represents the original asynchronous MOCell algorithm
 * hybridized with Diferential evolutions (GDE3), called CellDE. It uses an
 * archive based on spea2 fitness to store non-dominated solutions, and it is
 * described in:
 * J.J. Durillo, A.J. Nebro, F. Luna, E. Alba "Solving Three-Objective
 * Optimization Problems Using a new Hybrid Cellular Genetic Algorithm".
 * PPSN'08. Dortmund. September 2008.
 */
public class CellDE extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = 8699667515096532262L;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public CellDE() {
  } // CellDE


  /**
   * Runs of the CellDE algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   * @throws ClassNotFoundException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int populationSize, archiveSize, maxEvaluations, evaluations, feedBack;
    Operator crossoverOperator, selectionOperator;
    SolutionSet currentSolutionSet;
    SolutionSet archive;
    SolutionSet[] neighbors;
    Neighborhood neighborhood;
    Comparator<Solution> dominance = new DominanceComparator();
    Comparator<Solution> crowding = new CrowdingComparator();

    Distance distance = new Distance();

    //Read the params
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    feedBack = ((Integer) getInputParameter("feedBack")).intValue();

    //Read the operators
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    //Initialize the variables    
    currentSolutionSet = new SolutionSet(populationSize);
    archive = new org.uma.jmetal.util.archive.SPEA2DensityArchive(archiveSize);
    evaluations = 0;
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

    //Create the initial population
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem_);
      problem_.evaluate(solution);
      problem_.evaluateConstraints(solution);
      currentSolutionSet.add(solution);
      solution.setLocation(i);
      evaluations++;
    }

    while (evaluations < maxEvaluations) {
      for (int ind = 0; ind < currentSolutionSet.size(); ind++) {
        Solution individual = new Solution(currentSolutionSet.get(ind));

        Solution[] parents = new Solution[3];
        Solution offSpring;

        neighbors[ind] = neighborhood.getEightNeighbors(currentSolutionSet, ind);

        //parents
        parents[0] = (Solution) selectionOperator.execute(neighbors[ind]);
        parents[1] = (Solution) selectionOperator.execute(neighbors[ind]);
        parents[2] = individual;

        //Create a new solution, using genetic operators mutation and crossover
        offSpring = (Solution) crossoverOperator.execute(new Object[] {individual, parents});

        //->Evaluate offspring and constraints
        problem_.evaluate(offSpring);
        problem_.evaluateConstraints(offSpring);
        evaluations++;

        int flag = dominance.compare(individual, offSpring);

        if (flag == 1) { //The offSpring dominates
          offSpring.setLocation(individual.getLocation());
          //currentSolutionSet.reemplace(offSpring[0].getLocation(),offSpring[0]);
          currentSolutionSet.replace(ind, new Solution(offSpring));
          //newSolutionSet.add(offSpring);
          archive.add(new Solution(offSpring));
        } else if (flag == 0) { //Both two are non-dominates
          neighbors[ind].add(offSpring);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            distance
              .crowdingDistanceAssignment(rank.getSubfront(j), problem_.getNumberOfObjectives());
          }

          boolean deleteMutant = true;
          int compareResult = crowding.compare(individual, offSpring);
          if (compareResult == 1) {
            deleteMutant = false;
          }

          if (!deleteMutant) {
            offSpring.setLocation(individual.getLocation());
            //currentSolutionSet.reemplace(offSpring[0].getLocation(),offSpring[0]);
            //newSolutionSet.add(offSpring);
            currentSolutionSet.replace(offSpring.getLocation(), offSpring);
            archive.add(new Solution(offSpring));
          } else {
            //newSolutionSet.add(new Solution(currentSolutionSet.get(ind)));
            archive.add(new Solution(offSpring));
          }
        }
      }

      //Store a portion of the archive into the population
      for (int j = 0; j < feedBack; j++) {
        if (archive.size() > j) {
          int r = PseudoRandom.randInt(0, currentSolutionSet.size() - 1);
          if (r < currentSolutionSet.size()) {
            Solution individual = archive.get(j);
            individual.setLocation(r);
            currentSolutionSet.replace(r, new Solution(individual));
          }
        }
      }
    }
    return archive;
  }
} 
