package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Unit tests for class Normalizer of MOMBI")
class NormalizerTest {
  private static final double EPSILON = 1e-12;

  private final Normalizer normalizer = new Normalizer(List.of(0.0, -10.0), List.of(10.0, 10.0));

  @Nested
  @DisplayName("When normalizing a value")
  class WhenNormalizingAValue {
    @ParameterizedTest(name = "value {0} of the objective {1} is {2}")
    @CsvSource({
      "0.0, 0, 0.0",
      "5.0, 0, 0.5",
      "10.0, 0, 1.0",
      "-10.0, 1, 0.0",
      "0.0, 1, 0.5",
      "10.0, 1, 1.0",
      "15.0, 0, 1.5",
      "-5.0, 0, -0.5"
    })
    @DisplayName("given a value, then it is mapped with the range of its objective")
    void givenAValue_whenNormalizing_thenItIsMappedWithTheRangeOfItsObjective(
        double value, int objective, double expected) {
      // Act
      double result = normalizer.normalize(value, objective);

      // Assert
      assertEquals(expected, result, EPSILON);
    }

    @Test
    @DisplayName("given negative bounds, then the range is the difference of the bounds")
    void givenNegativeBounds_whenNormalizing_thenTheRangeIsTheDifferenceOfTheBounds() {
      // Arrange
      var negative = new Normalizer(List.of(-8.0), List.of(-4.0));

      // Act & Assert
      assertEquals(0.5, negative.normalize(-6.0, 0), EPSILON);
      assertEquals(1.0, negative.normalize(-4.0, 0), EPSILON);
    }

    @Test
    @DisplayName("given an objective that does not exist, then it is rejected")
    void givenAnObjectiveThatDoesNotExist_whenNormalizing_thenItIsRejected() {
      // Act & Assert
      assertThrows(IndexOutOfBoundsException.class, () -> normalizer.normalize(1.0, 2));
    }

    @Test
    @DisplayName("given equal bounds, then the result is not a finite number")
    void givenEqualBounds_whenNormalizing_thenTheResultIsNotFinite() {
      // Arrange
      var equalBounds = new Normalizer(List.of(1.0), List.of(1.0));

      // Act & Assert
      assertFalse(Double.isFinite(equalBounds.normalize(3.0, 0)));
      assertFalse(Double.isFinite(equalBounds.normalize(1.0, 0)));
    }
  }
}
