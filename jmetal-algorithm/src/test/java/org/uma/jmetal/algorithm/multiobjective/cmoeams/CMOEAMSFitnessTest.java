package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for class CMOEAMSFitness")
class CMOEAMSFitnessTest {
  private static final double EPSILON = 1e-12;

  @Nested
  @DisplayName("When computing the constraint violation")
  class WhenComputingTheConstraintViolation {
    @Test
    @DisplayName("given violated constraints, then each one is normalized by its maximum and they"
        + " are averaged")
    void givenViolatedConstraints_whenComputing_thenEachOneIsNormalizedAndTheyAreAveraged() {
      // Arrange: jMetal constraints are satisfied when they are not negative
      double[][] constraints = {{-2.0, -4.0}, {-1.0, 1.0}, {3.0, -1.0}};

      // Act
      double[] violation = CMOEAMSFitness.constraintViolation(constraints);

      // Assert: the violations are [2, 4], [1, 0] and [0, 1], whose maxima are 2 and 4
      assertArrayEquals(new double[] {1.0, 0.25, 0.125}, violation, EPSILON);
    }

    @Test
    @DisplayName("given a constraint that no solution violates, then it contributes zero")
    void givenAConstraintThatNoSolutionViolates_whenComputing_thenItContributesZero() {
      // Arrange
      double[][] constraints = {{-2.0, 0.0}, {-1.0, 1.0}, {3.0, 0.5}};

      // Act
      double[] violation = CMOEAMSFitness.constraintViolation(constraints);

      // Assert: the second constraint (0 / 0 in PlatEMO) counts as zero in the mean of two
      assertArrayEquals(new double[] {0.5, 0.25, 0.0}, violation, EPSILON);
    }

    @Test
    @DisplayName("given feasible solutions only, then their violation is zero")
    void givenFeasibleSolutionsOnly_whenComputing_thenTheirViolationIsZero() {
      // Arrange
      double[][] constraints = {{0.0, 2.0}, {1.0, 0.0}};

      // Act
      double[] violation = CMOEAMSFitness.constraintViolation(constraints);

      // Assert
      assertArrayEquals(new double[] {0.0, 0.0}, violation, EPSILON);
    }

    @Test
    @DisplayName("given a problem without constraints, then the violation is zero")
    void givenAProblemWithoutConstraints_whenComputing_thenTheViolationIsZero() {
      // Arrange
      double[][] constraints = {{}, {}, {}};

      // Act
      double[] violation = CMOEAMSFitness.constraintViolation(constraints);

      // Assert
      assertArrayEquals(new double[] {0.0, 0.0, 0.0}, violation, EPSILON);
    }

    @Test
    @DisplayName("given a NaN constraint value, then it counts as no violation, as max(NaN, 0) of"
        + " MATLAB")
    void givenANaNConstraintValue_whenComputing_thenItCountsAsNoViolation() {
      // Arrange
      double[][] constraints = {{Double.NaN}, {-3.0}};

      // Act
      double[] violation = CMOEAMSFitness.constraintViolation(constraints);

      // Assert
      assertArrayEquals(new double[] {0.0, 1.0}, violation, EPSILON);
    }
  }

  @Nested
  @DisplayName("When computing the shift-based density estimation")
  class WhenComputingTheShiftBasedDensityEstimation {
    // Normalized, they are (0, 1), (1, 0), (0.5, 0.5) and (1, 1)
    private final double[][] objectives = {{0.0, 4.0}, {2.0, 0.0}, {1.0, 2.0}, {2.0, 4.0}};

    @Test
    @DisplayName("given four solutions, then the third smallest shifted distance is used")
    void givenFourSolutions_whenComputing_thenTheThirdSmallestShiftedDistanceIsUsed() {
      // Act
      double[] density = CMOEAMSFitness.shiftBasedDensity(objectives);

      // Assert: the sorted shifted distances, with the solution itself, are [0, 0.5, 1, 1] for the
      // first two solutions, [0, 0.5, 0.5, 0.71] for the third and [0, 0, 0, 0] for the dominated
      // fourth, and floor(sqrt(4)) + 1 = 3
      assertArrayEquals(
          new double[] {1.0 / 3.0, 1.0 / 3.0, 1.0 / 2.5, 1.0 / 2.0}, density, EPSILON);
    }

