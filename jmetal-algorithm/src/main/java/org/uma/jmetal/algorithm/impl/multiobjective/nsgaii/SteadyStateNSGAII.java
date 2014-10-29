//  SteadyStateNSGAII.java
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

package org.uma.jmetal.algorithm.impl.multiobjective.nsgaii;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a steady-state version of NSGA-II.
 */
public class SteadyStateNSGAII extends NSGAIITemplate  {

  /** Constructor */
  protected SteadyStateNSGAII(Builder builder) {
    super(builder) ;
  }

  /**
   * Runs the steady-state version of the NSGA-II algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  @Override
  public void run()  {
    createInitialPopulation();
    population = evaluatePopulation(population);

    // Main loop
    while (!stoppingCondition()) {
      List<Solution<?>> parents = new ArrayList<>(2);
      parents.add(selectionOperator.execute(population));
      parents.add(selectionOperator.execute(population));

      List<Solution<?>> offSpring = crossoverOperator.execute(parents);

      mutationOperator.execute(offSpring.get(0));

      evaluations++ ;
      population.add(offSpring.get(0)) ;

      computeRanking(population);
      crowdingDistanceSelection();
    }
  }

  @Override
  public List<Solution<?>> getResult() {
    return getNonDominatedSolutions(population) ;
  }
} 
