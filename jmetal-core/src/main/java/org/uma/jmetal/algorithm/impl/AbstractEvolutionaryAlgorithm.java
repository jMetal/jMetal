package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionaryAlgorithm<Result> implements Algorithm <Result> {

  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  private List<Solution> population ;
  public List<Solution> getPopulation() {
    return population ;
  }

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
    initProgress();
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population) ;
      offspringPopulation = reproduction(matingPopulation) ;
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation) ;
      updateProgress();
    }
  }
}
