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

package jmetal.metaheuristics.nsgaIIb;

import com.google.inject.Inject;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparator.CrowdingComparator;
import jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * Implementation of NSGA-II.
 * This implementation of NSGA-II makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm. This version is used
 * in the paper:
 * A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba
 * "A Study of Convergence Speed in Multi-Objective Metaheuristics."
 * To be presented in: PPSN'08. Dortmund. September 2008.
 */

public abstract class NSGAIITemplate extends Algorithm {

  @Inject
  protected SolutionSetEvaluator evaluator_ ;

  protected int populationSize_;
  protected int maxEvaluations_;
  protected int evaluations_;

  protected SolutionSet population_;
  protected SolutionSet offspringPopulation_;


  protected Operator mutationOperator_;
  protected Operator crossoverOperator_;
  protected Operator selectionOperator_;

  private Distance distance_ ;

  public NSGAIITemplate(SolutionSetEvaluator evaluator) {
	  super();
    evaluations_ = 0 ;
    distance_ = new Distance();
    evaluator_ = evaluator ;
  }

  void readParameterSettings() {
    populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();

    mutationOperator_ = operators_.get("mutation");
    crossoverOperator_ = operators_.get("crossover");
    selectionOperator_ = operators_.get("selection");
  }

  SolutionSet createInitialPopulation(int populationSize) throws ClassNotFoundException, JMException {
    SolutionSet population ;
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      population.add(newSolution);
    }

    return population ;
  }

  void evaluatePopulation(SolutionSet population) throws JMException {
    evaluator_.evaluate(population, problem_) ;
    evaluations_ += population.size() ;
  }

  boolean stoppingCondition() {
    return evaluations_ == maxEvaluations_ ;
  }

  Ranking rankPopulation() throws JMException {
    SolutionSet union = population_.union(offspringPopulation_);

    return new Ranking(union) ;
  }

  void addRankedSolutionsToPopulation(Ranking ranking, int rank) throws JMException {
    SolutionSet front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population_.add(front.get(i));
    }
  }

  void computeCrowdingDistance(Ranking ranking, int rank) throws JMException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;
    distance_.crowdingDistanceAssignment(currentRankedFront, problem_.getNumberOfObjectives());
  }

  void addLastRankedSolutions(Ranking ranking, int rank) throws JMException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;

    currentRankedFront.sort(new CrowdingComparator());

    int i = 0 ;
    while (population_.size() < populationSize_) {
      population_.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  boolean populationIsNotFull() {
    return population_.size() < populationSize_ ;
  }

  boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank) {
    return ranking.getSubfront(rank).size() < (populationSize_ - population_.size()) ;
  }
  
  SolutionSet getNonDominatedSolutions() throws JMException {
    return new Ranking(population_).getSubfront(0);
  }

  void tearDown() {
    evaluator_.shutdown(); 
  }
} 