    @Test
    @DisplayName("given translated and scaled objectives, then the density does not change")
    void givenTranslatedAndScaledObjectives_whenComputing_thenTheDensityDoesNotChange() {
      // Arrange
      double[][] transformed =
          Arrays.stream(objectives)
              .map(row -> new double[] {10.0 * row[0] - 3.0, 0.5 * row[1] + 7.0})
              .toArray(double[][]::new);

      // Act
      double[] density = CMOEAMSFitness.shiftBasedDensity(transformed);

      // Assert
      assertArrayEquals(CMOEAMSFitness.shiftBasedDensity(objectives), density, EPSILON);
    }

    @Test
    @DisplayName("given three solutions, then the second smallest shifted distance is used")
    void givenThreeSolutions_whenComputing_thenTheSecondSmallestShiftedDistanceIsUsed() {
      // Arrange
      double[][] three = {{0.0, 1.0}, {1.0, 0.0}, {0.25, 0.25}};

      // Act
      double[] density = CMOEAMSFitness.shiftBasedDensity(three);

      // Assert: the shifted distances of the first solution are 0, 1 and 0.25 (the third shifted to
      // it is (0.25, 1)); those of the second are 0, 1 and 0.25; those of the third are 0, 0.75
      // and 0.75, so it is the least crowded
      assertArrayEquals(
          new double[] {1.0 / 2.25, 1.0 / 2.25, 1.0 / 2.75}, density, EPSILON);
    }

    @Test
    @DisplayName("given an objective with the same value in all the solutions, then the density is"
        + " NaN, as in PlatEMO")
    void givenAConstantObjective_whenComputing_thenTheDensityIsNaN() {
      // Arrange
      double[][] constant = {{1.0, 0.0}, {1.0, 1.0}, {1.0, 2.0}};

      // Act
      double[] density = CMOEAMSFitness.shiftBasedDensity(constant);

      // Assert
      assertTrue(Arrays.stream(density).allMatch(Double::isNaN));
    }
  }

  @Nested
  @DisplayName("When computing the fitness")
  class WhenComputingTheFitness {
    private final double[][] objectives = {{0.0, 4.0}, {2.0, 0.0}, {1.0, 2.0}, {2.0, 4.0}};
    private final double[] violation = {0.0, 0.0, 0.0, 0.5};

    @Test
    @DisplayName("given stage B, then a lower violation dominates and the feasible solutions are"
        + " compared by Pareto dominance")
    void givenStageB_whenComputing_thenTheConstrainedDominanceIsUsed() {
      // Act
      double[] fitness = CMOEAMSFitness.stageBFitness(objectives, violation);

      // Assert: the three feasible solutions are non-dominated and each one dominates the fourth,
      // so R = [0, 0, 0, 3]; D uses the second smallest cosine distance
      double first = 1.0 / (3.0 - 2.0 / Math.sqrt(5.0));
      double second = 1.0 / (3.0 - 1.0 / Math.sqrt(5.0));
      assertArrayEquals(
          new double[] {first, second, first, 3.0 + first}, fitness, EPSILON);
    }

    @Test
    @DisplayName("given stage A, then the fitness is that of [SDE, CV] with Pareto dominance")
    void givenStageA_whenComputing_thenTheFitnessIsThatOfSDEAndCV() {
      // Act
      double[] fitness = CMOEAMSFitness.stageAFitness(objectives, violation);

      // Assert: the points [SDE, CV] are (1/3, 0), (1/3, 0), (0.4, 0) and (0.5, 0.5); the first
      // two dominate the third and the first three dominate the fourth, so R = [0, 0, 4, 5]
      double fourth = 1.0 / (3.0 - Math.sqrt(0.5));
      assertArrayEquals(new double[] {0.5, 0.5, 4.5, 5.0 + fourth}, fitness, EPSILON);
    }

    @Test
    @DisplayName("given the same points in both stages, then stage A computes the fitness of the"
        + " points [SDE, CV] with no violation")
    void givenTheSamePoints_whenComputingStageA_thenItIsTheFitnessOfSDEAndCV() {
      // Arrange
      double[] density = CMOEAMSFitness.shiftBasedDensity(objectives);
      double[][] points = new double[objectives.length][];
      for (int i = 0; i < objectives.length; i++) {
        points[i] = new double[] {density[i], violation[i]};
      }

      // Act
      double[] fitness = CMOEAMSFitness.stageAFitness(objectives, violation);

      // Assert
      assertArrayEquals(
          CMOEAMSFitness.fitness(points, new double[objectives.length]), fitness, EPSILON);
    }

