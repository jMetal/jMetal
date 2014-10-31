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

  private int iterations ;
  public int getIterations() {
    return iterations ;
  }

  private List<Solution> population ;
  public List<Solution> getPopulation() {
    return population ;
  }

  protected SelectionOperator<List<Solution>, Solution> selectionOperator ;
  protected CrossoverOperator<List<Solution>, List<Solution>> crossoverOperator ;
  protected MutationOperator<Solution> mutationOperator ;

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<Solution> createInitialPopulation() ;
  protected abstract List<Solution> evaluatePopulation(List<Solution> population) ;
  protected abstract List<Solution> selection(List<Solution> population) ;
  protected abstract List<Solution> reproduction(List<Solution> population) ;
  protected abstract List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    List<Solution> offspringPopulation ;
    List<Solution> matingPopulation ;

    population = createInitialPopulation();
    population = evaluatePopulation(population);
    iterations = 1 ;
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population) ;
      offspringPopulation = reproduction(matingPopulation) ;
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation) ;
      iterations ++ ;
    }
  }
}
