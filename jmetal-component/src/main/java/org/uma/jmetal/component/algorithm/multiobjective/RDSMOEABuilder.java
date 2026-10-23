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
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * Class to configure and build an instance of a MOEA algorithm based on ranking and crowding distance components (RDS)
 * for replacement and selection
 *
 * @param <S>
 */
public class RDSMOEABuilder<S extends Solution<?>> {
  private String name;
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private RankingAndDensityEstimatorReplacement.RemovalPolicy removalPolicy;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Variation<S> variation;

  public RDSMOEABuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
                        CrossoverOperator<S> crossover, MutationOperator<S> mutation,
                        Ranking<S> ranking, DensityEstimator<S> densityEstimator,
                        RankingAndDensityEstimatorReplacement.RemovalPolicy removalPolicy) {
    name = "MOEA";

    this.ranking = ranking;
    this.densityEstimator = densityEstimator;
    this.removalPolicy = removalPolicy;

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public RDSMOEABuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  /**
   * Sets the ranking of the replacement and of the selection (a binary tournament that compares
   * the ranks and then the densities); the removal policy of the replacement is kept.
   */
  public RDSMOEABuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;

    return this;
  }

  public RDSMOEABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public RDSMOEABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation ;

    return this ;
  }

  public EvolutionaryAlgorithm<S> build() {
    Replacement<S> replacement =
        new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator, removalPolicy);

    int tournamentSize = 2 ;
    Selection<S> selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.matingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::value).reversed())));

    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
