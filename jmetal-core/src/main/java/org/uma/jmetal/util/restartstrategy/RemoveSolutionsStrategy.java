package org.uma.jmetal.util.restartstrategy;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;

/**
 * @author Antonio J. Nebro
 */
public interface RemoveSolutionsStrategy<S extends Solution<?>> {
  /**
   * Remove a number of solutions of a list of {@link Solution} objects
   * @param solutionList
   * @param problem
   * @return the number of deleted solutions
   */
  int remove(List<S> solutionList, DynamicProblem<S, ?> problem) ;
}
