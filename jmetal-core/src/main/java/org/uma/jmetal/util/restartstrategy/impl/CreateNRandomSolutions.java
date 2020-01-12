package org.uma.jmetal.util.restartstrategy.impl;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.restartstrategy.CreateNewSolutionsStrategy;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by antonio on 6/06/17.
 */
public class CreateNRandomSolutions<S extends Solution<?>> implements CreateNewSolutionsStrategy<S> {

  @Override
  public void create(List<S> solutionList, DynamicProblem<S, ?> problem, int numberOfSolutionsToCreate) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    IntStream.range(0, numberOfSolutionsToCreate)
            .forEach(s -> {solutionList.add(problem.createSolution());});
  }
}
