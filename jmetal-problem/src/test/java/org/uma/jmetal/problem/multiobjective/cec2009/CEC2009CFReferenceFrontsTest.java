package org.uma.jmetal.problem.multiobjective.cec2009;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the CEC 2009 problems CF1-CF10 ({@code
 * resources/referenceFronts/CEC2009CF*.csv}, written by {@code cec2009cf_reference_fronts.py} of
 * Evolver from the Pareto fronts of the report CES-487) against the problems. Solutions of the
 * Pareto set are built from the report and the C code of the competition: the position variables
 * (x1, and x2 for CF8-CF10) and every distance term at its minimum, except y2 (CF4-CF7) and y4
 * (CF6, CF7), which the constraints move. Every point of a front must be the objective vector of
 * such a solution, feasible, and no point may dominate another; random solutions of the feasible
 * parts of the Pareto set must be feasible and lie on the front; and the feasible solutions around
 * the Pareto set must not dominate the front, which must cover them.
 *
 * <p>The points of CF1 and CF8, and the ends of the parts of the other fronts, lie on the boundary
 * of the feasible region, where the constraints of the solutions that give them are 0 up to
 * rounding: about 1e-15, and up to 1e-8 for the points of the files, which have 10 significant
 * digits.
 */
@DisplayName("Reference fronts of the CEC 2009 problems CF1-CF10")
class CEC2009CFReferenceFrontsTest {

  private static final int VARIABLES = 10;
  private static final int SAMPLES = 3000;
  private static final double ROUNDING = 1e-7;

