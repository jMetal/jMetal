package org.uma.jmetal.problem.multiobjective.lircmop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the LIR-CMOP problems ({@code resources/referenceFronts/LIRCMOP*.csv},
 * written by {@code scripts/lircmop_reference_fronts.py} of Evolver) against the problems. Every
 * point of a front is mapped back to a decision vector, which jMetal must evaluate to that point
 * and find feasible. Feasible solutions sampled near the Pareto set must not dominate any point of
 * the front, and must be covered by it (some point of the front is no worse in every objective,
 * within a tolerance).
 *
 * <p>The objectives are built from the position variable x1 and two distances: g1, the sum of
 * (x_j - sin(c_j x1))^2 over the odd 1-based indices j &gt;= 3, and g2, the sum of (x_j - cos(c_j
 * x1))^2 over the even ones, with c_j = pi / 2 in LIRCMOP1-4 and c_j = 0.5 j / n pi in
 * LIRCMOP5-12. LIRCMOP13-14 have the distance t = 1.7057 + sum 10 (x_j - 0.5)^2 for j &gt;= 3.
 */
@DisplayName("Reference fronts of the LIR-CMOP problems")
class LIRCMOPReferenceFrontsTest {

  private static final int VARIABLES = 30;
  private static final int SAMPLES = 20000;
  private static final double A = 1.7057;
  private static final double SHIFT = 0.7057;
  private static final Set<Integer> SQUARE_ROOT_SHAPE = Set.of(2, 4, 5, 7, 10, 11);

  static Stream<Integer> problems() {
    return IntStream.rangeClosed(1, 14).boxed();
  }

