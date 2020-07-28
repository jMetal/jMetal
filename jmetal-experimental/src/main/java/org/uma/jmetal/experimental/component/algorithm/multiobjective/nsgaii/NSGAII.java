package org.uma.jmetal.experimental.component.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.experimental.component.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.component.catalogue.densityestimator.DensityEstimator;
import org.uma.jmetal.experimental.component.catalogue.densityestimator.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.experimental.component.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.component.catalogue.evaluation.SequentialEvaluation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.ranking.Ranking;
import org.uma.jmetal.experimental.component.catalogue.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.experimental.component.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.component.catalogue.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.component.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.termination.Termination;
import org.uma.jmetal.experimental.component.catalogue.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.Arrays;
import java.util.HashMap;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
@SuppressWarnings("serial")
public class NSGAII<S extends Solution<?>> extends ComponentBasedEvolutionaryAlgorithm<S> {

  /**
   * Constructor
   *
   * @param evaluation
   * @param initialPopulationCreation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public NSGAII(
      Evaluation<S> evaluation,
      InitialSolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      CrossoverAndMutationVariation<S> variation,
      RankingAndDensityEstimatorReplacement<S> replacement) {
    super(
        "NSGA-II",
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement);
  }

  /** Constructor */
  public NSGAII(
      Problem<S> problem,
      int populationSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Ranking<S> ranking) {
    this.name = "NSGA-II";
    this.problem = problem;
    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    DensityEstimator<S> densityEstimator = new CrowdingDistanceDensityEstimator<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossoverOperator, mutationOperator);

    this.selection =
        new NaryTournamentMatingPoolSelection<>(
            2,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    ranking.getSolutionComparator(), densityEstimator.getSolutionComparator())));

    this.termination = termination;

    this.evaluation = new SequentialEvaluation<>(problem);

    this.archive = null;
  }

  /** Constructor */
  public NSGAII(
      Problem<S> problem,
      int populationSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination) {
    this(
        problem,
        populationSize,
        offspringPopulationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        new FastNonDominatedSortRanking<>());
  }
}
