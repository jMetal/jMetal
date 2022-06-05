package org.uma.jmetal.util.restartstrategy;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;

/**
 * @author Antonio J. Nebro
 */
public interface CreateNewSolutionsStrategy<S extends Solution<?>> {
  /**
   * Add a number of new solutions to a list of {@link Solution} objects
   * @param solutionList
   * @param problem
   * @param numberOfNewSolutions
   */
  void create(List<S> solutionList, DynamicProblem<S, ?> problem, int numberOfNewSolutions) ;
}
