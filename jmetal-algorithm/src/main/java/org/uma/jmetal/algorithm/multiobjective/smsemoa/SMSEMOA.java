package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.util.SMSEMOAReplacement;
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
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
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
      Hypervolume<S> hypervolume,
      Ranking<S> ranking) {
    super(problem, populationSize, 1, crossoverOperator, mutationOperator, termination, ranking);

    this.replacement =
        new SMSEMOAReplacement<>(ranking, hypervolume);

    this.selection = new RandomMatingPoolSelection<>(variation.getMatingPoolSize());
  }

  /** Constructor */
  public SMSEMOA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination) {
    this(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        new PISAHypervolume<>(),
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
