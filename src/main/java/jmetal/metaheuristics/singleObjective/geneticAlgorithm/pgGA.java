//  pgGA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
import jmetal.util.parallel.SynchronousParallelTaskExecutor;

import java.util.Comparator;
import java.util.List;

/**
 * A multithreaded generational genetic algorithm
 */

public class pgGA extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = -409634466251175115L;
  SynchronousParallelTaskExecutor parallelEvaluator_;

  /**
   * Constructor
   *
   * @param problem   Problem to solve
   * @param evaluator Parallel evaluator
   */
  public pgGA() {
    super();
  } // pgGA

  public void seteEvaluator(SynchronousParallelTaskExecutor evaluator) {
	  parallelEvaluator_ = evaluator;
  }
  
  /**
   * Runs the pgGA algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws jmetal.util.JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;
    int numberOfThreads;

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0); // Single objective comparator

    //Read the parameters
    populationSize = (Integer) getInputParameter("populationSize");
    maxEvaluations = (Integer) getInputParameter("maxEvaluations");

    parallelEvaluator_.start(problem_);

    //Initialize the variables
    population = new SolutionSet(populationSize);
    offspringPopulation = new SolutionSet(populationSize);

    evaluations = 0;

    //Read the operators
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      parallelEvaluator_.addTask(new Object[] {newSolution});
    }

    List<Solution> solutionList = (List<Solution>) parallelEvaluator_.parallelExecution();

    for (Solution solution : solutionList) {
      population.add(solution);
      evaluations++;
    }

    population.sort(comparator);

    // Generations 
    while (evaluations < maxEvaluations) {
      // Copy the best two individuals to the offspring population
      offspringPopulation.add(new Solution(population.get(0)));
      offspringPopulation.add(new Solution(population.get(1)));

      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize / 2) - 1; i++) {
        if (evaluations < maxEvaluations) {
          //obtain parents
          parents[0] = (Solution) selectionOperator.execute(population);
          parents[1] = (Solution) selectionOperator.execute(population);
          Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
          mutationOperator.execute(offSpring[0]);
          mutationOperator.execute(offSpring[1]);
          parallelEvaluator_.addTask(new Object[] {offSpring[0]});
          parallelEvaluator_.addTask(new Object[] {offSpring[1]});
        }
      }

      List<Solution> solutions = (List<Solution>) parallelEvaluator_.parallelExecution();

      for (Solution solution : solutions) {
        offspringPopulation.add(solution);
        evaluations++;
      }

      // The offspring population becomes the new current population
      population.clear();
      for (int i = 0; i < populationSize; i++) {
        population.add(offspringPopulation.get(i));
      }
      offspringPopulation.clear();
      population.sort(comparator);
    } // while

    parallelEvaluator_.stop();

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(population.get(0));

    Configuration.logger_.info("Evaluations: " + evaluations);
    return resultPopulation;
  } // execute
} // pgGA
