package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * Asynchronous parallel version of NSGA-II: every time a worker evaluates a solution, it replaces
 * the worst one of the population (ranking and crowding distance), and the parents of a new solution
 * are chosen by binary tournament, by rank and then by crowding distance.
 *
 * <p>The replacement and the tournament share the ranking, so that the tournament compares the
 * ranks that the replacement computes. By default it is a merge non-dominated sorting, which uses
 * Pareto dominance and ignores the constraints; a {@link
 * org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking} with a {@link
 * org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator}, for
 * example, follows Deb's constrained-domination principle.
 *
 * @param <S> the type of the solutions
 */
public class AsynchronousMultiThreadedNSGAII<S extends Solution<?>>
    extends AsynchronousMultiThreadedGeneticAlgorithm<S> {

  /** Creates an asynchronous NSGA-II with the merge non-dominated sorting (Pareto dominance). */
  public AsynchronousMultiThreadedNSGAII(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      Termination termination) {
    this(numberOfCores, problem, populationSize, crossover, mutation, termination,
        new MergeNonDominatedSortRanking<>());
  }

  /**
   * Creates an asynchronous NSGA-II that ranks the population with the given ranking, which also
   * decides the binary tournament.
   */
  public AsynchronousMultiThreadedNSGAII(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      Termination termination,
      Ranking<S> ranking) {
    super(numberOfCores, problem, populationSize, crossover, mutation, tournament(ranking),
        replacement(ranking), termination);
  }

  /** The binary tournament, by the rank of the ranking and then by crowding distance. */
  static <S extends Solution<?>> SelectionOperator<List<S>, S> tournament(Ranking<S> ranking) {
    return new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>(ranking));
  }

  /** The replacement, by the rank of the ranking and then by crowding distance. */
  static <S extends Solution<?>> Replacement<S> replacement(Ranking<S> ranking) {
    return new RankingAndDensityEstimatorReplacement<>(
        ranking,
        new CrowdingDistanceDensityEstimator<>(),
        RankingAndDensityEstimatorReplacement.RemovalPolicy.ONE_SHOT);
  }
}
