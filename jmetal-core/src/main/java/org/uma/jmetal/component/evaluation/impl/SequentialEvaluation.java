package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class SequentialEvaluation<S extends Solution<?>> extends AbstractEvaluation<S> {
  public SequentialEvaluation() {
    super(new SequentialSolutionListEvaluator<S>()) ;
  }
}
