//  GenerationalGA.java
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

package org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Comparator;

/**
 * Class implementing a generational genetic algorithm
 */
public class GenerationalGeneticAlgorithm implements Algorithm {
  private static final long serialVersionUID = -8566068150403243344L;

  private Problem problem ;

  private int populationSize;
  private int maxEvaluations;

  private Operator mutation;
  private Operator crossover;
  private Operator selection;

  private SolutionSetEvaluator evaluator;

  /** Constructor */
  private GenerationalGeneticAlgorithm(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossover = builder.crossover ;
    this.mutation = builder.mutation ;
    this.selection = builder.selection ;
    this.evaluator = builder.evaluator ;
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

  public SolutionSetEvaluator getEvaluator() {
    return evaluator;
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int evaluations;

    SolutionSet population;
    SolutionSet offspringPopulation;

    Comparator<Solution> comparator;
    // Single objective comparator
    comparator = new ObjectiveComparator(0);

    // Initialize the variables
    population = new SolutionSet(populationSize);
    offspringPopulation = new SolutionSet(populationSize);

    evaluations = 0;

    // Create the initial population
    Solution newIndividual;
    for (int i = 0; i < populationSize; i++) {
      newIndividual = new Solution(problem);
      population.add(newIndividual);
    }

    evaluator.evaluate(population, problem) ;

    evaluations += populationSize ;

    // Sort population
    population.sort(comparator);
    while (evaluations < maxEvaluations) {

      // Copy the best two individuals to the offspring population
      offspringPopulation.add(new Solution(population.get(0)));
      offspringPopulation.add(new Solution(population.get(1)));

      // Reproductive cycle
      for (int i = 0; i < (populationSize / 2 - 1); i++) {
        // Selection
        Solution[] parents = new Solution[2];

        parents[0] = (Solution) selection.execute(population);
        parents[1] = (Solution) selection.execute(population);

        // Crossover
        Solution[] offspring = (Solution[]) crossover.execute(parents);

        // Mutation
        mutation.execute(offspring[0]);
        mutation.execute(offspring[1]);

        //  The two new individuals are inserted in the offspring
        //                population
        offspringPopulation.add(offspring[0]);
        offspringPopulation.add(offspring[1]);
      }

      evaluator.evaluate(offspringPopulation, problem) ;
      evaluations += offspringPopulation.size() ;

      // The offspring population becomes the new current population
      population.clear();
      for (int i = 0; i < populationSize; i++) {
        population.add(offspringPopulation.get(i));
      }
      offspringPopulation.clear();
      population.sort(comparator);
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(population.get(0));

    evaluator.shutdown();

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

    private SolutionSetEvaluator evaluator;

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

      evaluator = new SequentialSolutionSetEvaluator() ;
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

    public Builder setEvaluator(SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator;

      return this ;
    }

    public GenerationalGeneticAlgorithm build() {
      return new GenerationalGeneticAlgorithm(this) ;
    }
  }
} 
