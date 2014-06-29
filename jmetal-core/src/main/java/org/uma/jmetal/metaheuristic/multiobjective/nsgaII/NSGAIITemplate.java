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

package org.uma.jmetal.metaheuristic.multiobjective.nsgaII;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingComparator;
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

public abstract class NSGAIITemplate extends Algorithm {
  protected SolutionSetEvaluator evaluator ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  private Distance distance;

  @Deprecated
  public NSGAIITemplate(SolutionSetEvaluator evaluator) {
    super();
    evaluations = 0 ;
    distance = new Distance();
    this.evaluator = evaluator ;
  }

  protected NSGAIITemplate(Builder builder) {
    super() ;

    evaluator = builder.evaluator ;
    populationSize = builder.populationSize_ ;
    maxEvaluations = builder.maxEvaluations_ ;
    mutationOperator = builder.mutationOperator_ ;
    crossoverOperator = builder.crossoverOperator_ ;
    selectionOperator = builder.selectionOperator_ ;
    distance = new Distance();

    evaluations = 0 ;
  }

  @Deprecated
  void readParameterSettings() {
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    return evaluator.evaluate(population, problem_) ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations;
  }

  protected Ranking rankPopulation() throws JMetalException {
    SolutionSet union = population.union(offspringPopulation);

    return new Ranking(union) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank) throws JMetalException {
    SolutionSet front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void computeCrowdingDistance(Ranking ranking, int rank) throws JMetalException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;
    distance.crowdingDistanceAssignment(currentRankedFront, problem_.getNumberOfObjectives());
  }

  protected void addLastRankedSolutions(Ranking ranking, int rank) throws JMetalException {
    SolutionSet currentRankedFront = ranking.getSubfront(rank) ;

    currentRankedFront.sort(new CrowdingComparator());

    int i = 0 ;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected boolean populationIsNotFull() {
    return population.size() < populationSize;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank) {
    return ranking.getSubfront(rank).size() < (populationSize - population.size()) ;
  }

  protected SolutionSet getNonDominatedSolutions() throws JMetalException {
    return new Ranking(population).getSubfront(0);
  }

  protected void tearDown() {
    evaluator.shutdown();
  }

  public Operator getCrossoverOperator() {
    return crossoverOperator;
  }

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  public Operator getSelectionOperator() {
    return selectionOperator;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getEvaluations () { return evaluations;}

  public static class Builder {
    protected SolutionSetEvaluator evaluator ;
    protected Problem problem_ ;

    protected int populationSize_;
    protected int maxEvaluations_;

    protected Operator mutationOperator_;
    protected Operator crossoverOperator_;
    protected Operator selectionOperator_;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
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
        throw new JMetalException(NSGAIIVariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }
} 
