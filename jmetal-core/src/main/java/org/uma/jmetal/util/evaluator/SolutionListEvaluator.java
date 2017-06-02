package org.uma.jmetal.util.evaluator;

import org.uma.jmetal.problem.Problem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */

public interface SolutionListEvaluator<S> extends Serializable {
  List<S> evaluate(List<S> solutionList, Problem<S> problem) ;
  void shutdown() ;
}
