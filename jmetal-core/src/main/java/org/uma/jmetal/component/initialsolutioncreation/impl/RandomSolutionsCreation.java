package org.uma.jmetal.component.initialsolutioncreation.impl;

import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Class that creates a list of randomly instantiated solutions
 *
 * @param <S>
 */
public class RandomSolutionsCreation<S extends Solution<?>> implements InitialSolutionsCreation<S> {
  private final int numberOfSolutionsToCreate;
  private final Problem<S> problem;

  /**
   * Creates the list of solutions
   * @param problem Problem defining the solutions
   * @param numberOfSolutionsToCreate
   */
  public RandomSolutionsCreation(Problem<S> problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  public List<S> create() {
    List<S> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    IntStream.range(0, numberOfSolutionsToCreate)
        .forEach(i -> solutionList.add(problem.createSolution()));

    return solutionList;
  }
}
