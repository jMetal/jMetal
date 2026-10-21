package org.uma.jmetal.problem.multiobjective.dascmop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the DAS-CMOP problems with their default difficulty triplet ({@code
 * resources/referenceFronts/DASCMOP*.csv}, written by {@code scripts/dascmop_reference_fronts.py} of
 * Evolver) against the problems. Solutions of the Pareto set are built with the distance function
 * at its smallest feasible value, d = 0.5, and random position variables; those that the problem
 * finds feasible must not dominate any point of the front, and must be covered by it (some point of
 * the front is no worse in every objective, within a tolerance).
 */
@DisplayName("Reference fronts of the DAS-CMOP problems")
class DASCMOPReferenceFrontsTest {

  private static final int VARIABLES = 30;
  private static final int SAMPLES = 3000;
  private static final double D = 0.5;

  static Stream<Integer> problems() {
    return IntStream.rangeClosed(1, 9).boxed();
  }

  private static List<double[]> readFront(int number) throws IOException {
    String file = "DASCMOP" + number + (number <= 6 ? ".csv" : ".3D.csv");
    List<double[]> front = new ArrayList<>();
    for (String line : Files.readAllLines(Path.of("../resources/referenceFronts", file))) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /** The y in [0, 0.025] with y^2 - cos(20 pi y) + 1 = target, by bisection. */
  private static double cosineTerm(double target) {
    double low = 0.0;
    double high = 0.025;
    for (int i = 0; i < 100; i++) {
      double middle = 0.5 * (low + high);
      if (middle * middle - Math.cos(20.0 * Math.PI * middle) + 1.0 < target) {
        low = middle;
      } else {
        high = middle;
      }
    }
    return 0.5 * (low + high);
  }

  /** x_j = target + delta or target - delta, whichever stays in [0, 1]. */
  private static double offset(double target, double delta) {
    return target + delta <= 1.0 ? target + delta : target - delta;
  }

  /**
   * A solution with the given position variables and distance g, or null if no solution has them
   * (the distance function of DAS-CMOP1-3 cannot be below (x1 - sin(pi x1 / 2))^2).
   */
  private static double[] solution(int number, double x1, double x2, double g) {
    double[] x = new double[VARIABLES];
    x[0] = x1;
    x[1] = x2;
    if (number <= 3) {
      // g = sum_{j=1..n} (x_j - s)^2 with s = sin(pi x1 / 2): the x1 term is fixed, the others share
      // the rest of g
      double s = Math.sin(0.5 * Math.PI * x1);
      double rest = g - (x1 - s) * (x1 - s);
      if (rest < 0) {
        return null;
      }
      double delta = Math.sqrt(rest / (VARIABLES - 1));
      for (int j = 1; j < VARIABLES; j++) {
        x[j] = offset(s, delta);
      }
    } else if (number <= 8) {
      // g = (n - k) + sum_{j>k} ((x_j - 0.5)^2 - cos(20 pi (x_j - 0.5))), each term 1 + that term
      int positionVariables = number <= 6 ? 1 : 2;
      double y = cosineTerm(g / (VARIABLES - positionVariables));
      for (int j = positionVariables; j < VARIABLES; j++) {
        x[j] = 0.5 + y;
      }
    } else {
      double delta = Math.sqrt(g / (VARIABLES - 2));
      for (int j = 3; j <= VARIABLES; j++) {
        double target = Math.cos(0.25 * j / VARIABLES * Math.PI * (x1 + x2));
        x[j - 1] = offset(target, delta);
      }
    }
    return x;
  }

  /**
   * A solution of the Pareto set of the problem with random position variables and g = d, plus
   * 1e-9 so that rounding does not leave it just below the band of the Type-II constraint.
   */
  private static double[] paretoSetSolution(int number, Random random) {
    return solution(number, random.nextDouble(), random.nextDouble(), D + 1e-9);
  }

  /** f1 - f2 without the distance, as a function of x1 (two-objective problems). */
  private static double shapeDifference(int number, double x1) {
    return switch ((number - 1) % 3) {
      case 0 -> x1 - (1.0 - x1 * x1);
      case 1 -> x1 - (1.0 - Math.sqrt(x1));
      default -> x1 - (1.0 - Math.sqrt(x1) + 0.5 * Math.abs(Math.sin(5.0 * Math.PI * x1)));
    };
  }

  /**
   * The candidate solutions behind a point of the front: with two objectives, every x1 whose shape
   * gives f1 - f2 (found on a grid and refined by bisection), with g = f1 - x1; with three, the
   * position variables and g that invert the shape (a simplex for DAS-CMOP7, the unit sphere for
   * DAS-CMOP8-9).
   */
  private static List<double[]> candidates(int number, double[] f) {
    List<double[]> candidates = new ArrayList<>();
    if (number <= 6) {
      double target = f[0] - f[1];
      int steps = 20000;
      for (int i = 0; i <= steps; i++) {
        // A root at a grid point, where the |sin(5 pi x1)| of DAS-CMOP3 and DAS-CMOP6 has a cusp and
        // the difference touches the target without crossing it
        double point = (double) i / steps;
        if (Math.abs(shapeDifference(number, point) - target) < 1e-9) {
          double[] x = solution(number, point, 0.0, f[0] - point);
          if (x != null) {
            candidates.add(x);
          }
        }
      }
      for (int i = 0; i < steps; i++) {
        double low = (double) i / steps;
        double high = (double) (i + 1) / steps;
        if ((shapeDifference(number, low) - target) * (shapeDifference(number, high) - target) > 0) {
          continue;
        }
        for (int k = 0; k < 60; k++) {
          double middle = 0.5 * (low + high);
          if ((shapeDifference(number, low) - target) * (shapeDifference(number, middle) - target)
              <= 0) {
            high = middle;
          } else {
            low = middle;
          }
        }
        double x1 = 0.5 * (low + high);
        double[] x = solution(number, x1, 0.0, f[0] - x1);
        if (x != null) {
          candidates.add(x);
        }
      }
    } else if (number == 7) {
      double g = (f[0] + f[1] + f[2] - 1.0) / 3.0;
      double x2 = 1.0 - (f[2] - g);
      double x1 = x2 > 0 ? (f[0] - g) / x2 : 0.0;
      candidates.add(solution(number, Math.min(1.0, Math.max(0.0, x1)), x2, g));
    } else {
      // |f - g 1| = 1: 3 g^2 - 2 g sum(f) + |f|^2 - 1 = 0, the root with f - g 1 >= 0
      double sum = f[0] + f[1] + f[2];
      double squares = f[0] * f[0] + f[1] * f[1] + f[2] * f[2];
      double g = (sum - Math.sqrt(Math.max(0.0, sum * sum - 3.0 * (squares - 1.0)))) / 3.0;
      double x1 = Math.asin(Math.min(1.0, Math.max(0.0, f[2] - g))) * 2.0 / Math.PI;
      double x2 = Math.atan2(Math.max(0.0, f[1] - g), Math.max(0.0, f[0] - g)) * 2.0 / Math.PI;
      candidates.add(solution(number, x1, x2, g));
    }
    return candidates;
  }

  @ParameterizedTest(name = "DAS-CMOP{0}")
  @MethodSource("problems")
  @DisplayName("given each point of the front, then a feasible solution gives it")
  void givenEachPointOfTheFront_whenItsSolutionIsEvaluated_thenItIsFeasible(int number)
      throws IOException, ReflectiveOperationException {
    // Arrange
    AbstractDASCMOP problem = problem(number);
    List<double[]> front = readFront(number);

    // Act & Assert: the front is written with 10 significant digits, hence the tolerances
    for (double[] point : front) {
      boolean found = false;
      for (double[] x : candidates(number, point)) {
        DoubleSolution solution = problem.createSolution();
        for (int i = 0; i < x.length; i++) {
          solution.variables().set(i, x[i]);
        }
        problem.evaluate(solution);
        boolean feasible = Arrays.stream(solution.constraints()).allMatch(c -> c >= -1e-6);
        boolean same =
            IntStream.range(0, point.length)
                .allMatch(j -> Math.abs(solution.objectives()[j] - point[j]) < 1e-6);
        found |= feasible && same;
      }
      assertTrue(found, Arrays.toString(point) + " is not given by a feasible solution");
    }
  }

  private static AbstractDASCMOP problem(int number) throws ReflectiveOperationException {
    return (AbstractDASCMOP)
        Class.forName("org.uma.jmetal.problem.multiobjective.dascmop.DASCMOP" + number)
            .getConstructor()
            .newInstance();
  }

  @ParameterizedTest(name = "DAS-CMOP{0}")
  @MethodSource("problems")
  @DisplayName(
      "given feasible solutions of the Pareto set, then none dominates the front, which covers them")
  void givenFeasibleParetoSetSolutions_whenEvaluated_thenTheFrontCoversThem(int number)
      throws IOException, ReflectiveOperationException {
    // Arrange
    AbstractDASCMOP problem = problem(number);
    List<double[]> front = readFront(number);
    double tolerance = number <= 6 ? 0.01 : 0.05;
    Random random = new Random(number);
    List<double[]> feasible = new ArrayList<>();

    // Act
    for (int sample = 0; sample < SAMPLES; sample++) {
      DoubleSolution solution = problem.createSolution();
      double[] x = paretoSetSolution(number, random);
      for (int i = 0; i < x.length; i++) {
        solution.variables().set(i, x[i]);
      }
      problem.evaluate(solution);
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
          dominates &= objectives[j] < point[j] - 1e-6;
          noWorse &= point[j] <= objectives[j] + tolerance;
        }
        assertFalse(dominates, Arrays.toString(objectives) + " dominates " + Arrays.toString(point));
        covered |= noWorse;
      }
      assertTrue(covered, Arrays.toString(objectives) + " is not covered by the front");
    }
  }
}
