package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class CMAESUtils")
class CMAESUtilsTest {

  @Nested
  @DisplayName("When computing eigenvalues with tred2 and tql2")
  class WhenComputingEigenvalues {

    @Test
    @DisplayName("given a symmetric matrix, when decomposing, then the eigenvalues are sorted")
    void givenASymmetricMatrix_whenDecomposing_thenTheEigenvaluesAreSorted() {
      // Arrange
      double[][] matrix = {{2.0, 1.0}, {1.0, 2.0}};
      double[] eigenvalues = new double[2];
      double[] offDiagonal = new double[2];

      // Act
      CMAESUtils.tred2(2, matrix, eigenvalues, offDiagonal);
      CMAESUtils.tql2(2, eigenvalues, offDiagonal, matrix);

      // Assert
      assertArrayEquals(new double[] {1.0, 3.0}, eigenvalues, 1e-12);
    }

    @Test
    @DisplayName("given a matrix with NaN, when decomposing, then an exception is thrown")
    void givenAMatrixWithNaN_whenDecomposing_thenAnInvalidConditionExceptionIsThrown() {
      // Arrange
      double[][] matrix = {{Double.NaN, 1.0}, {1.0, 2.0}};
      double[] eigenvalues = new double[2];
      double[] offDiagonal = new double[2];
      CMAESUtils.tred2(2, matrix, eigenvalues, offDiagonal);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> CMAESUtils.tql2(2, eigenvalues, offDiagonal, matrix));
    }
  }
}
