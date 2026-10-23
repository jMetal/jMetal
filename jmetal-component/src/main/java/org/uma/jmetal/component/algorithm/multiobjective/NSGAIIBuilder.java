package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.Arrays;
import java.util.Comparator;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.ConstraintHandlingReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure and build an instance of the NSGA-II algorithm
 *
 * @param <S>
 */
public class NSGAIIBuilder<S extends Solution<?>> {
  private String name;
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private ConstraintHandlingTechnique<S> constraintHandlingTechnique;

  public NSGAIIBuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    name = "NSGAII";

    densityEstimator = new CrowdingDistanceDensityEstimator<>();
    ranking = new FastNonDominatedSortRanking<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.variation = new CrossoverAndMutationVariation<>(
        offspringPopulationSize, crossover, mutation);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public NSGAIIBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  /**
   * Sets the ranking of the replacement and of the default selection (a binary tournament that
   * compares the ranks and then the densities), whatever the order of the calls to the builder.
   */
  public NSGAIIBuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;

    return this;
  }

  /** Sets the density estimator of the replacement and of the default selection. */
  public NSGAIIBuilder<S> setDensityEstimator(DensityEstimator<S> densityEstimator) {
    this.densityEstimator = densityEstimator;

    return this;
  }

  /**
   * Sets the constraint-handling technique: the ranking of the replacement and of the default
   * selection becomes {@code technique.ranking()} (it replaces a ranking given with {@code
   * setRanking}), and the technique is updated once per generation with the progress of the run.
   * Without a technique the algorithm behaves as before (its default ranking ignores the
   * constraints). The technique only guides the search: an external archive keeps the feasible
   * solutions with the constrained-domination principle, which is recommended with {@link
   * org.uma.jmetal.util.constrainthandling.StochasticRanking} and {@link
   * org.uma.jmetal.util.constrainthandling.SelfAdaptivePenalty}, whose final population can contain
   * infeasible solutions. A technique has state, so it must not be shared by several algorithms.
   */
  public NSGAIIBuilder<S> setConstraintHandlingTechnique(
      ConstraintHandlingTechnique<S> technique) {
    this.constraintHandlingTechnique = technique;

    return this;
  }

  public NSGAIIBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public NSGAIIBuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation;

    return this;
  }

  /** Sets the selection, which replaces the default binary tournament. */
  public NSGAIIBuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    Ranking<S> algorithmRanking =
        constraintHandlingTechnique != null ? constraintHandlingTechnique.ranking() : ranking;
    RunProgress progress = new RunProgress();
    Replacement<S> replacement = new RankingAndDensityEstimatorReplacement<>(
        algorithmRanking, densityEstimator,
        RankingAndDensityEstimatorReplacement.RemovalPolicy.ONE_SHOT);
    if (constraintHandlingTechnique != null) {
      replacement =
          new ConstraintHandlingReplacement<>(constraintHandlingTechnique, replacement, progress);
    }
    Selection<S> algorithmSelection =
        selection != null ? selection : defaultSelection(algorithmRanking);

    var algorithm = new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation,
        termination, algorithmSelection, variation, replacement);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }

  /** A binary tournament that compares the ranks of the ranking and then the densities. */
  private Selection<S> defaultSelection(Ranking<S> ranking) {
    int tournamentSize = 2;
    return new NaryTournamentSelection<>(
        tournamentSize,
        variation.matingPoolSize(),
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::value).reversed())));
  }
}
