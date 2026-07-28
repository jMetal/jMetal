package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

/**
 * WFGHypervolume used to keep its working state (n, fs, safe, fr) in {@code static} fields -- a
 * direct port of the reference C implementation's global variables. That corrupted every
 * concurrently-running computation in the same JVM, even across entirely separate
 * WFGHypervolume instances, because static fields are shared at the class level regardless of how
 * many instances exist. This test drives many concurrent computations on two distinct,
 * hand-verifiable fronts and asserts each thread gets its own front's correct value -- it fails
 * with ArrayIndexOutOfBoundsException (or a corrupted result) against the pre-fix static-state
 * implementation.
 */
@DisplayName("WFGHypervolume concurrency tests")
class WFGHypervolumeConcurrencyTest {

  private static final double EPSILON = 0.00000001;
  private static final int NUMBER_OF_THREADS = 8;
  private static final int ITERATIONS_PER_THREAD = 50;

  @Nested
  @DisplayName("When computing hypervolume concurrently on different fronts")
  class ConcurrentComputationTests {

    @Test
    @DisplayName(
        "Given two distinct fronts with known hypervolumes, when computed concurrently many"
            + " times across multiple threads, then every result matches its own front's expected"
            + " value")
    void givenTwoDistinctFrontsWithKnownHypervolumes_whenComputedConcurrentlyAcrossManyThreads_thenEveryResultMatchesItsOwnFrontsExpectedValue()
        throws Exception {
      // Arrange: two fronts of different dimensionality (M=3 and M=4) with hand-verifiable
      // single-point hypervolumes -- HV of a single point p against reference r is the product
      // of (r[i] - p[i]) over all dimensions i.
      double[] referencePointA = {6.0, 6.0, 6.0};
      double[][] frontA = {{1.0, 2.0, 3.0}};
      double expectedHypervolumeA = 5.0 * 4.0 * 3.0;

      double[] referencePointB = {10.0, 10.0, 10.0, 10.0};
      double[][] frontB = {{2.0, 3.0, 4.0, 5.0}};
      double expectedHypervolumeB = 8.0 * 7.0 * 6.0 * 5.0;

      ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
      List<Callable<double[]>> tasks = new ArrayList<>();
      for (int i = 0; i < NUMBER_OF_THREADS; i++) {
        boolean useFrontA = i % 2 == 0;
        tasks.add(
            () -> {
              double[] results = new double[ITERATIONS_PER_THREAD];
              for (int iteration = 0; iteration < ITERATIONS_PER_THREAD; iteration++) {
                var hypervolume =
                    new WFGHypervolume(useFrontA ? referencePointA : referencePointB);
                results[iteration] = hypervolume.compute(useFrontA ? frontA : frontB);
              }
              return results;
            });
      }

      // Act
      List<Future<double[]>> futures = executor.invokeAll(tasks);
      executor.shutdown();

      // Assert
      for (int i = 0; i < futures.size(); i++) {
        boolean usedFrontA = i % 2 == 0;
        double expected = usedFrontA ? expectedHypervolumeA : expectedHypervolumeB;
        double[] results = futures.get(i).get();
        for (double result : results) {
          assertThat(result).isCloseTo(expected, org.assertj.core.data.Offset.offset(EPSILON));
        }
      }
    }
  }
}
