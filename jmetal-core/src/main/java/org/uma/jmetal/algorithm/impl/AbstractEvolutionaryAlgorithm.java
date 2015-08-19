package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 26/10/14.
 * @param <S> Solution
 * @param <R> Result
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution<?>, Population, R>  implements Algorithm<R>{
  private Population population;

  public Population getPopulation() {
    return population;
  }

  public void setPopulation(Population population) {
    this.population = population;
  }

  protected abstract void initProgress();

  protected abstract void updateProgress();

  protected abstract boolean isStoppingConditionReached();

  protected abstract Population createInitialPopulation();

  protected abstract List<S> selection(Population population);

  protected abstract List<S> reproduction(List<S> selectedIndividuals);

  protected abstract Population replacement(Population population, List<S> offsprings);

  @Override public abstract R getResult();

  @Override public void run() {
    List<S> offsprings;
    List<S> selectedIndividuals;

    population = createInitialPopulation();
    initProgress();
    while (!isStoppingConditionReached()) {
      selectedIndividuals = selection(population);
      offsprings = reproduction(selectedIndividuals);
      population = replacement(population, offsprings);
      updateProgress();
    }
  }
}
