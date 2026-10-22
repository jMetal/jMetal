package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Unit tests for class KNearestDistanceCalculator")
class KNearestDistanceCalculatorTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] LINE = {{0.0, 0.0}, {3.0, 4.0}, {6.0, 8.0}};

  @Nested
  @DisplayName("When computing the distances without normalizing")
  class WhenComputingWithoutNormalizing {
    @Test
    @DisplayName("given k = 1, then each row gets the distance to its nearest neighbour")
    void givenKOne_whenComputing_thenEachRowGetsTheDistanceToItsNearestNeighbour() {
      // Act
      double[] distances = new KNearestDistanceCalculator(1, false).compute(LINE);

      // Assert
      assertThat(distances).containsExactly(5.0, 5.0, 5.0);
    }

    @Test
    @DisplayName("given k = 2, then each row gets the distance to its second nearest neighbour")
    void givenKTwo_whenComputing_thenEachRowGetsTheDistanceToItsSecondNearestNeighbour() {
      // Act
      double[] distances = new KNearestDistanceCalculator(2, false).compute(LINE);

      // Assert
      assertThat(distances).containsExactly(10.0, 5.0, 10.0);
    }

    @Test
    @DisplayName("given duplicated rows, then their distance is zero")
    void givenDuplicatedRows_whenComputing_thenTheirDistanceIsZero() {
      // Arrange
      double[][] matrix = {{1.0, 1.0}, {1.0, 1.0}, {2.0, 2.0}};

      // Act
      double[] distances = new KNearestDistanceCalculator(1, false).compute(matrix);

      // Assert
      assertThat(distances).containsExactly(0.0, 0.0, Math.sqrt(2.0));
    }

    @Test
    @DisplayName("given a single column, then the distances are those of a line")
    void givenASingleColumn_whenComputing_thenTheDistancesAreThoseOfALine() {
      // Act
      double[] distances =
          new KNearestDistanceCalculator(1, false).compute(new double[][] {{-4.0}, {1.0}, {2.0}});

      // Assert
      assertThat(distances).containsExactly(5.0, 1.0, 1.0);
    }

    @Test
    @DisplayName("given a matrix, then it is not modified")
    void givenAMatrix_whenComputing_thenItIsNotModified() {
      // Arrange
      double[][] matrix = {{3.0, 4.0}, {1.0, 2.0}, {0.0, 0.0}};

      // Act
      new KNearestDistanceCalculator(1, false).compute(matrix);

      // Assert
      assertThat(matrix).isDeepEqualTo(new double[][] {{3.0, 4.0}, {1.0, 2.0}, {0.0, 0.0}});
    }
  }

  @Nested
  @DisplayName("When computing the distances normalizing the columns")
  class WhenComputingNormalizing {
    @Test
    @DisplayName("given columns of other scales, then the distances are those of the unit scale")
    void givenColumnsOfOtherScales_whenComputing_thenTheDistancesAreThoseOfTheUnitScale() {
      // Arrange: the columns are mapped to {0, 0.5, 1}
      double[][] scaled = {{0.0, 0.0}, {3.0, 40.0}, {6.0, 80.0}};

      // Act
      double[] distances = new KNearestDistanceCalculator(1, true).compute(scaled);

      // Assert
      assertThat(distances)
          .containsExactly(
              new double[] {Math.sqrt(0.5), Math.sqrt(0.5), Math.sqrt(0.5)}, offset(EPSILON));
    }

    @Test
    @DisplayName("given a constant column, then it does not change the distances")
    void givenAConstantColumn_whenComputing_thenItDoesNotChangeTheDistances() {
      // Arrange
      double[][] matrix = {{1.0, 5.0}, {2.0, 5.0}, {4.0, 5.0}};

      // Act
      double[] distances = new KNearestDistanceCalculator(1, true).compute(matrix);

      // Assert: the first column is mapped to {0, 1/3, 1}
      assertThat(distances)
          .containsExactly(new double[] {1.0 / 3.0, 1.0 / 3.0, 2.0 / 3.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given negative values, then the columns are normalized with their range")
    void givenNegativeValues_whenComputing_thenTheColumnsAreNormalizedWithTheirRange() {
      // Arrange: the same distances as the shifted matrix with positive values
      double[][] negative = {{-10.0}, {-6.0}, {-2.0}};
      double[][] positive = {{0.0}, {4.0}, {8.0}};

      // Act
      double[] fromNegative = new KNearestDistanceCalculator(1, true).compute(negative);
      double[] fromPositive = new KNearestDistanceCalculator(1, true).compute(positive);

      // Assert
      assertThat(fromNegative).containsExactly(fromPositive, offset(EPSILON));
    }

    @Test
    @DisplayName("given the normalization, then it does not modify the matrix")
    void givenTheNormalization_whenComputing_thenTheMatrixIsNotModified() {
      // Arrange
      double[][] matrix = {{3.0, 40.0}, {1.0, 20.0}, {0.0, 0.0}};

      // Act
      new KNearestDistanceCalculator(1, true).compute(matrix);

      // Assert
      assertThat(matrix).isDeepEqualTo(new double[][] {{3.0, 40.0}, {1.0, 20.0}, {0.0, 0.0}});
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @ParameterizedTest(name = "k = {0}")
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    @DisplayName("given a k that is not positive, then it is rejected")
    void givenAKThatIsNotPositive_whenConstructing_thenItIsRejected(int k) {
      // Act
      Throwable thrown = catchThrowable(() -> new KNearestDistanceCalculator(k, false));

      // Assert
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("given a null or empty matrix, then it is rejected")
    void givenANullOrEmptyMatrix_whenComputing_thenItIsRejected() {
      // Arrange
      var calculator = new KNearestDistanceCalculator(1, false);

      // Act
      Throwable nullMatrix = catchThrowable(() -> calculator.compute(null));
      Throwable emptyMatrix = catchThrowable(() -> calculator.compute(new double[0][]));

      // Assert
      assertThat(nullMatrix).isInstanceOf(IllegalArgumentException.class);
      assertThat(emptyMatrix).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("given rows of different lengths or a null row, then it is rejected")
    void givenAnIrregularMatrix_whenComputing_thenItIsRejected() {
      // Arrange
      var calculator = new KNearestDistanceCalculator(1, false);

      // Act
      Throwable raggedMatrix =
          catchThrowable(() -> calculator.compute(new double[][] {{1.0, 2.0}, {3.0}}));
      Throwable nullRow = catchThrowable(() -> calculator.compute(new double[][] {{1.0}, null}));

      // Assert
      assertThat(raggedMatrix).isInstanceOf(IllegalArgumentException.class);
      assertThat(nullRow).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{0} rows")
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("given k not lower than the number of rows, then it is rejected")
    void givenAKNotLowerThanTheRows_whenComputing_thenItIsRejected(int rows) {
      // Arrange
      double[][] matrix = new double[rows][1];
      var calculator = new KNearestDistanceCalculator(rows, false);

      // Act
      Throwable thrown = catchThrowable(() -> calculator.compute(matrix));

      // Assert
      assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }
  }
}
