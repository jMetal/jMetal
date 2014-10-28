package org.uma.jmetal.algorithm.impl.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm2;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class SteadyStateGeneticAlgorithm2 extends AbstractGeneticAlgorithm2 {
  private Comparator<Solution<?>> comparator = new ObjectiveComparator(0) ;

  public SteadyStateGeneticAlgorithm2() {
    maxIterations = 250 ;
    populationSize = 100 ;
  }

  @Override
  protected boolean stoppingCondition() {
    return (iterations >= maxIterations) ;
  }

  @Override
  protected List<Solution<?>> createInitialPopulation() {
    population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      Solution<?> newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  public Object getResult() {
    return population.get(0);
  }

  @Override
  protected List<Solution<?>> replacement(List pop, List offspringPop) {
    pop.sort(comparator);
    offspringPop.add(pop.get(0)) ;
    offspringPop.add(pop.get(1)) ;
    offspringPop.sort(comparator);
    offspringPop.remove(offspringPop.size() - 1) ;
    offspringPop.remove(offspringPop.size() - 1) ;

    return offspringPop;
  }

  @Override
  protected List<Solution<?>> reproduction(List population) {
    offspringPopulation = new ArrayList<>(populationSize+2) ;
    for (int i = 0; i < populationSize; i+=2) {
      Solution<?>[] parents = new Solution<?>[2];
      parents[0] = (Solution<?>)matingPopulation.get(i) ;
      parents[1] = (Solution<?>)matingPopulation.get(i+1) ;

      Solution<?>[] offspring = (Solution<?>[]) crossoverOperator.execute(parents);
      mutationOperator.execute(offspring[0]) ;
      mutationOperator.execute(offspring[1]) ;
    }
    return offspringPopulation ;
  }

  @Override
  protected List<Solution<?>> selection(List population) {
    matingPopulation = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      matingPopulation.set(i, selectionOperator.execute(population)) ;
    }

    return matingPopulation;
  }

  @Override
  protected void evaluatePopulation(List population) {
    for (Solution solution : (List<Solution<?>>)population) {
      problem.evaluate(solution);
    }
  }
}
