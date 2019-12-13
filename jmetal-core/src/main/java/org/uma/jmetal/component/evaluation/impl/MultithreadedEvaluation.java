package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultithreadedEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private Problem<S> problem;
  protected Observable<Map<String, Object>> observable;
  private SolutionListEvaluator<S> evaluator ;
  private int numberOfEvaluationIterations ;

  public MultithreadedEvaluation(Problem<S> problem, int numberOfThreads) {
    this.problem = problem;
    this.numberOfEvaluationIterations = 0 ;
    this.observable = new DefaultObservable<>("MultithreadedEvaluation") ;
    this.evaluator = new MultithreadedSolutionListEvaluator<>(numberOfThreads) ;
  }

  public List<S> evaluate(List<S> solutionList) {
    evaluator.evaluate(solutionList, problem) ;

    Map<String, Object>attributes = new HashMap<>() ;
    attributes.put("POPULATION", solutionList);
    attributes.put("EVALUATIONS", ++numberOfEvaluationIterations);
    observable.setChanged();
    observable.notifyObservers(attributes);

    return solutionList;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
