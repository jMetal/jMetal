package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionaryAlgorithm<Sol extends Solution, Result> implements Algorithm <Result> {
  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  private List<Sol> population ;
  public List<Sol> getPopulation() {
    return population ;
  }

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<Sol> createInitialPopulation() ;
  protected abstract List<Sol> evaluatePopulation(List<Sol> population) ;
  protected abstract List<Sol> selection(List<Sol> population) ;
  protected abstract List<Sol> reproduction(List<Sol> population) ;
  protected abstract List<Sol> replacement(List<Sol> population, List<Sol> offspringPopulation) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    List<Sol> offspringPopulation ;
    List<Sol> matingPopulation ;

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
