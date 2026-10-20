package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Binh2;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for the RVEA-family builders on problems with constraints")
class ConstrainedRVEABuilderTest {
  private Binh2 problem;

  @BeforeEach
  void setUp() {
    problem = new Binh2();
    JMetalRandom.getInstance().setSeed(42);
  }

  private static RVEABuilder<DoubleSolution> builder(
      String variant, Problem<DoubleSolution> problem, int budget) {
    var crossover = new SBXCrossover(1, 20);
    var mutation = new PolynomialMutation(0.5, 20);
    return switch (variant) {
      case "RVEA" -> new RVEABuilder<>(problem, 21, budget, crossover, mutation, 2, 0.1, 20);
      case "IRVEA" -> new IRVEABuilder<>(problem, 21, budget, crossover, mutation, 2, 0.1, 20);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  @Nested
  @DisplayName("When running RVEA and iRVEA on Binh2")
  class WhenRunningOnBinh2 {
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"RVEA", "IRVEA"})
    @DisplayName("given a seed, when running twice, then the results are identical and feasible")
    void givenASeed_whenRunningTwice_thenTheResultsAreIdenticalAndFeasible(String variant) {
      // Arrange: iRVEA draws its adaptive vectors when it is built, so each run rebuilds it
      var first = builder(variant, problem, 2100).build();

      // Act
      first.run();
      JMetalRandom.getInstance().setSeed(42);
      var second = builder(variant, problem, 2100).build();
      second.run();

      // Assert
      assertThat(first.numberOfEvaluations()).isEqualTo(2100);
      assertThat(first.result())
          .isNotEmpty()
          .allMatch(ConstraintHandling::isFeasible)
          .allMatch(solution -> Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
      assertThat(first.result()).hasSameSizeAs(second.result());
      for (int i = 0; i < first.result().size(); i++) {
        var solution = first.result().get(i);
        var other = second.result().get(i);
        assertThat(solution.variables()).isEqualTo(other.variables());
        assertThat(solution.objectives()).containsExactly(other.objectives());
      }
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"RVEA", "IRVEA"})
    @DisplayName(
        "given a budget that is not a multiple of the batch size, when running, then the last"
            + " batch overshoots it")
    void givenANonMultipleBudget_whenRunning_thenTheLastBatchOvershootsIt(String variant) {
      // Arrange
      var algorithm = builder(variant, problem, 100).build();

      // Act
      algorithm.run();

      // Assert: changing the termination of the RVEA family is outside the constraint handling
      assertThat(algorithm.numberOfEvaluations()).isEqualTo(105);
    }
  }

  @Nested
  @DisplayName("When running RVEA on a problem whose population keeps infeasible solutions")
  class WhenThePopulationKeepsInfeasibleSolutions {
    @Test
    @DisplayName(
        "given Srinivas, when running, then the result is the feasible non-dominated solutions of"
            + " the population")
    void givenSrinivas_whenRunning_thenTheResultIsTheFeasibleNonDominatedSolutions() {
      // Arrange
      var algorithm = builder("RVEA", new Srinivas(), 4200).build();
      var dominance = new DefaultDominanceComparator<DoubleSolution>();

      // Act
      algorithm.run();
      var result = algorithm.result();

      // Assert
      assertThat(algorithm.population())
          .anyMatch(solution -> !ConstraintHandling.isFeasible(solution));
      assertThat(result)
          .isNotEmpty()
          .allMatch(ConstraintHandling::isFeasible)
          .allMatch(solution -> algorithm.population().contains(solution));
      for (var solution : result) {
        assertThat(result).noneMatch(other -> dominance.compare(other, solution) < 0);
      }
    }
  }

  @Nested
  @DisplayName("When building RVEA* for a problem with constraints")
  class WhenBuildingRVEAStar {
    @Test
    @DisplayName("given Binh2, when building RVEA*, then the problem is rejected")
    void givenBinh2_whenBuildingRVEAStar_thenTheProblemIsRejected() {
      // Arrange
      var crossover = new SBXCrossover(1, 20);
      var mutation = new PolynomialMutation(0.5, 20);

      // Act
      Throwable thrown =
          catchThrowable(
              () -> new RVEAStarBuilder<>(problem, 21, 2100, crossover, mutation, 2, 0.1, 20));

      // Assert
      assertThat(thrown)
          .isInstanceOf(InvalidConditionException.class)
          .hasMessageContaining("RVEA* requires an unconstrained problem");
    }
  }
}
