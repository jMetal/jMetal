//  ElitistES.java
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

package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class implementing a (mu + lambda) Evolution Strategy (lambda must be divisible by mu)
 */
public class NonElitistEvolutionStrategy extends AbstractEvolutionStrategy<Solution, Solution> {
  private Problem problem ;

  private int mu;
  private int lambda;
  private int maxEvaluations ;
  private int evaluations ;
  private MutationOperator mutation ;

  private  Comparator<Solution> comparator;

  /** Constructor */
  private NonElitistEvolutionStrategy(Builder builder) {
    this.problem = builder.problem ;
    this.mu = builder.mu ;
    this.lambda = builder.lambda ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.mutation = builder.mutation ;

    comparator = new ObjectiveComparator(0);
  }

  /* Getters */
  public int getMu() {
    return mu;
  }

  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public MutationOperator getMutation() {
    return mutation;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int mu;
    private int lambda;
    private int maxEvaluations ;
    private MutationOperator mutation ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.mu = 1 ;
      this.lambda = 10 ;
      this.maxEvaluations = 250000 ;
      this.mutation = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    }

    public Builder setMu(int mu) {
      this.mu = mu ;

      return this ;
    }

    public Builder setLambda(int lambda) {
      this.lambda = lambda ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setMutationOperator(MutationOperator mutation) {
      this.mutation = mutation ;

      return this ;
    }

    public NonElitistEvolutionStrategy build() {
      return new NonElitistEvolutionStrategy(this) ;
    }
  }



  @Override
  protected void initProgress() {
    evaluations = 1 ;
  }

  @Override
  protected void updateProgress() {
    evaluations += lambda ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(mu) ;
    for (int i = 0; i < mu; i++) {
      Solution newIndividual = problem.createSolution() ;
      population.add(newIndividual);
    }

    return population;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    for (Solution solution: population) {
      problem.evaluate(solution);
    }

    return population;
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    return population ;
//    List<Solution> matingPopulation = new ArrayList<>(mu) ;
//    for (Solution solution: population) {
//      matingPopulation.add(solution.copy()) ;
//    }
//    return matingPopulation ;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> offspringPopulation = new ArrayList<>(lambda) ;
    for (int i = 0; i < mu; i++) {
      for (int j = 0; j < lambda/mu; j++) {
        Solution offspring = population.get(i).copy();
        mutation.execute(offspring);
        offspringPopulation.add(offspring);
      }
    }

    return offspringPopulation ;
  }

  @Override
  protected List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation) {
    offspringPopulation.sort(comparator);

    List<Solution> newPopulation = new ArrayList<>(mu) ;
    for (int i = 0; i < mu; i++) {
      newPopulation.add(offspringPopulation.get(i));
    }
    return newPopulation;
  }

  @Override
  public Solution getResult() {
    return getPopulation().get(0) ;
  }
}
