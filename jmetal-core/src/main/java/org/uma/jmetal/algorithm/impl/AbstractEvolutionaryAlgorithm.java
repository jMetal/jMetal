package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 26/10/14.
 * @param <S> Solution
 * @param <R> Result
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution<?>, Population extends Iterable<S>, R>  implements Algorithm<R>{
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

  protected abstract Population evaluatePopulation(Population population);

  protected abstract Population selection(Population population);

  protected abstract Population reproduction(Population population);

  protected abstract Population replacement(Population population, Population offspringPopulation);

  @Override public abstract R getResult();

  @Override public void run() {
    Population offspringPopulation;
    Population matingPopulation;

    population = createInitialPopulation();
    population = evaluatePopulation(population);
    initProgress();
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population);
      offspringPopulation = reproduction(matingPopulation);
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation);
      updateProgress();
    }
  }
}
