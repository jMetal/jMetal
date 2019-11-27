package org.uma.jmetal.auto.component.evaluation.impl;

import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultithreadedEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private Problem<S> problem;
  protected Observable<Map<String, Object>> observable;

  public MultithreadedEvaluation(Problem<S> problem) {
    this.problem = problem;
    this.observable = new DefaultObservable<>("MultithreadedEvaluation") ;
  }

  public List<S> evaluate(List<S> solutionList) {
    solutionList.parallelStream().forEach(solution -> problem.evaluate(solution));

    Map<String, Object>attributes = new HashMap<>() ;
    attributes.put("POPULATION", solutionList);
    observable.setChanged();
    observable.notifyObservers(attributes);

    return solutionList;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
