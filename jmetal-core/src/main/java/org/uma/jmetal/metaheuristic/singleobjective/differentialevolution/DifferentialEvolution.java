//  DifferentialEvolution.java
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
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * This class implements a differential evolution algorithm.
 */
public class DifferentialEvolution implements Algorithm {
  private static final long serialVersionUID = 7663009441358542943L;

  private Problem problem ;

  private int populationSize;
  private int maxEvaluations;
  private Crossover crossover ;
  private Selection selection ;

  /** Constructor */
  private DifferentialEvolution(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossover = builder.crossover ;
    this.selection = builder.selection ;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int populationSize;
    private int maxEvaluations;
    private Crossover crossover ;
    private Selection selection ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.populationSize = 100 ;
      this.maxEvaluations = 20000 ;
      this.crossover = new DifferentialEvolutionCrossover.Builder()
              .setCr(0.5)
              .setF(0.5)
              .setVariant("rand/1/bin")
              .build() ;

      this.selection = new DifferentialEvolutionSelection.Builder()
              .build() ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setCrossover (Crossover crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public Builder setSelection (Selection selection) {
      this.selection = selection ;

      return this ;
    }

    public DifferentialEvolution build() {
      return new DifferentialEvolution(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    SolutionSet population;
    SolutionSet offspringPopulation;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0);

    population = new SolutionSet(populationSize);

    int evaluations;
    evaluations = 0;

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      problem.evaluate(newSolution);
      problem.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    }

    population.sort(comparator);
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet
      offspringPopulation = new SolutionSet(populationSize);

      for (int i = 0; i < populationSize; i++) {
        // Obtain parents. Two parameters are required: the population and the
        //                 index of the current individual
        Solution parent[];
        parent = (Solution[]) selection.execute(new Object[] {population, i});

        Solution child;
        // Crossover. Two parameters are required: the current individual and the
        //            array of parents
        child = (Solution) crossover.execute(new Object[] {population.get(i), parent});

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
