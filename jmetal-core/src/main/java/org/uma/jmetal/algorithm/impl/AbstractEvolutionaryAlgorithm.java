package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution, Result> implements Algorithm <Result> {
  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  private List<S> population ;
  public List<S> getPopulation() {
    return population ;
  }

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<S> createInitialPopulation() ;
  protected abstract List<S> evaluatePopulation(List<S> population) ;
  protected abstract List<S> selection(List<S> population) ;
  protected abstract List<S> reproduction(List<S> population) ;
  protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    List<S> offspringPopulation ;
    List<S> matingPopulation ;

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
