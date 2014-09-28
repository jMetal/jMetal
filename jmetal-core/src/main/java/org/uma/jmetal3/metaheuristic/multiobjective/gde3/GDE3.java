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

package org.uma.jmetal3.metaheuristic.multiobjective.gde3;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.problem.ContinuousProblem;
import org.uma.jmetal3.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal3.util.comparator.DominanceComparator;
import org.uma.jmetal3.util.solutionattribute.CrowdingDistance;
import org.uma.jmetal3.util.solutionattribute.Ranking;
import org.uma.jmetal3.util.solutionattribute.impl.CrowdingDistanceImpl;
import org.uma.jmetal3.util.solutionattribute.impl.RankingImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** This class implements the GDE3 algorithm */
public class GDE3 implements org.uma.jmetal3.core.Algorithm<List<DoubleSolution>> {
  private ContinuousProblem problem ;
  protected int populationSize;
  protected int maxIterations;
  protected int iterations;
  
  protected List<Solution> population;
  protected List<Solution> offspringPopulation;

  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  protected Comparator dominanceComparator;

  protected Ranking ranking ;
  protected CrowdingDistance crowdingDistance;

  /**
   * @deprecated
   */
  @Deprecated
  public GDE3() {
    dominanceComparator = new DominanceComparator();
    iterations = 0 ;
    ranking = new RankingImpl() ;
    crowdingDistance = new CrowdingDistanceImpl() ;
  }

  /** Constructor */
  public GDE3(Builder builder) {
    super() ;
    problem = builder.problem;
    maxIterations = builder.maxIterations;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    populationSize = builder.populationSize;
    dominanceComparator = new DominanceComparator();
  }

  /* Getters */
  public Operator getCrossoverOperator() {
    return crossoverOperator;
  }

  public Operator getSelectionOperator() {
    return selectionOperator;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  /** Builder class */
  public static class Builder {
    protected ContinuousProblem problem;

    protected int populationSize;
    protected int maxIterations;
    
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    public Builder(ContinuousProblem problem) {
      this.problem = problem ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setCrossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder setSelection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public GDE3 build() {
      return new GDE3(this) ;
    }
  }

  /** Execute() method  */
  public List<DoubleSolution> execute() {
    createInitialPopulation();
    population = evaluatePopulation(population);

    // Generations ...
    while (!stoppingCondition()) {

      // Create the offSpring solutionSet
      offspringPopulation = new ArrayList<>(populationSize * 2);
      List<DoubleSolution> tmpSolutionSet = new ArrayList<>(populationSize) ;

      for (int i = 0; i < populationSize; i++) {
        // Obtain parents. Two parameters are required: the population and the
        //                 index of the current individual

        List<Solution> parents = new ArrayList<>(2);
        parents.add ((Solution) selectionOperator.execute(population));
        parents.add ((Solution) selectionOperator.execute(population));

        List<Solution> offSpring = (List<Solution>) crossoverOperator.execute(parents);

        Solution[] parent = (Solution[]) selectionOperator.execute(new Object[] {population, i});

        DoubleSolution child;
        // Crossover. Two parameters are required: the current individual and the array of parents
        child = (Solution) crossoverOperator.execute(new Object[] {population.get(i), parent});

        tmpSolutionSet.add(child);
      }
      tmpSolutionSet = evaluatePopulation(tmpSolutionSet);

      for (int i = 0; i < populationSize; i++) {
        // Dominance org.uma.test
        DoubleSolution child = tmpSolutionSet.get(i) ;
        int result;
        result = dominanceComparator.compare(population.get(i), child);
        if (result == -1) {
          // Solution i dominates child
          offspringPopulation.add(population.get(i));
        } else if (result == 1) {
          // child dominates
          offspringPopulation.add(child);
        } else {
          // the two solutions are non-dominated
          offspringPopulation.add(child);
          offspringPopulation.add(population.get(i));
        }
      }

      // Ranking the offspring population
      computeRanking(offspringPopulation);

      population.clear();
      int rankingIndex = 0 ;
      while (populationIsNotFull()) {
        if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
          addRankedSolutionsToPopulation(ranking, rankingIndex);
          rankingIndex ++ ;
        } else {
          crowdingDistance.computeCrowdingDistance(ranking.getSubfront(rankingIndex));
          addLastRankedSolutions(ranking, rankingIndex);
        }
      }

      iterations++ ;
    }

    tearDown();

    return getNonDominatedSolutions() ;
  }

  protected void createInitialPopulation() {
    population = new ArrayList<>(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution() ;
      population.add(newSolution);
    }
  }

  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    for (int i = 0 ; i < population.size(); i++) {
      problem.evaluate(population.get(i)) ;
    }

    return population ;
  }

  protected boolean stoppingCondition() {
    return iterations == maxIterations;
  }

  protected void addLastRankedSolutions(Ranking ranking, int rank) throws
    JMetalException {
    List<Solution> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

    int i = 0 ;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank)  {
    List<Solution> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void computeRanking(List<Solution> solutionSet) {
    ranking.computeRanking(solutionSet) ;
  }

  protected boolean populationIsNotFull() {
    return population.size() < populationSize;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank) {
    return ranking.getSubfront(rank).size() < (populationSize - population.size()) ;
  }

  protected List<Solution> getNonDominatedSolutions(List<Solution> solutionSet) {
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }

  protected void tearDown() {
  }
} 