  private static DoubleProblem problem(int number) throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP" + number)
            .getConstructor()
            .newInstance();
  }

  private static List<double[]> readFront(int number) throws IOException {
    List<double[]> front = new ArrayList<>();
    for (String line :
        Files.readAllLines(Path.of("../resources/referenceFronts", "LIRCMOP" + number + ".csv"))) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /** The shape h(x1) of the second objective: 1 - sqrt(x1) or 1 - x1^2. */
  private static double shape(int number, double x1) {
    return SQUARE_ROOT_SHAPE.contains(number) ? 1.0 - Math.sqrt(x1) : 1.0 - x1 * x1;
  }

  /** The x1 in [0, 1] with h(x1) = y, for y in [0, 1]. */
  private static double inverseShape(int number, double y) {
    return SQUARE_ROOT_SHAPE.contains(number) ? (1.0 - y) * (1.0 - y) : Math.sqrt(1.0 - y);
  }

  /** x_j = target + delta or target - delta, whichever stays in [0, 1]. */
  private static double offset(double target, double delta) {
    return target + delta <= 1.0 ? target + delta : target - delta;
  }

  /** A solution of LIRCMOP1-12 with the position variable x1 and the distances g1 and g2. */
  private static double[] solution(int number, double x1, double g1, double g2) {
    double[] x = new double[VARIABLES];
    x[0] = x1;
    int odd = (VARIABLES - 1) / 2;
    int even = VARIABLES / 2;
    double delta1 = Math.sqrt(Math.max(0.0, g1) / odd);
    double delta2 = Math.sqrt(Math.max(0.0, g2) / even);
    for (int j = 2; j <= VARIABLES; j++) {
      double angle = (number <= 4 ? 0.5 : 0.5 * j / VARIABLES) * Math.PI * x1;
      x[j - 1] = j % 2 == 1 ? offset(Math.sin(angle), delta1) : offset(Math.cos(angle), delta2);
    }
    return x;
  }

  /** A solution of LIRCMOP13-14 with the angles x1 and x2 and the distance t. */
  private static double[] sphereSolution(double x1, double x2, double t) {
    double[] x = new double[VARIABLES];
    x[0] = x1;
    x[1] = x2;
    double delta = Math.sqrt(Math.max(0.0, t - A) / (10.0 * (VARIABLES - 2)));
    for (int j = 2; j < VARIABLES; j++) {
      x[j] = 0.5 + delta;
    }
    return x;
  }

  /**
   * A decision vector whose objectives are the point f. LIRCMOP1-4: x1 = f1 - 0.5 with g1 = g2 =
   * 0.5. LIRCMOP5-8: f = (x1 + 10 g1, h(x1) + 10 g2) + 0.7057, with the largest x1 that keeps g1
   * &gt;= 0, which needs the smallest g2. LIRCMOP9-12: f = 1.7057 (x1 (10 g1 + 1), h(x1) (10 g2 +
   * 1)), likewise, but x1 &lt; 1 when f2 &gt; 0, since h(1) = 0. LIRCMOP13-14: the angles of f
   * with the distance |f|.
   */
  private static double[] decisionVector(int number, double[] f) {
    if (number <= 4) {
      return solution(number, f[0] - 0.5, 0.5, 0.5);
    }
    if (number <= 8) {
      double x1 = Math.min(1.0, Math.max(0.0, f[0] - SHIFT));
      return solution(
          number, x1, (f[0] - SHIFT - x1) / 10.0, (f[1] - SHIFT - shape(number, x1)) / 10.0);
    }
    if (number <= 12) {
      double x1 = Math.min(1.0, f[0] / A);
      if (x1 == 1.0 && f[1] > 0) {
        x1 = 0.5 * (inverseShape(number, Math.min(1.0, f[1] / A)) + 1.0);
      }
      double g1 = x1 > 0 ? (f[0] / (A * x1) - 1.0) / 10.0 : 0.0;
      double h = shape(number, x1);
      double g2 = h > 0 ? (f[1] / (A * h) - 1.0) / 10.0 : 0.0;
      return solution(number, x1, g1, g2);
    }
    double radius = Math.sqrt(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
    double x1 = Math.asin(Math.min(1.0, f[2] / radius)) * 2.0 / Math.PI;
    double x2 = Math.atan2(f[1], f[0]) * 2.0 / Math.PI;
    return sphereSolution(x1, Math.min(1.0, Math.max(0.0, x2)), radius);
  }

  /** A random solution near the Pareto set: small distances beyond their smallest feasible value. */
  private static double[] nearParetoSetSolution(int number, Random random) {
    if (number <= 4) {
      return solution(
          number,
          random.nextDouble(),
          0.5 + 0.01 * random.nextDouble(),
          0.5 + 0.01 * random.nextDouble());
    }
    if (number <= 12) {
      double scale = number <= 8 ? 0.2 : 0.15;
      double u1 = random.nextDouble();
      double u2 = random.nextDouble();
      return solution(number, random.nextDouble(), scale * u1 * u1, scale * u2 * u2);
    }
    return sphereSolution(random.nextDouble(), random.nextDouble(), A + 0.3 * random.nextDouble());
  }

  private static DoubleSolution evaluated(DoubleProblem problem, double[] x) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }
    return problem.evaluate(solution);
  }

  @ParameterizedTest(name = "LIRCMOP{0}")
  @MethodSource("problems")
  @DisplayName("given each point of the front, then a feasible solution gives it")
  void givenEachPointOfTheFront_whenItsSolutionIsEvaluated_thenItIsFeasible(int number)
      throws IOException, ReflectiveOperationException {
    // Arrange
    DoubleProblem problem = problem(number);
    List<double[]> front = readFront(number);

    // Act & Assert
    assertFalse(front.isEmpty());
    for (double[] point : front) {
      double[] x = decisionVector(number, point);
      assertTrue(
          Arrays.stream(x).allMatch(value -> value >= 0.0 && value <= 1.0),
          Arrays.toString(point) + " needs variables out of bounds");
      DoubleSolution solution = evaluated(problem, x);
      for (int j = 0; j < point.length; j++) {
        assertTrue(
            Math.abs(solution.objectives()[j] - point[j]) < 1e-9,
            Arrays.toString(point) + " is evaluated as " + Arrays.toString(solution.objectives()));
      }
      assertTrue(
          Arrays.stream(solution.constraints()).allMatch(c -> c >= -1e-9),
          Arrays.toString(point) + " violates " + Arrays.toString(solution.constraints()));
    }
  }

  @ParameterizedTest(name = "LIRCMOP{0}")
  @MethodSource("problems")
  @DisplayName(
      "given feasible solutions near the Pareto set, then none dominates the front, which covers"
          + " them")
  void givenFeasibleSolutionsNearTheParetoSet_whenEvaluated_thenTheFrontCoversThem(int number)
      throws IOException, ReflectiveOperationException {
    // Arrange
    DoubleProblem problem = problem(number);
    List<double[]> front = readFront(number);
    double tolerance = number <= 12 ? 0.01 : 0.05;
    Random random = new Random(number);
    List<double[]> feasible = new ArrayList<>();

    // Act
    for (int sample = 0; sample < SAMPLES; sample++) {
      DoubleSolution solution = evaluated(problem, nearParetoSetSolution(number, random));
      if (ConstraintHandling.isFeasible(solution)) {
        feasible.add(solution.objectives().clone());
      }
    }

    // Assert
    assertFalse(feasible.isEmpty());
    for (double[] objectives : feasible) {
      boolean covered = false;
      for (double[] point : front) {
        boolean dominates = true;
        boolean noWorse = true;
        for (int j = 0; j < objectives.length; j++) {
          dominates &= objectives[j] < point[j] - 1e-9;
          noWorse &= point[j] <= objectives[j] + tolerance;
        }
        assertFalse(dominates, Arrays.toString(objectives) + " dominates " + Arrays.toString(point));
        covered |= noWorse;
      }
      assertTrue(covered, Arrays.toString(objectives) + " is not covered by the front");
    }
  }
}