    @Test
    @DisplayName("given a lower violation and worse objectives, then the solution dominates")
    void givenALowerViolationAndWorseObjectives_whenComputing_thenTheSolutionDominates() {
      // Arrange
      double[][] points = {{5.0, 5.0}, {1.0, 1.0}};
      double[] cv = {0.1, 0.2};

      // Act
      double[] fitness = CMOEAMSFitness.fitness(points, cv);

      // Assert: the first dominates the second (R = 1); with N = 2 the density uses the nearest
      // solution, at cosine distance 0
      assertArrayEquals(new double[] {0.5, 1.5}, fitness, EPSILON);
    }

    @Test
    @DisplayName("given non-dominated solutions, then their fitness is lower than 1")
    void givenNonDominatedSolutions_whenComputing_thenTheirFitnessIsLowerThanOne() {
      // Arrange
      double[][] points = {{1.0, 0.0}, {0.0, 1.0}, {0.6, 0.6}, {2.0, 2.0}};

      // Act
      double[] fitness = CMOEAMSFitness.fitness(points, new double[points.length]);

      // Assert: the fourth is dominated by the three others, each of which dominates only it
      assertTrue(fitness[0] < 1.0 && fitness[1] < 1.0 && fitness[2] < 1.0);
      assertEquals(3.0 + 1.0 / (3.0 - Math.sqrt(0.5)), fitness[3], EPSILON);
    }

    @Test
    @DisplayName("given a single solution, then its density term is zero")
    void givenASingleSolution_whenComputing_thenItsDensityTermIsZero() {
      // Act
      double[] fitness = CMOEAMSFitness.fitness(new double[][] {{1.0, 2.0}}, new double[1]);

      // Assert: the only distance is the infinite one of the solution to itself
      assertArrayEquals(new double[] {0.0}, fitness, EPSILON);
    }
  }

  @Nested
  @DisplayName("When computing the cosine distances")
  class WhenComputingTheCosineDistances {
    @Test
    @DisplayName("given vectors, then the distance is one minus the cosine of their angle")
    void givenVectors_whenComputing_thenTheDistanceIsOneMinusTheCosine() {
      // Arrange
      double[][] points = {{1.0, 0.0}, {0.0, 3.0}, {2.0, 2.0}, {-1.0, 0.0}};

      // Act
      double[][] distance = CMOEAMSFitness.cosineDistances(points);

      // Assert
      assertEquals(0.0, distance[0][0], EPSILON);
      assertEquals(1.0, distance[0][1], EPSILON);
      assertEquals(1.0 - Math.sqrt(0.5), distance[0][2], EPSILON);
      assertEquals(2.0, distance[0][3], EPSILON);
      assertEquals(distance[2][1], distance[1][2], EPSILON);
    }

    @Test
    @DisplayName("given a zero vector, then its distances are NaN, as in MATLAB's pdist2")
    void givenAZeroVector_whenComputing_thenItsDistancesAreNaN() {
      // Arrange
      double[][] points = {{0.0, 0.0}, {1.0, 1.0}};

      // Act
      double[][] distance = CMOEAMSFitness.cosineDistances(points);

      // Assert
      assertTrue(Double.isNaN(distance[0][0]));
      assertTrue(Double.isNaN(distance[0][1]));
      assertTrue(Double.isNaN(distance[1][0]));
      assertEquals(0.0, distance[1][1], EPSILON);
    }
  }

  @Nested
  @DisplayName("When sorting values as MATLAB")
  class WhenSortingValuesAsMatlab {
    @Test
    @DisplayName("given ties and NaN values, then the sort is stable and NaN goes last")
    void givenTiesAndNaNValues_whenSorting_thenTheSortIsStableAndNaNGoesLast() {
      // Arrange
      double[] values = {Double.NaN, 2.0, 1.0, 2.0, Double.POSITIVE_INFINITY, 1.0};

      // Act
      int[] indices = CMOEAMSFitness.sortedIndices(values);

      // Assert
      assertArrayEquals(new int[] {2, 5, 1, 3, 4, 0}, indices);
    }
  }
}
