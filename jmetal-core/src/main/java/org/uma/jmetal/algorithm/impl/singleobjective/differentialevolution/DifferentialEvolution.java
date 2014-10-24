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

package org.uma.jmetal.algorithm.impl.singleobjective.differentialevolution;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a differential evolution algorithm.
 */
public class DifferentialEvolution implements Algorithm<List<DoubleSolution>> {
  private static final long serialVersionUID = 7663009441358542943L;

  private ContinuousProblem problem ;

  private int populationSize;
  private int maxEvaluations;
  private DifferentialEvolutionCrossover crossover ;
  private DifferentialEvolutionSelection selection ;

  private List<DoubleSolution> population;
  private List<DoubleSolution> offspringPopulation;

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
    private ContinuousProblem problem ;
    private int populationSize;
    private int maxEvaluations;
    private DifferentialEvolutionCrossover crossover ;
    private DifferentialEvolutionSelection selection ;

    public Builder(ContinuousProblem problem) {
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

    public Builder setCrossover (DifferentialEvolutionCrossover crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public Builder setSelection (DifferentialEvolutionSelection selection) {
      this.selection = selection ;

      return this ;
    }

    public DifferentialEvolution build() {
      return new DifferentialEvolution(this) ;
    }
  }

  /** Execute() method */
  @Override
  public void run() throws JMetalException {
    Comparator<Solution<?>> comparator;
    comparator = new ObjectiveComparator(0);

    population = new ArrayList<>(populationSize);

    int evaluations;
    evaluations = 0;

    DoubleSolution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution() ;
      problem.evaluate(newSolution);
      evaluations++;
      population.add(newSolution);
    }

    population.sort(comparator);
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet
      offspringPopulation = new ArrayList<>(populationSize);

      for (int i = 0; i < populationSize; i++) {
        // Obtain parents. Two parameters are required: the population and the
        //                 index of the current individual
        selection.setIndex(i);
        List<DoubleSolution> parents = selection.execute(population);

        crossover.setCurrentSolution(population.get(i));
        List<DoubleSolution>children = crossover.execute(parents);

        problem.evaluate(children.get(0));

        evaluations++;

        if (comparator.compare(population.get(i), children.get(0)) < 0) {
          offspringPopulation.add(population.get(i));
        } else {
          offspringPopulation.add(children.get(0));
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
  }

  @Override
  public List<DoubleSolution> getResult() {
    // Return a population with the best individual
    List<DoubleSolution> resultPopulation = new ArrayList<>(1);
    resultPopulation.add(population.get(0));

    return resultPopulation ;
  }
}
