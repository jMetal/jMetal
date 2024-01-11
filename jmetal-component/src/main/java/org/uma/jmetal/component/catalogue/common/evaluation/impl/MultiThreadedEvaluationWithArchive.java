package org.uma.jmetal.component.catalogue.common.evaluation.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that evaluates a list of solutions using threads.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class MultiThreadedEvaluationWithArchive<S extends Solution<?>> implements Evaluation<S> {
  private int computedEvaluations;
  private final Problem<S> problem;
  private final int numberOfThreads ;
  private Archive<S> archive ;


  public MultiThreadedEvaluationWithArchive(int numberOfThreads, Problem<S> problem, Archive<S> archive) {
    Check.that(numberOfThreads >= 0, "The number of threads is a negative value: " + numberOfThreads) ;
    Check.notNull(problem);

    this.archive = archive ;

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
  public List<S> evaluate(List<S> solutionList) {
    Check.notNull(solutionList);
    solutionList.parallelStream().forEach(problem::evaluate);
    computedEvaluations = solutionList.size();

    solutionList.forEach(solution -> archive.add((S)solution.copy()));

    return solutionList;
  }

  @Override
  public int computedEvaluations() {
    return computedEvaluations;
  }

  public int numberOfThreads() {
    return numberOfThreads ;
  }

  @Override
  public Problem<S> problem() {
    return problem ;
  }
}
