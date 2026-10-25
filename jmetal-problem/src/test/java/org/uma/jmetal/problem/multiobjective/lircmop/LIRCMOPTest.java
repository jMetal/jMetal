package org.uma.jmetal.problem.multiobjective.lircmop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for the LIR-CMOP problems")
class LIRCMOPTest {

  private static DoubleProblem problem(int id) throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP" + id)
            .getConstructor()
            .newInstance();
  }

  private static DoubleProblem problem(int id, int variables) throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP" + id)
            .getConstructor(int.class)
            .newInstance(variables);
  }

  /**
   * Rows of {@code lircmop/reference-values.csv}, computed with lircmop_reference_fronts.py of
   * Evolver, an independent Python transcription of Table 6 of the paper: problem, number of
   * variables n, the n variables, the objectives and the constraints.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                LIRCMOPTest.class.getResourceAsStream("/lircmop/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      List<double[]> rows =
          reader
              .lines()
              .filter(line -> !line.startsWith("#") && !line.isBlank())
              .map(line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
              .toList();
      return rows.stream().map(row -> Arguments.of((int) row[0], (int) row[1], row));
    }
  }

  private static void assertClose(double expected, double actual, String message) {
    assertEquals(expected, actual, 1e-10 * Math.max(1.0, Math.abs(expected)), message);
  }

  /**
   * A solution of the Pareto set of LIRCMOP5 and LIRCMOP6 (g1 = g2 = 0) with x1 = 0.5: the variable
   * x_j, with the 1-based index j, is sin(0.5 j / n pi x1) for odd j and cos(0.5 j / n pi x1) for
   * even j.
   */
  private static DoubleSolution paretoOptimalSolution(DoubleProblem problem) {
    DoubleSolution solution = problem.createSolution();
    int n = problem.numberOfVariables();
    solution.variables().set(0, 0.5);
    for (int i = 1; i < n; i++) {
      double angle = 0.5 * (i + 1) / n * Math.PI * 0.5;
      solution.variables().set(i, i % 2 == 0 ? Math.sin(angle) : Math.cos(angle));
    }
    return solution;
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class EvaluationTests {

    @ParameterizedTest(name = "LIRCMOP{0}")
    @CsvSource({
      "1, 2, 2", "2, 2, 2", "3, 2, 3", "4, 2, 3", "5, 2, 2", "6, 2, 2", "7, 2, 3", "8, 2, 3",
      "9, 2, 2", "10, 2, 2", "11, 2, 2", "12, 2, 2", "13, 3, 2", "14, 3, 3"
    })
    @DisplayName("given each problem, when a random solution is evaluated, then it has all its values")
    void givenEachProblem_whenEvaluated_thenTheSolutionHasAllItsValues(
        int id, int objectives, int constraints) throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(id);
      DoubleSolution solution = problem.createSolution();

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(constraints, problem.numberOfConstraints());
      assertEquals(constraints, solution.constraints().length);
    }

    @ParameterizedTest(name = "LIRCMOP{0} with {1} variables")
    @MethodSource("org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOPTest#referenceValues")
    @DisplayName("given a solution, then its objectives and constraints are the reference ones")
    void givenASolution_whenEvaluated_thenItsValuesAreTheReferenceOnes(
        int id, int variables, double[] row) throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(id, variables);
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < variables; i++) {
        solution.variables().set(i, row[2 + i]);
      }

      // Act
      problem.evaluate(solution);

      // Assert
      int objectives = problem.numberOfObjectives();
      int constraints = problem.numberOfConstraints();
      double[] expected = Arrays.copyOfRange(row, 2 + variables, row.length);
      assertEquals(variables, problem.numberOfVariables());
      assertEquals(objectives + constraints, expected.length);
      for (int i = 0; i < objectives; i++) {
        assertClose(expected[i], solution.objectives()[i], "objective " + i);
      }
      for (int i = 0; i < constraints; i++) {
        assertClose(expected[objectives + i], solution.constraints()[i], "constraint " + i);
      }
    }
  }

  @Nested
  @DisplayName("When evaluating a Pareto-optimal solution of LIRCMOP5 and LIRCMOP6")
  class ParetoOptimalTests {

    @Test
    @DisplayName("given LIRCMOP5, when evaluated, then f2 = 1 - sqrt(x1) + 0.7057")
    void givenLIRCMOP5_whenEvaluated_thenTheObjectivesAreOnTheFront()
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(5);
      DoubleSolution solution = paretoOptimalSolution(problem);

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(0.5 + 0.7057, solution.objectives()[0], 1e-12);
      assertEquals(1 - Math.sqrt(0.5) + 0.7057, solution.objectives()[1], 1e-12);
    }

    @Test
    @DisplayName("given LIRCMOP6, when evaluated, then f2 = 1 - x1^2 + 0.7057")
    void givenLIRCMOP6_whenEvaluated_thenTheObjectivesAreOnTheFront()
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(6);
      DoubleSolution solution = paretoOptimalSolution(problem);

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(0.5 + 0.7057, solution.objectives()[0], 1e-12);
      assertEquals(1 - 0.25 + 0.7057, solution.objectives()[1], 1e-12);
    }
  }

  @Nested
  @DisplayName("When evaluating the constraints of LIRCMOP14")
  class LIRCMOP14ConstraintTests {

    @Test
    @DisplayName(
        "given objectives whose squared norm is between 1.6^2 and 1.75^2, when the constraints"
            + " are evaluated, then only the third one is violated")
    void givenObjectivesInTheThirdRing_whenEvaluated_thenOnlyTheThirdConstraintIsViolated() {
      // Arrange: the squared norm of the objectives is 1.7^2
      LIRCMOP14 problem = new LIRCMOP14();
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < 3; i++) {
        solution.objectives()[i] = Math.sqrt(1.7 * 1.7 / 3);
      }

      // Act
      problem.evaluateConstraints(solution);

      // Assert: c = (f - r1^2)(f - r2^2), with f = 1.7^2, for each pair of radii
      double f = 1.7 * 1.7;
      assertEquals((f - 3 * 3) * (f - 2 * 2), solution.constraints()[0], 1e-12);
      assertEquals((f - 1.9 * 1.9) * (f - 1.8 * 1.8), solution.constraints()[1], 1e-12);
      assertEquals((f - 1.75 * 1.75) * (f - 1.6 * 1.6), solution.constraints()[2], 1e-12);
      assertTrue(solution.constraints()[0] > 0);
      assertTrue(solution.constraints()[1] > 0);
      assertTrue(solution.constraints()[2] < 0);
    }
  }
}
