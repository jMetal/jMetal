package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

/** The builders that take a constraint-handling technique, configured for the tests. */
final class ConstraintHandlingBuilders {
  static final int POPULATION_SIZE = 20;

  private ConstraintHandlingBuilders() {}

  private static PolynomialMutation mutation(DoubleProblem problem) {
    return new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
  }

  /** The algorithms that rank their population, which take any technique. */
  static Map<
          String,
          BiFunction<DoubleProblem, ConstraintHandlingTechnique<DoubleSolution>,
              Algorithm<List<DoubleSolution>>>>
      rankingAlgorithms(int evaluations) {
    var termination = new TerminationByEvaluations(evaluations);
    Map<
            String,
            BiFunction<DoubleProblem, ConstraintHandlingTechnique<DoubleSolution>,
                Algorithm<List<DoubleSolution>>>>
        algorithms = new LinkedHashMap<>();
    algorithms.put(
        "NSGA-II",
        (problem, technique) ->
            new NSGAIIBuilder<>(
                    problem, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
                    mutation(problem))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "NSGA-II-DE",
        (problem, technique) ->
            new NSGAIIDEBuilder(
                    problem, POPULATION_SIZE, 1.0, 0.5, mutation(problem),
                    DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "RDSMOEA",
        (problem, technique) ->
            new RDSMOEABuilder<>(
                    problem, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
                    mutation(problem), new FastNonDominatedSortRanking<>(),
                    new CrowdingDistanceDensityEstimator<>(),
                    RankingAndDensityEstimatorReplacement.RemovalPolicy.ONE_SHOT)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "NSGA-III",
        (problem, technique) ->
            new NSGAIIIBuilder<>(
                    problem, POPULATION_SIZE - 1, new SBXCrossover(0.9, 20.0), mutation(problem))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "SMS-EMOA",
        (problem, technique) ->
            new SMSEMOABuilder<>(
                    problem, POPULATION_SIZE, new SBXCrossover(0.9, 20.0), mutation(problem))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "SMS-EMOA-DE",
        (problem, technique) ->
            new SMSEMOADEBuilder(
                    problem, POPULATION_SIZE, 1.0, 0.5, mutation(problem),
                    DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "AGE-MOEA",
        (problem, technique) ->
            new AGEMOEABuilder<>(
                    problem, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
                    mutation(problem),
                    new AGEMOEAEnvironmentalSelection<>(problem.numberOfObjectives()))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "AGE-MOEA-II",
        (problem, technique) ->
            new AGEMOEABuilder<>(
                    problem, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
                    mutation(problem),
                    new AGEMOEA2EnvironmentalSelection<>(problem.numberOfObjectives()))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    return algorithms;
  }

  /** The algorithms that compare pairs of solutions, which only take pairwise techniques. */
  static Map<
          String,
          BiFunction<DoubleProblem, PairwiseConstraintHandlingTechnique<DoubleSolution>,
              Algorithm<List<DoubleSolution>>>>
      pairwiseAlgorithms(int evaluations) {
    var termination = new TerminationByEvaluations(evaluations);
    Map<
            String,
            BiFunction<DoubleProblem, PairwiseConstraintHandlingTechnique<DoubleSolution>,
                Algorithm<List<DoubleSolution>>>>
        algorithms = new LinkedHashMap<>();
    algorithms.put(
        "PAES",
        (problem, technique) ->
            new PAESBuilder<>(
                    problem, POPULATION_SIZE, mutation(problem),
                    new CrowdingDistanceArchive<DoubleSolution>(POPULATION_SIZE))
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "SMPSO",
        (problem, technique) ->
            new SMPSOBuilder(problem, POPULATION_SIZE)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "MOEA/D",
        (problem, technique) ->
            new MOEADBuilder<>(
                    problem, POPULATION_SIZE, new SBXCrossover(0.9, 20.0), mutation(problem),
                    "../resources/weightVectorFiles/moead",
                    new RandomPermutationCycle(POPULATION_SIZE), false)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    algorithms.put(
        "MOEA/D-DE",
        (problem, technique) ->
            new MOEADDEBuilder(
                    problem, POPULATION_SIZE, 1.0, 0.5, mutation(problem),
                    "../resources/weightVectorFiles/moead",
                    new RandomPermutationCycle(POPULATION_SIZE), false)
                .setTermination(termination)
                .setConstraintHandlingTechnique(technique)
                .build());
    return algorithms;
  }
}
