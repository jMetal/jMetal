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

package org.uma.jmetal.metaheuristic.multiobjective.nsgaII;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * Implementation of NSGA-II.
 * This implementation of NSGA-II makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm. This version is used
 * in the paper:
 * A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba
 * "A Study of Convergence Speed in Multi-Objective Metaheuristics."
 * To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII extends NSGAIITemplate {
  private static final long serialVersionUID = 5815971727148859507L;

  public NSGAII(Builder builder) {
    super(builder) ;
    problem_ = builder.problem_ ;
    maxEvaluations_ = builder.maxEvaluations_ ;
    crossoverOperator_ = builder.crossoverOperator_ ;
    mutationOperator_ = builder.mutationOperator_ ;
    selectionOperator_ = builder.selectionOperator_ ;
    populationSize_ = builder.populationSize_ ;
  }

  public NSGAII(SolutionSetEvaluator evaluator) {
	  super(evaluator);
  }

  /**
   * Runs the NSGA-II algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    //readParameterSettings();
    createInitialPopulation();
    population_ = evaluatePopulation(population_);

    evaluations_ += population_.size();

    // Main loop
    while (!stoppingCondition()) {
      offspringPopulation_ = new SolutionSet(populationSize_);
      for (int i = 0; i < (populationSize_ / 2); i++) {
        if (!stoppingCondition()) {
          Solution[] parents = new Solution[2];
          parents[0] = (Solution) selectionOperator_.execute(population_);
          parents[1] = (Solution) selectionOperator_.execute(population_);

          Solution[] offSpring = (Solution[]) crossoverOperator_.execute(parents);

          mutationOperator_.execute(offSpring[0]);
          mutationOperator_.execute(offSpring[1]);

          offspringPopulation_.add(offSpring[0]);
          offspringPopulation_.add(offSpring[1]);
        }
      }

      offspringPopulation_ = evaluatePopulation(offspringPopulation_);
      evaluations_ += offspringPopulation_.size() ;

      Ranking ranking = new Ranking(population_.union(offspringPopulation_));

      population_.clear();
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

    tearDown() ;

    return getNonDominatedSolutions() ;
  }
} 
