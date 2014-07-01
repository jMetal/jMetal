//  AsyncMOCell3 (Formerly: aMOCell3.java)
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
 * This class represents an asynchronous version of MOCell algorithm. It is
 * based on AsyncMOCell1 but replacing the worst neighbor.
 */
public class AsyncMOCell3 extends MOCellTemplate {
  private static final long serialVersionUID = 7923330703025056895L;

  public AsyncMOCell3(Builder builder) {
    super(builder);
  }


  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    population = new SolutionSet(populationSize);
    archive = new CrowdingArchive(archiveSize, problem_.getNumberOfObjectives());
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

    evaluations = 0;

    createInitialPopulation();
    while (!stoppingCondition()) {
      for (int ind = 0; ind < population.size(); ind++) {
        Solution individual = new Solution(population.get(ind));

        Solution[] parents = new Solution[2];
        Solution[] offSpring;

        neighbors[ind] = neighborhood.getEightNeighbors(population, ind);
        neighbors[ind].add(individual);

        //parents
        parents[0] = (Solution) selectionOperator.execute(neighbors[ind]);
        parents[1] = (Solution) selectionOperator.execute(neighbors[ind]);

        offSpring = (Solution[]) crossoverOperator.execute(parents);
        mutationOperator.execute(offSpring[0]);

        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        evaluations++;

        int flag = dominanceComparator.compare(individual, offSpring[0]);

        if (flag == 1) {
          offSpring[0].setLocation(individual.getLocation());
          population.replace(offSpring[0].getLocation(), offSpring[0]);
          archive.add(new Solution(offSpring[0]));
        } else if (flag == 0) {
          neighbors[ind].add(offSpring[0]);
          offSpring[0].setLocation(-1);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            Distance.crowdingDistance(rank.getSubfront(j));
          }
          Solution worst = neighbors[ind].worst(densityEstimatorComparator);

          if (worst.getLocation() == -1) {
            archive.add(new Solution(offSpring[0]));
          } else {
            offSpring[0].setLocation(worst.getLocation());
            population.replace(offSpring[0].getLocation(), offSpring[0]);
            archive.add(new Solution(offSpring[0]));
          }
        }
      }

      archiveFeedback();
    }

    return archive;
  }
}

