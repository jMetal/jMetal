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
import org.uma.jmetal.component.catalogue.ea.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.DifferentialEvolutionCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;

/**
 * Class to configure and build an instance of the NSGA-II algorithm using DE operators
 */
public class NSGAIIDEBuilder {
  private String name;
  private Ranking<DoubleSolution> ranking;
  private DensityEstimator<DoubleSolution> densityEstimator;
  private Evaluation<DoubleSolution> evaluation;
  private SolutionsCreation<DoubleSolution> createInitialPopulation;
  private Termination termination;
  private Selection<DoubleSolution> selection;
  private Variation<DoubleSolution> variation;
  private Replacement<DoubleSolution> replacement;

  private SequenceGenerator<Integer> sequenceGenerator;

  public NSGAIIDEBuilder(
      Problem<DoubleSolution> problem,
      int populationSize,
      int offspringPopulationSize,
      double cr,
      double f,
      MutationOperator<DoubleSolution> mutation,
      DE_VARIANT differentialEvolutionVariant) {
    name = "NSGAIIDE";

    densityEstimator = new CrowdingDistanceDensityEstimator<>();
    ranking = new FastNonDominatedSortRanking<>();
    sequenceGenerator = new IntegerBoundedSequenceGenerator(populationSize);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            cr, f, differentialEvolutionVariant);

    this.variation =
        new DifferentialEvolutionCrossoverVariation(
            offspringPopulationSize, crossover, mutation, sequenceGenerator);

    int numberOfParentsToSelect = crossover.numberOfRequiredParents() ;
    this.selection =
        new DifferentialEvolutionSelection(variation.getMatingPoolSize(), numberOfParentsToSelect, false,
            sequenceGenerator);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public NSGAIIDEBuilder setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public NSGAIIDEBuilder setRanking(Ranking<DoubleSolution> ranking) {
    this.ranking = ranking;
    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    return this;
  }

  public NSGAIIDEBuilder setEvaluation(Evaluation<DoubleSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public EvolutionaryAlgorithm<DoubleSolution> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
