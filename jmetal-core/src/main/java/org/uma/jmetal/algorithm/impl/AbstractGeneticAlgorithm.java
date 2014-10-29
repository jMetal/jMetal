package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractGeneticAlgorithm<Result> implements Algorithm <Result> {

  protected int iterations ;
  protected int maxIterations ;
  protected int populationSize ;
  protected Problem problem ;

  protected List<Solution<?>> population ;
  protected List<Solution<?>> offspringPopulation ;
  protected List<Solution<?>> matingPopulation ;

  protected SelectionOperator<List<Solution<?>>,Solution<?>> selectionOperator ;
  protected CrossoverOperator<List<Solution<?>>,List<Solution<?>>> crossoverOperator ;
  protected MutationOperator mutationOperator ;

  protected abstract boolean stoppingCondition() ;
  protected abstract List<Solution<?>> createInitialPopulation() ;
  protected abstract void evaluatePopulation(List<Solution<?>> population) ;
  protected abstract List<Solution<?>> selection(List<Solution<?>> population) ;
  protected abstract List<Solution<?>> reproduction(List<Solution<?>> population) ;
  protected abstract List<Solution<?>> replacement(List<Solution<?>> population1, List<Solution<?>> population2) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    population = createInitialPopulation();
    evaluatePopulation(population);
    iterations = 1 ;
    while (!stoppingCondition()) {
      matingPopulation = selection(population) ;
      offspringPopulation = reproduction(matingPopulation) ;
      evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation) ;

      iterations ++ ;
    }
  }
}
