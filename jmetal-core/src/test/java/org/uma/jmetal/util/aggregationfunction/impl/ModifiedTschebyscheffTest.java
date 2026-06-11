package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for class ModifiedTschebyscheff")
class ModifiedTschebyscheffTest {

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
      assertThat(new ModifiedTschebyscheff(false).normalizeObjectives()).isFalse();
    }

    @Test
    @DisplayName("given normalization enabled, when querying normalizeObjectives, then it is true")
    void givenNormalizationEnabled_whenQueryingFlag_thenItIsTrue() {
      assertThat(new ModifiedTschebyscheff(true).normalizeObjectives()).isTrue();
    }
  }

  @Nested
  @DisplayName("When computing without normalization")
  class WhenComputingWithoutNormalization {

    private final ModifiedTschebyscheff subject = new ModifiedTschebyscheff(false);

    @Test
    @DisplayName("given equal weights, when computed, then it is the maximum deviation divided by the weight")
    void givenEqualWeights_whenComputed_thenItIsTheMaxDeviationOverWeight() {
      // max(|3-0|/0.5, |4-0|/0.5) = max(6.0, 8.0) = 8.0
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(8.0, within(1e-12));
    }

    @Test
    @DisplayName("given a zero weight, when computed, then that objective is replaced by a negligible term")
    void givenZeroWeight_whenComputed_thenThatObjectiveIsNegligible() {
      // objective 0: |3-0|/1.0 = 3.0; objective 1 (weight 0): 0.0000001*|4-0|; max = 3.0
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {1.0, 0.0}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(3.0, within(1e-6));
    }
  }

  @Nested
  @DisplayName("When computing with normalization")
  class WhenComputingWithNormalization {

    @Test
    @DisplayName("given ideal and nadir bounds, when computed, then deviations are normalized before dividing by the weight")
    void givenBounds_whenComputed_thenDeviationsAreNormalized() {
      ModifiedTschebyscheff subject = new ModifiedTschebyscheff(true);
      subject.epsilon(0.0);

      // max((3/10)/0.5, (4/10)/0.5) = max(0.6, 0.8) = 0.8
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(0.8, within(1e-12));
    }
  }
}
