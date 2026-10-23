package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.constrainthandling.EpsilonConstrained;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.constrainthandling.PushPullSearch;
import org.uma.jmetal.util.constrainthandling.SelfAdaptivePenalty;
import org.uma.jmetal.util.constrainthandling.StochasticRanking;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Every algorithm that takes a constraint-handling technique finds a feasible front on Srinivas
 * with each technique it accepts: the algorithms that rank their population with the six of them,
 * and the ones that compare pairs of solutions with the four pairwise ones.
 */
@DisplayName("Integration tests of the constraint-handling techniques in the builders")
class ConstraintHandlingTechniqueBuildersIT {
  private static final int EVALUATIONS = 10000;

  /** The pairwise techniques, in a fixed order. */
  private static Map<String, Supplier<PairwiseConstraintHandlingTechnique<DoubleSolution>>>
      pairwiseTechniques() {
    Map<String, Supplier<PairwiseConstraintHandlingTechnique<DoubleSolution>>> techniques =
        new LinkedHashMap<>();
    techniques.put("CDP", ConstrainedDominance::new);
    techniques.put(
        "CDP with the number of violated constraints",
        () ->
            new ConstrainedDominance<>(
                ConstrainedDominance.ViolationMeasure.NUMBER_OF_VIOLATED_CONSTRAINTS));
    techniques.put("epsilon-constrained", () -> new EpsilonConstrained<>(0.2, 5.0, 0.5));
    techniques.put("push and pull", () -> new PushPullSearch<>(0.001, 0.02, 0.8, 2.0, 0.95, 0.1));
    return techniques;
  }

  /** All the techniques, in a fixed order. */
  private static Map<String, Supplier<? extends ConstraintHandlingTechnique<DoubleSolution>>>
      allTechniques() {
    Map<String, Supplier<? extends ConstraintHandlingTechnique<DoubleSolution>>> techniques =
        new LinkedHashMap<>(pairwiseTechniques());
    techniques.put("stochastic ranking", () -> new StochasticRanking<>(0.45));
    techniques.put("self-adaptive penalty", SelfAdaptivePenalty::new);
    return techniques;
  }

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
  }

  private static void assertTheResultIsFeasible(List<DoubleSolution> result) {
    assertThat(result).isNotEmpty();
    assertThat(ConstraintHandling.feasibilityRatio(result)).isGreaterThan(0.9);
  }

  @Nested
  @DisplayName("When the algorithm ranks its population")
  class WhenTheAlgorithmRanksItsPopulation {
    static Stream<Arguments> algorithmsAndTechniques() {
      List<Arguments> arguments = new ArrayList<>();
      var techniques = allTechniques();
      ConstraintHandlingBuilders.rankingAlgorithms(EVALUATIONS)
          .forEach(
              (algorithm, build) ->
                  techniques.forEach(
                      (technique, create) ->
                          arguments.add(
                              Arguments.of(algorithm, technique, build, create))));
      return arguments.stream();
    }

    @ParameterizedTest(name = "{0} with {1}")
    @MethodSource("algorithmsAndTechniques")
    @DisplayName("given a technique, when solving Srinivas, then the result is feasible")
    void givenATechnique_whenSolvingSrinivas_thenTheResultIsFeasible(
        String algorithmName,
        String techniqueName,
        BiFunction<
                DoubleProblem,
                ConstraintHandlingTechnique<DoubleSolution>,
                Algorithm<List<DoubleSolution>>>
            build,
        Supplier<? extends ConstraintHandlingTechnique<DoubleSolution>> technique) {
      // Arrange
      var algorithm = build.apply(new Srinivas(), technique.get());

      // Act
      algorithm.run();

      // Assert
      assertTheResultIsFeasible(algorithm.result());
    }
  }

  @Nested
  @DisplayName("When the algorithm compares pairs of solutions")
  class WhenTheAlgorithmComparesPairsOfSolutions {
    static Stream<Arguments> algorithmsAndTechniques() {
      List<Arguments> arguments = new ArrayList<>();
      ConstraintHandlingBuilders.pairwiseAlgorithms(EVALUATIONS)
          .forEach(
              (algorithm, build) ->
                  pairwiseTechniques().forEach(
                      (technique, create) -> {
                        // With the number of violated constraints, two infeasible solutions that
                        // violate as many constraints are compared by their objectives, so the
                        // (1+1) strategy of PAES, without a population, can stay infeasible
                        if (!(algorithm.equals("PAES") && technique.startsWith("CDP with"))) {
                          arguments.add(Arguments.of(algorithm, technique, build, create));
                        }
                      }));
      return arguments.stream();
    }

    @ParameterizedTest(name = "{0} with {1}")
    @MethodSource("algorithmsAndTechniques")
    @DisplayName("given a pairwise technique, when solving Srinivas, then the result is feasible")
    void givenAPairwiseTechnique_whenSolvingSrinivas_thenTheResultIsFeasible(
        String algorithmName,
        String techniqueName,
        BiFunction<
                DoubleProblem,
                PairwiseConstraintHandlingTechnique<DoubleSolution>,
                Algorithm<List<DoubleSolution>>>
            build,
        Supplier<PairwiseConstraintHandlingTechnique<DoubleSolution>> technique) {
      // Arrange
      var algorithm = build.apply(new Srinivas(), technique.get());

      // Act
      algorithm.run();

      // Assert
      assertTheResultIsFeasible(algorithm.result());
    }
  }

  @Nested
  @DisplayName("When the algorithm has an external archive")
  class WhenTheAlgorithmHasAnExternalArchive {
    static Stream<Arguments> techniques() {
      return allTechniques().entrySet().stream()
          .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest(name = "NSGA-II with {0}")
    @MethodSource("techniques")
    @DisplayName("given a technique, when solving Srinivas, then the archive keeps only feasible")
    void givenATechnique_whenSolvingSrinivas_thenTheArchiveKeepsOnlyFeasibleSolutions(
        String techniqueName,
        Supplier<? extends ConstraintHandlingTechnique<DoubleSolution>> technique) {
      // Arrange
      var problem = new Srinivas();
      var archive = new NonDominatedSolutionListArchive<DoubleSolution>();
      var algorithm =
          new NSGAIIBuilder<>(
                  problem, 20, 20, new SBXCrossover(0.9, 20.0),
                  new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
              .setTermination(new TerminationByEvaluations(EVALUATIONS))
              .setEvaluation(new SequentialEvaluationWithArchive<>(problem, archive))
              .setConstraintHandlingTechnique(technique.get())
              .build();

      // Act
      algorithm.run();

      // Assert: the archive compares with CDP, so no infeasible solution displaces a feasible one
      assertThat(archive.solutions()).isNotEmpty();
      assertThat(ConstraintHandling.feasibilityRatio(archive.solutions())).isEqualTo(1.0);
    }
  }
}
