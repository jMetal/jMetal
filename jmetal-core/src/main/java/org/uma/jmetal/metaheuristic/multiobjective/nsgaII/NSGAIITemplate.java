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

public abstract class NSGAIITemplate implements Algorithm {
  protected SolutionSetEvaluator evaluator ;

  protected Problem problem ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  private Distance distance;

  /** Constructor */
  protected NSGAIITemplate(Builder builder) {
    problem = builder.problem;
    evaluator = builder.evaluator ;
    populationSize = builder.populationSize;
    maxEvaluations = builder.maxEvaluations;
    mutationOperator = builder.mutationOperator;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    distance = new Distance();

    evaluations = 0 ;
  }

  /* Getters */
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

  public int getEvaluations () {
    return evaluations;
  }

  /** Builder class */
  public static class Builder {
    protected SolutionSetEvaluator evaluator ;
    protected Problem problem;

    protected int populationSize;
    protected int maxEvaluations;

    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
      this.problem = problem ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setCrossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder setMutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder setSelection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public NSGAIITemplate build(String nsgaIIvariant) {
      NSGAIITemplate algorithm  ;
      if ("NSGAII".equals(nsgaIIvariant)) {
        algorithm = new NSGAII(this);
      } else if ("SteadyStateNSGAII".equals(nsgaIIvariant)) {
        algorithm =  new SteadyStateNSGAII(this) ;
      } else {
        throw new JMetalException(nsgaIIvariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    evaluations += population.size() ;

    return evaluator.evaluate(population, problem) ;
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
    distance.crowdingDistanceAssignment(currentRankedFront);
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

  protected SolutionSet getNonDominatedSolutions(SolutionSet solutionSet) throws JMetalException {
    return new Ranking(solutionSet).getSubfront(0);
  }

  protected void crowdingDistanceSelection(Ranking ranking) {
    population.clear();
    int rankingIndex = 0;
    while (populationIsNotFull()) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex);
        rankingIndex++;
      } else {
        computeCrowdingDistance(ranking, rankingIndex);
        addLastRankedSolutions(ranking, rankingIndex);
      }
    }
  }

  protected void tearDown() {
    evaluator.shutdown();
  }
} 
