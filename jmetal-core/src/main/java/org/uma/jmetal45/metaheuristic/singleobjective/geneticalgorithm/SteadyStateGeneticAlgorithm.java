//  SteadyStateGeneticAlgorithm.java
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

package org.uma.jmetal45.metaheuristic.singleobjective.geneticalgorithm;

import org.uma.jmetal45.core.*;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.operator.selection.BinaryTournament;
import org.uma.jmetal45.operator.selection.Selection;
import org.uma.jmetal45.operator.selection.WorstSolutionSelection;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.comparator.ObjectiveComparator;

import java.util.Comparator;

/**
 * Class implementing a steady-state genetic algorithm
 */
public class SteadyStateGeneticAlgorithm implements Algorithm {
  private static final long serialVersionUID = -6340093758636629106L;

  private Problem problem ;

  private int populationSize;
  private int maxEvaluations;

  private Operator mutation;
  private Operator crossover;
  private Operator selection;

  /** Constructor */
  private SteadyStateGeneticAlgorithm(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossover = builder.crossover ;
    this.mutation = builder.mutation ;
    this.selection = builder.selection ;
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public Operator getMutation() {
    return mutation;
  }

  public Operator getCrossover() {
    return crossover;
  }

  public Operator getSelection() {
    return selection;
  }

  /** Execute method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations;

    SolutionSet population;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0);

    Operator findWorstSolution ;
    findWorstSolution = new WorstSolutionSelection.Builder(comparator)
            .build() ;

    // Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    // Create the initial population
    Solution newIndividual;
    for (int i = 0; i < populationSize; i++) {
      newIndividual = new Solution(problem);
      problem.evaluate(newIndividual);
      evaluations++;
      population.add(newIndividual);
    }

    // main loop
    while (evaluations < maxEvaluations) {
      Solution[] parents = new Solution[2];

      // Selection
      parents[0] = (Solution) selection.execute(population);
      parents[1] = (Solution) selection.execute(population);

      // Crossover
      Solution[] offspring = (Solution[]) crossover.execute(parents);

      // Mutation
      mutation.execute(offspring[0]);

      // Evaluation of the new individual
      problem.evaluate(offspring[0]);

      evaluations++;

      // Replacement: replace the last individual is the new one is better
      int worstIndividual = (Integer) findWorstSolution.execute(population);

      if (comparator.compare(population.get(worstIndividual), offspring[0]) > 0) {
        population.remove(worstIndividual);
        population.add(offspring[0]);
      }
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(population.best(comparator));

    return resultPopulation;
  }

  /** Builder class */
  public static class Builder {
    private final Problem problem ;
    private int populationSize;
    private int maxEvaluations;

    private Mutation mutation;
    private Crossover crossover;
    private Selection selection;

    public Builder(Problem problem) {
      this.problem = problem ;
      populationSize = 100 ;
      maxEvaluations = 25000 ;

      crossover = new SBXCrossover.Builder()
              .setProbability(0.9)
              .setDistributionIndex(20.0)
              .build() ;

      mutation = new PolynomialMutation.Builder()
              .setProbability(1.0 / problem.getNumberOfVariables())
              .setDistributionIndex(20.0)
              .build() ;

      selection = new BinaryTournament.Builder()
              .build() ;

    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;

      return this ;
    }

    public Builder setMutation(Mutation mutation) {
      this.mutation = mutation;

      return this ;
    }

    public Builder setCrossover(Crossover crossover) {
      this.crossover = crossover;

      return this ;
    }

    public Builder setSelection(Selection selection) {
      this.selection = selection;

      return this ;
    }

    public SteadyStateGeneticAlgorithm build() {
      return new SteadyStateGeneticAlgorithm(this) ;
    }
  }
}
