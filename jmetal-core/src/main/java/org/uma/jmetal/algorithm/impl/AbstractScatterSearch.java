package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 11/06/2015.
 * @param <S> Solution
 * @param <R> Result
 */
public abstract class AbstractScatterSearch<S extends Solution, R>  implements Algorithm<R>{
  private List<S> population;

  public List<S> getPopulation() {
    return population;
  }

  public void setPopulation(List<S> population) {
    this.population = population;
  }

  private int populationSize ;
  public int getPopulationSize () {
    return populationSize ;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;
  }

  protected abstract boolean isStoppingConditionReached();

  public abstract boolean restartConditionIsFulfilled();

  protected abstract void restart();

  protected abstract S diversificationGeneration();

  protected abstract S improvement(S solution);

  public abstract void referenceSetUpdate(boolean firstTime);

  public abstract List<List<S>> subsetGeneration();

  public abstract List<S> solutionCombination(List<List<S>> population);

  @Override public abstract R getResult();

  @Override public void run() {
    initializationPhase() ;
    referenceSetUpdate(true);
    while (!isStoppingConditionReached()) {
      if (restartConditionIsFulfilled()) {
        restart();
      } else {
        List<List<S>> subset = subsetGeneration();
        List<S> combinedSolutions = solutionCombination(subset) ;
        List<S> improvedSolutions = new ArrayList<>() ;
        for (S solution : combinedSolutions) {
          improvedSolutions.add(improvement(solution)) ;
        }
        referenceSetUpdate(false);
      }
    }
  }

  /**
   * Initialization phase of the scatter search: the population is filled with diverse solutions that
   * have been improved.
   * @return The population
   */
  public void initializationPhase() {
    population = new ArrayList<>(populationSize) ;
    while (population.size() < populationSize) {
      S newSolution = diversificationGeneration() ;
      S improvedSolution = improvement(newSolution) ;
      population.add(improvedSolution) ;
    }
  }
}
