package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Binh2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Constrained RVEA-family builder integration")
class ConstrainedRVEABuilderTest {
  private Binh2 problem;

  @BeforeEach
  void setUp() {
    problem = new Binh2();
    JMetalRandom.getInstance().setSeed(42);
  }

  private RVEABuilder<DoubleSolution> builder(String variant, int budget) {
    var crossover = new SBXCrossover(1, 20);
    var mutation = new PolynomialMutation(0.5, 20);
    return switch (variant) {
      case "RVEA" -> new RVEABuilder<>(problem, 21, budget, crossover, mutation, 2, 0.1, 20);
      case "RVEAStar" ->
          new RVEAStarBuilder<>(problem, 21, budget, crossover, mutation, 2, 0.1, 20);
      case "IRVEA" -> new IRVEABuilder<>(problem, 21, budget, crossover, mutation, 2, 0.1, 20);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName("given Binh2 and a seed, when running twice, then return identical feasible results")
  void givenBinh2AndSeed_whenRunningTwice_thenReturnIdenticalFeasibleResults(String variant) {
    // Arrange
    var first = builder(variant, 2100).build();

    // Act
    first.run();
    JMetalRandom.getInstance().setSeed(42);
    var second = builder(variant, 2100).build();
    second.run();

    // Assert
    assertEquals(2, problem.numberOfConstraints());
    assertEquals(2100, first.numberOfEvaluations());
    assertFalse(first.result().isEmpty());
    assertEquals(first.result().size(), second.result().size());
    for (int i = 0; i < first.result().size(); i++) {
      var solution = first.result().get(i);
      assertTrue(ConstraintHandling.isFeasible(solution));
      assertTrue(Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
      assertEquals(solution.variables(), second.result().get(i).variables());
      assertArrayEquals(solution.objectives(), second.result().get(i).objectives());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName(
      "given a nonmultiple budget, when using default batches, then retain documented overshoot")
  void givenNonmultipleBudget_whenUsingDefaultBatches_thenRetainDocumentedOvershoot(
      String variant) {
    // Arrange
    var algorithm = builder(variant, 100).build();

    // Act
    algorithm.run();

    // Assert: changing upstream termination/batching is outside the constraint-layer scope.
    assertEquals(105, algorithm.numberOfEvaluations());
  }
}
