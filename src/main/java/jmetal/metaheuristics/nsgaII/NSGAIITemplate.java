//  NSGAIITemplate.java
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

  @Deprecated
  public NSGAIITemplate(SolutionSetEvaluator evaluator) {
    super();
    evaluations_ = 0 ;
    distance_ = new Distance();
    evaluator_ = evaluator ;
  }

  protected NSGAIITemplate(Builder builder) {
    super() ;

    evaluator_ = builder.evaluator_ ;
    populationSize_ = builder.populationSize_ ;
    maxEvaluations_ = builder.maxEvaluations_ ;
    mutationOperator_ = builder.mutationOperator_ ;
    crossoverOperator_ = builder.crossoverOperator_ ;
    selectionOperator_ = builder.selectionOperator_ ;
    evaluations_ = 0 ;
    distance_ = new Distance();
  }

  @Deprecated
  void readParameterSettings() {
    populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();

    mutationOperator_ = operators_.get("mutation");
    crossoverOperator_ = operators_.get("crossover");
    selectionOperator_ = operators_.get("selection");
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
    return evaluations_ == maxEvaluations_ ;
  }

  protected Ranking rankPopulation() throws JMException {
    SolutionSet union = population_.union(offspringPopulation_);

    return new Ranking(union) ;
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

  public Operator getCrossoverOperator() {
    return crossoverOperator_ ;
  }

  public Operator getMutationOperator() {
    return mutationOperator_ ;
  }

  public Operator getSelectionOperator() {
    return selectionOperator_ ;
  }

  public int getPopulationSize() {
    return populationSize_ ;
  }

  public int getMaxEvaluations() {
    return maxEvaluations_ ;
  }

  public int getEvaluations () { return evaluations_ ;}

  public static class Builder {
    protected SolutionSetEvaluator evaluator_ ;
    protected Problem problem_ ;

    protected int populationSize_;
    protected int maxEvaluations_;

    protected Operator mutationOperator_;
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

    public Builder maxEvaluations(int maxEvaluations) {
      maxEvaluations_ = maxEvaluations ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      crossoverOperator_ = crossover ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator_ = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator_ = selection ;

      return this ;
    }

    public NSGAIITemplate build(String NSGAIIVariant) {
      NSGAIITemplate algorithm = null ;
      if ("NSGAII".equals(NSGAIIVariant)) {
        algorithm = new NSGAII(this);
      } else if ("SteadyStateNSGAII".equals(NSGAIIVariant)) {
        algorithm =  new SteadyStateNSGAII(this) ;
      } else {
        throw new JMException(NSGAIIVariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }
} 
