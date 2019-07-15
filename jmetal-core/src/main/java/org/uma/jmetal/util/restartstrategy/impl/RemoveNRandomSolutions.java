package org.uma.jmetal.util.restartstrategy.impl;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveNRandomSolutions<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveNRandomSolutions(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    } else if (solutionList.size() == 0) {
      throw new JMetalException("The solution list is empty") ;
    }

    int numberOfSolutionsToRemove = numberOfSolutionsToDelete ;
    IntStream.range(0, numberOfSolutionsToRemove)
            .forEach(s -> {
                      int chosen = JMetalRandom.getInstance().nextInt(0, solutionList.size()-1);
                      solutionList.remove(chosen);
                    }
            );
    return numberOfSolutionsToDelete ;
  }
}
