package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.LinearDecreasingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.LinearIncreasingStrategy;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the linear strategies of the inertia weight")
class LinearInertiaWeightStrategiesTest {
  private static final double MINIMUM = 0.1;
  private static final double MAXIMUM = 0.9;
  private static final int ITERATIONS = 10;
  private static final int SWARM_SIZE = 3;

  /** The strategy is asked once per particle in each iteration. */
  private static double[] weightsOfTheIterations(InertiaWeightComputingStrategy strategy, int iterations) {
    double[] weights = new double[iterations];
    for (int iteration = 0; iteration < iterations; iteration++) {
      double first = strategy.compute();
      for (int particle = 1; particle < SWARM_SIZE; particle++) {
        assertThat(strategy.compute()).as("particle %d", particle).isEqualTo(first);
      }
      weights[iteration] = first;
    }
    return weights;
  }

  @Test
  @DisplayName("given a decreasing strategy, when asked for each iteration, then the weight goes from the maximum down to the minimum")
  void givenADecreasingStrategy_whenAskedForEachIteration_thenTheWeightDecreasesLinearly() {
    var strategy = new LinearDecreasingStrategy(MINIMUM, MAXIMUM, ITERATIONS, SWARM_SIZE);

    double[] weights = weightsOfTheIterations(strategy, ITERATIONS);

    assertThat(weights[0]).isEqualTo(MAXIMUM);
    for (int iteration = 0; iteration < ITERATIONS; iteration++) {
      assertThat(weights[iteration])
          .isCloseTo(MAXIMUM - (MAXIMUM - MINIMUM) * iteration / ITERATIONS, within(1e-12));
    }
    // The next iteration is the last one
    assertThat(strategy.compute()).isCloseTo(MINIMUM, within(1e-12));
  }

  @Test
  @DisplayName("given an increasing strategy, when asked for each iteration, then the weight goes from the minimum up to the maximum")
  void givenAnIncreasingStrategy_whenAskedForEachIteration_thenTheWeightIncreasesLinearly() {
    var strategy = new LinearIncreasingStrategy(MINIMUM, MAXIMUM, ITERATIONS, SWARM_SIZE);

    double[] weights = weightsOfTheIterations(strategy, ITERATIONS);

    assertThat(weights[0]).isEqualTo(MINIMUM);
    for (int iteration = 0; iteration < ITERATIONS; iteration++) {
      assertThat(weights[iteration])
          .isCloseTo(MINIMUM + (MAXIMUM - MINIMUM) * iteration / ITERATIONS, within(1e-12));
    }
    assertThat(strategy.compute()).isCloseTo(MAXIMUM, within(1e-12));
  }

  @Test
  @DisplayName("given more iterations than the maximum, when asked, then the weight stays at its last value")
  void givenMoreIterationsThanTheMaximum_whenAsked_thenTheWeightStaysAtItsLastValue() {
    var strategy = new LinearDecreasingStrategy(MINIMUM, MAXIMUM, ITERATIONS, SWARM_SIZE);

    double[] weights = weightsOfTheIterations(strategy, 3 * ITERATIONS);

    assertThat(weights[3 * ITERATIONS - 1]).isCloseTo(MINIMUM, within(1e-12));
    assertThat(weights[ITERATIONS + 5]).isCloseTo(MINIMUM, within(1e-12));
  }

  @Test
  @DisplayName("given arguments that are not valid, when created, then they are rejected")
  void givenInvalidArguments_whenCreated_thenTheyAreRejected() {
    assertThrows(InvalidConditionException.class, () -> new LinearDecreasingStrategy(0.9, 0.1, 10, 3));
    assertThrows(InvalidConditionException.class, () -> new LinearIncreasingStrategy(0.1, 0.9, 10, 0));
    assertThrows(InvalidConditionException.class, () -> new LinearIncreasingStrategy(0.1, 0.9, 0, 3));
    assertThrows(InvalidConditionException.class, () -> new LinearDecreasingStrategy(0.1, 0.9, 0, 3));
  }
}
