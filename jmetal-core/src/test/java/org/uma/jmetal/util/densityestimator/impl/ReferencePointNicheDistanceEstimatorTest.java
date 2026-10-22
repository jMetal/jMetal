package org.uma.jmetal.util.densityestimator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class ReferencePointNicheDistanceEstimator")
class ReferencePointNicheDistanceEstimatorTest {
  private static final double EPSILON = 1e-9;
  private static final double[][] POINTS = {
    {0.0, 1.0}, {0.2, 0.8}, {0.5, 0.5}, {0.7, 0.3}, {1.0, 0.0}
  };

  private ReferencePointNicheDistanceEstimator<DoubleSolution> estimator;

  @BeforeEach
  void setUp() {
    estimator =
        new ReferencePointNicheDistanceEstimator<>(
            List.of(new double[] {1.0, 0.0}, new double[] {0.5, 0.5}, new double[] {0.0, 1.0}),
            2);
  }

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  private static List<DoubleSolution> solutions(
      double scale1, double scale2, double shift1, double shift2) {
    List<DoubleSolution> solutions = new ArrayList<>();
    for (double[] point : POINTS) {
      solutions.add(solution(point[0] * scale1 + shift1, point[1] * scale2 + shift2));
    }
    return solutions;
  }

  @Nested
  @DisplayName("When computing the distances")
  class WhenComputingTheDistances {
    @Test
    @DisplayName("given a front, then each solution gets its closest reference point and distance")
    void givenAFront_whenComputing_thenEachSolutionGetsItsClosestReferencePointAndDistance() {
      // Arrange
      List<DoubleSolution> front = solutions(1.0, 1.0, 0.0, 0.0);

      // Act
      estimator.compute(front);

      // Assert
      int[] referencePoints = {2, 2, 1, 1, 0};
      double[] distances = {0.0, 0.2, 0.0, Math.sqrt(0.08), 0.0};
      for (int i = 0; i < front.size(); i++) {
        assertThat(estimator.referencePointIndex(front.get(i))).isEqualTo(referencePoints[i]);
        assertThat(estimator.value(front.get(i))).isEqualTo(distances[i], offset(EPSILON));
      }
    }

    @ParameterizedTest(name = "scales ({0}, {1}) and shifts ({2}, {3})")
    @CsvSource({
      "1000.0, 1.0, 0.0, 0.0",
      "1.0, 0.001, 0.0, 0.0",
      "1.0, 1.0, 100.0, -50.0",
      "1.0, 1.0, -1000.0, -7.0",
      "250.0, 0.004, -9000.0, -17.0"
    })
    @DisplayName("given a scaled or translated front, then the distances are the same")
    void givenAScaledOrTranslatedFront_whenComputing_thenTheDistancesAreTheSame(
        double scale1, double scale2, double shift1, double shift2) {
      // Arrange
      List<DoubleSolution> reference = solutions(1.0, 1.0, 0.0, 0.0);
      List<DoubleSolution> transformed = solutions(scale1, scale2, shift1, shift2);

      // Act
      estimator.compute(reference);
      var other =
          new ReferencePointNicheDistanceEstimator<DoubleSolution>(
              estimator.getReferencePoints(), 2);
      other.compute(transformed);

      // Assert
      for (int i = 0; i < reference.size(); i++) {
        assertThat(other.value(transformed.get(i)))
            .isEqualTo(estimator.value(reference.get(i)), offset(EPSILON));
        assertThat(other.referencePointIndex(transformed.get(i)))
            .isEqualTo(estimator.referencePointIndex(reference.get(i)));
      }
    }

