package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class SequentialEvaluation<S extends Solution<?>> extends AbstractEvaluation<S> {
  public SequentialEvaluation(Problem<S> problem) {
    super(new SequentialSolutionListEvaluator<S>(), problem);
  }
}
