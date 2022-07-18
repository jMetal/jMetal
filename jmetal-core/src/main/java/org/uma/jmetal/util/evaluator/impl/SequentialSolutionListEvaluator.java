package org.uma.jmetal.util.evaluator.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class SequentialSolutionListEvaluator<S> implements SolutionListEvaluator<S> {

  @Override
  public List<S> evaluate(@NotNull List<S> solutionList, @NotNull Problem<S> problem) throws JMetalException {
      for (S s : solutionList) {
          problem.evaluate(s);
      }

      return solutionList;
  }

  @Override
  public void shutdown() {
    // This method is an intentionally-blank override.
  }
}
