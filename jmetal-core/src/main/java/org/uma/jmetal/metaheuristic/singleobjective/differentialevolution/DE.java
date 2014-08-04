//  DE.java
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

package org.uma.jmetal.metaheuristic.singleobjective.differentialevolution;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * This class implements a differential evolution algorithm.
 */
public class DE extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = 7663009441358542943L;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public DE() {
	  super();
  } // gDE

  /**
   * Runs of the DE algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;

    SolutionSet population;
    SolutionSet offspringPopulation;

    Operator selectionOperator;
    Operator crossoverOperator;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0);

    Solution parent[];

    //Read the parameters
    populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();

    selectionOperator = operators.get("selection");
    crossoverOperator = operators.get("crossover");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      problem.evaluate(newSolution);
      problem.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    } //for

    // Generations ...
    population.sort(comparator);
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet
      offspringPopulation = new SolutionSet(populationSize);

      for (int i = 0; i < populationSize; i++) {
        // Obtain parents. Two parameters are required: the population and the
        //                 index of the current individual
        parent = (Solution[]) selectionOperator.execute(new Object[] {population, i});

        Solution child;

        // Crossover. Two parameters are required: the current individual and the
        //            array of parents
        child = (Solution) crossoverOperator.execute(new Object[] {population.get(i), parent});

        problem.evaluate(child);

        evaluations++;

        if (comparator.compare(population.get(i), child) < 0) {
          offspringPopulation.add(new Solution(population.get(i)));
        } else {
          offspringPopulation.add(child);
        }
      }

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

    JMetalLogger.logger.log(Level.INFO, "Evaluations: " + evaluations);

    return resultPopulation;
  }
}
