package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 26/10/14.
 * @param <S> Solution
 * @param <R> Result
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution, R>  implements Algorithm<R>,
    Measurable {
  private List<S> population ;
  public List<S> getPopulation() {
    return population ;
  }
  public void setPopulation(List<S> population) {
    this.population = population ;
  }

  protected SimpleMeasureManager measureManager ;
  protected DurationMeasure durationMeasure ;

  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<S> createInitialPopulation() ;
  protected abstract List<S> evaluatePopulation(List<S> population) ;
  protected abstract List<S> selection(List<S> population) ;
  protected abstract List<S> reproduction(List<S> population) ;
  protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation) ;

  public AbstractEvolutionaryAlgorithm() {
    durationMeasure = new DurationMeasure() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
  }

  @Override
  public abstract R getResult() ;

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

  /**
   * @deprecated Should be properly implemented by the leaf implementations or
   *             should return a proper manager with {@link Measure}s already
   *             applicable from that level of implementation.
   */
  @Deprecated
  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }
}
