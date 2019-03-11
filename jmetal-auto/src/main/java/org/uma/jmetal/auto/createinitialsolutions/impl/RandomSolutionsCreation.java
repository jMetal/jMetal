package org.uma.jmetal.auto.createinitialsolutions.impl;

import org.uma.jmetal.auto.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RandomSolutionsCreation<S extends Solution<?>> implements CreateInitialSolutions<S> {
  private final int numberOfSolutionsToCreate;
  private final Problem<S> problem ;

  public RandomSolutionsCreation(Problem<S> problem, int numberOfSolutionsToCreate) {
    this.problem = problem ;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate ;
  }

  public List<S> create() {
    List<S> solutionList = new ArrayList<>(numberOfSolutionsToCreate) ;
    IntStream.range(0, numberOfSolutionsToCreate).forEach(i -> solutionList.add(problem.createSolution()));

    return solutionList ;
  }
}
