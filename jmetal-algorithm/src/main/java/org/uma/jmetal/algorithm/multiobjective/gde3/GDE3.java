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

import org.uma.jmetal.algorithm.impl.AbstractDifferentialEvolution;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.*;

/** This class implements the GDE3 algorithm */
public class GDE3 extends AbstractDifferentialEvolution<List<DoubleSolution>> {
  private ContinuousProblem problem ;
  protected int populationSize;
  protected int maxIterations;
  protected int iterations;

  protected DifferentialEvolutionCrossover crossoverOperator;
  protected DifferentialEvolutionSelection selectionOperator;

  protected Comparator dominanceComparator;

  protected Ranking ranking ;
  protected DensityEstimator crowdingDistance;

  protected SolutionListEvaluator evaluator ;

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

    evaluator = new SequentialSolutionListEvaluator() ;
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

  @Override
  protected void initProgress() {
    iterations = 1 ;
  }

  @Override
  protected void updateProgress() {
    iterations++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    population = evaluator.evaluate(population, problem) ;

    return population ;
  }

  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    List<DoubleSolution> matingPopulation = new LinkedList<>() ;
    for (int i = 0; i < populationSize; i++) {
      // Obtain parents. Two parameters are required: the population and the
      //                 index of the current individual
      selectionOperator.setIndex(i);
      List<DoubleSolution> parents = selectionOperator.execute(population);

      matingPopulation.addAll(parents) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
    List<DoubleSolution> offspringPopulation = new ArrayList<>() ;

    for (int i = 0; i < populationSize; i++) {
      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      List<DoubleSolution>parents = new ArrayList<>(3) ;
      for (int j = 0 ;  j < 3 ; j++) {
        parents.add(matingPopulation.get(0)) ;
        matingPopulation.remove(0) ;
      }

      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      List<DoubleSolution>children = crossoverOperator.execute(parents);

      offspringPopulation.add(children.get(0));
    }

    return offspringPopulation;
  }

  @Override
  protected List<DoubleSolution> replacement(List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    List<DoubleSolution> tmpList = new ArrayList<>() ;
    for (int i = 0; i < populationSize; i++) {
      // Dominance test
      DoubleSolution child = offspringPopulation.get(i) ;
      int result;
      result = dominanceComparator.compare(population.get(i), child);
      if (result == -1) {
        // Solution i dominates child
        tmpList.add(population.get(i));
      } else if (result == 1) {
        // child dominates
        tmpList.add(child);
      } else {
        // the two solutions are non-dominated
        tmpList.add(child);
        tmpList.add(population.get(i));
      }
    }
    Ranking ranking = computeRanking(tmpList);
    List<DoubleSolution> pop = crowdingDistanceSelection(ranking);

    return pop;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }


  protected Ranking computeRanking(List<DoubleSolution> solutionList) {
    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }

  protected List<DoubleSolution> crowdingDistanceSelection(Ranking ranking) {
    CrowdingDistance crowdingDistance = new CrowdingDistance() ;
    List<DoubleSolution> population = new ArrayList<>(populationSize) ;
    int rankingIndex = 0;
    while (populationIsNotFull(population)) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population ;
  }

  protected boolean populationIsNotFull(List<DoubleSolution> population) {
    return population.size() < populationSize;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank, List<DoubleSolution> population) {
    return ranking.getSubfront(rank).size() < (populationSize - population.size()) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank, List<DoubleSolution> population) {
    List<DoubleSolution> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking ranking, int rank, List<DoubleSolution>population) {
    List<DoubleSolution> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

    int i = 0 ;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected List<DoubleSolution> getNonDominatedSolutions(List<DoubleSolution> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList) ;
  }
} 
