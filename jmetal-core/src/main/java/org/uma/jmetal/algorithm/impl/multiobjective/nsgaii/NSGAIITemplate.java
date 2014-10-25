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

package org.uma.jmetal.algorithm.impl.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Template for implement NSGA-II and variants
 */

public abstract class NSGAIITemplate implements Algorithm<List<Solution<?>>> {
  protected SolutionSetEvaluator evaluator ;

  protected Problem problem ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected List<Solution<?>> population;
  protected List<Solution<?>> offspringPopulation;

  protected MutationOperator mutationOperator;
  protected CrossoverOperator<List<Solution<?>>, List<Solution<?>>> crossoverOperator;
  protected SelectionOperator<List<Solution<?>>, Solution> selectionOperator;

  protected String variant ;

  protected Ranking ranking ;
  protected DensityEstimator crowdingDistance;

  /** Constructor */
  protected NSGAIITemplate(Builder builder) {
    problem = builder.problem;
    populationSize = builder.populationSize;
    maxEvaluations = builder.maxEvaluations;
    mutationOperator = builder.mutationOperator;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    variant = builder.variant ;

    ranking = new DominanceRanking() ;
    crowdingDistance = new CrowdingDistance() ;

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
    protected Problem problem;

    protected int populationSize;
    protected int maxEvaluations;

    protected MutationOperator mutationOperator;
    protected CrossoverOperator crossoverOperator;
    protected SelectionOperator selectionOperator;

    protected String variant ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.variant = "NSGAII" ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setCrossover(CrossoverOperator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder setMutation(MutationOperator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder setSelection(SelectionOperator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public Builder setVariant(String variant) {
      this.variant = variant ;

      return this ;
    }

    public NSGAIITemplate build() {
      NSGAIITemplate algorithm  ;
      if ("NSGAII".equals(variant)) {
        algorithm = new NSGAII(this);
      } else if ("SteadyStateNSGAII".equals(variant)) {
        algorithm = new NSGAII(this);
      } else {
        throw new JMetalException(variant + " variant unknown") ;
      }

      return algorithm ;
    }
  }

  protected void createInitialPopulation() throws JMetalException {
    population = new ArrayList<>(populationSize);

    Solution solution;
    for (int i = 0; i < populationSize; i++) {
      solution = problem.createSolution();
      population.add(solution);
    }

  }

  protected List<Solution<?>> evaluatePopulation(List<Solution<?>> population) throws JMetalException {
    evaluations += population.size() ;

    for (Solution<?> solution : population) {
      problem.evaluate(solution);
    }

    return population ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank) throws JMetalException {
    List<Solution<?>> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void addLastRankedSolutions(Ranking ranking, int rank) throws JMetalException {
    List<Solution<?>> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

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

  protected List<Solution<?>> getNonDominatedSolutions(List<Solution<?>> solutionSet) {
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }

  protected void computeRanking(List<Solution<?>> solutionSet) {
    ranking.computeRanking(solutionSet) ;
  }

  protected void crowdingDistanceSelection() {
    population.clear();
    int rankingIndex = 0;
    while (populationIsNotFull()) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutions(ranking, rankingIndex);
      }
    }
  }

  protected void tearDown() {
  }
} 
