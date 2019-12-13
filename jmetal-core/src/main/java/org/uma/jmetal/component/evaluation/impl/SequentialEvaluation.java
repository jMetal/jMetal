package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequentialEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private Problem<S> problem;
  private Observable<Map<String, Object>> observable;
  private int numberOfEvaluationIterations ;
  private SolutionListEvaluator<S> evaluator ;


  public SequentialEvaluation(Problem<S> problem) {
    this.problem = problem;
    this.observable = new DefaultObservable<>("SequentialEvaluation") ;
    this.numberOfEvaluationIterations = 0 ;
    this.evaluator = new SequentialSolutionListEvaluator<>() ;
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
