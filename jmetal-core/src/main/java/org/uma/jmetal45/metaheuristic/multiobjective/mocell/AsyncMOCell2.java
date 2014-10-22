//  AsyncMOCell2.java (Formerly: aMOCell2.java)
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

package org.uma.jmetal45.metaheuristic.multiobjective.mocell;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.util.Distance;
import org.uma.jmetal45.util.Neighborhood;
import org.uma.jmetal45.util.Ranking;
import org.uma.jmetal45.util.archive.CrowdingArchive;

/**
 * This class represents an asynchronous version of the MOCell algorithm, which
 * applies an setArchive feedback through parent selection.
 */
public class AsyncMOCell2 extends MOCellTemplate implements Algorithm {
  private static final long serialVersionUID = 6036292773938388363L;

  /** Constructor */
  public AsyncMOCell2(Builder builder) {
    super(builder);
  }

  /** run() method */
  public SolutionSet execute() throws ClassNotFoundException {
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];
    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives());

    createInitialPopulation();
    population = evaluatePopulation(population) ;
    evaluations = population.size() ;

    while (!stoppingCondition()) {
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

        if (flag == 1) {
          offSpring[0].setLocation(individual.getLocation());
          population.replace(offSpring[0].getLocation(), offSpring[0]);
          archive.add(new Solution(offSpring[0]));
        } else if (flag == 0) {
          neighbors[ind].add(offSpring[0]);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            Distance.crowdingDistance(rank.getSubfront(j));
          }

          int compareResult = densityEstimatorComparator.compare(individual, offSpring[0]);
          if (compareResult == 1) {
            offSpring[0].setLocation(individual.getLocation());
            population.replace(offSpring[0].getLocation(), offSpring[0]);
            archive.add(new Solution(offSpring[0]));
          }
          archive.add(new Solution(offSpring[0]));
        }
      }
    }
    return archive;
  }
}

