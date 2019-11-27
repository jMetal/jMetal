package org.uma.jmetal.util.restartstrategy.impl;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.removeSolutionsFromList;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveFirstNSolutions<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveFirstNSolutions(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    removeSolutionsFromList(solutionList, numberOfSolutionsToDelete);
    return numberOfSolutionsToDelete ;
  }
}
