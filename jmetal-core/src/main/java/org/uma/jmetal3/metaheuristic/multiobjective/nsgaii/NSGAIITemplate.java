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

package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal3.core.Algorithm;
import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.operator.crossover.CrossoverOperator;
import org.uma.jmetal3.operator.mutation.MutationOperator;
import org.uma.jmetal3.operator.selection.SelectionOperator;
import org.uma.jmetal3.util.comparator.CrowdingComparator;

import java.util.ArrayList;
import java.util.List;

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

  protected List<NSGAIISolution> population;
  protected List<NSGAIISolution> offspringPopulation;

  protected MutationOperator mutationOperator;
  protected CrossoverOperator crossoverOperator;
  protected SelectionOperator selectionOperator;

  protected String variant ;

  //private Distance distance;

  /** Constructor */
  protected NSGAIITemplate(Builder builder) {
    problem = builder.problem;
    evaluator = builder.evaluator ;
    populationSize = builder.populationSize;
    maxEvaluations = builder.maxEvaluations;
    mutationOperator = builder.mutationOperator;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    variant = builder.variant ;

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

    protected MutationOperator mutationOperator;
    protected CrossoverOperator crossoverOperator;
    protected SelectionOperator selectionOperator;

    protected String variant ;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
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
        algorithm =  new SteadyStateNSGAII(this) ;
      } else {
        throw new JMetalException(variant + " variant unknown") ;
      }

      return algorithm ;
    }
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new ArrayList<>(populationSize);

//    Solution newSolution;
//    for (int i = 0; i < populationSize; i++) {
//      newSolution = problem.createSolution();
//      population.add(newSolution);
//    }

    NSGAIISolution newNSGAIISolution;
    for (int i = 0; i < populationSize; i++) {
      newNSGAIISolution = new NSGAIISolution(problem.createSolution());
      population.add(newNSGAIISolution);
    }

  }

  protected List<NSGAIISolution> evaluatePopulation(List<NSGAIISolution> population) throws JMetalException {
    evaluations += population.size() ;

    for (int i = 0 ; i < population.size(); i++) {
      problem.evaluate(population.get(i).getProblemSolution()) ;
      //problem.evaluateConstraints(solutionSet.get(i)) ;
    }

    return population ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations;
  }

  protected Ranking rankPopulation() throws JMetalException {
    ArrayList<NSGAIISolution> union = new ArrayList<NSGAIISolution>() ;
    union.addAll(population);
    union.addAll(offspringPopulation);

    return new Ranking<>(union) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank) throws JMetalException {
    List<NSGAIISolution> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void computeCrowdingDistance(Ranking ranking, int rank) throws JMetalException {
    List<NSGAIISolution> currentRankedFront = ranking.getSubfront(rank) ;
    Distance.crowdingDistanceAssignment(currentRankedFront);
  }

  protected void addLastRankedSolutions(Ranking ranking, int rank) throws JMetalException {
    List<NSGAIISolution> currentRankedFront = ranking.getSubfront(rank) ;

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

  protected List<Solution> getNonDominatedSolutions(List<NSGAIISolution> solutionSet) throws JMetalException {
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
