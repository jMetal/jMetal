package org.uma.jmetal.problem.multiobjective.mw;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the MW problems ({@code resources/referenceFronts/MW*.csv}) against the
 * problems. Solutions of the Pareto set are built with random position variables and the distance
 * variables at the minimum of the distance function (g = 1), or moved away from it; those that the
 * problem finds feasible must not dominate any point of the front, and must be covered by it (some
 * point of the front is no worse in every objective, within a tolerance).
 */
@DisplayName("Reference fronts of the MW problems")
class MWReferenceFrontsTest {

  private static final int VARIABLES = 15;
  private static final int SAMPLES = 3000;

  private static List<double[]> readFront(String file) throws IOException {
    List<double[]> front = new ArrayList<>();
    for (String line : Files.readAllLines(Path.of("../resources/referenceFronts", file))) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /**
   * A solution of the Pareto set: random position variables in [0, upper] and the distance
   * variables at the minimum of g1, g2 or g3 (Eqs. 12-14 of Ma and Wang, 2019).
   */
  private static double[] paretoSetSolution(
      String distance, int objectives, double upper, Random random) {
    int n = VARIABLES;
    int m = objectives;
    double[] x = new double[n];
    for (int i = 0; i < m - 1; i++) {
      x[i] = upper * random.nextDouble();
    }
    for (int i = m; i <= n; i++) {
      x[i - 1] =
          switch (distance) {
            case "g1" -> Math.pow(0.5 + (i - 1) / (2.0 * n), 1.0 / (n - m));
            case "g2" -> (i - 1) / (double) n;
            default -> 1.0 - (x[i - 2] - 0.5) * (x[i - 2] - 0.5);
          };
    }
    // Half of the solutions move the distance variables away from the minimum, by a scale from
    // 1e-3 to 0.2, so that g > 1: on MW5, MW9, MW11 and MW12 the front is on the boundary of the
    // feasible region, where g = 1 is infeasible
    if (random.nextBoolean()) {
      double scale = Math.pow(10.0, -3.0 + 2.3 * random.nextDouble());
      for (int i = m - 1; i < n; i++) {
        x[i] = Math.min(upper, Math.max(0.0, x[i] + scale * random.nextGaussian()));
      }
    }
    return x;
  }

  private static DoubleProblem problem(int number, int objectives)
      throws ReflectiveOperationException {
    Class<?> problemClass = Class.forName("org.uma.jmetal.problem.multiobjective.mw.MW" + number);
    boolean scalable = number == 4 || number == 8 || number == 14;
    return (DoubleProblem)
        (scalable
            ? problemClass.getConstructor(int.class, int.class).newInstance(VARIABLES, objectives)
            : problemClass.getConstructor(int.class).newInstance(VARIABLES));
  }

  @ParameterizedTest(name = "{0} ({1} objectives)")
  @CsvSource({
    "1, 2, g1, 1.0, MW1.csv",
    "2, 2, g2, 1.0, MW2.csv",
    "3, 2, g3, 1.0, MW3.csv",
    "4, 2, g1, 1.0, MW4.2D.csv",
    "4, 3, g1, 1.0, MW4.3D.csv",
    "5, 2, g1, 1.0, MW5.csv",
    "6, 2, g2, 1.1, MW6.csv",
    "7, 2, g3, 1.0, MW7.csv",
    "8, 2, g2, 1.0, MW8.2D.csv",
    "8, 3, g2, 1.0, MW8.3D.csv",
    "9, 2, g1, 1.0, MW9.csv",
    "10, 2, g2, 1.0, MW10.csv",
    "11, 2, g3, 1.4142135623730951, MW11.csv",
    "12, 2, g1, 1.0, MW12.csv",
    "13, 2, g2, 1.5, MW13.csv",
    "14, 2, g3, 1.5, MW14.2D.csv",
    "14, 3, g3, 1.5, MW14.3D.csv"
  })
  @DisplayName(
      "given feasible solutions of the Pareto set, then none dominates the front, which covers them")
  void givenFeasibleParetoSetSolutions_whenEvaluated_thenTheFrontCoversThem(
      int number, int objectives, String distance, double upper, String file)
      throws IOException, ReflectiveOperationException {
    // Arrange
    DoubleProblem problem = problem(number, objectives);
    List<double[]> front = readFront(file);
    double tolerance = objectives == 2 ? 0.01 : 0.05;
    Random random = new Random(number * 10L + objectives);
    List<double[]> feasible = new ArrayList<>();

    // Act
    for (int sample = 0; sample < SAMPLES; sample++) {
      DoubleSolution solution = problem.createSolution();
      double[] x = paretoSetSolution(distance, objectives, upper, random);
      for (int i = 0; i < x.length; i++) {
        solution.variables().set(i, x[i]);
      }
      problem.evaluate(solution);
      if (ConstraintHandling.isFeasible(solution)) {
        feasible.add(solution.objectives().clone());
      }
    }

    // Assert
    assertFalse(feasible.isEmpty(), "no feasible sample");
    for (double[] objectivesOfSample : feasible) {
      boolean covered = false;
      for (double[] point : front) {
        boolean dominates = true;
        boolean noWorse = true;
        for (int j = 0; j < objectivesOfSample.length; j++) {
          dominates &= objectivesOfSample[j] < point[j] - 1e-6;
          noWorse &= point[j] <= objectivesOfSample[j] + tolerance;
        }
        assertFalse(
            dominates,
            Arrays.toString(objectivesOfSample) + " dominates " + Arrays.toString(point));
        covered |= noWorse;
      }
      assertTrue(covered, Arrays.toString(objectivesOfSample) + " is not covered by the front");
    }
  }
}
