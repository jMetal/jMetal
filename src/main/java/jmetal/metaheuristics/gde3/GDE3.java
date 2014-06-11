//  GDE3.java
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

package jmetal.metaheuristics.gde3;

import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparator.CrowdingComparator;
import jmetal.util.comparator.DominanceComparator;
import jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Comparator;

/**
 * This class implements the GDE3 algorithm.
 */
public class GDE3 extends Algorithm {

  private static final long serialVersionUID = -8007862618252202475L;

  protected SolutionSetEvaluator evaluator_ ;

  protected int populationSize_;
  protected int maxIterations_;
  protected int iterations_;

  protected SolutionSet population_;
  protected SolutionSet offspringPopulation_;

  protected Operator crossoverOperator_;
  protected Operator selectionOperator_;

  protected Comparator dominance_;

  protected Distance distance_ ;

  public GDE3(SolutionSetEvaluator evaluator) {
    super();
    evaluator_ = evaluator ;
    distance_ = new Distance();
    dominance_ = new DominanceComparator();
    iterations_ = 0 ;
  }

  public GDE3(Builder builder) {
    problem_ = builder.problem_ ;
    maxIterations_ = builder.maxIterations_ ;
    crossoverOperator_ = builder.crossoverOperator_ ;
    selectionOperator_ = builder.selectionOperator_ ;
    populationSize_ = builder.populationSize_ ;
  }

  /**
   * Runs of the GDE3 algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws jmetal.util.JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    readParameterSettings();
    createInitialPopulation();
    evaluatePopulation(population_);

    // Generations ...
    while (!stoppingCondition()) {
      System.out.println("Itertaions " + iterations_) ;
      // Create the offSpring solutionSet      
      offspringPopulation_ = new SolutionSet(populationSize_ * 2);
      SolutionSet tmpSolutionSet = new SolutionSet(populationSize_) ;

      for (int i = 0; i < populationSize_; i++) {
        // Obtain parents. Two parameters are required: the population and the 
        //                 index of the current individual
        Solution[] parent = (Solution[]) selectionOperator_.execute(new Object[] {population_, i});

        Solution child;
        // Crossover. Two parameters are required: the current individual and the 
        //            array of parents
        child = (Solution) crossoverOperator_.execute(new Object[] {population_.get(i), parent});

        tmpSolutionSet.add(child);
      }
      evaluatePopulation(tmpSolutionSet);

      for (int i = 0; i < populationSize_; i++) {
        // Dominance test
        Solution child = tmpSolutionSet.get(i) ;
        int result;
        result = dominance_.compare(population_.get(i), child);
        if (result == -1) { 
          // Solution i dominates child
          offspringPopulation_.add(population_.get(i));
        } 
        else if (result == 1) { 
          // child dominates
          offspringPopulation_.add(child);
        } else { 
          // the two solutions are non-dominated
          offspringPopulation_.add(child);
          offspringPopulation_.add(population_.get(i));
        }
      }

      // Ranking the offspring population
      Ranking ranking = new Ranking(offspringPopulation_);

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

      iterations_ ++ ;
    }

    tearDown();

    return getNonDominatedSolutions() ;
  }

  @Deprecated
  void readParameterSettings() {
    populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
    maxIterations_ = ((Integer) this.getInputParameter("maxIterations")).intValue();

    selectionOperator_ = operators_.get("selection");
    crossoverOperator_ = operators_.get("crossover");
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMException {
    population_ = new SolutionSet(populationSize_);

    Solution newSolution;
    for (int i = 0; i < populationSize_; i++) {
      newSolution = new Solution(problem_);
      population_.add(newSolution);
    }
  }

  protected void evaluatePopulation(SolutionSet population) throws JMException {
    evaluator_.evaluate(population, problem_) ;
  }

  protected boolean stoppingCondition() {
    return iterations_ == maxIterations_ ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank) throws JMException {
    SolutionSet front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population_.add(front.get(i));
    }
  }

  protected void computeCrowdingDistance(Ranking ranking, int rank) throws JMException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;
    distance_.crowdingDistanceAssignment(currentRankedFront, problem_.getNumberOfObjectives());
  }

  protected void addLastRankedSolutions(Ranking ranking, int rank) throws JMException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;

    currentRankedFront.sort(new CrowdingComparator());

    int i = 0 ;
    while (population_.size() < populationSize_) {
      population_.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected boolean populationIsNotFull() {
    return population_.size() < populationSize_ ;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank) {
    return ranking.getSubfront(rank).size() < (populationSize_ - population_.size()) ;
  }

  protected SolutionSet getNonDominatedSolutions() throws JMException {
    return new Ranking(population_).getSubfront(0);
  }

  protected void tearDown() {
    evaluator_.shutdown();
  }

  public static class Builder {
    protected SolutionSetEvaluator evaluator_ ;
    protected Problem problem_ ;

    protected int populationSize_;
    protected  int maxIterations_;

    protected Operator crossoverOperator_;
    protected Operator selectionOperator_;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      evaluator_ = evaluator ;
      problem_ = problem ;
    }

    public Builder populationSize(int populationSize) {
      populationSize_ = populationSize ;

      return this ;
    }

    public Builder maxIterations(int maxIterations) {
      maxIterations_ = maxIterations ;

      return this ;
    }

    public Builder evaluator(SolutionSetEvaluator evaluator) {
      evaluator_ = evaluator ;

      return this ;
    }

    public Builder crossover(Operator mutation) {
      crossoverOperator_ = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator_ = selection ;

      return this ;
    }

    public GDE3 build() {
      return new GDE3(this) ;
    }
  }
} 
