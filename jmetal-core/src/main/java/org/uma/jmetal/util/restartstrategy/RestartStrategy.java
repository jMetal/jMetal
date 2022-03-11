package org.uma.jmetal.util.restartstrategy;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;

/**
 * Created by antonio on 6/06/17.
 */
public interface RestartStrategy<S extends Solution<?>> {
  void restart(List<S> solutionList, DynamicProblem<S,?> problem);
}
