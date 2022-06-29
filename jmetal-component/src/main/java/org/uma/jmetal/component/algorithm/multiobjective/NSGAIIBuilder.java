package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.Arrays;
import java.util.Comparator;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/**
 * Class to configure and build an instance of the NSGA-II algorithm
 * @param <S>
 */
public class NSGAIIBuilder<S extends Solution<?>> {

  private String name;
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private Archive<S> externalArchive;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public NSGAIIBuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    name = "NSGAII";

    densityEstimator = new CrowdingDistanceDensityEstimator<>();
    ranking = new FastNonDominatedSortRanking<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    this.selection =
        new NaryTournamentMatingPoolSelection<>(
            2,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::getValue).reversed())));

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);

    this.externalArchive = null;
  }

  public NSGAIIBuilder<S> setName(String newName) {
    this.name = newName;

    return this;
  }

  public NSGAIIBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public NSGAIIBuilder<S> setArchive(Archive<S> externalArchive) {
    this.externalArchive = externalArchive;

    return this;
  }

  public NSGAIIBuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;
    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    return this;
  }

  public NSGAIIBuilder<S> setDensityEstimator(DensityEstimator<S> densityEstimator) {
    this.densityEstimator = densityEstimator;
    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, evaluation, createInitialPopulation, termination,
        selection, variation, replacement, externalArchive);
  }
}
