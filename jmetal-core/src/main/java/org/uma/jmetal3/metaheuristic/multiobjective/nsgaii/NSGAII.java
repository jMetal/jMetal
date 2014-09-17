//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.Ranking;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of NSGA-II.
 * This implementation of NSGA-II makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm. This version is used
 * in the paper:
 * A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba
 * "A Study of Convergence Speed in Multi-Objective Metaheuristics."
 * To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII extends NSGAIITemplate  {
  private static final long serialVersionUID = 5815971727148859507L;

  protected NSGAII(Builder builder) {
    super(builder) ;
  }

  /**
   * Runs the NSGA-II algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public List<?> execute() throws JMetalException {
    createInitialPopulation();
    population = evaluatePopulation(population);

    // Main loop
    while (!stoppingCondition()) {
      offspringPopulation = new ArrayList<>(populationSize);
      for (int i = 0; i < (populationSize / 2); i++)
        if (!stoppingCondition()) {
          List<Solution> parents = new ArrayList<>(2);
          parents.add ((Solution) selectionOperator.execute(population));
          parents.add ((Solution) selectionOperator.execute(population));

          List<Solution> offSpring = (List<Solution>) crossoverOperator.execute(parents);

          mutationOperator.execute(offSpring.get(0));
          mutationOperator.execute(offSpring.get(1));

          offspringPopulation.add(offSpring.get(0));
          offspringPopulation.add(offSpring.get(1));
        }

      offspringPopulation = evaluatePopulation(offspringPopulation);

      List<Solution> union = new ArrayList<>() ;
      union.addAll(population) ;
      union.addAll(offspringPopulation) ;

      Ranking ranking = new Ranking(union);

      crowdingDistanceSelection(ranking);
    }

    tearDown() ;
    return getNonDominatedSolutions(population) ;
  }
} 
