package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for class NonDominatedSorting")
class NonDominatedSortingTest {

  @Nested
  @DisplayName("When checking the dominance")
  class WhenCheckingTheDominance {

    @Test
    @DisplayName("given a vector better in one component, when checking, then it dominates")
    void givenAVectorBetterInOneComponent_whenChecking_thenItDominates() {
      // Act & Assert
      assertTrue(NonDominatedSorting.dominates(new double[] {1, 2}, new double[] {1, 3}));
      assertFalse(NonDominatedSorting.dominates(new double[] {1, 3}, new double[] {1, 2}));
    }

    @Test
    @DisplayName("given equal or incomparable vectors, when checking, then none dominates")
    void givenEqualOrIncomparableVectors_whenChecking_thenNoneDominates() {
      // Act & Assert
      assertFalse(NonDominatedSorting.dominates(new double[] {1, 2}, new double[] {1, 2}));
      assertFalse(NonDominatedSorting.dominates(new double[] {1, 2}, new double[] {2, 1}));
      assertFalse(NonDominatedSorting.dominates(new double[] {2, 1}, new double[] {1, 2}));
    }
  }

  @Nested
  @DisplayName("When sorting")
  class WhenSorting {

    @Test
    @DisplayName("given several fronts, when sorting, then each vector gets its front number")
    void givenSeveralFronts_whenSorting_thenEachVectorGetsItsFrontNumber() {
      // Arrange
      double[][] points = {{3, 3}, {1, 2}, {2, 1}, {2, 2}, {4, 4}, {0, 5}};

      // Act
      int[] fronts = NonDominatedSorting.frontNumbers(points);

      // Assert
      assertArrayEquals(new int[] {3, 1, 1, 2, 4, 1}, fronts);
    }

    @Test
    @DisplayName("given equal vectors, when sorting, then they are in the same front")
    void givenEqualVectors_whenSorting_thenTheyAreInTheSameFront() {
      // Arrange
      double[][] points = {{1, 1}, {1, 1}, {2, 2}};

      // Act
      int[] fronts = NonDominatedSorting.frontNumbers(points);

      // Assert
      assertArrayEquals(new int[] {1, 1, 2}, fronts);
    }

    @Test
    @DisplayName("given no vectors, when sorting, then the result is empty")
    void givenNoVectors_whenSorting_thenTheResultIsEmpty() {
      // Act
      int[] fronts = NonDominatedSorting.frontNumbers(new double[0][]);

      // Assert
      assertEquals(0, fronts.length);
    }
  }
}
