package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.problem.multiobjective.maf.MaF01;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

@DisplayName("Unit tests for RVEA variant builders and component integration")
class RVEAVariantsBuilderTest {
  private Problem<DoubleSolution> problem;
  private long previousSeed;

  @BeforeEach
  void setUp() {
    problem = new DTLZ2();
    previousSeed = JMetalRandom.getInstance().getSeed();
    JMetalRandom.getInstance().setSeed(12345);
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setSeed(previousSeed);
  }

  @Nested
  @DisplayName("When running benchmark problems")
  class Benchmarks {
    @ParameterizedTest
    @MethodSource(
        "org.uma.jmetal.component.algorithm.multiobjective.RVEAVariantsBuilderTest#benchmarks")
    @DisplayName(
        "given DTLZ or MaF, when running a variant, then produce finite bounded solutions within"
            + " the budget")
    void givenBenchmark_whenRunningVariant_thenProduceFiniteBoundedSolutions(
        String variant, String problemName, int objectives) {
      // Arrange
      problem =
          switch (problemName) {
            case "DTLZ2" -> new DTLZ2(objectives + 9, objectives);
            case "DTLZ5" -> new DTLZ5(objectives + 9, objectives);
            case "DTLZ7" -> new DTLZ7(objectives + 19, objectives);
            case "MaF1" -> new MaF01(objectives + 9, objectives);
            default -> throw new IllegalArgumentException(problemName);
          };
      int divisions = objectives > 3 ? 1 : 4;
      int size = ReferencePointGenerator.calculateNumberOfReferencePoints(objectives, divisions);
      var algorithm = builder(variant, size, size * 15, divisions).build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(variant, algorithm.name());
      assertEquals(size * 15, algorithm.numberOfEvaluations());
      assertTrue(!algorithm.result().isEmpty() && algorithm.result().size() <= size);
      for (DoubleSolution solution : algorithm.result()) {
        assertTrue(Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
        for (int i = 0; i < solution.variables().size(); i++) {
          assertTrue(solution.variables().get(i) >= solution.getBounds(i).getLowerBound());
          assertTrue(solution.variables().get(i) <= solution.getBounds(i).getUpperBound());
        }
      }
    }

    @ParameterizedTest
    @ValueSource(strings = {"RVEA", "RVEA*", "iRVEA"})
    @DisplayName(
        "given a fixed seed, when running DTLZ2, then approach its unit-sphere Pareto front")
    void givenFixedSeed_whenRunningDtlz2_thenApproachUnitSphereFront(String variant) {
      // Arrange
      var initial = Stream.generate(problem::createSolution).limit(15).toList();
      initial.forEach(problem::evaluate);
      double initialError = radialError(initial);
      var algorithm =
          builder(variant, 15, 4500, 4).setCreateInitialPopulation(() -> initial).build();

      // Act
      algorithm.run();

      // Assert
      assertTrue(
          radialError(algorithm.result()) < initialError * 0.5,
          () ->
              variant + " radial error: " + radialError(algorithm.result()) + " / " + initialError);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RVEA", "RVEA*", "iRVEA"})
    @DisplayName(
        "given a reused builder and reset seed, when building fresh runs, then trajectories agree")
    void givenReusedBuilderAndSeed_whenBuildingFreshRuns_thenTrajectoriesAgree(String variant) {
      // Arrange
      var builder = builder(variant, 15, 300, 4);
      JMetalRandom.getInstance().setSeed(19);
      var first = builder.build();
      first.run();

      // Act
      JMetalRandom.getInstance().setSeed(19);
      var second = builder.build();
      second.run();

      // Assert
      assertEquals(first.result().size(), second.result().size());
      for (int i = 0; i < first.result().size(); i++) {
        assertArrayEquals(first.result().get(i).objectives(), second.result().get(i).objectives());
      }
    }
  }

  @Nested
  @DisplayName("When configuring components")
  class Configuration {
    @Test
    @DisplayName(
        "given odd offspring batches, when configuring variation, then mating size and progress"
            + " stay consistent")
    void givenOddOffspringBatches_whenConfiguringVariation_thenMatingAndProgressStayConsistent() {
      // Arrange
      var variation =
          new CrossoverAndMutationVariation<>(
              7,
              new SBXCrossover(1, 20),
              new PolynomialMutation(1.0 / problem.numberOfVariables(), 20));
      var algorithm = builder("iRVEA", 15, 36, 4).setVariation(variation).build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(36, algorithm.numberOfEvaluations());
      assertTrue(!algorithm.result().isEmpty());
    }

    @Test
    @DisplayName(
        "given a changed builder initializer, when running an earlier build, then it uses the"
            + " captured initializer")
    void givenChangedBuilderInitializer_whenRunningEarlierBuild_thenUseCapturedInitializer() {
      // Arrange
      var builder = builder("RVEA*", 15, 15, 4);
      EvolutionaryAlgorithm<DoubleSolution> algorithm = builder.build();
      builder.setCreateInitialPopulation(List::of);

      // Act
      algorithm.run();

      // Assert
      assertEquals(15, algorithm.result().size());
      assertEquals(15, algorithm.numberOfEvaluations());
    }

    @Test
    @DisplayName(
        "given invalid configuration, when building or initializing, then reject it explicitly")
    void givenInvalidConfiguration_whenBuildingOrInitializing_thenRejectItExplicitly() {
      // Arrange
      var builder = builder("RVEA*", 15, 300, 4);

      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> builder("RVEA*", 14, 300, 4));
      assertThrows(InvalidConditionException.class, () -> builder("iRVEA", 15, 14, 4));
      builder.setTermination(status -> false);
      assertThrows(InvalidConditionException.class, builder::build);
      builder.setTermination(new TerminationByEvaluations(300));
      var invalidInitial = builder.setCreateInitialPopulation(List::of).build();
      assertThrows(InvalidConditionException.class, invalidInitial::run);
    }
  }

  private RVEABuilder<DoubleSolution> builder(String variant, int size, int budget, int divisions) {
    var crossover = new SBXCrossover(1, 20);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20);
    return switch (variant) {
      case "RVEA" ->
          new RVEABuilder<>(problem, size, budget, crossover, mutation, 2, 0.1, divisions);
      case "RVEA*" ->
          new RVEAStarBuilder<>(problem, size, budget, crossover, mutation, 2, 0.1, divisions);
      case "iRVEA" ->
          new IRVEABuilder<>(problem, size, budget, crossover, mutation, 2, 0.1, divisions);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  static Stream<Arguments> benchmarks() {
    return Stream.of("RVEA", "RVEA*", "iRVEA")
        .flatMap(
            variant ->
                Stream.of(
                    Arguments.of(variant, "DTLZ2", 3),
                    Arguments.of(variant, "DTLZ5", 3),
                    Arguments.of(variant, "DTLZ7", 3),
                    Arguments.of(variant, "MaF1", 3),
                    Arguments.of(variant, "DTLZ2", 7)));
  }

  private static double radialError(List<DoubleSolution> population) {
    return population.stream()
        .mapToDouble(
            solution ->
                Math.abs(
                    Math.sqrt(Arrays.stream(solution.objectives()).map(x -> x * x).sum()) - 1.0))
        .average()
        .orElseThrow();
  }
}
