//  acGA.java
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

package org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BestSolutionSelection;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;

/**
 * Class implementing an asynchronous cellular genetic algorithm
 */
public class AsynchronousCellularGeneticAlgorithm implements Algorithm {
  private static final long serialVersionUID = -3128274013412638310L;

  private Problem problem ;

  private int populationSize ;
  private int maxEvaluations ;
  private Operator mutation;
  private Operator crossover;
  private Operator selection;

  /** Constructor */
  private AsynchronousCellularGeneticAlgorithm(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossover = builder.crossover ;
    this.mutation = builder.mutation ;
    this.selection = builder.selection ;  }

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

    public AsynchronousCellularGeneticAlgorithm build() {
      return new AsynchronousCellularGeneticAlgorithm(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    SolutionSet[] neighbors;
    SolutionSet population;
    Neighborhood neighborhood;

    Comparator<Solution> comparator;
    comparator = new ObjectiveComparator(0);

    Operator findBestSolution = new BestSolutionSelection.Builder(comparator)
            .build() ;

    //HashMap<String, Object> selectionParameters = new HashMap<String, Object>();
    //selectionParameters.put("comparator", comparator);
    //findBestSolution = new BestSolutionSelection(selectionParameters);

    //Initialize the variables
    int evaluations ;
    evaluations = 0;
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

    population = new SolutionSet(populationSize);
    //Create the initial population
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem);
      problem.evaluate(solution);
      population.add(solution);
      solution.setLocation(i);
      evaluations++;
    }

    boolean solutionFound = false;
    while ((evaluations < maxEvaluations) && !solutionFound) {
      for (int ind = 0; ind < population.size(); ind++) {
        Solution individual = new Solution(population.get(ind));

        Solution[] parents = new Solution[2];
        Solution[] offSpring;

        neighbors[ind] = neighborhood.getEightNeighbors(population, ind);
        neighbors[ind].add(individual);

        //parents
        parents[0] = (Solution) selection.execute(neighbors[ind]);
        parents[1] = (Solution) selection.execute(neighbors[ind]);

        //Create a new solution, using genetic operator mutation and crossover
        if (crossover != null) {
          offSpring = (Solution[]) crossover.execute(parents);
        } else {
          offSpring = new Solution[1];
          offSpring[0] = new Solution(parents[0]);
        }
        mutation.execute(offSpring[0]);

        //->Evaluate offspring and constraints
        problem.evaluate(offSpring[0]);
        //problem.evaluateConstraints(offSpring[0]);
        evaluations++;

        if (comparator.compare(individual, offSpring[0]) > 0) {
          population.replace(ind, offSpring[0]);
        }
      }
    }

    Solution bestSolution = population.best(comparator);
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(bestSolution);

    return resultPopulation;
  }
}

