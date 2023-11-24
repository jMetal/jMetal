package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

public class AsynchronousMultiThreadedNSGAIIV2<S extends Solution<?>>
    extends AsynchronousMultiThreadedNSGAII<S> {

  protected Archive<S> externalArchive;

  public AsynchronousMultiThreadedNSGAIIV2(
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

  @Override
  public void processComputedTask(ParallelTask<S> task) {
    externalArchive.add(task.getContents());
    super.processComputedTask(task);
  }

  @Override
  public List<S> getResult() {
    return externalArchive.solutions();
  }
}
