package org.uma.jmetal.auto.component.catalogue.common.evaluation.impl;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public abstract  class AbstractEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private SolutionListEvaluator<S> evaluator ;
  private int numberOfComputedEvaluations ;
  private Problem<S> problem ;

  public AbstractEvaluation(SolutionListEvaluator<S> evaluator, Problem<S> problem) {
    this.numberOfComputedEvaluations = 0 ;
    this.evaluator = evaluator ;
    this.problem = problem ;
  }

  @Override
  public List<S> evaluate(List<S> solutionList) {
    evaluator.evaluate(solutionList, problem) ;

    numberOfComputedEvaluations += solutionList.size() ;

    return solutionList;
  }

  public int getComputedEvaluations() {
    return numberOfComputedEvaluations ;
  }
}
