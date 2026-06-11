package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for class PenaltyBoundaryIntersection")
class PenaltyBoundaryIntersectionTest {

  private static IdealPoint idealPoint(double... values) {
    IdealPoint point = new IdealPoint(values.length);
    point.update(values);
    return point;
  }

  private static NadirPoint nadirPoint(double... values) {
    NadirPoint point = new NadirPoint(values.length);
    point.update(values);
    return point;
  }

  @Nested
  @DisplayName("When checking its properties")
  class WhenCheckingProperties {

    @Test
    @DisplayName("given the default constructor, when querying normalizeObjectives, then it is false")
    void givenDefaultConstructor_whenQueryingFlag_thenItIsFalse() {
      assertThat(new PenaltyBoundaryIntersection().normalizeObjectives()).isFalse();
    }

    @Test
    @DisplayName("given normalization enabled, when querying normalizeObjectives, then it is true")
    void givenNormalizationEnabled_whenQueryingFlag_thenItIsTrue() {
      assertThat(new PenaltyBoundaryIntersection(5.0, true).normalizeObjectives()).isTrue();
    }
  }

  @Nested
  @DisplayName("When computing without normalization")
  class WhenComputingWithoutNormalization {

    @Test
    @DisplayName("given a vector aligned with the weight direction, when computed, then the perpendicular distance is zero")
    void givenAlignedVector_whenComputed_thenPerpendicularDistanceIsZero() {
      PenaltyBoundaryIntersection subject = new PenaltyBoundaryIntersection(5.0, false);

      // weight is a unit vector; [3,4] projects exactly onto it: d1 = 5, d2 = 0, g = 5 + 5*0 = 5
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(5.0, within(1e-12));
    }

    @Test
    @DisplayName("given a vector off the weight direction, when computed, then it is d1 plus theta times d2")
    void givenOffDirectionVector_whenComputed_thenItIsD1PlusThetaTimesD2() {
      PenaltyBoundaryIntersection subject = new PenaltyBoundaryIntersection(5.0, false);

      // d1 = 4*0.6 + 3*0.8 = 4.8; perpendicular = [1.12, -0.84], d2 = 1.4; g = 4.8 + 5*1.4 = 11.8
      double result =
          subject.compute(
              new double[] {4.0, 3.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(11.8, within(1e-12));
    }

    @Test
    @DisplayName("given a larger theta, when computed, then the perpendicular distance is penalized more heavily")
    void givenLargerTheta_whenComputed_thenPerpendicularDistanceIsPenalizedMore() {
      // d1 = 4.8, d2 = 1.4; g = 4.8 + 10*1.4 = 18.8
      double result =
          new PenaltyBoundaryIntersection(10.0, false)
              .compute(
                  new double[] {4.0, 3.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
                  nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(18.8, within(1e-12));
    }
  }

  @Nested
  @DisplayName("When computing with normalization")
  class WhenComputingWithNormalization {

    @Test
    @DisplayName("given a degenerate objective whose nadir equals its ideal, when computed, then the result is finite")
    void givenDegenerateObjective_whenComputed_thenResultIsFinite() {
      // Objective 0 has nadir == ideal == 0. Without the epsilon guard in the d2 loop this
      // divides by zero and yields a non-finite result. The guard must keep it finite.
      PenaltyBoundaryIntersection subject = new PenaltyBoundaryIntersection(5.0, true);

      double result =
          subject.compute(
              new double[] {1.0, 5.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(0.0, 10.0));

      assertThat(result).isFinite();
    }
  }
}
