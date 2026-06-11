package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for class AugmentedTschebyscheff")
class AugmentedTschebyscheffTest {

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
      assertThat(new AugmentedTschebyscheff(false).normalizeObjectives()).isFalse();
    }

    @Test
    @DisplayName("given normalization enabled, when querying normalizeObjectives, then it is true")
    void givenNormalizationEnabled_whenQueryingFlag_thenItIsTrue() {
      assertThat(new AugmentedTschebyscheff(true).normalizeObjectives()).isTrue();
    }
  }

  @Nested
  @DisplayName("When computing without normalization")
  class WhenComputingWithoutNormalization {

    @Test
    @DisplayName("given equal weights, when computed, then it is the Tchebycheff term plus the augmentation term")
    void givenEqualWeights_whenComputed_thenItIsTchebycheffPlusAugmentation() {
      AugmentedTschebyscheff subject = new AugmentedTschebyscheff(0.0001, false);

      // deviations weighted: [1.5, 2.0]; max = 2.0; sum = 3.5; g = 2.0 + 0.0001*3.5 = 2.00035
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(1.0, 1.0));

      assertThat(result).isCloseTo(2.00035, within(1e-12));
    }

    @Test
    @DisplayName("given a zero augmentation coefficient, when computed, then it reduces to the plain Tchebycheff value")
    void givenZeroRho_whenComputed_thenItReducesToPlainTchebycheff() {
      AugmentedTschebyscheff augmented = new AugmentedTschebyscheff(0.0, false);
      Tschebyscheff plain = new Tschebyscheff(false);

      double[] vector = {3.0, 4.0};
      double[] weight = {0.5, 0.5};

      double augmentedResult =
          augmented.compute(vector, weight, idealPoint(0.0, 0.0), nadirPoint(1.0, 1.0));
      double plainResult =
          plain.compute(vector, weight, idealPoint(0.0, 0.0), nadirPoint(1.0, 1.0));

      assertThat(augmentedResult).isCloseTo(plainResult, within(1e-12));
    }
  }

  @Nested
  @DisplayName("When computing with normalization")
  class WhenComputingWithNormalization {

    @Test
    @DisplayName("given ideal and nadir bounds, when computed, then deviations are normalized before aggregation")
    void givenBounds_whenComputed_thenDeviationsAreNormalized() {
      AugmentedTschebyscheff subject = new AugmentedTschebyscheff(0.0001, true);
      subject.epsilon(0.0);

      // normalized deviations: [3/10, 4/10] = [0.3, 0.4]; weighted: [0.15, 0.2]
      // max = 0.2; sum = 0.35; g = 0.2 + 0.0001*0.35 = 0.200035
      double result =
          subject.compute(
              new double[] {3.0, 4.0}, new double[] {0.5, 0.5}, idealPoint(0.0, 0.0),
              nadirPoint(10.0, 10.0));

      assertThat(result).isCloseTo(0.200035, within(1e-12));
    }
  }
}
