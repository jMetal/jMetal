package org.uma.jmetal.util.restartstrategy.impl;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.restartstrategy.CreateNewSolutionsStrategy;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * Created by antonio on 6/06/17.
 */
public class DefaultRestartStrategy<S extends Solution<?>> implements RestartStrategy<S> {
  private final RemoveSolutionsStrategy<S> removeSolutionsStrategy;
  private final CreateNewSolutionsStrategy<S> createNewSolutionsStrategy;

  public DefaultRestartStrategy(RemoveSolutionsStrategy<S> removeSolutionsStrategy,
                                CreateNewSolutionsStrategy<S> createNewSolutionsStrategy) {
    this.removeSolutionsStrategy = removeSolutionsStrategy ;
    this.createNewSolutionsStrategy = createNewSolutionsStrategy ;
  }

  public void restart(List<S> solutionList, DynamicProblem<S,?> problem) {
    var numberOfRemovedSolutions = removeSolutionsStrategy.remove(solutionList, problem);
    createNewSolutionsStrategy.create(solutionList, problem, numberOfRemovedSolutions); ;
  }
}
