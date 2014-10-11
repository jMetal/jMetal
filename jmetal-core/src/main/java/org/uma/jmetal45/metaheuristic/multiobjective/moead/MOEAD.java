//  MOEAD.java
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

package org.uma.jmetal45.metaheuristic.multiobjective.moead;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;

public class MOEAD extends MOEADTemplate implements Algorithm {
  private static final long serialVersionUID = -8602634334286344579L;

  /** Constructor */
  protected MOEAD(Builder builder) {
    super(builder) ;
  }

  /** Execute() method */
  public SolutionSet execute() throws ClassNotFoundException {
    evaluations = 0 ;

    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();
    initializeIdealPoint();

    do {
      int[] permutation = new int[populationSize];
      Utils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];

        NeighborType neighborType = chooseNeighborType() ;
        Solution[] parents = parentSelection(subProblemId, neighborType) ;
        Solution child = (Solution) crossover.execute(new Object[] {population.get(subProblemId), parents});

        mutation.execute(child);
        problem.evaluate(child);

        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }
    } while (!stoppingCondition());

    return population;
  }
}

