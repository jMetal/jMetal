package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.createinitialsolutions.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.evaluation.Evaluation;
import org.uma.jmetal.auto.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.replacement.Replacement;
import org.uma.jmetal.auto.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.selection.MatingPoolSelection;
import org.uma.jmetal.auto.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.termination.Termination;
import org.uma.jmetal.auto.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.auto.variation.Variation;
import org.uma.jmetal.auto.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.BLXAlphaCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import picocli.CommandLine.Option;

import java.util.Arrays;

enum CreateInitialSolutionsStrategyList {
  random
}

enum MutationType {
  polynomial,
  uniform
}

enum SelectionType {
  random,
  tournament
}

enum VariationType {
  rankingAndCrowding
}

enum CrossoverType {
  sbx,
  blx_alpha
}

public class AutoNSGAII {
  /* Fixed parameters */
  int populationSize = 100;
  DoubleProblem problem = new ZDT1();

  /* Fixed components */
  Termination termination = new TerminationByEvaluations(25000);
  Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);
  Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>());
  DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
  MultiComparator<DoubleSolution> rankingAndCrowdingComparator =
      new MultiComparator<>(
          Arrays.asList(ranking.getSolutionComparator(), densityEstimator.getSolutionComparator()));

  Replacement<DoubleSolution> replacement = new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator) ;

  /* Configurable components*/
  /* Crossover */
  @Option(
      names = {"--crossover"},
      required = true,
      description = "Crossover: ${COMPLETION-CANDIDATES}")
  private CrossoverType crossoverType;

  @Option(
      names = {"--crossoverProbability"},
      description = "Crossover probability (default: ${DEFAULT-VALUE})")
  private double crossoverProbability = 0.9;

  CrossoverOperator<DoubleSolution> crossover ;

  /* Mutation */
  @Option(
      names = {"--mutation"},
      required = true,
      description = "Mutation: ${COMPLETION-CANDIDATES}")
  private MutationType mutationType;

  @Option(
      names = {"--mutationProbability"},
      description = "Mutation probability (default: ${DEFAULT-VALUE})")
  private double mutationProbability = 0.01;

  MutationOperator<DoubleSolution> mutation ;

  @Option(
      names = {"--offspringPopulationSize"},
      description = "offspring population size (default: ${DEFAULT-VALUE})")
  private int offspringPopulationSize = populationSize;

  @Option(
      names = {"--createInitialSolutions"},
      required = true,
      description = "Create initial solutions: ${COMPLETION-CANDIDATES}")
  private CreateInitialSolutionsStrategyList createInitialSolutionsType =
      CreateInitialSolutionsStrategyList.random;

  CreateInitialSolutions<DoubleSolution> createInitialSolutions ;

  @Option(
      names = {"--variation"},
      required = true,
      description = "Variation: ${COMPLETION-CANDIDATES}")
  private VariationType variationType;
  Variation<DoubleSolution> variation ;

  /* Selection */
  @Option(
          names = {"--selection"},
          required = true,
          description = "Selection: ${COMPLETION-CANDIDATES}")
  private SelectionType selectionType;

  @Option(
          names = {"--selectionTournamentSize"},
          description = "Selection: number of selected solutions")
  private int selectionTournamentSize = 2;

  MatingPoolSelection<DoubleSolution> selection ;

  EvolutionaryAlgorithm<DoubleSolution> configureAndGetAlgorithm() {
    crossover = getCrossover();
    mutation = getMutation();
    createInitialSolutions = getCreateInitialSolutionStrategy() ;
    variation = getVariation();
    selection = getSelection() ;

    EvolutionaryAlgorithm<DoubleSolution> nsgaii =
        new EvolutionaryAlgorithm<>(
            "NSGAII",
            evaluation,
            createInitialSolutions,
            termination,
            selection,
            variation,
            replacement);

    return nsgaii;
  }

  Variation<DoubleSolution> getVariation() {
    switch (variationType) {
      case rankingAndCrowding:
        return new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);
      default:
        throw new RuntimeException(variationType + " is not a valid variation component");
    }
  }

  MatingPoolSelection<DoubleSolution> getSelection() {
    switch (selectionType) {
      case random:
        return new RandomMatingPoolSelection<>(variation.getMatingPoolSize());
      case tournament:
        return new NaryTournamentMatingPoolSelection<>(
            selectionTournamentSize, variation.getMatingPoolSize(), rankingAndCrowdingComparator);
      default:
        throw new RuntimeException(selectionType + " is not a valid selection operator");
    }
  }

  CrossoverOperator<DoubleSolution> getCrossover() {
    switch (crossoverType) {
      case sbx:
        return new SBXCrossover(crossoverProbability, 0.20);
      case blx_alpha:
        return new BLXAlphaCrossover(crossoverProbability, 0.5);
      default:
        throw new RuntimeException(crossoverType + " is not a valid crossover operator");
    }
  }

  MutationOperator<DoubleSolution> getMutation() {
    switch (mutationType) {
      case polynomial:
        return new PolynomialMutation(mutationProbability, 0.20);
      case uniform:
        return new UniformMutation(crossoverProbability, 0.5);
      default:
        throw new RuntimeException(mutationType + " is not a valid mutation operator");
    }
  }

  CreateInitialSolutions<DoubleSolution> getCreateInitialSolutionStrategy() {
    switch (createInitialSolutionsType) {
      case random:
        return new RandomSolutionsCreation(problem, populationSize);
      default:
        throw new RuntimeException(
            createInitialSolutions + " is not a valid initialization strategy");
    }
  }
}
