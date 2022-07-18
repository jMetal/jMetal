package org.uma.jmetal.util.evaluator.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class MultiThreadedSolutionListEvaluator<S> implements SolutionListEvaluator<S> {

  private final int numberOfThreads;

  public MultiThreadedSolutionListEvaluator(int numberOfThreads) {
    if (numberOfThreads == 0) {
      this.numberOfThreads = Runtime.getRuntime().availableProcessors();
    } else {
      this.numberOfThreads = numberOfThreads;
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
          "" + this.numberOfThreads);
    }
    JMetalLogger.logger.info("Number of cores: " + numberOfThreads);
  }

  @Override
  public @NotNull List<S> evaluate(List<S> solutionList, @NotNull Problem<S> problem) {
    solutionList.parallelStream().forEach(problem::evaluate);

    return solutionList;
  }

  public int getNumberOfThreads() {
    return numberOfThreads;
  }

  @Override
  public void shutdown() {
    //This method is an intentionally-blank override.
  }
}
