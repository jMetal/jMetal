package org.uma.jmetal.util.aggregationfunction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

@DisplayName("Unit tests for the normalization of the aggregation functions")
class AggregationFunctionNormalizationTest {
  private static final double EPSILON = 1e-9;
  private static final String FUNCTIONS =
      "org.uma.jmetal.util.aggregationfunction.impl.AggregationFunctionNormalizationTest#functions";

  static Stream<Arguments> functions() {
    return Stream.of(
        function("Tschebyscheff", () -> new Tschebyscheff(true)),
        function("WeightedSum", () -> new WeightedSum(true)),
        function("ModifiedTschebyscheff", () -> new ModifiedTschebyscheff(true)),
        function("AugmentedTschebyscheff", () -> new AugmentedTschebyscheff(true)),
        function("PenaltyBoundaryIntersection", () -> new PenaltyBoundaryIntersection(5.0, true)),
        function(
            "InvertedPenaltyBoundaryIntersection",
            () -> new InvertedPenaltyBoundaryIntersection(0.1, true)));
  }

  private static Arguments function(String name, Supplier<AggregationFunction> function) {
    return Arguments.of(name, function);
  }

  private static IdealPoint ideal(double... values) {
    var point = new IdealPoint(values.length);
    point.update(values);
    return point;
  }

  private static NadirPoint nadir(double... values) {
    var point = new NadirPoint(values.length);
    point.update(values);
    return point;
  }

  private static double[] transformed(double[] values, double[] scales, double[] shifts) {
    double[] result = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      result[i] = values[i] * scales[i] + shifts[i];
    }
    return result;
  }

  private static final double[] VECTOR = {0.6, 0.3, 0.5};
  private static final double[] WEIGHTS = {0.5, 0.3, 0.2};
  private static final double[] IDEAL = {0.1, 0.0, 0.2};
  private static final double[] NADIR = {1.1, 0.8, 1.2};

  @Nested
  @DisplayName("When the objectives are normalized")
  class WhenTheObjectivesAreNormalized {
    @ParameterizedTest(name = "{0}")
    @MethodSource(FUNCTIONS)
    @DisplayName("given a translation of the objectives, then the value is the same")
    void givenATranslation_whenComputing_thenTheValueIsTheSame(
        String name, Supplier<AggregationFunction> supplier) {
      // Arrange
      AggregationFunction function = supplier.get();
      function.epsilon(0.0);
      double[] scales = {1.0, 1.0, 1.0};
      double[] shifts = {-250.0, 40.0, -7.5};

      // Act
      double original = function.compute(VECTOR, WEIGHTS, ideal(IDEAL), nadir(NADIR));
      double moved =
          function.compute(
              transformed(VECTOR, scales, shifts),
              WEIGHTS,
              ideal(transformed(IDEAL, scales, shifts)),
              nadir(transformed(NADIR, scales, shifts)));

      // Assert
      assertThat(moved).isEqualTo(original, offset(EPSILON));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(FUNCTIONS)
    @DisplayName("given a scaling of each objective, then the value is the same")
    void givenAScaling_whenComputing_thenTheValueIsTheSame(
        String name, Supplier<AggregationFunction> supplier) {
      // Arrange
      AggregationFunction function = supplier.get();
      function.epsilon(0.0);
      double[] scales = {1000.0, 0.001, 25.0};
      double[] shifts = {0.0, 0.0, 0.0};

      // Act
      double original = function.compute(VECTOR, WEIGHTS, ideal(IDEAL), nadir(NADIR));
      double scaled =
          function.compute(
              transformed(VECTOR, scales, shifts),
              WEIGHTS,
              ideal(transformed(IDEAL, scales, shifts)),
              nadir(transformed(NADIR, scales, shifts)));

      // Assert
      assertThat(scaled).isEqualTo(original, offset(EPSILON));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(FUNCTIONS)
    @DisplayName("given a scaling and a translation to negative values, then the value is the same")
    void givenAScalingAndATranslation_whenComputing_thenTheValueIsTheSame(
        String name, Supplier<AggregationFunction> supplier) {
      // Arrange: the objectives become negative
      AggregationFunction function = supplier.get();
      function.epsilon(0.0);
      double[] scales = {40.0, 0.02, 3.0};
      double[] shifts = {-90.0, -1.0, -500.0};

      // Act
      double original = function.compute(VECTOR, WEIGHTS, ideal(IDEAL), nadir(NADIR));
      double moved =
          function.compute(
              transformed(VECTOR, scales, shifts),
              WEIGHTS,
              ideal(transformed(IDEAL, scales, shifts)),
              nadir(transformed(NADIR, scales, shifts)));

      // Assert
      assertThat(moved).isEqualTo(original, offset(EPSILON));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(FUNCTIONS)
    @DisplayName("given an ideal point equal to the nadir point, then the value is finite")
    void givenEqualIdealAndNadirPoints_whenComputing_thenTheValueIsFinite(
        String name, Supplier<AggregationFunction> supplier) {
      // Arrange: with the default epsilon, an objective with a range of zero does not divide by it
      AggregationFunction function = supplier.get();
      double[] same = {0.5, 0.5, 0.5};

      // Act
      double value = function.compute(VECTOR, WEIGHTS, ideal(same), nadir(same));

      // Assert
      assertThat(Double.isFinite(value)).isTrue();
    }
  }
}
