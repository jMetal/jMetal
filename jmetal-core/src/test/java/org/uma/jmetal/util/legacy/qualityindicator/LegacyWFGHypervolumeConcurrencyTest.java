package org.uma.jmetal.util.legacy.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

/**
 * The legacy WFGHypervolume kept its working state in static fields, so that the computations of
 * different instances in different threads corrupted each other (see the test of the class of the
 * qualityindicator package). Every thread must get the value that a single thread gets.
 */
@DisplayName("Concurrency of the legacy WFGHypervolume")
class LegacyWFGHypervolumeConcurrencyTest {
  private static final int THREADS = 8;
  private static final int ITERATIONS = 100;

  private static List<DoubleSolution> front(int objectives, double... values) {
    List<DoubleSolution> front = new ArrayList<>();
    for (int i = 0; i < values.length / objectives; i++) {
      DoubleSolution solution = new FakeDoubleProblem(2, objectives, 0).createSolution();
      System.arraycopy(values, i * objectives, solution.objectives(), 0, objectives);
      front.add(solution);
    }
    return front;
  }

  @Test
  @DisplayName("given two fronts with different numbers of objectives, when evaluated concurrently, then each gets its own value")
  void givenTwoFronts_whenEvaluatedConcurrently_thenEachGetsItsOwnValue() throws Exception {
    // Arrange
    // The legacy indicator inverts the front, which must be in [0, 1]
    double[] referenceA = {1.0, 1.0, 1.0};
    List<DoubleSolution> frontA = front(3, 0.1, 0.2, 0.3, 0.3, 0.1, 0.2, 0.2, 0.3, 0.1);
    double[] referenceB = {1.0, 1.0, 1.0, 1.0};
    List<DoubleSolution> frontB = front(4, 0.2, 0.3, 0.4, 0.5, 0.5, 0.4, 0.3, 0.2);
    double expectedA = new WFGHypervolume<DoubleSolution>(referenceA).evaluate(new ArrayList<>(frontA));
    double expectedB = new WFGHypervolume<DoubleSolution>(referenceB).evaluate(new ArrayList<>(frontB));
    assertThat(expectedA).isPositive();
    assertThat(expectedB).isPositive();
    assertThat(expectedA).isNotEqualTo(expectedB);

    ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    List<Callable<double[]>> tasks = new ArrayList<>();
    for (int i = 0; i < THREADS; i++) {
      boolean useA = i % 2 == 0;
      tasks.add(
          () -> {
            double[] results = new double[ITERATIONS];
            for (int iteration = 0; iteration < ITERATIONS; iteration++) {
              var hypervolume = new WFGHypervolume<DoubleSolution>(useA ? referenceA : referenceB);
              results[iteration] = hypervolume.evaluate(new ArrayList<>(useA ? frontA : frontB));
            }
            return results;
          });
    }

    // Act
    List<Future<double[]>> futures = executor.invokeAll(tasks);
    executor.shutdown();

    // Assert
    for (int i = 0; i < futures.size(); i++) {
      double expected = i % 2 == 0 ? expectedA : expectedB;
      for (double result : futures.get(i).get()) {
        assertThat(result).isEqualTo(expected);
      }
    }
  }
}
