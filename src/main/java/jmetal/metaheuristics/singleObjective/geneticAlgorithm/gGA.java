//  gGA.java
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

package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.core.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;

/**
 * Class implementing a generational genetic algorithm
 */
public class gGA extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = -8566068150403243344L;

  /**
   * Constructor
   * Create a new GGA instance.
   *
   * @param problem Problem to solve.
   */
  public gGA() {
    super();
  } // GGA

  /**
   * Execute the GGA algorithm
   *
   * @throws JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;

    SolutionSet population;
    SolutionSet offspringPopulation;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0); // Single objective comparator

    // Read the params
    populationSize = (Integer) this.getInputParameter("populationSize");
    maxEvaluations = (Integer) this.getInputParameter("maxEvaluations");

    // Initialize the variables
    population = new SolutionSet(populationSize);
    offspringPopulation = new SolutionSet(populationSize);

    evaluations = 0;

    // Read the operators
    mutationOperator = this.operators_.get("mutation");
    crossoverOperator = this.operators_.get("crossover");
    selectionOperator = this.operators_.get("selection");

    // Create the initial population
    Solution newIndividual;
    for (int i = 0; i < populationSize; i++) {
      newIndividual = new Solution(problem_);
      problem_.evaluate(newIndividual);
      evaluations++;
      population.add(newIndividual);
    } //for       

    // Sort population
    population.sort(comparator);
    while (evaluations < maxEvaluations) {
      if ((evaluations % 10) == 0) {
        Configuration.logger_.info(evaluations + ": " + population.get(0).getObjective(0));
      } //

      // Copy the best two individuals to the offspring population
      offspringPopulation.add(new Solution(population.get(0)));
      offspringPopulation.add(new Solution(population.get(1)));

      // Reproductive cycle
      for (int i = 0; i < (populationSize / 2 - 1); i++) {
        // Selection
        Solution[] parents = new Solution[2];

        parents[0] = (Solution) selectionOperator.execute(population);
        parents[1] = (Solution) selectionOperator.execute(population);

        // Crossover
        Solution[] offspring = (Solution[]) crossoverOperator.execute(parents);

        // Mutation
        mutationOperator.execute(offspring[0]);
        mutationOperator.execute(offspring[1]);

        // Evaluation of the new individual
        problem_.evaluate(offspring[0]);
        problem_.evaluate(offspring[1]);

        evaluations += 2;

        // Replacement: the two new individuals are inserted in the offspring
        //                population
        offspringPopulation.add(offspring[0]);
        offspringPopulation.add(offspring[1]);
      } // for

      // The offspring population becomes the new current population
      population.clear();
      for (int i = 0; i < populationSize; i++) {
        population.add(offspringPopulation.get(i));
      }
      offspringPopulation.clear();
      population.sort(comparator);
    } // while

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(population.get(0));

    Configuration.logger_.info("Evaluations: " + evaluations);
    return resultPopulation;
  } // execute
} // gGA
