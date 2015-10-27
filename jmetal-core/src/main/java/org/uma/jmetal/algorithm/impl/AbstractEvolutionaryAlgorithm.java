package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 26/10/14.
 * @param <S> Solution
 * @param <R> Result
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution<?>, R>  implements Algorithm<R>{
  private List<S> population;
  private int maxPopulationSize ;

  public List<S> getPopulation() {
    return population;
  }

  public void setPopulation(List<S> population) {
    this.population = population;
  }

  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize ;
  }
  public int getMaxPopulationSize() {
    return maxPopulationSize ;
  }

  protected abstract void initProgress();

  protected abstract void updateProgress();

  protected abstract boolean isStoppingConditionReached();

  protected abstract List<S> createInitialPopulation();

  protected abstract List<S> evaluatePopulation(List<S> population);

  protected abstract List<S> selection(List<S> population);

  protected abstract List<S> reproduction(List<S> population);

  protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation);

  @Override public abstract R getResult();

  @Override public void run() {
    List<S> offspringPopulation;
    List<S> matingPopulation;

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
