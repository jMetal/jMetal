package org.uma.jmetal.util.referencepoint;

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

@DisplayName("Unit tests for class ReferencePointGenerator")
class RVEAReferencePointGeneratorTest {
  @Nested
  @DisplayName("When generating a simplex lattice")
  class Lattice {
    @ParameterizedTest
    @CsvSource({"2, 3, 4", "3, 12, 91", "3, 13, 105", "7, 1, 7", "8, 3, 120"})
    @DisplayName(
        "given objectives and divisions, when generating, then obtain the binomial number of"
            + " simplex points")
    void givenObjectivesAndDivisions_whenGenerating_thenObtainBinomialNumberOfPoints(
        int objectives, int divisions, int expected) {
      // Arrange / Act
      var vectors = ReferencePointGenerator.generateSingleLayer(objectives, divisions);

      // Assert
      assertEquals(expected, vectors.size());
      assertEquals(
          expected,
          ReferencePointGenerator.calculateNumberOfReferencePoints(objectives, divisions));
      assertEquals(expected, vectors.stream().map(Arrays::toString).distinct().count());
      for (double[] vector : vectors) {
        assertEquals(1.0, Arrays.stream(vector).sum(), 1.0e-12);
        assertTrue(Arrays.stream(vector).allMatch(x -> x >= 0 && x <= 1));
      }
    }

    @Test
    @DisplayName(
        "given two layers, when generating, then interior points are shifted toward the center")
    void givenTwoLayers_whenGenerating_thenInteriorPointsShiftTowardCenter() {
      // Arrange / Act
      var vectors = ReferencePointGenerator.generateTwoLayers(8, 3, 2);

      // Assert
      assertEquals(156, vectors.size());
      assertTrue(
          vectors.subList(120, 156).stream()
              .allMatch(vector -> Arrays.stream(vector).allMatch(x -> x > 0)));
      assertTrue(
          vectors.stream().allMatch(vector -> Math.abs(Arrays.stream(vector).sum() - 1) < 1.0e-12));
    }

    @Test
    @DisplayName("given invalid dimensions, when generating, then reject them")
    void givenInvalidDimensions_whenGenerating_thenRejectThem() {
      // Arrange / Act & Assert
      assertThrows(
          InvalidConditionException.class, () -> ReferencePointGenerator.generateSingleLayer(1, 3));
      assertThrows(
          InvalidConditionException.class, () -> ReferencePointGenerator.generateSingleLayer(3, 0));
    }
  }
}
