package org.uma.jmetal.util.restartstrategy.impl;

import static org.uma.jmetal.util.SolutionListUtils.removeSolutionsFromList;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveFirstNSolutions<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveFirstNSolutions(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(List<S> solutionList, @Nullable DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    removeSolutionsFromList(solutionList, numberOfSolutionsToDelete);
    return numberOfSolutionsToDelete ;
  }
}
