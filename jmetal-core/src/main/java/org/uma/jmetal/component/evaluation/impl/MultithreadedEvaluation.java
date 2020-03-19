package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;

public class MultithreadedEvaluation<S extends Solution<?>> extends AbstractEvaluation<S> {
  public MultithreadedEvaluation(int numberOfThreads, Problem<S> problem) {
    super(new MultithreadedSolutionListEvaluator<S>(numberOfThreads), problem) ;
  }
}
