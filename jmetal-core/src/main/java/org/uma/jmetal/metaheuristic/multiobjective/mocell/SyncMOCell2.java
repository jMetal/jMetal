//  SyncMOCell2.java (Formerly: sMOCell2)
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

package org.uma.jmetal.metaheuristic.multiobjective.mocell;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.archive.CrowdingArchive;

/**
 * This class represents a synchronous version of MOCell algorithm, which
 * applies an setArchive feedback through parent selection.
 */
public class SyncMOCell2 extends MOCellTemplate {
  private static final long serialVersionUID = -2245599709758873327L;

  /** Constructor */
  public SyncMOCell2(Builder builder) {
    super(builder);
  }

  /** execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    SolutionSet newSolutionSet;

    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives());
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

    createInitialPopulation();
    population = evaluatePopulation(population) ;
    evaluations = population.size() ;

    while (!stoppingCondition()) {
      newSolutionSet = new SolutionSet(populationSize);
      for (int ind = 0; ind < population.size(); ind++) {
        Solution individual = new Solution(population.get(ind));

        Solution[] parents = new Solution[2];
        Solution[] offSpring;

        neighbors[ind] = neighborhood.getEightNeighbors(population, ind);
        neighbors[ind].add(individual);

        parents[0] = (Solution) selectionOperator.execute(neighbors[ind]);
        if (archive.size() > 0) {
          parents[1] = (Solution) selectionOperator.execute(archive);
        } else {
          parents[1] = (Solution) selectionOperator.execute(neighbors[ind]);
        }

        offSpring = (Solution[]) crossoverOperator.execute(parents);
        mutationOperator.execute(offSpring[0]);

        problem.evaluate(offSpring[0]);
        problem.evaluateConstraints(offSpring[0]);
        evaluations++;

        int flag = dominanceComparator.compare(individual, offSpring[0]);

        if (flag == -1) {
          newSolutionSet.add(new Solution(population.get(ind)));
        } else if (flag == 1) {
          offSpring[0].setLocation(individual.getLocation());
          newSolutionSet.add(offSpring[0]);
          archive.add(new Solution(offSpring[0]));
        } else if (flag == 0) { //The individuals are non-dominates
          neighbors[ind].add(offSpring[0]);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            Distance.crowdingDistance(rank.getSubfront(j));
          }
          boolean deleteMutant = true;

          int compareResult = densityEstimatorComparator.compare(individual, offSpring[0]);
          if (compareResult == 1) {
            deleteMutant = false;
          }

          if (!deleteMutant) {
            offSpring[0].setLocation(individual.getLocation());
            newSolutionSet.add(offSpring[0]);
          } else {
            newSolutionSet.add(new Solution(population.get(ind)));
          }
          archive.add(new Solution(offSpring[0]));
        }
      }
      population = newSolutionSet;
    }
    return archive;
  }
}

