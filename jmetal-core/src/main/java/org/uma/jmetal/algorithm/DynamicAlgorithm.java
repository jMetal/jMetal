package org.uma.jmetal.algorithm;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

import java.util.Map;

/**
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public interface DynamicAlgorithm<Result> extends Algorithm<Result> {
  DynamicProblem<?, ?> getDynamicProblem() ;

  void restart();
  RestartStrategy<?> getRestartStrategy();
  Observable<Map<String, Object>> getObservable() ;
}