    @Test
    @DisplayName("given an objective with the same value in every solution, then it does not fail")
    void givenAConstantObjective_whenComputing_thenItDoesNotFail() {
      // Arrange: the second objective is constant, so its normalized value is zero
      List<DoubleSolution> front = new ArrayList<>();
      for (double value : new double[] {0.0, 0.5, 1.0}) {
        front.add(solution(value, 4.0));
      }

      // Act
      estimator.compute(front);

      // Assert: the normalized solutions are (0, 0), (0.5, 0) and (1, 0)
      for (DoubleSolution solution : front) {
        assertThat(Double.isFinite(estimator.value(solution))).isTrue();
      }
      assertThat(estimator.value(front.get(2))).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a single solution, then it is at the origin of the normalized space")
    void givenASingleSolution_whenComputing_thenItIsAtTheOrigin() {
      // Arrange
      DoubleSolution only = solution(-3.0, 8.0);

      // Act
      estimator.compute(List.of(only));

      // Assert: the distance of the origin to the reference point (0.5, 0.5) is zero as well
      assertThat(estimator.value(only)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given an empty list, then nothing is computed")
    void givenAnEmptyList_whenComputing_thenNothingIsComputed() {
      // Act & Assert
      estimator.compute(List.of());
    }

    @Test
    @DisplayName("given a front, then the objectives of the solutions are not modified")
    void givenAFront_whenComputing_thenTheObjectivesAreNotModified() {
      // Arrange
      List<DoubleSolution> front = solutions(3.0, 4.0, 5.0, 6.0);

      // Act
      estimator.compute(front);

      // Assert
      assertThat(front.get(1).objectives()).containsExactly(0.2 * 3.0 + 5.0, 0.8 * 4.0 + 6.0);
    }
  }

  @Nested
  @DisplayName("When asking for the values")
  class WhenAskingForTheValues {
    @Test
    @DisplayName("given a solution that has not been computed, then the distance is the maximum")
    void givenAnUncomputedSolution_whenAskingTheValue_thenItIsTheMaximum() {
      // Act & Assert
      assertThat(estimator.value(solution(0.1, 0.1))).isEqualTo(Double.MAX_VALUE);
      assertThat(estimator.referencePointIndex(solution(0.1, 0.1))).isNull();
    }

    @Test
    @DisplayName("given a null solution, then it is rejected")
    void givenANullSolution_whenAskingTheValue_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> estimator.value(null));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given a front, then the comparator puts the closest to a reference point first")
    void givenAFront_whenComparing_thenTheClosestToAReferencePointIsFirst() {
      // Arrange
      List<DoubleSolution> front = solutions(1.0, 1.0, 0.0, 0.0);
      estimator.compute(front);

      // Act
      List<DoubleSolution> sorted = new ArrayList<>(front);
      sorted.sort(estimator.comparator());

      // Assert: the farthest one is (0.7, 0.3), with 0.283, and then (0.2, 0.8), with 0.2
      assertThat(sorted.get(sorted.size() - 1)).isSameAs(front.get(3));
      assertThat(sorted.get(sorted.size() - 2)).isSameAs(front.get(1));
    }
  }

  @Nested
  @DisplayName("When creating the estimator")
  class WhenCreatingTheEstimator {
    @Test
    @DisplayName("given null or empty reference points, then they are rejected")
    void givenNullOrEmptyReferencePoints_whenCreating_thenTheyAreRejected() {
      // Act
      Throwable nullPoints =
          catchThrowable(() -> new ReferencePointNicheDistanceEstimator<DoubleSolution>(null, 2));
      Throwable emptyPoints =
          catchThrowable(
              () -> new ReferencePointNicheDistanceEstimator<DoubleSolution>(List.of(), 2));

      // Assert
      assertThat(nullPoints).isInstanceOf(NullParameterException.class);
      assertThat(emptyPoints).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a reference point at the origin, then its distance is the maximum")
    void givenAReferencePointAtTheOrigin_whenComputing_thenItsDistanceIsTheMaximum() {
      // Arrange
      var withOrigin =
          new ReferencePointNicheDistanceEstimator<DoubleSolution>(
              List.of(new double[] {0.0, 0.0}), 2);
      DoubleSolution only = solution(1.0, 2.0);

      // Act
      withOrigin.compute(List.of(only));

      // Assert
      assertThat(withOrigin.value(only)).isEqualTo(Double.MAX_VALUE);
    }
  }
}
