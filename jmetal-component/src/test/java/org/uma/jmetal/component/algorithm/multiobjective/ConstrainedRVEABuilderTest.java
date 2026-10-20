package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Binh2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Builders of the RVEA family on a problem with constraints")
class ConstrainedRVEABuilderTest {
  private Binh2 problem;

  @BeforeEach
  void setUp() {
    problem = new Binh2();
    JMetalRandom.getInstance().setSeed(42);
  }

  private RVEABuilder<DoubleSolution> builder(String variant) {
    var crossover = new SBXCrossover(1, 20);
    var mutation = new PolynomialMutation(0.5, 20);
    return switch (variant) {
      case "RVEA" -> new RVEABuilder<>(problem, 21, 2100, crossover, mutation, 2, 0.1, 20);
      case "RVEAStar" -> new RVEAStarBuilder<>(problem, 21, 2100, crossover, mutation, 2, 0.1, 20);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"RVEA", "RVEAStar"})
  @DisplayName("given Binh2 and a seed, when run twice, then the same feasible result is returned")
  void givenBinh2AndASeed_whenRunTwice_thenTheSameFeasibleResultIsReturned(String variant) {
    // Arrange
    var first = builder(variant).build();
    first.run();
    JMetalRandom.getInstance().setSeed(42);
    var second = builder(variant).build();

    // Act
    second.run();

    // Assert
    assertFalse(first.result().isEmpty());
    assertEquals(first.result().size(), second.result().size());
    for (int i = 0; i < first.result().size(); i++) {
      var solution = first.result().get(i);
      assertTrue(ConstraintHandling.isFeasible(solution));
      assertArrayEquals(solution.objectives(), second.result().get(i).objectives());
    }
  }

  @Test
  @DisplayName("given Binh2, when building iRVEA, then the problem is rejected")
  void givenBinh2_whenBuildingIrvea_thenTheProblemIsRejected() {
    // Arrange
    var crossover = new SBXCrossover(1, 20);
    var mutation = new PolynomialMutation(0.5, 20);

    // Act & Assert
    assertThrows(
        InvalidConditionException.class,
        () -> new IRVEABuilder<>(problem, 21, 2100, crossover, mutation, 2, 0.1, 20));
  }
}
