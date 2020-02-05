package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.component.densityestimator.impl.HypervolumeContributionDensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.*;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class SMSEMOA<S extends Solution<?>> extends NSGAII<S> {

  /** Constructor */
  public SMSEMOA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      HypervolumeContributionDensityEstimator<S> densityEstimator,
      Ranking<S> ranking) {
    super(problem, populationSize, 1, crossoverOperator, mutationOperator, termination, ranking);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    this.selection = new RandomMatingPoolSelection<>(variation.getMatingPoolSize());
  }

  /** Constructor */
  public SMSEMOA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      HypervolumeContributionDensityEstimator<S> densityEstimator) {
    this(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        densityEstimator,
        new FastNonDominatedSortRanking<>());
  }

  @Override
  public String getName() {
    return "SMS-EMOA";
  }

  @Override
  public String getDescription() {
    return "SMS-EMOA";
  }
}
