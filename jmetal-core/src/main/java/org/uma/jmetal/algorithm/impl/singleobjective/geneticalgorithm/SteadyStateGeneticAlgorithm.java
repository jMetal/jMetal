package org.uma.jmetal.algorithm.impl.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class SteadyStateGeneticAlgorithm extends AbstractGeneticAlgorithm {
  Comparator<Solution<?>> comparator = new ObjectiveComparator(0) ;

  @Override
  protected void initialization() {
    population = new ArrayList<Solution<?>>() ;
    offspringPopulation = new ArrayList<Solution<?>>() ;
  }

  @Override
  protected boolean stoppingCondition() {
    return (evaluations >= maxEvaluations) ;
  }

  @Override
  protected void createInitialPopulation() {
    for (int i = 0; i < populationSize; i++) {
      Solution<?> newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
  }

  
  @Override
  protected List<Solution<?>> evaluatePopulation(List population) {
    for (Object solution: population) {
      problem.evaluate((Solution)solution);

      increaseEvaluations();
    }
    return population;
  }

  @Override
  public Object getResult() {
    return null;
  }

  @Override
  protected void solutionSelection(List population) {

  }


}
