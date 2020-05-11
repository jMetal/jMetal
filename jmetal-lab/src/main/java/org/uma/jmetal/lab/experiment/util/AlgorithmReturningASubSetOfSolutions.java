package org.uma.jmetal.lab.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

import java.util.List;

@SuppressWarnings("serial")
public class AlgorithmReturningASubSetOfSolutions<S extends Solution<?>> implements Algorithm<List<S>> {
  private Algorithm<List<S>> algorithm;
  private int numberOfSolutionsToReturn;

  public AlgorithmReturningASubSetOfSolutions(
          Algorithm<List<S>> algorithm, int numberOfSolutionsToReturn) {
    this.algorithm = algorithm;
    this.numberOfSolutionsToReturn = numberOfSolutionsToReturn;
  }

  @Override
  public void run() {
    algorithm.run();
  }

  @Override
  public List<S> getResult() {
    return SolutionListUtils.distanceBasedSubsetSelection(
            algorithm.getResult(), numberOfSolutionsToReturn);
  }

  @Override
  public String getName() {
    return algorithm.getName();
  }

  @Override
  public String getDescription() {
    return algorithm.getDescription();
  }
}
