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

package org.uma.jmetal.metaheuristic.multiobjective.nsgaII;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * This class implements a steady-state version of NSGA-II.
 */
public class SteadyStateNSGAII extends NSGAIITemplate implements Algorithm {

  private static final long serialVersionUID = 3588191288161132897L;

  /** Constructor */
  protected SteadyStateNSGAII(Builder builder) {
    super(builder) ;
  }


  /**
   * Runs the ssNSGA-II algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    createInitialPopulation();
    population = evaluatePopulation(population);

    while (!stoppingCondition()) {
      offspringPopulation = new SolutionSet(1);
      Solution[] parents = new Solution[2];

      parents[0] = (Solution) selectionOperator.execute(population);
      parents[1] = (Solution) selectionOperator.execute(population);

      Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);

      mutationOperator.execute(offSpring[0]);

      problem.evaluate(offSpring[0]);
      problem.evaluateConstraints(offSpring[0]);

      offspringPopulation.add(offSpring[0]);

      evaluations++;

      Ranking ranking = new Ranking(population.union(offspringPopulation));

      population.clear();
      int rankingIndex = 0 ;
      while (populationIsNotFull()) {
        if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
          addRankedSolutionsToPopulation(ranking, rankingIndex);
          rankingIndex ++ ;
        } else {
          computeCrowdingDistance(ranking, rankingIndex) ;
          addLastRankedSolutions(ranking, rankingIndex);
        }
      }
    }

    tearDown();

    return getNonDominatedSolutions(population) ;
  } 
} 
