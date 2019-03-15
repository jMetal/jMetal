package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.util.observable.Observable;
import org.uma.jmetal.auto.util.observable.ObservableEntity;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a metaheuristic
 * @author Antonio J. Nebro
 * @version 1.0
 */
public abstract class Metaheuristic<S extends Solution<?>> implements Algorithm<List<S>>, ObservableEntity {
  protected List<S> solutions ;
  protected Problem<S> problem ;
  protected Observable<Map<String, Object>> observable ;
  protected Map<String, Object> attributes ;

  protected abstract boolean stoppingConditionIsMet();
  protected abstract List<S> createInitialSolutionSet();
  protected abstract List<S> evaluateSolutions(List<S> solutions);
  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;
  protected abstract void step() ;

  @Override
  public void run() {
    solutions = createInitialSolutionSet();
    solutions = evaluateSolutions(solutions);
    initProgress();
    while (!stoppingConditionIsMet()) {
      step() ;
      updateProgress();
    }
  }

  public List<S> getSolutions() {
    return solutions ;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable ;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public Problem<S>getProblem() {
    return problem ;
  }
}
