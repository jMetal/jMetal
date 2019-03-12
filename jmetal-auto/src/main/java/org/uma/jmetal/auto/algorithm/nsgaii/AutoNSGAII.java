package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.component.createinitialsolutions.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import picocli.CommandLine;
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

@CommandLine.Command(name = "autonsgaII", subcommands = {
        NaryTournamentMatingPoolSelection.class,
})

public class AutoNSGAII {
  /* Fixed parameters */
  int populationSize = 100;

  @Option(names = {"-p","--problemName"}, required = true,
          description = "problem name})")
  private String problemName ;

  @Option(names = {"-rf","--referenceFront"}, required = true,
          description = "reference front")
  private String referenceParetoFront ;

  /* Fixed components */
  Termination termination = new TerminationByEvaluations(25000);
  Evaluation<DoubleSolution> evaluation ;
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

  @Option(
          names = {"--sbxCrossoverDistributionIndex"},
          description = "SBX Crossover Distribution Index (default: ${DEFAULT-VALUE})")
  private double sbxCrossoverDistributionIndex = 0.20;

  @Option(
          names = {"--blxAlphaCrossoverAlphaValue"},
          description = "BLX-alpha Crossover Alpha value (default: ${DEFAULT-VALUE})")
  private double blxAlphaCrossoverAlphaValue = 0.5;

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

  @Option(
          names = {"--polynomialMutationDistributionIndex"},
          description = "Polynomial Mutation Distribution Index (default: ${DEFAULT-VALUE})")
  private double polynomialMutationDistributionIndex = 0.20;

  @Option(
          names = {"--uniformMutationPerturbation"},
          description = "Uniform Mutation Perturbation (default: ${DEFAULT-VALUE})")
  private double uniformMutationPerturbation = 0.5;


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

  /* Variation */
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
    evaluation = new SequentialEvaluation<>(getProblem());

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
        return new SBXCrossover(crossoverProbability, sbxCrossoverDistributionIndex);
      case blx_alpha:
        return new BLXAlphaCrossover(crossoverProbability, blxAlphaCrossoverAlphaValue);
      default:
        throw new RuntimeException(crossoverType + " is not a valid crossover operator");
    }
  }

  MutationOperator<DoubleSolution> getMutation() {
    switch (mutationType) {
      case polynomial:
        return new PolynomialMutation(mutationProbability, polynomialMutationDistributionIndex);
      case uniform:
        return new UniformMutation(crossoverProbability, uniformMutationPerturbation);
      default:
        throw new RuntimeException(mutationType + " is not a valid mutation operator");
    }
  }

  CreateInitialSolutions<DoubleSolution> getCreateInitialSolutionStrategy() {
    switch (createInitialSolutionsType) {
      case random:
        return new RandomSolutionsCreation(getProblem(), populationSize);
      default:
        throw new RuntimeException(
            createInitialSolutions + " is not a valid initialization strategy");
    }
  }

  DoubleProblem getProblem(){
    return  (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);
  }
  String getReferenceParetoFront(){
    return "pareto_fronts/"+referenceParetoFront;
  }

}
