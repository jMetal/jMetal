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

package org.uma.jmetal.algorithm.multiobjective.gde3;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** This class implements the GDE3 algorithm */
public class GDE3 implements Algorithm<List<DoubleSolution>> {
  private ContinuousProblem problem ;
  protected int populationSize;
  protected int maxIterations;
  protected int iterations;
  
  protected List<DoubleSolution> population;
  protected List<DoubleSolution> offspringPopulation;

  protected DifferentialEvolutionCrossover crossoverOperator;
  protected DifferentialEvolutionSelection selectionOperator;

  protected Comparator dominanceComparator;

  protected Ranking ranking ;
  protected DensityEstimator crowdingDistance;

  /** Constructor */
  public GDE3(Builder builder) {
    problem = builder.problem;
    maxIterations = builder.maxIterations;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    populationSize = builder.populationSize;
    dominanceComparator = new DominanceComparator();

    dominanceComparator = new DominanceComparator();
    iterations = 0 ;
    ranking = new DominanceRanking() ;
    crowdingDistance = new CrowdingDistance() ;
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
    
    protected DifferentialEvolutionCrossover crossoverOperator;
    protected DifferentialEvolutionSelection selectionOperator;

    public Builder(ContinuousProblem problem) {
      this.problem = problem ;
      selectionOperator = new DifferentialEvolutionSelection.Builder().build() ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setCrossover(DifferentialEvolutionCrossover crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder setSelection(DifferentialEvolutionSelection selection) {
      selectionOperator = selection ;

      return this ;
    }

    public GDE3 build() {
      return new GDE3(this) ;
    }
  }

  /** Execute() method  */
  public void run() {
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
        selectionOperator.setIndex(i);
        List<DoubleSolution> parents = selectionOperator.execute(population);

        crossoverOperator.setCurrentSolution(population.get(i));
        List<DoubleSolution>children = crossoverOperator.execute(parents);

        tmpSolutionSet.add(children.get(0));
      }

      evaluatePopulation(tmpSolutionSet);

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
      ranking.computeRanking(offspringPopulation);

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

      iterations++ ;
    }

    tearDown();
  }

  @Override
  public List<DoubleSolution> getResult() {
    return getNonDominatedSolutions(population) ;
  }

  protected void createInitialPopulation() {
    population = new ArrayList<>(populationSize);

    DoubleSolution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution();
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

  protected void addLastRankedSolutions(Ranking ranking, int rank) {
    List<DoubleSolution> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

    int i = 0 ;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank)  {
    List<DoubleSolution> front ;

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

  protected List<DoubleSolution> getNonDominatedSolutions(List<DoubleSolution> solutionSet) {
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }

  protected void tearDown() {
  }
} 
