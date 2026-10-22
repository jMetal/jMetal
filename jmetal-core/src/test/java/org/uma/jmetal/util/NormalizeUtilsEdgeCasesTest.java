package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the edge cases of class NormalizeUtils")
class NormalizeUtilsEdgeCasesTest {
  private static final double EPSILON = 1e-12;

  static Stream<Arguments> invalidMatrices() {
    return Stream.of(
        Arguments.of("without rows", new double[0][]),
        Arguments.of("without columns", new double[][] {{}, {}}),
        Arguments.of("with a shorter row", new double[][] {{1.0, 2.0}, {3.0}}),
        Arguments.of("with a longer row", new double[][] {{1.0}, {3.0, 9.0}}),
        Arguments.of("with a NaN", new double[][] {{1.0}, {Double.NaN}, {3.0}}),
        Arguments.of("with only NaN", new double[][] {{Double.NaN}, {Double.NaN}}),
        Arguments.of(
            "with a positive infinity", new double[][] {{1.0}, {Double.POSITIVE_INFINITY}}),
        Arguments.of("with a negative infinity", new double[][] {{Double.NEGATIVE_INFINITY}}));
  }

  @Nested
  @DisplayName("When normalizing a value")
  class WhenNormalizingAValue {
    @ParameterizedTest(name = "[{0}, {1}]")
    @MethodSource(
        "org.uma.jmetal.util.NormalizeUtilsEdgeCasesTest#invertedBounds")
    @DisplayName("given a lower bound greater than the upper one, then it is rejected")
    void givenInvertedSourceBounds_whenNormalizing_thenItIsRejected(double min, double max) {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.normalize(0.25, min, max));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given an inverted target range, then it is rejected")
    void givenAnInvertedTargetRange_whenNormalizing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.normalize(0.25, 1.0, 0.0, 0.0, 1.0));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a NaN bound, then it is rejected")
    void givenANaNBound_whenNormalizing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.normalize(0.5, 0.0, Double.NaN));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a value out of the bounds, then the result is out of the target range")
    void givenAValueOutOfTheBounds_whenNormalizing_thenTheResultIsOutOfTheTargetRange() {
      // Act & Assert
      assertThat(NormalizeUtils.normalize(-1.0, 0.0, 2.0)).isEqualTo(-0.5);
      assertThat(NormalizeUtils.normalize(3.0, 0.0, 2.0)).isEqualTo(1.5);
    }

    @Test
    @DisplayName("given equal bounds, then the result is the midpoint of the target range")
    void givenEqualBounds_whenNormalizing_thenTheResultIsTheMidpointOfTheTargetRange() {
      // Act & Assert
      assertThat(NormalizeUtils.normalize(7.0, 5.0, 5.0)).isEqualTo(0.5);
      assertThat(NormalizeUtils.normalize(7.0, -1.0, 3.0, 5.0, 5.0)).isEqualTo(1.0);
    }

    @Test
    @DisplayName("given a value on a bound, then the result is a bound of the target range")
    void givenAValueOnABound_whenNormalizing_thenTheResultIsABoundOfTheTargetRange() {
      // Act & Assert
      assertThat(NormalizeUtils.normalize(-4.0, -4.0, 6.0)).isEqualTo(0.0);
      assertThat(NormalizeUtils.normalize(6.0, -4.0, 6.0)).isEqualTo(1.0);
    }
  }

  static Stream<Arguments> invertedBounds() {
    return Stream.of(Arguments.of(1.0, 0.0), Arguments.of(0.0, -1.0), Arguments.of(-1.0, -2.0));
  }

  @Nested
  @DisplayName("When normalizing a vector")
  class WhenNormalizingAVector {
    @Test
    @DisplayName("given a null vector, then it is rejected")
    void givenANullVector_whenNormalizing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.getNormalizedVector(null, 0.0, 1.0));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given an empty vector, then the result is empty")
    void givenAnEmptyVector_whenNormalizing_thenTheResultIsEmpty() {
      // Act & Assert
      assertThat(NormalizeUtils.getNormalizedVector(new double[0], 0.0, 1.0)).isEmpty();
    }

    @Test
    @DisplayName("given equal bounds, then every value is mapped to 0.5")
    void givenEqualBounds_whenNormalizing_thenEveryValueIsMappedToHalf() {
      // Act
      double[] result = NormalizeUtils.getNormalizedVector(new double[] {1.0, 2.0, 3.0}, 4.0, 4.0);

      // Assert
      assertThat(result).containsExactly(0.5, 0.5, 0.5);
    }

    @Test
    @DisplayName("given a vector, then it is not modified")
    void givenAVector_whenNormalizing_thenItIsNotModified() {
      // Arrange
      double[] vector = {10.0, 20.0};

      // Act
      NormalizeUtils.getNormalizedVector(vector, 0.0, 40.0);

      // Assert
      assertThat(vector).containsExactly(10.0, 20.0);
    }
  }

  @Nested
  @DisplayName("When computing the minimum and maximum values of the columns of a matrix")
  class WhenComputingTheMinimumAndMaximumValues {
    @Test
    @DisplayName("given a null matrix, then it is rejected")
    void givenANullMatrix_whenComputing_thenItIsRejected() {
      // Act & Assert
      assertThat(catchThrowable(() -> NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(null)))
          .isInstanceOf(NullParameterException.class);
      assertThat(catchThrowable(() -> NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(null)))
          .isInstanceOf(NullParameterException.class);
    }

    @ParameterizedTest(name = "matrix {0}")
    @MethodSource("org.uma.jmetal.util.NormalizeUtilsEdgeCasesTest#invalidMatrices")
    @DisplayName("given an invalid matrix, then it is rejected")
    void givenAnInvalidMatrix_whenComputing_thenItIsRejected(
        String description, double[][] matrix) {
      // Act & Assert
      assertThat(catchThrowable(() -> NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix)))
          .isInstanceOf(InvalidConditionException.class);
      assertThat(catchThrowable(() -> NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix)))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a matrix with a null row, then it is rejected")
    void givenAMatrixWithANullRow_whenComputing_thenItIsRejected() {
      // Arrange
      double[][] matrix = {{1.0, 2.0}, null};

      // Act & Assert
      assertThat(catchThrowable(() -> NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix)))
          .isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given a column of equal values, then its minimum and maximum are that value")
    void givenAColumnOfEqualValues_whenComputing_thenTheMinimumAndMaximumAreThatValue() {
      // Arrange
      double[][] matrix = {{5.0, 1.0}, {5.0, 2.0}, {5.0, 3.0}};

      // Act & Assert
      assertThat(NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix))
          .containsExactly(5.0, 1.0);
      assertThat(NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix))
          .containsExactly(5.0, 3.0);
    }

    @Test
    @DisplayName("given values of both signs and zero, then the extremes are found")
    void givenValuesOfBothSigns_whenComputing_thenTheExtremesAreFound() {
      // Arrange
      double[][] matrix = {{0.0}, {-3.5}, {2.5}, {0.0}};

      // Act & Assert
      assertThat(NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix)).containsExactly(-3.5);
      assertThat(NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix)).containsExactly(2.5);
    }

    @Test
    @DisplayName("given values of the order of Double.MAX_VALUE, then the extremes are found")
    void givenVeryLargeValues_whenComputing_thenTheExtremesAreFound() {
      // Arrange
      double[][] matrix = {{-Double.MAX_VALUE}, {Double.MAX_VALUE}, {0.0}};

      // Act & Assert
      assertThat(NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix))
          .containsExactly(-Double.MAX_VALUE);
      assertThat(NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix))
          .containsExactly(Double.MAX_VALUE);
    }

    @Test
    @DisplayName("given a matrix, then it is not modified")
    void givenAMatrix_whenComputing_thenItIsNotModified() {
      // Arrange
      double[][] matrix = {{3.0, 4.0}, {1.0, 2.0}};

      // Act
      NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix);
      NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix);

      // Assert
      assertThat(matrix).isDeepEqualTo(new double[][] {{3.0, 4.0}, {1.0, 2.0}});
    }
  }

  @Nested
  @DisplayName("When normalizing a matrix with the bounds of its own columns")
  class WhenNormalizingAMatrix {
    @Test
    @DisplayName("given a null matrix, then it is rejected")
    void givenANullMatrix_whenNormalizing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.normalize((double[][]) null));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @ParameterizedTest(name = "matrix {0}")
    @MethodSource("org.uma.jmetal.util.NormalizeUtilsEdgeCasesTest#invalidMatrices")
    @DisplayName("given an invalid matrix, then it is rejected")
    void givenAnInvalidMatrix_whenNormalizing_thenItIsRejected(
        String description, double[][] matrix) {
      // Act
      Throwable thrown = catchThrowable(() -> NormalizeUtils.normalize(matrix));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a column of equal values, then it is mapped to 0.5 and the others are not")
    void givenAColumnOfEqualValues_whenNormalizing_thenItIsMappedToHalf() {
      // Act
      double[][] result = NormalizeUtils.normalize(new double[][] {{1.0, 5.0}, {2.0, 5.0}});

      // Assert
      assertThat(result).isDeepEqualTo(new double[][] {{0.0, 0.5}, {1.0, 0.5}});
    }

    @Test
    @DisplayName("given a matrix with one row, then every value is mapped to 0.5")
    void givenAMatrixWithOneRow_whenNormalizing_thenEveryValueIsMappedToHalf() {
      // Act
      double[][] result = NormalizeUtils.normalize(new double[][] {{3.0, -7.0}});

      // Assert
      assertThat(result).isDeepEqualTo(new double[][] {{0.5, 0.5}});
    }

    @Test
    @DisplayName("given a matrix with columns of different scales, then each is normalized alone")
    void givenColumnsOfDifferentScales_whenNormalizing_thenEachColumnIsNormalizedAlone() {
      // Arrange
      double[][] matrix = {{0.0, -1000.0, 1e-6}, {5.0, -500.0, 3e-6}, {10.0, 0.0, 2e-6}};

      // Act
      double[][] result = NormalizeUtils.normalize(matrix);

      // Assert
      assertThat(result[0]).containsExactly(new double[] {0.0, 0.0, 0.0}, offset(EPSILON));
      assertThat(result[1]).containsExactly(new double[] {0.5, 0.5, 1.0}, offset(EPSILON));
      assertThat(result[2]).containsExactly(new double[] {1.0, 1.0, 0.5}, offset(EPSILON));
    }

    @Test
    @DisplayName("given a matrix, then it is not modified")
    void givenAMatrix_whenNormalizing_thenItIsNotModified() {
      // Arrange
      double[][] matrix = {{3.0, 4.0}, {1.0, 2.0}};

      // Act
      NormalizeUtils.normalize(matrix);

      // Assert
      assertThat(matrix).isDeepEqualTo(new double[][] {{3.0, 4.0}, {1.0, 2.0}});
    }
  }

  @Nested
  @DisplayName("When normalizing a matrix with given bounds")
  class WhenNormalizingAMatrixWithGivenBounds {
    private final double[][] matrix = {{1.0, 2.0}};
    private final double[] validMin = {0.0, 0.0};
    private final double[] validMax = {1.0, 1.0};

    @Test
    @DisplayName("given a null argument, then it is rejected")
    void givenANullArgument_whenNormalizing_thenItIsRejected() {
      // Act
      Throwable nullMatrix =
          catchThrowable(() -> NormalizeUtils.normalize(null, validMin, validMax));
      Throwable nullMinimums =
          catchThrowable(() -> NormalizeUtils.normalize(matrix, null, validMax));
      Throwable nullMaximums =
          catchThrowable(() -> NormalizeUtils.normalize(matrix, validMin, null));

      // Assert
      assertThat(nullMatrix).isInstanceOf(NullParameterException.class);
      assertThat(nullMinimums).isInstanceOf(NullParameterException.class);
      assertThat(nullMaximums).isInstanceOf(NullParameterException.class);
    }

    @ParameterizedTest(name = "matrix {0}")
    @MethodSource("org.uma.jmetal.util.NormalizeUtilsEdgeCasesTest#invalidMatrices")
    @DisplayName("given an invalid matrix, then it is rejected")
    void givenAnInvalidMatrix_whenNormalizing_thenItIsRejected(
        String description, double[][] invalid) {
      // Act
      Throwable thrown =
          catchThrowable(() -> NormalizeUtils.normalize(invalid, validMin, validMax));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given bounds that are not as many as the columns, then they are rejected")
    void givenBoundsOfAnotherLength_whenNormalizing_thenTheyAreRejected() {
      // Act
      Throwable fewerMinimums =
          catchThrowable(() -> NormalizeUtils.normalize(matrix, new double[] {0.0}, validMax));
      Throwable moreMaximums =
          catchThrowable(
              () -> NormalizeUtils.normalize(matrix, validMin, new double[] {1.0, 1.0, 1.0}));

      // Assert
      assertThat(fewerMinimums).isInstanceOf(InvalidConditionException.class);
      assertThat(moreMaximums).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a minimum greater than its maximum, then the bounds are rejected")
    void givenAMinimumGreaterThanItsMaximum_whenNormalizing_thenTheBoundsAreRejected() {
      // Arrange
      double[] invertedMin = {0.0, 3.0};
      double[] invertedMax = {1.0, 2.0};

      // Act
      Throwable thrown =
          catchThrowable(() -> NormalizeUtils.normalize(matrix, invertedMin, invertedMax));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given bounds that are not finite, then they are rejected")
    void givenBoundsThatAreNotFinite_whenNormalizing_thenTheyAreRejected() {
      // Arrange
      double[] infiniteMax = {1.0, Double.POSITIVE_INFINITY};

      // Act
      Throwable nanMinimum =
          catchThrowable(
              () -> NormalizeUtils.normalize(matrix, new double[] {Double.NaN, 0.0}, validMax));
      Throwable infiniteMaximum =
          catchThrowable(() -> NormalizeUtils.normalize(matrix, validMin, infiniteMax));

      // Assert
      assertThat(nanMinimum).isInstanceOf(InvalidConditionException.class);
      assertThat(infiniteMaximum).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given values out of the bounds, then they are mapped out of [0, 1]")
    void givenValuesOutOfTheBounds_whenNormalizing_thenTheyAreMappedOutOfZeroAndOne() {
      // Arrange
      double[][] values = {{-1.0, 3.0}, {0.0, 0.0}};

      // Act
      double[][] result =
          NormalizeUtils.normalize(values, new double[] {0.0, 0.0}, new double[] {2.0, 2.0});

      // Assert
      assertThat(result).isDeepEqualTo(new double[][] {{-0.5, 1.5}, {0.0, 0.0}});
    }

    @Test
    @DisplayName("given equal bounds in a column, then its values are mapped to 0.5")
    void givenEqualBounds_whenNormalizing_thenTheValuesAreMappedToHalf() {
      // Act
      double[][] result =
          NormalizeUtils.normalize(
              new double[][] {{1.0, 5.0}}, new double[] {0.0, 5.0}, new double[] {2.0, 5.0});

      // Assert
      assertThat(result).isDeepEqualTo(new double[][] {{0.5, 0.5}});
    }

    @Test
    @DisplayName("given the bounds of a front, then its own extremes are mapped to 0 and 1")
    void givenTheBoundsOfAFront_whenNormalizing_thenItsExtremesAreMappedToZeroAndOne() {
      // Arrange
      double[][] front = {{-6295.7, 12.0}, {-6060.4, 30.0}, {-6178.05, 21.0}};

      // Act
      double[][] result =
          NormalizeUtils.normalize(
              front,
              NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(front),
              NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(front));

      // Assert
      assertThat(result[0]).containsExactly(new double[] {0.0, 0.0}, offset(EPSILON));
      assertThat(result[1]).containsExactly(new double[] {1.0, 1.0}, offset(EPSILON));
      assertThat(result[2]).containsExactly(new double[] {0.5, 0.5}, offset(EPSILON));
    }

    @Test
    @DisplayName("given bounds, then the arrays are not modified")
    void givenBounds_whenNormalizing_thenTheArraysAreNotModified() {
      // Arrange
      double[] min = {0.0, 0.0};
      double[] max = {4.0, 4.0};

      // Act
      NormalizeUtils.normalize(matrix, min, max);

      // Assert
      assertThat(min).containsExactly(0.0, 0.0);
      assertThat(max).containsExactly(4.0, 4.0);
      assertThat(matrix).isDeepEqualTo(new double[][] {{1.0, 2.0}});
    }
  }
}
