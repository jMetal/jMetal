package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for class Subregions")
class SubregionsTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] WEIGHTS = {{1e-6, 1.0}, {0.5, 0.5}, {1.0, 1e-6}};

  @Nested
  @DisplayName("When associating points with the weight vectors")
  class WhenAssociating {

    @Test
    @DisplayName("given points, when associating, then the vector with the smallest angle wins")
    void givenPoints_whenAssociating_thenTheVectorWithTheSmallestAngleWins() {
      // Arrange: the scale of a point does not change its angle
      double[][] points = {{0.0, 3.0}, {0.1, 0.9}, {2.0, 2.0}, {0.9, 0.1}, {5.0, 0.0}};

      // Act
      int[] regions = Subregions.associate(points, WEIGHTS);

      // Assert
      assertArrayEquals(new int[] {0, 0, 1, 2, 2}, regions);
    }

    @Test
    @DisplayName("given a point at the same angle of two vectors, when associating, then the first")
    void givenAPointAtTheSameAngleOfTwoVectors_whenAssociating_thenTheFirstOneWins() {
      // Arrange
      double[][] weights = {{1.0, 0.0}, {0.0, 1.0}};

      // Act
      int region = Subregions.associate(new double[] {1.0, 1.0}, weights);

      // Assert
      assertEquals(0, region);
    }

    @Test
    @DisplayName("given the origin, when associating, then the first vector is chosen")
    void givenTheOrigin_whenAssociating_thenTheFirstVectorIsChosen() {
      // Act
      int region = Subregions.associate(new double[] {0.0, 0.0}, WEIGHTS);

      // Assert
      assertEquals(0, region);
    }
  }

  @Nested
  @DisplayName("When computing the Tchebycheff function")
  class WhenComputingTheTchebycheffFunction {

    @Test
    @DisplayName("given a point, when computing, then the largest weighted distance is returned")
    void givenAPoint_whenComputing_thenTheLargestWeightedDistanceIsReturned() {
      // Act
      double value =
          Subregions.tchebycheff(
              new double[] {3.0, 2.0}, new double[] {1.0, 1.0}, new double[] {0.5, 0.25});

      // Assert: max(2 / 0.5, 1 / 0.25)
      assertEquals(4.0, value, EPSILON);
    }

    @Test
    @DisplayName("given a set, when computing in own subregions, then the ideal of the set is used")
    void givenASet_whenComputingInOwnSubregions_thenTheIdealOfTheSetIsUsed() {
      // Arrange: the ideal point is (1, 0.5); the first point is in the subregion of (0.5, 0.5)
      // and the second one in the subregion of (1, 1e-6)
      double[][] points = {{1.0, 1.0}, {1.5, 0.5}};

      // Act
      double[] values = Subregions.tchebycheffInOwnSubregion(points, WEIGHTS);

      // Assert: max(0 / 0.5, 0.5 / 0.5) and max(0.5 / 1, 0 / 1e-6)
      assertArrayEquals(new double[] {1.0, 0.5}, values, EPSILON);
    }

    @Test
    @DisplayName("given a set, when computing the ideal and nadir points, then they are the bounds")
    void givenASet_whenComputingTheIdealAndNadirPoints_thenTheyAreTheBounds() {
      // Arrange
      double[][] points = {{1.0, 4.0}, {3.0, 2.0}, {2.0, 5.0}};

      // Act & Assert
      assertArrayEquals(new double[] {1.0, 2.0}, Subregions.idealPoint(points), EPSILON);
      assertArrayEquals(new double[] {3.0, 5.0}, Subregions.nadirPoint(points), EPSILON);
    }
  }

  @Nested
  @DisplayName("When looking for the extreme values")
  class WhenLookingForTheExtremeValues {

    @Test
    @DisplayName("given ties, when looking for the maximum and minimum, then the first index wins")
    void givenTies_whenLookingForTheMaximumAndMinimum_thenTheFirstIndexWins() {
      // Arrange
      double[] values = {1.0, 3.0, 0.0, 3.0, 0.0};

      // Act & Assert
      assertEquals(1, Subregions.indexOfMaximum(values));
      assertEquals(2, Subregions.indexOfMinimum(values));
    }
  }
}
