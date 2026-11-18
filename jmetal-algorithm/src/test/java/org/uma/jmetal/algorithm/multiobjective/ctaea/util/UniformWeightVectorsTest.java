package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class UniformWeightVectors")
class UniformWeightVectorsTest {
  private static final double EPSILON = 1e-12;

  @Nested
  @DisplayName("When generating the weight vectors")
  class WhenGeneratingTheWeightVectors {

    @ParameterizedTest
    @CsvSource({
      "100, 2, 100",
      "100, 3, 91",
      "92, 3, 91",
      "105, 5, 105",
      "156, 8, 156",
      "275, 10, 275",
      "2, 3, 3",
      "5, 3, 3"
    })
    @DisplayName("given N and M, when generating, then the number of vectors is the one of PlatEMO")
    void givenNAndM_whenGenerating_thenTheNumberOfVectorsIsTheOneOfPlatEMO(
        int numberOfVectors, int numberOfObjectives, int expectedNumberOfVectors) {
      // Act
      double[][] weights = UniformWeightVectors.generate(numberOfVectors, numberOfObjectives);

      // Assert
      assertEquals(expectedNumberOfVectors, weights.length);
      for (double[] weight : weights) {
        assertEquals(numberOfObjectives, weight.length);
      }
    }

    @ParameterizedTest
    @CsvSource({"100, 2", "100, 3", "105, 5", "275, 10"})
    @DisplayName("given N and M, when generating, then every vector sums 1 except for the floor")
    void givenNAndM_whenGenerating_thenEveryVectorSumsOneExceptForTheFloor(
        int numberOfVectors, int numberOfObjectives) {
      // Act
      double[][] weights = UniformWeightVectors.generate(numberOfVectors, numberOfObjectives);

      // Assert
      double tolerance = numberOfObjectives * UniformWeightVectors.MINIMUM_COMPONENT;
      for (double[] weight : weights) {
        assertEquals(1.0, Arrays.stream(weight).sum(), tolerance);
        for (double component : weight) {
          assertTrue(component >= UniformWeightVectors.MINIMUM_COMPONENT);
        }
      }
    }

    @Test
    @DisplayName("given three objectives, when generating, then the order is the one of PlatEMO")
    void givenThreeObjectives_whenGenerating_thenTheOrderIsTheOneOfPlatEMO() {
      // Arrange: UniformPoint(6, 3) gives the rows [0 0 2; 0 1 1; 0 2 0; 1 0 1; 1 1 0; 2 0 0]/2
      double floor = UniformWeightVectors.MINIMUM_COMPONENT;
      double[][] expected = {
        {floor, floor, 1.0},
        {floor, 0.5, 0.5},
        {floor, 1.0, floor},
        {0.5, floor, 0.5},
        {0.5, 0.5, floor},
        {1.0, floor, floor}
      };

      // Act
      double[][] weights = UniformWeightVectors.generate(6, 3);

      // Assert
      assertEquals(expected.length, weights.length);
      for (int i = 0; i < expected.length; i++) {
        assertArrayEquals(expected[i], weights[i], EPSILON);
      }
    }

    @Test
    @DisplayName("given H1 smaller than M, when generating, then the inner layer is shrunk")
    void givenH1SmallerThanM_whenGenerating_thenTheInnerLayerIsShrunk() {
      // Arrange: UniformPoint(105, 5) has an outer layer with H1 = 4 (70 vectors) and an inner
      // one with H2 = 3 (35 vectors), whose vectors are W2 / 2 + 1 / (2 M)
      int numberOfObjectives = 5;

      // Act
      double[][] weights = UniformWeightVectors.generate(105, numberOfObjectives);

      // Assert
      double[] firstInnerVector = weights[70];
      assertArrayEquals(new double[] {0.1, 0.1, 0.1, 0.1, 0.6}, firstInnerVector, EPSILON);
      for (int i = 70; i < weights.length; i++) {
        for (double component : weights[i]) {
          assertTrue(component >= 1.0 / (2 * numberOfObjectives) - EPSILON);
        }
      }
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {

    @Test
    @DisplayName("given zero vectors, when generating, then an exception is thrown")
    void givenZeroVectors_whenGenerating_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> UniformWeightVectors.generate(0, 3));
    }

    @Test
    @DisplayName("given one objective, when generating, then an exception is thrown")
    void givenOneObjective_whenGenerating_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> UniformWeightVectors.generate(10, 1));
    }
  }
}
