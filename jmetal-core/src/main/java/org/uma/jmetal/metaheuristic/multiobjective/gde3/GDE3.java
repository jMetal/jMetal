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

package org.uma.jmetal.metaheuristic.multiobjective.gde3;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Comparator;

/**
 * This class implements the GDE3 algorithm.
 */
public class GDE3 extends Algorithm {
  private static final long serialVersionUID = -8007862618252202475L;

  protected SolutionSetEvaluator evaluator;

  protected int populationSize;
  protected int maxIterations;
  protected int iterations;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  protected Comparator dominanceComparator;

  protected Distance distance;

  /**
   * @deprecated
   */
  @Deprecated
  public GDE3(SolutionSetEvaluator evaluator) {
    super();
    this.evaluator = evaluator ;
    distance = new Distance();
    dominanceComparator = new DominanceComparator();
    iterations = 0 ;
  }

  /** Constructor */
  public GDE3(Builder builder) {
    super() ;
    problem = builder.problem;
    maxIterations = builder.maxIterations;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    populationSize = builder.populationSize;
    evaluator = builder.evaluator;

    distance = new Distance();
    dominanceComparator = new DominanceComparator();
  }

  /** execute() method  */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    createInitialPopulation();
    population = evaluatePopulation(population);

    // Generations ...
    while (!stoppingCondition()) {

      // Create the offSpring solutionSet
      offspringPopulation = new SolutionSet(populationSize * 2);
      SolutionSet tmpSolutionSet = new SolutionSet(populationSize) ;

      for (int i = 0; i < populationSize; i++) {
        // Obtain parents. Two parameters are required: the population and the 
        //                 index of the current individual
        Solution[] parent = (Solution[]) selectionOperator.execute(new Object[] {population, i});

        Solution child;
        // Crossover. Two parameters are required: the current individual and the 
        //            array of parents
        child = (Solution) crossoverOperator.execute(new Object[] {population.get(i), parent});

        tmpSolutionSet.add(child);
      }
      tmpSolutionSet = evaluatePopulation(tmpSolutionSet);

      for (int i = 0; i < populationSize; i++) {
        // Dominance org.uma.test
        Solution child = tmpSolutionSet.get(i) ;
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
      Ranking ranking = new Ranking(offspringPopulation);

      population.clear();
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

      iterations++ ;
    }

    tearDown();

    return getNonDominatedSolutions() ;
  }

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

  @Deprecated
  void readParameterSettings() {
    populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
    maxIterations = ((Integer) this.getInputParameter("maxIterations")).intValue();

    selectionOperator = operators.get("selection");
    crossoverOperator = operators.get("crossover");
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
    return evaluator.evaluate(population, problem) ;
  }

  protected boolean stoppingCondition() {
    return iterations == maxIterations;
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

  protected SolutionSet getNonDominatedSolutions() throws JMetalException {
    return new Ranking(population).getSubfront(0);
  }

  protected void tearDown() {
    evaluator.shutdown();
  }

  /** Builder class */
  public static class Builder {
    protected SolutionSetEvaluator evaluator;
    protected Problem problem;

    protected int populationSize;
    protected  int maxIterations;

    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
      this.problem = problem ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder maxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder evaluator(SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public GDE3 build() {
      return new GDE3(this) ;
    }
  }
} 
