package org.uma.jmetal.problem.multiobjective.dcdtlz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the DC-DTLZ problems with 2 and 3 objectives ({@code
 * resources/referenceFronts/DC*_DTLZ*.2D.csv} and {@code .3D.csv}, written by {@code
 * scripts/dcdtlz_reference_fronts.py} of Evolver) against the problems. Every point of a front is
 * mapped back to its position variables, with the distance variables at 0.5: the problem must give
 * that point, and the solution must be feasible. The feasible samples of the Pareto set without
 * constraints must lie on the front, and none may dominate it.
 */
@DisplayName("Reference fronts of the DC-DTLZ problems with 2 and 3 objectives")
class DCDTLZReferenceFrontsTest {

  private static final List<String> PROBLEMS =
      List.of("DC1_DTLZ1", "DC1_DTLZ3", "DC2_DTLZ1", "DC2_DTLZ3", "DC3_DTLZ1", "DC3_DTLZ3");
  private static final int SAMPLES = 2000;
  private static final double TOLERANCE = 0.02;
  private static final double ON_FRONT = 1e-9;

  static Stream<Arguments> fronts() {
    return PROBLEMS.stream()
        .flatMap(name -> Stream.of(Arguments.of(name, 2), Arguments.of(name, 3)));
  }

  /** The problem with the given number of objectives and the default distance variables. */
  private static DCDTLZ problem(String name, int objectives) throws ReflectiveOperationException {
    int distanceVariables = name.endsWith("DTLZ1") ? 5 : 10;
    return (DCDTLZ)
        Class.forName("org.uma.jmetal.problem.multiobjective.dcdtlz." + name)
            .getConstructor(int.class, int.class)
            .newInstance(objectives + distanceVariables - 1, objectives);
  }

  private static List<double[]> readFront(String name, int objectives) throws IOException {
    List<double[]> front = new ArrayList<>();
    Path path = Path.of("../resources/referenceFronts", name + "." + objectives + "D.csv");
    for (String line : Files.readAllLines(path)) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /**
   * The position variables of a point of the front of DTLZ1 (sum f = 0.5) or of DTLZ3 (the unit
   * sphere). x_j depends on f_{m-j+1} and on the objectives before it: x_j = S / (S + f_{m-j+1})
   * with S their sum for DTLZ1, and x_j = atan2(f_{m-j+1}, |S|) 2 / pi with S their vector for
   * DTLZ3. When they are all zero, x_j does not change the point, and 0 satisfies the constraints.
   */
  private static double[] positionVariables(double[] f, boolean linear) {
    int m = f.length;
    double[] x = new double[m - 1];
    for (int j = 0; j < m - 1; j++) {
      double last = f[m - 1 - j];
      double rest = 0.0;
      for (int i = 0; i < m - 1 - j; i++) {
        rest += linear ? f[i] : f[i] * f[i];
      }
      if (linear) {
        x[j] = rest + last > 0 ? rest / (rest + last) : 0.0;
      } else {
        x[j] = Math.atan2(last, Math.sqrt(rest)) * 2.0 / Math.PI;
      }
    }
    return x;
  }

  private static DoubleSolution evaluated(DCDTLZ problem, double[] positionVariables) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < problem.numberOfVariables(); i++) {
      solution.variables().set(i, i < positionVariables.length ? positionVariables[i] : 0.5);
    }
    return problem.evaluate(solution);
  }

  /** Whether a dominates b: not worse in any objective, and better by more than a margin in one. */
  private static boolean dominatesWithin(double[] a, double[] b, double margin) {
    boolean better = false;
    for (int j = 0; j < a.length; j++) {
      if (a[j] > b[j]) {
        return false;
      }
      better |= a[j] < b[j] - margin;
    }
    return better;
  }

  @Nested
  @DisplayName("When the points of a front are evaluated")
  class FrontPoints {

    @ParameterizedTest(name = "{0} with {1} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.dcdtlz.DCDTLZReferenceFrontsTest#fronts")
    @DisplayName("given a front, then the problem gives every point, from a feasible solution")
    void givenAFront_whenItsPointsAreEvaluated_thenTheyAreFeasibleAndReproduced(
        String name, int objectives) throws IOException, ReflectiveOperationException {
      // Arrange
      DCDTLZ problem = problem(name, objectives);
      List<double[]> front = readFront(name, objectives);
      boolean linear = name.endsWith("DTLZ1");

      // Act & Assert
      assertTrue(front.size() > (objectives == 2 ? 900 : 9000), name + ": " + front.size());
      for (double[] point : front) {
        DoubleSolution solution = evaluated(problem, positionVariables(point, linear));
        for (int j = 0; j < objectives; j++) {
          assertEquals(point[j], solution.objectives()[j], ON_FRONT, Arrays.toString(point));
        }
        double tightest = Arrays.stream(solution.constraints()).min().orElseThrow();
        assertTrue(tightest >= -ON_FRONT, Arrays.toString(point) + ": " + tightest);
      }
    }
  }

  @Nested
  @DisplayName("When the Pareto set without constraints is sampled")
  class ParetoSetSamples {

    @ParameterizedTest(name = "{0} with {1} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.dcdtlz.DCDTLZReferenceFrontsTest#fronts")
    @DisplayName("given its feasible samples, then none dominates the front and all lie on it")
    void givenFeasibleSamples_whenEvaluated_thenTheyLieOnTheFront(String name, int objectives)
        throws IOException, ReflectiveOperationException {
      // Arrange
      DCDTLZ problem = problem(name, objectives);
      List<double[]> front = readFront(name, objectives);
      Random random = new Random(1);
      List<double[]> feasible = new ArrayList<>();

      // Act
      for (int s = 0; s < SAMPLES; s++) {
        double[] position = random.doubles(objectives - 1).toArray();
        DoubleSolution solution = evaluated(problem, position);
        if (ConstraintHandling.isFeasible(solution)) {
          feasible.add(solution.objectives().clone());
        }
      }

      // Assert
      assertFalse(feasible.isEmpty(), name);
      for (double[] sample : feasible) {
        double nearest = Double.MAX_VALUE;
        for (double[] point : front) {
          assertFalse(dominatesWithin(sample, point, 1e-12), Arrays.toString(sample));
          double distance = 0;
          for (int j = 0; j < objectives; j++) {
            distance += (sample[j] - point[j]) * (sample[j] - point[j]);
          }
          nearest = Math.min(nearest, Math.sqrt(distance));
        }
        assertTrue(nearest < TOLERANCE, Arrays.toString(sample) + ": " + nearest);
      }
    }
  }
}
