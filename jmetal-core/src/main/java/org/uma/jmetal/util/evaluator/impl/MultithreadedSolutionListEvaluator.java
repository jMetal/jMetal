package org.uma.jmetal.util.evaluator.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class MultithreadedSolutionListEvaluator<S> implements SolutionListEvaluator<S> {

  private final int numberOfThreads;

  public MultithreadedSolutionListEvaluator(int numberOfThreads) {
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
  public List<S> evaluate(List<S> solutionList, Problem<S> problem) {
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
