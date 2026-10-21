package org.uma.jmetal.problem.multiobjective.cdtlz;

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
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the C-DTLZ problems with 3 objectives ({@code
 * resources/referenceFronts/*.3D.csv}, written by {@code scripts/cdtlz_reference_fronts.py} of
 * Evolver) against the problems. The constraints of these problems depend on the objectives only,
 * so every point of a front can be checked: it must be feasible, and on C3-DTLZ1 and C3-DTLZ4,
 * whose fronts lie on the constraints, on one of them. The fronts of the other problems are on
 * their Pareto set without constraints (distance variables at 0.5): the feasible samples of that
 * set must lie on the front, and none may dominate it.
 */
@DisplayName("Reference fronts of the C-DTLZ problems with 3 objectives")
class CDTLZReferenceFrontsTest {

  private static final int OBJECTIVES = 3;
  private static final int SAMPLES = 2000;
  private static final double TOLERANCE = 0.02;
  private static final double ON_CONSTRAINT = 1e-6;

  private interface ConstraintEvaluator {
    void evaluateConstraints(DoubleSolution solution);
  }

  static Stream<Arguments> fronts() {
    var c1dtlz1 = new C1_DTLZ1();
    var c1dtlz3 = new C1_DTLZ3();
    var c2dtlz2 = new C2_DTLZ2();
    var convex = new ConvexC2_DTLZ2();
    var c3dtlz1 = new C3_DTLZ1();
    var c3dtlz4 = new C3_DTLZ4();
    return Stream.of(
        Arguments.of("C1_DTLZ1", c1dtlz1, (ConstraintEvaluator) c1dtlz1::evaluateConstraints, false),
        Arguments.of("C1_DTLZ3", c1dtlz3, (ConstraintEvaluator) c1dtlz3::evaluateConstraints, false),
        Arguments.of("C2_DTLZ2", c2dtlz2, (ConstraintEvaluator) c2dtlz2::evaluateConstraints, false),
        Arguments.of(
            "ConvexC2_DTLZ2", convex, (ConstraintEvaluator) convex::evaluateConstraints, false),
        Arguments.of("C3_DTLZ1", c3dtlz1, (ConstraintEvaluator) c3dtlz1::evaluateConstraints, true),
        Arguments.of("C3_DTLZ4", c3dtlz4, (ConstraintEvaluator) c3dtlz4::evaluateConstraints, true));
  }

  static Stream<Arguments> paretoSets() {
    return Stream.of(
        Arguments.of("C1_DTLZ1", new C1_DTLZ1()),
        Arguments.of("C1_DTLZ3", new C1_DTLZ3()),
        Arguments.of("C2_DTLZ2", new C2_DTLZ2()),
        Arguments.of("ConvexC2_DTLZ2", new ConvexC2_DTLZ2()));
  }

  private static List<double[]> readFront(String name) throws IOException {
    List<double[]> front = new ArrayList<>();
    for (String line : Files.readAllLines(Path.of("../resources/referenceFronts", name + ".3D.csv"))) {
      if (!line.isBlank()) {
        front.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return front;
  }

  /**
   * Whether a dominates b: not worse in any objective, and better by more than the margin in one.
   * "Not worse" is exact: near the axes the objectives of convex C2-DTLZ2 are as small as 1e-13,
   * so any absolute margin there would let two points of the front dominate each other.
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

  @Nested
  @DisplayName("When the points of a front are evaluated")
  class FrontPoints {

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.cdtlz.CDTLZReferenceFrontsTest#fronts")
    @DisplayName("given a front, then every point is feasible, and on a constraint for C3")
    void givenAFront_whenItsPointsAreEvaluated_thenTheyAreFeasible(
        String name, DoubleProblem problem, ConstraintEvaluator evaluator, boolean onConstraints)
        throws IOException {
      // Arrange
      List<double[]> front = readFront(name);

      // Act & Assert
      assertTrue(front.size() > 1000, name + ": " + front.size() + " points");
      for (double[] point : front) {
        DoubleSolution solution = problem.createSolution();
        System.arraycopy(point, 0, solution.objectives(), 0, OBJECTIVES);
        evaluator.evaluateConstraints(solution);
        double tightest = Arrays.stream(solution.constraints()).min().orElseThrow();
        assertTrue(tightest >= -ON_CONSTRAINT, name + " " + Arrays.toString(point));
        if (onConstraints) {
          assertEquals(0.0, tightest, ON_CONSTRAINT, name + " " + Arrays.toString(point));
        }
      }
    }
  }

  @Nested
  @DisplayName("When the Pareto set without constraints is sampled")
  class ParetoSetSamples {

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.cdtlz.CDTLZReferenceFrontsTest#paretoSets")
    @DisplayName("given its feasible samples, then none dominates the front and all lie on it")
    void givenFeasibleSamples_whenEvaluated_thenTheyLieOnTheFront(String name, DoubleProblem problem)
        throws IOException {
      // Arrange
      List<double[]> front = readFront(name);
      Random random = new Random(1);
      BiConsumer<DoubleSolution, Random> paretoSet =
          (solution, r) -> {
            for (int i = 0; i < solution.variables().size(); i++) {
              solution.variables().set(i, i < OBJECTIVES - 1 ? r.nextDouble() : 0.5);
            }
          };
      List<double[]> feasible = new ArrayList<>();

      // Act
      for (int s = 0; s < SAMPLES; s++) {
        DoubleSolution solution = problem.createSolution();
        paretoSet.accept(solution, random);
        problem.evaluate(solution);
        if (ConstraintHandling.isFeasible(solution)) {
          feasible.add(solution.objectives().clone());
        }
      }

      // Assert
      assertFalse(feasible.isEmpty(), name);
      for (double[] sample : feasible) {
        double nearest = Double.MAX_VALUE;
        for (double[] point : front) {
          assertFalse(dominatesWithin(sample, point, 1e-12), name + " " + Arrays.toString(sample));
          double distance = 0;
          for (int j = 0; j < OBJECTIVES; j++) {
            distance += (sample[j] - point[j]) * (sample[j] - point[j]);
          }
          nearest = Math.min(nearest, Math.sqrt(distance));
        }
        assertTrue(nearest < TOLERANCE, name + " " + Arrays.toString(sample) + ": " + nearest);
      }
    }
  }
}
