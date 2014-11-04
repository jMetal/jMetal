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

package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractDifferentialEvolution;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a differential evolution algorithm.
 */
public class DifferentialEvolution2 extends AbstractDifferentialEvolution<Solution> {
  private ContinuousProblem problem ;

  private int populationSize;
  private int maxEvaluations;
  private int evaluations ;

  private List<DoubleSolution> population;
  private List<DoubleSolution> offspringPopulation;

  private SolutionListEvaluator evaluator ;

  private Comparator<Solution> comparator;

  /** Constructor */
  private DifferentialEvolution2(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossoverOperator = builder.crossoverOperator ;
    this.selectionOperator = builder.selectionOperator ;

    this.evaluator = new SequentialSolutionListEvaluator() ;
    comparator = new ObjectiveComparator(0);
  }

  /** Builder class */
  public static class Builder {
    private ContinuousProblem problem ;
    private int populationSize;
    private int maxEvaluations;
    private DifferentialEvolutionCrossover crossoverOperator ;
    private DifferentialEvolutionSelection selectionOperator ;

    public Builder(ContinuousProblem problem) {
      this.problem = problem ;
      this.populationSize = 100 ;
      this.maxEvaluations = 20000 ;
      this.crossoverOperator = new DifferentialEvolutionCrossover.Builder()
              .setCr(0.5)
              .setF(0.5)
              .setVariant("rand/1/bin")
              .build() ;

      this.selectionOperator = new DifferentialEvolutionSelection.Builder()
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
      this.crossoverOperator = crossover ;

      return this ;
    }

    public Builder setSelection (DifferentialEvolutionSelection selection) {
      this.selectionOperator = selection ;

      return this ;
    }

    public DifferentialEvolution2 build() {
      return new DifferentialEvolution2(this) ;
    }
  }

  @Override
  protected void initProgress() {
    evaluations = populationSize ;
  }

  @Override
  protected void updateProgress() {
    evaluations += populationSize ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    List<DoubleSolution> pop = evaluator.evaluate(population, problem) ;

    return pop ;
  }

  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    List<DoubleSolution> matingPopulation = new LinkedList<>() ;
    for (int i = 0; i < populationSize; i++) {
      // Obtain parents. Two parameters are required: the population and the
      //                 index of the current individual
      selectionOperator.setIndex(i);
      List<DoubleSolution> parents = selectionOperator.execute(population);

      matingPopulation.addAll(parents) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
    List<DoubleSolution> offspringPopulation = new ArrayList<>() ;

    for (int i = 0; i < populationSize; i++) {
      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      List<DoubleSolution>parents = new ArrayList<>(3) ;
      for (int j = 0 ;  j < 3 ; j++) {
        parents.add(matingPopulation.get(0)) ;
        matingPopulation.remove(0) ;
      }

      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      List<DoubleSolution>children = crossoverOperator.execute(parents);

      offspringPopulation.add(children.get(0));
    }

    return offspringPopulation;
  }

  @Override
  protected List<DoubleSolution> replacement(List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    List<DoubleSolution> pop = new ArrayList<>() ;

    for (int i = 0; i < populationSize; i++) {
      if (comparator.compare(population.get(i), offspringPopulation.get(i)) < 0) {
        pop.add(population.get(i));
      } else {
        pop.add(offspringPopulation.get(0));
      }
    }

    return null;
  }

  @Override
  public DoubleSolution getResult() {
    // Return a population with the best individual
    population.sort(comparator);

    return population.get(0) ;
  }



/*
  @Override
  public void run() throws JMetalException {
    Comparator<Solution> comparator;
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

*/
}
