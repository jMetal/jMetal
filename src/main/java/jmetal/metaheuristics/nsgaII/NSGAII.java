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

package jmetal.metaheuristics.nsgaII;

import jmetal.core.*;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparator.CrowdingComparator;
import jmetal.util.evaluator.SolutionSetEvaluator;

import com.google.inject.*;
/**
 * Implementation of NSGA-II.
 * This implementation of NSGA-II makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm. This version is used
 * in the paper:
 * A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba
 * "A Study of Convergence Speed in Multi-Objective Metaheuristics."
 * To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII extends Algorithm {
  private static final long serialVersionUID = 5815971727148859507L;

  //@Inject
  private SolutionSetEvaluator evaluator_ ;

  private int populationSize_;
  private int maxEvaluations_;
  private int evaluations_;

  private SolutionSet population_;
  private SolutionSet offspringPopulation_;


  private Operator mutationOperator_;
  private Operator crossoverOperator_;
  private Operator selectionOperator_;

  private Distance distance_ ;

  //public NSGAII(Problem problemToSolve, SolutionSetEvaluator evaluator) {
  //public NSGAII(Problem problemToSolve) {
 
  public NSGAII() {
	  super();
    evaluations_ = 0 ;
    distance_ = new Distance();
  }
  
  
  /**
   * Runs the NSGA-II algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws jmetal.util.JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    readParameterSettings();
    population_ = createInitialPopulation(populationSize_);
    evaluatePopulation(population_);

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

      evaluatePopulation(offspringPopulation_);
      Ranking ranking = rankPopulation() ;

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
