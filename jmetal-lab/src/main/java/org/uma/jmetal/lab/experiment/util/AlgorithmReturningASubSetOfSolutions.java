package org.uma.jmetal.lab.experiment.util;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

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
  public List<S> result() {
    return SolutionListUtils.distanceBasedSubsetSelection(
            algorithm.result(), numberOfSolutionsToReturn);
  }

  @Override
  public String name() {
    return algorithm.name();
  }

  @Override
  public String description() {
    return algorithm.description();
  }
}
