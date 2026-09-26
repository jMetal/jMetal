package org.uma.jmetal.problem.multiobjective.dtlz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for class DTLZMinusTransform")
class DTLZMinusTransformTest {

  @Nested
  @DisplayName("When computing the distance-function maximum")
  class WhenComputingTheDistanceMaximum {

    @Test
    @DisplayName("given a number of distance variables, then the quadratic maximum is 0.25 per variable")
    void givenANumberOfDistanceVariables_whenComputingTheQuadraticMaximum_thenItIsQuarterPerVariable() {
      assertThat(DTLZMinusTransform.quadraticDistanceMaximum(1)).isCloseTo(0.25, within(1.0e-12));
      assertThat(DTLZMinusTransform.quadraticDistanceMaximum(5)).isCloseTo(1.25, within(1.0e-12));
    }

    @Test
    @DisplayName("given a number of distance variables, then the multimodal maximum scales linearly")
    void givenANumberOfDistanceVariables_whenComputingTheMultimodalMaximum_thenItScalesLinearly() {
      double oneVariable = DTLZMinusTransform.multimodalDistanceMaximum(1);
      double fiveVariables = DTLZMinusTransform.multimodalDistanceMaximum(5);

      assertThat(oneVariable).isPositive();
      assertThat(fiveVariables).isCloseTo(5.0 * oneVariable, within(1.0e-9));
    }
  }

  @Nested
  @DisplayName("When rescaling negated objectives")
  class WhenRescaling {

    @Test
    @DisplayName("given h values on the DTLZ1-type front (g = 0), then the result is one minus h")
    void givenValuesOnTheSumFront_whenRescaling_thenResultIsOneMinusH() {
      double[] h = {0.1, 0.4};
      double[] objectives = h.clone();

      DTLZMinusTransform.negateSumFront(objectives, 0.5, 100.0);

      assertThat(objectives[0]).isCloseTo(1.0 - h[0], within(1.0e-9));
      assertThat(objectives[1]).isCloseTo(1.0 - h[1], within(1.0e-9));
    }

    @Test
    @DisplayName("given h values away from the DTLZ1-type front (g > 0), then Eq. (9) is applied")
    void givenValuesAwayFromTheSumFront_whenRescaling_thenEquation9IsApplied() {
      double[] h = {0.1, 0.4};
      double g = 2.0;
      double gMax = 10.0;
      double[] objectives = {(1 + g) * h[0], (1 + g) * h[1]};

      DTLZMinusTransform.negateSumFront(objectives, 0.5, gMax);

      double weight = 1.0 - g / (1.0 + gMax);
      assertThat(objectives[0]).isCloseTo(1.0 - weight * h[0], within(1.0e-9));
      assertThat(objectives[1]).isCloseTo(1.0 - weight * h[1], within(1.0e-9));
    }

    @Test
    @DisplayName("given h values on the DTLZ2-type front (g = 0), then the result is one minus h")
    void givenValuesOnTheSphereFront_whenRescaling_thenResultIsOneMinusH() {
      double[] h = {0.6, 0.8};
      double[] objectives = h.clone();

      DTLZMinusTransform.negateSphereFront(objectives, 100.0);

      assertThat(objectives[0]).isCloseTo(1.0 - h[0], within(1.0e-9));
      assertThat(objectives[1]).isCloseTo(1.0 - h[1], within(1.0e-9));
    }

    @Test
    @DisplayName("given h values away from the DTLZ2-type front (g > 0), then Eq. (9) is applied")
    void givenValuesAwayFromTheSphereFront_whenRescaling_thenEquation9IsApplied() {
      double[] h = {0.6, 0.8};
      double g = 3.0;
      double gMax = 10.0;
      double[] objectives = {(1 + g) * h[0], (1 + g) * h[1]};

      DTLZMinusTransform.negateSphereFront(objectives, gMax);

      double weight = 1.0 - g / (1.0 + gMax);
      assertThat(objectives[0]).isCloseTo(1.0 - weight * h[0], within(1.0e-9));
      assertThat(objectives[1]).isCloseTo(1.0 - weight * h[1], within(1.0e-9));
    }
  }
}
