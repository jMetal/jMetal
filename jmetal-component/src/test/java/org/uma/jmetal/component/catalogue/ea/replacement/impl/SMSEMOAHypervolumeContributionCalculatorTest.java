package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class SMSEMOAHypervolumeContributionCalculator")
class SMSEMOAHypervolumeContributionCalculatorTest {
  private static final double EPSILON = 1e-9;

  private SMSEMOAHypervolumeContributionCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new SMSEMOAHypervolumeContributionCalculator();
  }

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  private static List<DoubleSolution> solutions(double[]... points) {
    List<DoubleSolution> solutions = new ArrayList<>();
    for (double[] point : points) {
      solutions.add(solution(point));
    }
    return solutions;
  }

  /** The hypervolume by inclusion-exclusion, an oracle that does not depend on the calculator. */
  private static double hypervolume(List<double[]> points, double[] referencePoint) {
    double result = 0.0;
    int n = points.size();
    for (int subset = 1; subset < (1 << n); subset++) {
      double volume = 1.0;
      for (int objective = 0; objective < referencePoint.length; objective++) {
        double worst = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n; i++) {
          if ((subset & (1 << i)) != 0) {
            worst = Math.max(worst, points.get(i)[objective]);
          }
        }
        volume *= Math.max(0.0, referencePoint[objective] - worst);
      }
      result += (Integer.bitCount(subset) % 2 == 1 ? 1.0 : -1.0) * volume;
    }
    return result;
  }

  private static double[] expectedContributions(double[][] points, double[] referencePoint) {
    double total = hypervolume(Arrays.asList(points), referencePoint);
    double[] contributions = new double[points.length];
    for (int omitted = 0; omitted < points.length; omitted++) {
      List<double[]> others = new ArrayList<>();
      for (int i = 0; i < points.length; i++) {
        if (i != omitted) {
          others.add(points[i]);
        }
      }
      contributions[omitted] = total - hypervolume(others, referencePoint);
    }
    return contributions;
  }

  /** Four non-dominated points of the simplex, with the other objectives set to constants. */
  private static double[][] simplexPoints(int objectives, double shift) {
    double[][] base = {{0.0, 1.0}, {0.2, 0.8}, {0.7, 0.3}, {1.0, 0.0}};
    double[][] points = new double[base.length][objectives];
    for (int i = 0; i < base.length; i++) {
      points[i][0] = base[i][0] + shift;
      points[i][1] = base[i][1] + shift;
      for (int j = 2; j < objectives; j++) {
        points[i][j] = 0.25 * j + shift;
      }
    }
    return points;
  }

  private static double[] referencePoint(int objectives, double value) {
    double[] referencePoint = new double[objectives];
    Arrays.fill(referencePoint, value);
    return referencePoint;
  }

  @Nested
  @DisplayName("When computing the contributions with two objectives")
  class WhenComputingWithTwoObjectives {
    @Test
    @DisplayName("given three points, then each contribution is its exclusive area")
    void givenThreePoints_whenComputing_thenEachContributionIsItsExclusiveArea() {
      // Arrange
      var front = solutions(new double[] {1, 8}, new double[] {4, 4}, new double[] {8, 1});

      // Act
      double[] contributions = calculator.compute(front, new double[] {10, 10});

      // Assert: 3 x 2, 4 x 4 and 2 x 3
      assertThat(contributions).containsExactly(new double[] {6.0, 16.0, 6.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given the same points translated to negative values, then they are the same")
    void givenNegativeValues_whenComputing_thenTheContributionsAreTheSame() {
      // Arrange
      var front =
          solutions(new double[] {-99, -92}, new double[] {-96, -96}, new double[] {-92, -99});

      // Act
      double[] contributions = calculator.compute(front, new double[] {-90, -90});

      // Assert
      assertThat(contributions).containsExactly(new double[] {6.0, 16.0, 6.0}, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When computing the contributions with three objectives")
  class WhenComputingWithThreeObjectives {
    @Test
    @DisplayName("given a point that dominates another, then the dominated one contributes nothing")
    void givenADominatedPoint_whenComputing_thenItContributesNothing() {
      // Arrange
      var front = solutions(new double[] {2, 3, 4}, new double[] {3, 4, 5});

      // Act
      double[] contributions = calculator.compute(front, new double[] {10, 10, 10});

      // Assert: 8 * 7 * 6 - 7 * 6 * 5 = 126
      assertThat(contributions).containsExactly(new double[] {126.0, 0.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given a single point, then its contribution is the volume of its box")
    void givenASinglePoint_whenComputing_thenTheContributionIsTheVolumeOfItsBox() {
      // Act
      double[] contributions =
          calculator.compute(solutions(new double[] {2, 3, 4}), new double[] {10, 10, 10});

      // Assert
      assertThat(contributions).containsExactly(new double[] {336.0}, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When computing the contributions with several objectives")
  class WhenComputingWithSeveralObjectives {
    @ParameterizedTest(name = "{0} objectives, reference point {1}")
    @CsvSource({
      "2, 1.1", "3, 1.1", "4, 1.1", "5, 1.1", "4, 1.0", "4, 2.0", "5, 3.0", "6, 1.5"
    })
    @DisplayName("given points of the simplex, then the contributions are those of the oracle")
    void givenPoints_whenComputing_thenTheContributionsAreThoseOfTheOracle(
        int objectives, double reference) {
      // Arrange
      double[][] points = simplexPoints(objectives, 0.0);
      double[] referencePoint = referencePoint(objectives, reference);
      referencePoint[objectives - 1] = Math.max(reference, 0.25 * (objectives - 1) + 0.1);

      // Act
      double[] contributions = calculator.compute(solutions(points), referencePoint);

      // Assert
      assertThat(contributions)
          .containsExactly(expectedContributions(points, referencePoint), offset(EPSILON));
    }

    @ParameterizedTest(name = "{0} objectives, shifted by {1}")
    @CsvSource({"2, -5", "3, -5", "4, -5", "5, -5", "4, -100", "4, 100"})
    @DisplayName("given points and reference point translated, then the contributions are the same")
    void givenATranslation_whenComputing_thenTheContributionsAreTheSame(
        int objectives, double shift) {
      // Arrange
      double[] referencePoint = referencePoint(objectives, 1.1);
      referencePoint[objectives - 1] = 0.25 * (objectives - 1) + 0.1;
      double[] shiftedReferencePoint = new double[objectives];
      for (int j = 0; j < objectives; j++) {
        shiftedReferencePoint[j] = referencePoint[j] + shift;
      }

      // Act
      double[] original =
          calculator.compute(solutions(simplexPoints(objectives, 0.0)), referencePoint);
      double[] translated =
          new SMSEMOAHypervolumeContributionCalculator()
              .compute(solutions(simplexPoints(objectives, shift)), shiftedReferencePoint);

      // Assert
      assertThat(translated).containsExactly(original, offset(1e-7));
    }

    @Test
    @DisplayName("given a point out of the reference point, then it contributes nothing")
    void givenAPointOutOfTheReferencePoint_whenComputing_thenItContributesNothing() {
      // Arrange
      var front = solutions(new double[] {0.2, 0.2, 0.2, 0.2}, new double[] {5.0, 0.1, 0.1, 0.1});

      // Act
      double[] contributions = calculator.compute(front, new double[] {1, 1, 1, 1});

      // Assert
      assertThat(contributions).containsExactly(new double[] {0.4096, 0.0}, offset(EPSILON));
    }

    @Test
    @DisplayName("given a single point, then its contribution is the volume of its box")
    void givenASinglePoint_whenComputing_thenTheContributionIsTheVolumeOfItsBox() {
      // Act
      double[] contributions =
          calculator.compute(
              solutions(new double[] {0.5, 0.5, 0.5, 0.5}), new double[] {1, 1, 1, 1});

      // Assert
      assertThat(contributions).containsExactly(new double[] {0.0625}, offset(EPSILON));
    }

    @Test
    @DisplayName("given a reference point other than (1, ..., 1), then the contributions are right")
    void givenAnotherReferencePoint_whenComputing_thenTheContributionsAreRight() {
      // Arrange
      var front = solutions(new double[] {0.2, 0.8, 0.5, 0.5}, new double[] {0.8, 0.2, 0.5, 0.5});

      // Act
      double[] contributions = calculator.compute(front, new double[] {2, 2, 2, 2});

      // Assert: the union has 6.48 and each point alone 4.86
      assertThat(contributions).containsExactly(new double[] {1.62, 1.62}, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When the front is special")
  class WhenTheFrontIsSpecial {
    @Test
    @DisplayName("given an empty front, then there are no contributions")
    void givenAnEmptyFront_whenComputing_thenThereAreNoContributions() {
      // Act & Assert
      assertThat(calculator.compute(List.of(), new double[] {1, 1})).isEmpty();
    }

    @Test
    @DisplayName("given a null front or reference point, then it is rejected")
    void givenNullArguments_whenComputing_thenTheyAreRejected() {
      // Act
      Throwable nullFront = catchThrowable(() -> calculator.compute(null, new double[] {1, 1}));
      Throwable nullReference = catchThrowable(() -> calculator.compute(List.of(), null));

      // Assert
      assertThat(nullFront).isInstanceOf(NullParameterException.class);
      assertThat(nullReference).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given duplicated points, then each covers the other and neither contributes")
    void givenDuplicatedPoints_whenComputing_thenNeitherContributes() {
      // Arrange
      var front =
          solutions(
              new double[] {1, 2, 3, 4}, new double[] {1, 2, 3, 4}, new double[] {4, 1, 2, 3});

      // Act
      double[] contributions = calculator.compute(front, new double[] {5, 5, 5, 5});

      // Assert
      assertThat(contributions[0]).isEqualTo(0.0, offset(EPSILON));
      assertThat(contributions[1]).isEqualTo(0.0, offset(EPSILON));
      assertThat(contributions[2]).isPositive();
    }
  }
}
