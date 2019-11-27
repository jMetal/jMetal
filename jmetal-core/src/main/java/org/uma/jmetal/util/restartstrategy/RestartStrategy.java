package org.uma.jmetal.util.restartstrategy;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by antonio on 6/06/17.
 */
public interface RestartStrategy<S extends Solution<?>> {
  void restart(List<S> solutionList, DynamicProblem<S,?> problem);
}
