package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for class InvertedPenaltyBoundaryIntersection")
class InvertedPenaltyBoundaryIntersectionTest {

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
      assertThat(new InvertedPenaltyBoundaryIntersection().normalizeObjectives()).isFalse();
    }

    @Test
    @DisplayName("given normalization enabled, when querying normalizeObjectives, then it is true")
    void givenNormalizationEnabled_whenQueryingFlag_thenItIsTrue() {
      assertThat(new InvertedPenaltyBoundaryIntersection(0.1, true).normalizeObjectives()).isTrue();
    }
  }

  @Nested
  @DisplayName("When computing without normalization")
  class WhenComputingWithoutNormalization {

    @Test
    @DisplayName("given a displacement aligned with the weight direction, when computed, then the result is minus d1")
    void givenAlignedDisplacement_whenComputed_thenResultIsMinusD1() {
      InvertedPenaltyBoundaryIntersection subject =
          new InvertedPenaltyBoundaryIntersection(0.1, false);

      // u = nadir - f = [10-7, 10-6] = [3, 4], aligned with the unit weight [0.6, 0.8]:
      // d1 = 5, d2 = 0, result = 0.1*0 - 5 = -5
      double result =
          subject.compute(
              new double[] {7.0, 6.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(-5.0, within(1e-12));
    }

    @Test
    @DisplayName("given a displacement off the weight direction, when computed, then it is theta times d2 minus d1")
    void givenOffDirectionDisplacement_whenComputed_thenItIsThetaD2MinusD1() {
      InvertedPenaltyBoundaryIntersection subject =
          new InvertedPenaltyBoundaryIntersection(0.1, false);

      // u = [10-6, 10-2] = [4, 8]; d1 = 4*0.6 + 8*0.8 = 8.8; perpendicular = [-1.28, 0.96], d2 = 1.6
      // result = 0.1*1.6 - 8.8 = -8.64
      double result =
          subject.compute(
              new double[] {6.0, 2.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(-8.64, within(1e-12));
    }

    @Test
    @DisplayName("given a larger theta, when computed, then the perpendicular distance is rewarded less")
    void givenLargerTheta_whenComputed_thenPerpendicularDistanceIsRewardedLess() {
      // d1 = 8.8, d2 = 1.6; result = 1.0*1.6 - 8.8 = -7.2
      double result =
          new InvertedPenaltyBoundaryIntersection(1.0, false)
              .compute(
                  new double[] {6.0, 2.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
                  nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(-7.2, within(1e-12));
    }
  }

  @Nested
  @DisplayName("When computing with normalization")
  class WhenComputingWithNormalization {

    @Test
    @DisplayName("given a degenerate objective whose nadir equals its ideal, when computed, then the result is finite")
    void givenDegenerateObjective_whenComputed_thenResultIsFinite() {
      InvertedPenaltyBoundaryIntersection subject =
          new InvertedPenaltyBoundaryIntersection(0.1, true);

      double result =
          subject.compute(
              new double[] {1.0, 5.0}, new double[] {0.6, 0.8}, idealPoint(0.0, 0.0),
              nadirPoint(0.0, 10.0));

      assertThat(result).isFinite();
    }
  }
}
