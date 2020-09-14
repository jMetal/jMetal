package org.uma.jmetal.experimental.parallel.asynchronous.algorithm.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

public class AsynchronousMultiThreadedNSGAII<S extends Solution<?>>
    extends AsynchronousMultithreadedGeneticAlgorithm<S> {

  public AsynchronousMultiThreadedNSGAII(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      Termination termination) {
    super(numberOfCores,problem, populationSize, crossover,mutation, new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
            new RankingAndDensityEstimatorReplacement<>(
                    new MergeNonDominatedSortRanking<>(),
                    new CrowdingDistanceDensityEstimator<>(),
                    Replacement.RemovalPolicy.oneShot),termination);
  }
}
