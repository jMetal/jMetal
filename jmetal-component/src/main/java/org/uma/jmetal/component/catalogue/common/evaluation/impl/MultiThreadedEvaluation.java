package org.uma.jmetal.component.catalogue.common.evaluation.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

public class MultiThreadedEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private int computedEvaluations;
  private final Problem<S> problem;
  private final int numberOfThreads ;

  public MultiThreadedEvaluation(int numberOfThreads, Problem<S> problem) {
    Check.that(numberOfThreads >= 0, "The number of threads is a negative value: " + numberOfThreads) ;
    Check.notNull(problem);

    if (numberOfThreads == 0) {
      numberOfThreads = Runtime.getRuntime().availableProcessors();
    }
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
        "" + numberOfThreads);

    this.numberOfThreads = numberOfThreads ;
    this.problem = problem;
    computedEvaluations = 0;
  }

  @Override
  public List<S> evaluate(@NotNull List<S> solutionList) {
    Check.notNull(solutionList);
    solutionList.parallelStream().forEach(problem::evaluate);
    computedEvaluations = solutionList.size();

    return solutionList;
  }

  @Override
  public int getComputedEvaluations() {
    return computedEvaluations;
  }

  public int getNumberOfThreads() {
    return numberOfThreads ;
  }
}
