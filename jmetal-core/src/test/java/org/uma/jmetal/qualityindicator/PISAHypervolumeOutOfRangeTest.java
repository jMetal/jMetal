package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

@DisplayName("Unit tests for the values out of [0, 1] of class PISAHypervolume")
class PISAHypervolumeOutOfRangeTest {
  private static final double EPSILON = 1e-12;

  /** It assumes normalized fronts, with the reference point (1, ..., 1). */
  private final PISAHypervolume twoObjectives = new PISAHypervolume(new double[] {1.0, 1.0});

  private final PISAHypervolume threeObjectives =
      new PISAHypervolume(new double[] {1.0, 1.0, 1.0});

  @Nested
  @DisplayName("When a coordinate is out of the range")
  class WhenACoordinateIsOutOfTheRange {
    @ParameterizedTest(name = "point ({0}, {1}) has {2}")
    @CsvSource({
      "0.5, 0.5, 0.25",
      "0.0, 0.0, 1.0",
      "1.0, 0.0, 0.0",
      "0.0, 1.0, 0.0",
      "1.5, 0.2, 0.0",
      "0.2, 7.0, 0.0",
      "-0.5, 0.5, 0.5",
      "0.5, -2.0, 0.5",
      "-3.0, -3.0, 1.0"
    })
    @DisplayName("given a point, then a coordinate above one adds nothing, and a negative is zero")
    void givenAPoint_whenComputing_thenTheHypervolumeIsThatOfThePointInTheUnitBox(
        double f1, double f2, double expected) {
      // Act
      double value = twoObjectives.compute(new double[][] {{f1, f2}});

      // Assert
      assertThat(value).isEqualTo(expected, offset(EPSILON));
    }

    @Test
    @DisplayName("given a point out of the unit box and another inside, then only that counts")
    void givenAPointOutOfTheUnitBox_whenComputing_thenItDoesNotChangeTheHypervolume() {
      // Arrange
      double[][] inside = {{0.5, 0.5}};
      double[][] withAnOutsidePoint = {{0.5, 0.5}, {2.0, 0.1}};

      // Act & Assert
      assertThat(twoObjectives.compute(withAnOutsidePoint))
          .isEqualTo(twoObjectives.compute(inside), offset(EPSILON));
    }

    @Test
    @DisplayName("given three objectives, then the values out of the range are treated the same")
    void givenThreeObjectives_whenComputing_thenTheValuesOutOfTheRangeAreTreatedTheSame() {
      // Act & Assert
      assertThat(threeObjectives.compute(new double[][] {{0.5, 0.5, 0.5}}))
          .isEqualTo(0.125, offset(EPSILON));
      assertThat(threeObjectives.compute(new double[][] {{-1.0, 0.5, 0.5}}))
          .isEqualTo(0.25, offset(EPSILON));
      assertThat(threeObjectives.compute(new double[][] {{0.5, 0.5, 1.2}})).isZero();
    }

    @Test
    @DisplayName("given a front, then the points out of the range do not modify it")
    void givenAFront_whenComputing_thenItIsNotModified() {
      // Arrange
      double[][] front = {{-0.5, 2.0}, {0.5, 0.5}};

      // Act
      twoObjectives.compute(front);

      // Assert
      assertThat(front).isDeepEqualTo(new double[][] {{-0.5, 2.0}, {0.5, 0.5}});
    }
  }

  @Nested
  @DisplayName("When the front is special")
  class WhenTheFrontIsSpecial {
    @Test
    @DisplayName("given an empty front, then the hypervolume is zero")
    void givenAnEmptyFront_whenComputing_thenTheHypervolumeIsZero() {
      // Act & Assert
      assertThat(twoObjectives.compute(new double[0][])).isZero();
    }

    @Test
    @DisplayName("given a null front, then it is rejected")
    void givenANullFront_whenComputing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> twoObjectives.compute(null));

      // Assert
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("given duplicated points, then they count once")
    void givenDuplicatedPoints_whenComputing_thenTheyCountOnce() {
      // Act & Assert
      assertThat(twoObjectives.compute(new double[][] {{0.5, 0.5}, {0.5, 0.5}}))
          .isEqualTo(0.25, offset(EPSILON));
    }

    @Test
    @DisplayName("given a dominated point, then it does not add hypervolume")
    void givenADominatedPoint_whenComputing_thenItDoesNotAddHypervolume() {
      // Act & Assert
      assertThat(twoObjectives.compute(new double[][] {{0.5, 0.5}, {0.7, 0.8}}))
          .isEqualTo(0.25, offset(EPSILON));
    }
  }
}
