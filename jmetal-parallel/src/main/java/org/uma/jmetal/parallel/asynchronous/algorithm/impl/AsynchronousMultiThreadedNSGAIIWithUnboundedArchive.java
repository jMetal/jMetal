package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

/**
 * {@link AsynchronousMultiThreadedNSGAII} with an external archive of non-dominated solutions to
 * which every evaluated solution is added. The archive has no size limit, but {@link #result()}
 * does not return all of it: it returns {@code populationSize} solutions of the archive selected by
 * {@link BestSolutionsArchive}, which is a distance-based subset selection (all of them if there
 * are fewer).
 *
 * @param <S> the type of the solutions
 */
public class AsynchronousMultiThreadedNSGAIIWithUnboundedArchive<S extends Solution<?>>
    extends AsynchronousMultiThreadedNSGAII<S> {

  protected Archive<S> externalArchive;

  /**
   * Creates an asynchronous NSGA-II with the merge non-dominated sorting and an unbounded archive.
   * The parameters are the ones of {@link AsynchronousMultiThreadedNSGAII}; {@code populationSize}
   * is also the number of solutions of the result.
   */
  public AsynchronousMultiThreadedNSGAIIWithUnboundedArchive(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      Termination termination) {
    super(numberOfCores, problem, populationSize, crossover, mutation, termination);

    externalArchive = new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(),
        populationSize);
  }

  /** Adds the evaluated solution to the archive and then to the population. */
  @Override
  public void processComputedTask(ParallelTask<S> task) {
    externalArchive.add(task.getContents());
    super.processComputedTask(task);
  }

  /**
   * Returns {@code populationSize} solutions of the archive, selected by distance, or all of them
   * if there are fewer.
   */
  @Override
  public List<S> result() {
    return externalArchive.solutions();
  }
}
