package org.uma.jmetal.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * Unit tests for {@link ReferencePointUtils} class.
 */
@DisplayName("ReferencePointUtils Tests")
class ReferencePointUtilsTest {

  @Nested
  @DisplayName("createReferenceFrontFromReferencePoint method")
  class CreateReferenceFrontFromReferencePointTest {

    @Test
    @DisplayName("throws IllegalArgumentException when referencePoint is null")
    void throwsExceptionWhenReferencePointIsNull() {
      // Arrange
      double[] referencePoint = null;

      // Act & Assert
      assertThrows(
          NullParameterException.class,
          () -> ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint));
    }

    @Test
    @DisplayName("throws IllegalArgumentException when referencePoint is empty")
    void throwsExceptionWhenReferencePointIsEmpty() {
      // Arrange
      double[] referencePoint = new double[0];

      // Act & Assert
      InvalidConditionException exception =
          assertThrows(
              InvalidConditionException.class,
              () -> ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint));
      assertEquals("The reference point cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("creates correct reference front for single objective")
    void createsCorrectFrontForSingleObjective() {
      // Arrange
      double[] referencePoint = {1.0};
      double[][] expectedFront = {{1.0}};

      // Act
      double[][] result = ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint);

      // Assert
      assertArrayEquals(expectedFront, result);
    }

    @Test
    @DisplayName("creates correct reference front for two objectives")
    void createsCorrectFrontForTwoObjectives() {
      // Arrange
      double[] referencePoint = {1.0, 2.0};
      double[][] expectedFront = {{1.0, 0.0}, {0.0, 2.0}};

      // Act
      double[][] result = ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint);

      // Assert
      assertArrayEquals(expectedFront, result);
    }

    @Test
    @DisplayName("creates correct reference front for three objectives")
    void createsCorrectFrontForThreeObjectives() {
      // Arrange
      double[] referencePoint = {1.0, 2.0, 3.0};
      double[][] expectedFront = {
        {1.0, 0.0, 0.0},
        {0.0, 2.0, 0.0},
        {0.0, 0.0, 3.0}
      };

      // Act
      double[][] result = ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint);

      // Assert
      assertArrayEquals(expectedFront, result);
    }
  }

  @Nested
  @DisplayName("extractReferencePointFromReferenceFront method")
  class ExtractReferencePointFromReferenceFrontTest {

    @Test
    @DisplayName("throws NullPointerException when referenceFront is null")
    void throwsExceptionWhenReferenceFrontIsNull() {
      // Arrange
      double[][] referenceFront = null;

      // Act & Assert
      assertThrows(
          NullParameterException.class,
          () -> ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront));
    }

    @Test
    @DisplayName("throws IllegalArgumentException when referenceFront is empty")
    void throwsExceptionWhenReferenceFrontIsEmpty() {
      // Arrange
      double[][] referenceFront = new double[0][];

      // Act & Assert
      InvalidConditionException exception =
          assertThrows(
              InvalidConditionException.class,
              () -> ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront));
      assertEquals("The reference front cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("throws IllegalArgumentException when referenceFront contains null points")
    void throwsExceptionWhenReferenceFrontContainsNullPoints() {
      // Arrange
      double[][] referenceFront = new double[2][];
      referenceFront[0] = new double[] {1.0, 2.0};
      referenceFront[1] = null;

      // Act & Assert
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront));
      assertEquals("The reference front contains null points", exception.getMessage());
    }

    @Test
    @DisplayName("throws IllegalArgumentException when points have inconsistent dimensions")
    void throwsExceptionWhenPointsHaveInconsistentDimensions() {
      // Arrange
      double[][] referenceFront = {{1.0, 2.0}, {3.0}};

      // Act & Assert
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront));
      assertEquals(
          "All points in the reference front must have the same number of objectives",
          exception.getMessage());
    }

    @Test
    @DisplayName("extracts correct reference point from single-point front")
    void extractsCorrectPointFromSinglePointFront() {
      // Arrange
      double[][] referenceFront = {{1.0, 2.0, 3.0}};
      double[] expectedPoint = {1.0, 2.0, 3.0};

      // Act
      double[] result = ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront);

      // Assert
      assertArrayEquals(expectedPoint, result, 0.0001);
    }

    @Test
    @DisplayName("extracts correct reference point from multi-point front")
    void extractsCorrectPointFromMultiPointFront() {
      // Arrange
      double[][] referenceFront = {
        {1.0, 4.0, 3.0},
        {4.0, 2.0, 1.0},
        {2.0, 3.0, 5.0}
      };
      double[] expectedPoint = {4.0, 4.0, 5.0};

      // Act
      double[] result = ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront);

      // Assert
      assertArrayEquals(expectedPoint, result, 0.0001);
    }

    @Test
    @DisplayName("handles negative values correctly")
    void handlesNegativeValuesCorrectly() {
      // Arrange
      double[][] referenceFront = {
        {-1.0, -2.0, -3.0},
        {-4.0, -5.0, -6.0},
        {-7.0, -8.0, -9.0}
      };
      double[] expectedPoint = {-1.0, -2.0, -3.0};

      // Act
      double[] result = ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront);

      // Assert
      assertArrayEquals(expectedPoint, result, 0.0001);
    }
  }
}
