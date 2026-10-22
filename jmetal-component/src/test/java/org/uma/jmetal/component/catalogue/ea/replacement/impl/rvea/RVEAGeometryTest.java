package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class RVEAGeometry")
class RVEAGeometryTest {
  private static final double EPSILON = 1e-12;

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  private static List<DoubleSolution> population(double[]... points) {
    List<DoubleSolution> population = new ArrayList<>();
    for (double[] point : points) {
      population.add(solution(point));
    }
    return population;
  }

  @Nested
  @DisplayName("When computing the norm and the unit vector")
  class WhenComputingTheNormAndTheUnitVector {
    @Test
    @DisplayName("given a vector, then the norm is its Euclidean length")
    void givenAVector_whenComputingTheNorm_thenItIsItsEuclideanLength() {
      // Act & Assert
      assertThat(RVEAGeometry.norm(new double[] {3.0, 4.0})).isEqualTo(5.0, offset(EPSILON));
      assertThat(RVEAGeometry.norm(new double[] {0.0, 0.0, 0.0})).isZero();
      assertThat(RVEAGeometry.norm(new double[] {-2.0, 0.0, 0.0})).isEqualTo(2.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given very large values, then the norm does not overflow")
    void givenVeryLargeValues_whenComputingTheNorm_thenItDoesNotOverflow() {
      // Act
      double norm = RVEAGeometry.norm(new double[] {1e200, 1e200});

      // Assert
      assertThat(Double.isFinite(norm)).isTrue();
      assertThat(norm / 1e200).isEqualTo(Math.sqrt(2.0), offset(1e-9));
    }

    @Test
    @DisplayName("given a vector, then the unit vector has the same direction and length one")
    void givenAVector_whenMakingItUnit_thenItHasLengthOne() {
      // Act
      double[] unit = RVEAGeometry.unit(new double[] {3.0, 4.0});

      // Assert
      assertThat(unit).containsExactly(new double[] {0.6, 0.8}, offset(EPSILON));
    }

    @ParameterizedTest(name = "magnitude {0}")
    @ValueSource(doubles = {1e-300, 1e-100, 1.0, 1e100, 1e300})
    @DisplayName("given vectors of any magnitude, then the unit vector is the same")
    void givenAnyMagnitude_whenMakingItUnit_thenTheUnitVectorIsTheSame(double magnitude) {
      // Act
      double[] unit = RVEAGeometry.unit(new double[] {magnitude, magnitude});

      // Assert
      assertThat(unit).containsExactly(new double[] {Math.sqrt(0.5), Math.sqrt(0.5)}, offset(1e-9));
    }

    @Test
    @DisplayName("given a zero or non-finite vector, then there is no direction")
    void givenAZeroOrNonFiniteVector_whenMakingItUnit_thenItIsRejected() {
      // Act
      Throwable zero = catchThrowable(() -> RVEAGeometry.unit(new double[] {0.0, 0.0}));
      Throwable nan = catchThrowable(() -> RVEAGeometry.unit(new double[] {Double.NaN, 1.0}));
      Throwable infinite =
          catchThrowable(() -> RVEAGeometry.unit(new double[] {Double.POSITIVE_INFINITY, 1.0}));

      // Assert
      assertThat(zero).isInstanceOf(InvalidConditionException.class);
      assertThat(nan).isInstanceOf(InvalidConditionException.class);
      assertThat(infinite).isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When computing the angle between two vectors")
  class WhenComputingTheAngle {
    @Test
    @DisplayName("given orthogonal, parallel and opposite vectors, then the angle is known")
    void givenKnownVectors_whenComputingTheAngle_thenItIsKnown() {
      // Act & Assert
      assertThat(RVEAGeometry.angle(new double[] {1, 0}, new double[] {0, 1}))
          .isEqualTo(Math.PI / 2.0, offset(EPSILON));
      // The arc cosine amplifies the rounding of the cosine near one: parallel vectors give 1e-8
      assertThat(RVEAGeometry.angle(new double[] {1, 1}, new double[] {5, 5}))
          .isEqualTo(0.0, offset(1e-7));
      assertThat(RVEAGeometry.angle(new double[] {1, 0}, new double[] {-3, 0}))
          .isEqualTo(Math.PI, offset(EPSILON));
      assertThat(RVEAGeometry.angle(new double[] {1, 0}, new double[] {1, 1}))
          .isEqualTo(Math.PI / 4.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a vector and a multiple of it, then rounding does not give a NaN")
    void givenAMultipleOfAVector_whenComputingTheAngle_thenItIsNotNaN() {
      // Act
      double angle =
          RVEAGeometry.angle(new double[] {0.1, 0.2, 0.3}, new double[] {0.3, 0.6, 0.9});

      // Assert: the cosine can be a little greater than one
      assertThat(angle).isBetween(0.0, 1e-7);
    }

    @Test
    @DisplayName("given a zero vector, then the angle is zero")
    void givenAZeroVector_whenComputingTheAngle_thenItIsZero() {
      // Act & Assert
      assertThat(RVEAGeometry.angle(new double[] {0, 0}, new double[] {1, 2})).isZero();
      assertThat(RVEAGeometry.angle(new double[] {1, 2}, new double[] {0, 0})).isZero();
    }

    @Test
    @DisplayName("given vectors scaled, then the angle does not change")
    void givenScaledVectors_whenComputingTheAngle_thenItDoesNotChange() {
      // Act
      double original = RVEAGeometry.angle(new double[] {1, 2, 3}, new double[] {3, 1, 2});
      double scaled =
          RVEAGeometry.angle(new double[] {100, 200, 300}, new double[] {0.3, 0.1, 0.2});

      // Assert
      assertThat(scaled).isEqualTo(original, offset(1e-9));
    }
  }

  @Nested
  @DisplayName("When copying and validating the reference directions")
  class WhenValidatingTheDirections {
    @Test
    @DisplayName("given vectors, then the copy does not share the arrays")
    void givenVectors_whenCopying_thenTheCopyDoesNotShareTheArrays() {
      // Arrange
      double[][] vectors = {{1.0, 2.0}, {3.0, 4.0}};

      // Act
      double[][] copy = RVEAGeometry.copy(vectors);
      copy[0][0] = 99.0;

      // Assert
      assertThat(vectors[0][0]).isEqualTo(1.0);
      assertThat(copy[1]).containsExactly(3.0, 4.0);
    }

    @Test
    @DisplayName("given valid vectors, then the directions are unit vectors")
    void givenValidVectors_whenMakingTheDirections_thenTheyAreUnitVectors() {
      // Act
      double[][] directions =
          RVEAGeometry.directions(List.of(new double[] {3, 4}, new double[] {0, 2}), 2);

      // Assert
      assertThat(directions[0]).containsExactly(new double[] {0.6, 0.8}, offset(EPSILON));
      assertThat(directions[1]).containsExactly(new double[] {0.0, 1.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given invalid vectors, then they are rejected")
    void givenInvalidVectors_whenMakingTheDirections_thenTheyAreRejected() {
      // Act
      Throwable nullVectors = catchThrowable(() -> RVEAGeometry.directions(null, 2));
      Throwable noVectors = catchThrowable(() -> RVEAGeometry.directions(List.of(), 2));
      Throwable otherDimension =
          catchThrowable(() -> RVEAGeometry.directions(List.of(new double[] {1, 2, 3}), 2));
      Throwable negative =
          catchThrowable(() -> RVEAGeometry.directions(List.of(new double[] {1, -2}), 2));
      Throwable notFinite =
          catchThrowable(() -> RVEAGeometry.directions(List.of(new double[] {Double.NaN, 1}), 2));
      Throwable zero =
          catchThrowable(() -> RVEAGeometry.directions(List.of(new double[] {0, 0}), 2));

      // Assert
      assertThat(nullVectors).isInstanceOf(NullParameterException.class);
      assertThat(noVectors).isInstanceOf(InvalidConditionException.class);
      assertThat(otherDimension).isInstanceOf(InvalidConditionException.class);
      assertThat(negative).isInstanceOf(InvalidConditionException.class);
      assertThat(notFinite).isInstanceOf(InvalidConditionException.class);
      assertThat(zero).isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When translating and normalizing the objectives")
  class WhenTranslatingAndNormalizing {
    @Test
    @DisplayName("given a population, then each objective is translated to start at zero")
    void givenAPopulation_whenTranslating_thenEachObjectiveStartsAtZero() {
      // Arrange
      var population = population(new double[] {3, -1}, new double[] {5, -4}, new double[] {4, 2});

      // Act
      double[][] translated = RVEAGeometry.translate(population);

      // Assert
      assertThat(translated[0]).containsExactly(0.0, 3.0);
      assertThat(translated[1]).containsExactly(2.0, 0.0);
      assertThat(translated[2]).containsExactly(1.0, 6.0);
    }

    @Test
    @DisplayName("given objectives that are all negative, then the translation starts at zero")
    void givenNegativeObjectives_whenTranslating_thenTheTranslationStartsAtZero() {
      // Arrange
      var population = population(new double[] {-10, -7}, new double[] {-6, -9});

      // Act
      double[][] translated = RVEAGeometry.translate(population);

      // Assert
      assertThat(translated[0]).containsExactly(0.0, 2.0);
      assertThat(translated[1]).containsExactly(4.0, 0.0);
    }

    @Test
    @DisplayName("given an objective with the same value, then its translation is zero")
    void givenAConstantObjective_whenTranslating_thenItsTranslationIsZero() {
      // Arrange
      var population = population(new double[] {5, 1}, new double[] {5, 2});

      // Act
      double[][] translated = RVEAGeometry.translate(population);

      // Assert
      assertThat(translated[0][0]).isZero();
      assertThat(translated[1][0]).isZero();
    }

    @Test
    @DisplayName("given an objective range that overflows, then it is rejected")
    void givenAnOverflowingRange_whenTranslating_thenItIsRejected() {
      // Arrange
      var population =
          population(new double[] {-Double.MAX_VALUE, 0}, new double[] {Double.MAX_VALUE, 0});

      // Act
      Throwable thrown = catchThrowable(() -> RVEAGeometry.translate(population));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given translated points, then the ranges are the maxima of each objective")
    void givenTranslatedPoints_whenComputingTheRanges_thenTheyAreTheMaxima() {
      // Act & Assert
      assertThat(RVEAGeometry.ranges(new double[][] {{0, 3}, {2, 0}, {1, 6}}))
          .containsExactly(2.0, 6.0);
      assertThat(RVEAGeometry.ranges(new double[][] {{0, 0}, {0, 0}})).containsExactly(0.0, 0.0);
    }

    @Test
    @DisplayName("given translated points, then they are normalized with their ranges")
    void givenTranslatedPoints_whenNormalizing_thenTheyAreNormalizedWithTheirRanges() {
      // Act
      double[][] normalized = RVEAGeometry.normalize(new double[][] {{0, 3}, {2, 0}, {1, 6}});

      // Assert
      assertThat(normalized[0]).containsExactly(new double[] {0.0, 0.5}, offset(EPSILON));
      assertThat(normalized[1]).containsExactly(new double[] {1.0, 0.0}, offset(EPSILON));
      assertThat(normalized[2]).containsExactly(new double[] {0.5, 1.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given an objective with a range of zero, then its normalized value is zero")
    void givenAZeroRange_whenNormalizing_thenTheNormalizedValueIsZero() {
      // Act
      double[][] normalized = RVEAGeometry.normalize(new double[][] {{0, 3}, {0, 6}});

      // Assert
      assertThat(normalized[0]).containsExactly(new double[] {0.0, 0.5}, offset(EPSILON));
      assertThat(normalized[1]).containsExactly(new double[] {0.0, 1.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given translated points, then the normalization does not modify them")
    void givenTranslatedPoints_whenNormalizing_thenTheyAreNotModified() {
      // Arrange
      double[][] translated = {{0, 3}, {2, 0}};

      // Act
      RVEAGeometry.normalize(translated);

      // Assert
      assertThat(translated).isDeepEqualTo(new double[][] {{0, 3}, {2, 0}});
    }
  }

  @Nested
  @DisplayName("When associating points with reference vectors")
  class WhenAssociatingPointsWithVectors {
    private final double[][] vectors = {{1, 0}, {1, 1}, {0, 1}};

    @Test
    @DisplayName("given points, then each is associated with the vector of the smallest angle")
    void givenPoints_whenLookingForTheNearest_thenTheVectorOfTheSmallestAngleIsFound() {
      // Act & Assert
      assertThat(RVEAGeometry.nearest(new double[] {5, 1}, vectors)).isEqualTo(0);
      assertThat(RVEAGeometry.nearest(new double[] {1, 1.2}, vectors)).isEqualTo(1);
      assertThat(RVEAGeometry.nearest(new double[] {0.1, 4}, vectors)).isEqualTo(2);
    }

    @Test
    @DisplayName("given a point at the same angle of two vectors, then the first one is chosen")
    void givenATie_whenLookingForTheNearest_thenTheFirstOneIsChosen() {
      // Arrange: the angle of (1, 1) to (1, 0) and to (0, 1) is the same
      double[][] twoVectors = {{1, 0}, {0, 1}};

      // Act & Assert
      assertThat(RVEAGeometry.nearest(new double[] {1, 1}, twoVectors)).isEqualTo(0);
    }

    @Test
    @DisplayName("given the origin, then the first vector is chosen")
    void givenTheOrigin_whenLookingForTheNearest_thenTheFirstVectorIsChosen() {
      // Act & Assert: the angle of the origin is zero for every vector
      assertThat(RVEAGeometry.nearest(new double[] {0, 0}, vectors)).isEqualTo(0);
    }

    @Test
    @DisplayName("given points, then only the vectors with a point are active")
    void givenPoints_whenLookingForTheActiveVectors_thenOnlyThoseWithAPointAreActive() {
      // Act
      boolean[] active =
          RVEAGeometry.active(new double[][] {{5, 1}, {6, 0.5}, {0.1, 4}}, vectors);

      // Assert
      assertThat(active).containsExactly(true, false, true);
    }
  }

  @Nested
  @DisplayName("When computing the smallest angle between reference vectors")
  class WhenComputingTheSmallestAngles {
    @Test
    @DisplayName("given two orthogonal vectors, then the smallest angle of each is a right angle")
    void givenTwoOrthogonalVectors_whenComputing_thenTheAngleIsARightAngle() {
      // Act
      double[] gamma = RVEAGeometry.gamma(new double[][] {{1, 0}, {0, 1}});

      // Assert
      assertThat(gamma).containsExactly(new double[] {Math.PI / 2, Math.PI / 2}, offset(EPSILON));
    }

    @Test
    @DisplayName("given several vectors, then each has the angle to its nearest neighbour")
    void givenSeveralVectors_whenComputing_thenEachHasTheAngleToItsNearestNeighbour() {
      // Arrange: the angles to the x axis are 0, 10 and 40 degrees
      double[][] vectors = {
        {1, 0},
        {Math.cos(Math.toRadians(10)), Math.sin(Math.toRadians(10))},
        {Math.cos(Math.toRadians(40)), Math.sin(Math.toRadians(40))}
      };

      // Act
      double[] gamma = RVEAGeometry.gamma(vectors);

      // Assert
      assertThat(gamma[0]).isEqualTo(Math.toRadians(10), offset(EPSILON));
      assertThat(gamma[1]).isEqualTo(Math.toRadians(10), offset(EPSILON));
      assertThat(gamma[2]).isEqualTo(Math.toRadians(30), offset(EPSILON));
    }

    @Test
    @DisplayName("given a single vector, then its angle is a right angle")
    void givenASingleVector_whenComputing_thenTheAngleIsARightAngle() {
      // Act & Assert
      assertThat(RVEAGeometry.gamma(new double[][] {{1, 1}}))
          .containsExactly(new double[] {Math.PI / 2}, offset(EPSILON));
    }

    @Test
    @DisplayName("given duplicated vectors, then the angle is positive and tiny")
    void givenDuplicatedVectors_whenComputing_thenTheAngleIsPositiveAndTiny() {
      // Act
      double[] gamma = RVEAGeometry.gamma(new double[][] {{1, 1}, {1, 1}});

      // Assert: a zero angle would divide the penalty of the APD by zero
      assertThat(gamma[0]).isBetween(RVEAGeometry.MIN_ANGLE, 1e-6);
      assertThat(gamma[1]).isBetween(RVEAGeometry.MIN_ANGLE, 1e-6);
    }
  }
}
