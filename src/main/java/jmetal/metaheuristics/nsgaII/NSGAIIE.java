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
import jmetal.qualityIndicator.QualityIndicator;
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

public class NSGAIIE extends Algorithm {
  private static final long serialVersionUID = 5815971727148859507L;

  private SolutionSetEvaluator evaluator_ ;

  private int populationSize_;
  private int maxEvaluations_;
  private int evaluations_;

  private QualityIndicator indicators_;

  private SolutionSet population_;
  private SolutionSet offspringPopulation_;


  private Operator mutationOperator_;
  private Operator crossoverOperator_;
  private Operator selectionOperator_;

  private Distance distance_ ;
  private int requiredEvaluations_;


  public NSGAIIE(Problem problemToSolve, SolutionSetEvaluator evaluator) {
    super(problemToSolve);
    evaluator_ = evaluator ;
    evaluations_ = 0 ;
    distance_ = new Distance();
    requiredEvaluations_ = 0;
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
    populationEvaluation(population_);

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

      populationEvaluation(offspringPopulation_);

      Ranking ranking = rankPopulations() ;

      addRankedZeroSolutionsToPopulation(ranking) ;

      int rankingIndex = 1 ;
      while (population_.size() < populationSize_) {
        if (ranking.getSubfront(rankingIndex).size() < (populationSize_-population_.size())) {
          addRankedSolutionsToPopulation(ranking, rankingIndex);
          rankingIndex ++ ;
        } else {
          computeCrowdingDistance(ranking, rankingIndex) ;
          addLastRankedSolutions(ranking, rankingIndex);
        }
      }

      /*
      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if ((indicators != null) &&
        (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        }
      }
      */
    }

    // Return as output parameter the required evaluations
    //setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population_);

    return ranking.getSubfront(0);
  }

  private void readParameterSettings() {
    populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators_ = (QualityIndicator) getInputParameter("indicators");

    mutationOperator_ = operators_.get("mutation");
    crossoverOperator_ = operators_.get("crossover");
    selectionOperator_ = operators_.get("selection");
  }

  private SolutionSet createInitialPopulation(int populationSize) throws ClassNotFoundException, JMException {
    SolutionSet population ;
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      population.add(newSolution);
    }

    return population ;
  }

  private void populationEvaluation(SolutionSet population) throws JMException {
    evaluator_.evaluate(population, problem_) ;
    evaluations_ += population.size() ;
  }

  private boolean stoppingCondition() {
    return evaluations_ < maxEvaluations_ ;
  }

  private Ranking rankPopulations() {
    SolutionSet union = population_.union(offspringPopulation_);

    return new Ranking(union) ;
  }

  private void addRankedZeroSolutionsToPopulation(Ranking ranking) {
    population_.clear();

    int rank = 0 ;
    addRankedSolutionsToPopulation(ranking, rank);
  }

  private void addRankedSolutionsToPopulation(Ranking ranking, int rank) {
    SolutionSet front ;

    front = ranking.getSubfront(rank);
    for (int i = rank; i < front.size(); i++) {
      population_.add(front.get(i));
    }
  }

  private void computeCrowdingDistance(Ranking ranking, int rank) {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;
    distance_.crowdingDistanceAssignment(currentRankedFront, problem_.getNumberOfObjectives());
  }

  private void addLastRankedSolutions(Ranking ranking, int rank) {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;

    currentRankedFront.sort(new CrowdingComparator());

    int i = 0 ;
    while (population_.size() < populationSize_) {
      population_.add(currentRankedFront.get(i)) ;
    }
  }

} 