  private static DoubleProblem problem(int number) throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.cec2009.CEC2009CF" + number)
            .getConstructor()
            .newInstance();
  }

  private static List<double[]> readFront(int number) throws IOException {
    List<double[]> front = new ArrayList<>();
    Path file = Path.of("../resources/referenceFronts", "CEC2009CF" + number + ".csv");
    for (String line : Files.readAllLines(file)) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /**
   * The solution of the Pareto set with the given position variables (x1, and x2 for CF8-CF10):
   * y_j = 0 for every j, except y2 = 0, 0.5 x1 - 0.25 or 1 in CF4 and CF5, and y2 = sqrt(max(0,
   * (x1 - 0.5)(1 - x1))) and y4 = sqrt(max(0, 0.25 sqrt(1 - x1) - 0.5 (1 - x1))) in CF6 and CF7,
   * the smallest values that the constraints allow.
   */
  static double[] paretoSetSolution(int number, int n, double... position) {
    double[] x = new double[n];
    double x1 = position[0];
    x[0] = x1;
    if (number >= 8) {
      x[1] = position[1];
      for (int j = 3; j <= n; j++) {
        x[j - 1] = 2.0 * x[1] * Math.sin(2.0 * Math.PI * x1 + j * Math.PI / n);
      }
      return x;
    }
    for (int j = 2; j <= n; j++) {
      double angle = 6.0 * Math.PI * x1 + j * Math.PI / n;
      boolean odd = j % 2 == 1;
      x[j - 1] =
          switch (number) {
            case 1 -> Math.pow(x1, 0.5 * (1.0 + 3.0 * (j - 2.0) / (n - 2.0)));
            case 2 -> odd ? Math.sin(angle) : Math.cos(angle);
            case 3, 4 -> Math.sin(angle);
            case 5, 6 -> 0.8 * x1 * (odd ? Math.cos(angle) : Math.sin(angle));
            default -> odd ? Math.cos(angle) : Math.sin(angle);
          };
    }
    if (number == 4 || number == 5) {
      x[1] += x1 <= 0.5 ? 0.0 : x1 <= 0.75 ? 0.5 * x1 - 0.25 : 1.0;
    }
    if (number == 6 || number == 7) {
      x[1] += Math.sqrt(Math.max(0.0, (x1 - 0.5) * (1.0 - x1)));
      x[3] += Math.sqrt(Math.max(0.0, 0.25 * Math.sqrt(1.0 - x1) - 0.5 * (1.0 - x1)));
    }
    return x;
  }

  /** The position variables of the solution of the Pareto set whose objectives are a point. */
  private static double[] positionOf(double[] point) {
    if (point.length == 2) {
      return new double[] {point[0]};
    }
    return new double[] {
      Math.atan2(point[2], Math.hypot(point[0], point[1])) * 2.0 / Math.PI,
      Math.atan2(point[1], point[0]) * 2.0 / Math.PI
    };
  }

  /**
   * Random position variables of the feasible parts of the Pareto set (the report): x1 = i / 20
   * (CF1); x1 in {0}, [1/16, 1/4] or [9/16, 1] (CF2); x1 in {0}, [1/2, sqrt(1/2)] or [sqrt(3/4), 1]
   * (CF3); x1 in [0, 1] (CF4-CF7); x1 in [0, 1] and x2 in {0, 1/3, 1/2, 2/3, 1} (CF8); x1 in [0,
   * 1] and x2 in [0, 1/3], [1/2, 2/3] or {1} (CF9, CF10).
   */
  private static double[] feasiblePosition(int number, Random random) {
    double u = random.nextDouble();
    return switch (number) {
      case 1 -> new double[] {random.nextInt(21) / 20.0};
      case 2 -> new double[] {pick(random, new double[][] {{0, 0}, {1 / 16.0, 0.25}, {0.5625, 1}})};
      case 3 ->
          new double[] {
            pick(random, new double[][] {{0, 0}, {0.5, Math.sqrt(0.5)}, {Math.sqrt(0.75), 1}})
          };
      case 8 -> new double[] {u, new double[] {0, 1 / 3.0, 0.5, 2 / 3.0, 1}[random.nextInt(5)]};
      case 9, 10 ->
          new double[] {u, pick(random, new double[][] {{0, 1 / 3.0}, {0.5, 2 / 3.0}, {1, 1}})};
      default -> new double[] {u};
    };
  }

  private static double pick(Random random, double[][] intervals) {
    double[] interval = intervals[random.nextInt(intervals.length)];
    return interval[0] + (interval[1] - interval[0]) * random.nextDouble();
  }

  private static DoubleSolution evaluated(DoubleProblem problem, double[] x) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }
    return problem.evaluate(solution);
  }

  /**
   * Whether a dominates b: not worse in any objective, and better by more than the margin in one.
   * "Not worse" is exact, as in the C-DTLZ test: the fronts have points with an objective at 0.
   */
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

  private static double distance(double[] a, double[] b) {
    double sum = 0.0;
    for (int j = 0; j < a.length; j++) {
      sum += (a[j] - b[j]) * (a[j] - b[j]);
    }
    return Math.sqrt(sum);
  }

  @Nested
  @DisplayName("When the points of a front are evaluated")
  class FrontPoints {

    @ParameterizedTest(name = "CF{0}")
    @CsvSource({
      "1, 21", "2, 1000", "3, 1000", "4, 999", "5, 999", "6, 999", "7, 999", "8, 5001", "9, 4975",
      "10, 4975"
    })
    @DisplayName("given a front, then every point is given by a feasible Pareto-set solution")
    void givenAFront_whenItsPointsAreEvaluated_thenTheyAreFeasibleSolutionsOfTheParetoSet(
        int number, int size) throws IOException, ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number);
      List<double[]> front = readFront(number);

      // Act & Assert
      assertEquals(size, front.size());
      for (double[] point : front) {
        DoubleSolution solution =
            evaluated(problem, paretoSetSolution(number, VARIABLES, positionOf(point)));
        for (int j = 0; j < point.length; j++) {
          assertEquals(point[j], solution.objectives()[j], 1e-8, () -> Arrays.toString(point));
        }
        for (double constraint : solution.constraints()) {
          assertTrue(constraint >= -ROUNDING, () -> Arrays.toString(point) + ": " + constraint);
        }
      }
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("given a front, then no point dominates another")
    void givenAFront_whenItsPointsAreCompared_thenNoneDominatesAnother(int number)
        throws IOException {
      // Arrange
      List<double[]> front = readFront(number);

      // Act & Assert
      for (double[] a : front) {
        for (double[] b : front) {
          assertFalse(
              dominatesWithin(a, b, 0.0),
              () -> Arrays.toString(a) + " dominates " + Arrays.toString(b));
        }
      }
    }
  }

  @Nested
  @DisplayName("When the Pareto set is sampled")
  class ParetoSetSamples {

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("given solutions of its feasible parts, then they are feasible and on the front")
    void givenSolutionsOfTheFeasibleParts_whenEvaluated_thenTheyLieOnTheFront(int number)
        throws IOException, ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number);
      List<double[]> front = readFront(number);
      double tolerance = number >= 8 ? 0.02 : 0.005;
      Random random = new Random(number);

      for (int sample = 0; sample < SAMPLES; sample++) {
        // Act
        DoubleSolution solution =
            evaluated(
                problem, paretoSetSolution(number, VARIABLES, feasiblePosition(number, random)));

        // Assert: the rounding error of the constraint of CF8-CF10 grows as 1 / (1 - f3^2) near
        // the top of the front
        double[] objectives = solution.objectives();
        double rounding = 1e-12;
        if (number >= 8) {
          rounding /= Math.min(1.0, 1.0 - objectives[2] * objectives[2]);
        }
        for (double constraint : solution.constraints()) {
          assertTrue(
              constraint >= -rounding, () -> Arrays.toString(objectives) + ": " + constraint);
        }
        double nearest =
            front.stream().mapToDouble(p -> distance(p, objectives)).min().orElseThrow();
        assertTrue(nearest < tolerance, () -> Arrays.toString(objectives) + ": " + nearest);
      }
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName(
        "given feasible solutions around it, then none dominates the front, which covers them")
    void givenFeasibleSolutionsAroundIt_whenEvaluated_thenTheFrontCoversThem(int number)
        throws IOException, ReflectiveOperationException {
      // Arrange: random position variables; half of the solutions move the other variables by a
      // scale from 1e-6 to 1, within the bounds
      DoubleProblem problem = problem(number);
      List<double[]> front = readFront(number);
      int positions = problem.numberOfObjectives() - 1;
      double tolerance = positions == 1 ? 0.01 : 0.03;
      Random random = new Random(100L + number);
      List<double[]> feasible = new ArrayList<>();

      // Act
      for (int sample = 0; sample < SAMPLES; sample++) {
        double[] position = new double[positions];
        Arrays.setAll(position, i -> random.nextDouble());
        double[] x = paretoSetSolution(number, VARIABLES, position);
        if (random.nextBoolean()) {
          double scale = Math.pow(10.0, -6.0 + 6.0 * random.nextDouble());
          for (int i = positions; i < VARIABLES; i++) {
            var bounds = problem.variableBounds().get(i);
            x[i] =
                Math.min(
                    bounds.getUpperBound(),
                    Math.max(bounds.getLowerBound(), x[i] + scale * random.nextGaussian()));
          }
        }
        DoubleSolution solution = evaluated(problem, x);
        if (ConstraintHandling.isFeasible(solution)) {
          feasible.add(solution.objectives().clone());
        }
      }

      // Assert
      assertFalse(feasible.isEmpty(), "no feasible sample");
      for (double[] objectives : feasible) {
        boolean covered = false;
        for (double[] point : front) {
          assertFalse(
              dominatesWithin(objectives, point, 1e-9),
              () -> Arrays.toString(objectives) + " dominates " + Arrays.toString(point));
          boolean noWorse = true;
          for (int j = 0; j < point.length; j++) {
            noWorse &= point[j] <= objectives[j] + tolerance;
          }
          covered |= noWorse;
        }
        assertTrue(covered, () -> Arrays.toString(objectives) + " is not covered by the front");
      }
    }
  }
}
