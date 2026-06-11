package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for class WeightedSum")
class WeightedSumTest {

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
    @DisplayName("given normalization disabled, when querying normalizeObjectives, then it is false")
    void givenNormalizationDisabled_whenQueryingFlag_thenItIsFalse() {
      assertThat(new WeightedSum(false).normalizeObjectives()).isFalse();
    }

    @Test
    @DisplayName("given normalization enabled, when querying normalizeObjectives, then it is true")
    void givenNormalizationEnabled_whenQueryingFlag_thenItIsTrue() {
      assertThat(new WeightedSum(true).normalizeObjectives()).isTrue();
    }
  }

  @Nested
  @DisplayName("When computing without normalization")
  class WhenComputingWithoutNormalization {

    private final WeightedSum subject = new WeightedSum(false);

    @Test
    @DisplayName("given equal weights, when computed, then it is the weighted sum of the raw objectives")
    void givenEqualWeights_whenComputed_thenItIsTheWeightedSum() {
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(3.5, within(1e-12));
    }

    @Test
    @DisplayName("given a single non-zero weight, when computed, then only that objective contributes")
    void givenSingleNonZeroWeight_whenComputed_thenOnlyThatObjectiveContributes() {
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {1.0, 0.0}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(3.0, within(1e-12));
    }
  }

  @Nested
  @DisplayName("When computing with normalization")
  class WhenComputingWithNormalization {

    @Test
    @DisplayName("given ideal and nadir bounds, when computed, then objectives are normalized before weighting")
    void givenBounds_whenComputed_thenObjectivesAreNormalized() {
      WeightedSum subject = new WeightedSum(true);
      subject.epsilon(0.0);

      // value_i = (5 - 0) / (10 - 0) = 0.5 for each objective; 0.5*0.5 + 0.5*0.5 = 0.5
      double result =
          subject.compute(
              new double[] {5.0, 5.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(0.5, within(1e-12));
    }
  }
}
