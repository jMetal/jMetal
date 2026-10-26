package org.uma.jmetal.problem.multiobjective.cec2009;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the CEC 2009 constrained problems CF1-CF10")
class CEC2009CFTest {

  private static DoubleProblem problem(int number) throws ReflectiveOperationException {
    return (DoubleProblem) problemClass(number).getConstructor().newInstance();
  }

  private static DoubleProblem problem(int number, int variables)
      throws ReflectiveOperationException {
    return (DoubleProblem) problemClass(number).getConstructor(int.class).newInstance(variables);
  }

  private static Class<?> problemClass(int number) throws ClassNotFoundException {
    return Class.forName("org.uma.jmetal.problem.multiobjective.cec2009.CEC2009CF" + number);
  }

  private static DoubleSolution evaluated(DoubleProblem problem, double... x) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }
    return problem.evaluate(solution);
  }

  /**
   * Rows of {@code cec2009cf/reference-values.csv}, computed with cec2009cf_reference_fronts.py of
   * Evolver, an independent NumPy transcription of the C code of the competition: problem, number
   * of variables n, the n variables, the objectives and the constraints. For every problem, with 10
   * variables, 7, the smallest number that it allows and those of the STEP suite (3 for CF4, 30 for
   * CF8), there are three random vectors, one of the Pareto set and two whose constraint is about
   * 1e-6 or 1e-7 above and below 0.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                CEC2009CFTest.class.getResourceAsStream("/cec2009cf/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      List<double[]> rows =
          reader
              .lines()
              .filter(line -> !line.startsWith("#") && !line.isBlank())
              .map(
                  line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
              .toList();
      return rows.stream().map(row -> Arguments.of((int) row[0], (int) row[1], row));
    }
  }

  private static void assertClose(double expected, double actual, String message) {
    assertEquals(expected, actual, 1e-10 * Math.max(1.0, Math.abs(expected)), message);
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class EvaluationTests {

    @ParameterizedTest(name = "CF{0} with {1} variables")
    @MethodSource(
        "org.uma.jmetal.problem.multiobjective.cec2009.CEC2009CFTest#referenceValues")
    @DisplayName("given a solution, then its objectives and constraints are the reference ones")
    void givenASolution_whenEvaluated_thenItsValuesAreTheReferenceOnes(
        int number, int variables, double[] row) throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number, variables);
      double[] x = Arrays.copyOfRange(row, 2, 2 + variables);

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert
      int objectives = problem.numberOfObjectives();
      int constraints = problem.numberOfConstraints();
      double[] expected = Arrays.copyOfRange(row, 2 + variables, row.length);
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
  @DisplayName("When creating a problem")
  class ConstructorTests {

    @ParameterizedTest(name = "CF{0}")
    @CsvSource({
      "1, 2, 1, 0.0, 1.0",
      "2, 2, 1, -1.0, 1.0",
      "3, 2, 1, -2.0, 2.0",
      "4, 2, 1, -2.0, 2.0",
      "5, 2, 1, -2.0, 2.0",
      "6, 2, 2, -2.0, 2.0",
      "7, 2, 2, -2.0, 2.0",
      "8, 3, 1, -4.0, 4.0",
      "9, 3, 1, -2.0, 2.0",
      "10, 3, 1, -2.0, 2.0"
    })
    @DisplayName(
        "given the default constructor, then the problem has 10 variables and the report's bounds")
    void givenTheDefaultConstructor_whenCreated_thenItHasTheReportsDimensionsAndBounds(
        int number, int objectives, int constraints, double lower, double upper)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = problem(number);

      // Assert
      assertEquals("CEC2009CF" + number, problem.name());
      assertEquals(10, problem.numberOfVariables());
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(constraints, problem.numberOfConstraints());
      for (int i = 0; i < problem.numberOfVariables(); i++) {
        var bounds = problem.variableBounds().get(i);
        boolean position = i < objectives - 1;
        assertEquals(position ? 0.0 : lower, bounds.getLowerBound().doubleValue());
        assertEquals(position ? 1.0 : upper, bounds.getUpperBound().doubleValue());
      }
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("given a number of variables, then the problem has it")
    void givenANumberOfVariables_whenCreated_thenTheProblemHasIt(int number)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = problem(number, 30);

      // Assert
      assertEquals(30, problem.numberOfVariables());
      assertEquals(30, problem.variableBounds().size());
    }

    @ParameterizedTest(name = "CF{0} with {1} variables")
    @CsvSource({"4, 3, 2", "4, 10, 2", "8, 30, 3"})
    @DisplayName(
        "given the dimensions of the STEP suite (STEP6, STEP11, STEP30), then the problem has them")
    void givenTheDimensionsOfTheStepSuite_whenCreated_thenTheProblemHasThem(
        int number, int variables, int objectives) throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = problem(number, variables);
      DoubleSolution solution = problem.evaluate(problem.createSolution());

      // Assert
      assertEquals(variables, problem.numberOfVariables());
      assertEquals(variables, problem.variableBounds().size());
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(1, problem.numberOfConstraints());
      assertTrue(Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
      assertTrue(Arrays.stream(solution.constraints()).allMatch(Double::isFinite));
    }

    @ParameterizedTest(name = "CF{0} with {1} variables")
    @CsvSource({"1, 2", "2, 2", "3, 2", "4, 1", "5, 1", "6, 3", "7, 3", "8, 4", "9, 4", "10, 4"})
    @DisplayName("given fewer variables than the problem needs, then it is rejected")
    void givenTooFewVariables_whenCreated_thenTheProblemIsRejected(int number, int variables) {
      // Act & Assert
      var thrown = assertThrows(InvocationTargetException.class, () -> problem(number, variables));
      assertTrue(thrown.getCause() instanceof InvalidConditionException);
    }
  }

  @Nested
  @DisplayName("When evaluating a solution at the bounds of the decision space")
  class BoundsTests {

    @ParameterizedTest(name = "CF{0}")
    @CsvSource({
      "1, 3", "2, 3", "3, 3", "4, 2", "5, 2", "6, 4", "7, 4", "8, 5", "9, 5", "10, 5", "1, 10",
      "2, 10", "3, 10", "4, 10", "5, 10", "6, 10", "7, 10", "8, 10", "9, 10", "10, 10"
    })
    @DisplayName("given the variables at a bound, then the objectives and constraints are finite")
    void givenTheVariablesAtABound_whenEvaluated_thenTheValuesAreFinite(int number, int variables)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number, variables);
      double[] lower = new double[variables];
      double[] upper = new double[variables];
      for (int i = 0; i < variables; i++) {
        lower[i] = problem.variableBounds().get(i).getLowerBound();
        upper[i] = problem.variableBounds().get(i).getUpperBound();
      }

      // Act & Assert
      for (double[] x : List.of(lower, upper)) {
        DoubleSolution solution = evaluated(problem, x);
        assertTrue(Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
        assertTrue(Arrays.stream(solution.constraints()).allMatch(Double::isFinite));
      }
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {8, 9, 10})
    @DisplayName(
        "given x1 = 1 and the distance terms at 0, then f3 is 1 and the constraint is 0, not NaN")
    void givenTheTopOfTheFront_whenEvaluated_thenTheConstraintIsZero(int number)
        throws ReflectiveOperationException {
      // Arrange: y_j = x_j - 2 x2 sin(2 pi x1 + j pi / n) = 0 with x2 = 0
      DoubleProblem problem = problem(number);
      double[] x = new double[10];
      x[0] = 1.0;

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert: the code of the competition divides by 1 - f3^2 = 0 here
      assertEquals(1.0, solution.objectives()[2]);
      assertEquals(0.0, solution.objectives()[0], 1e-15);
      assertEquals(0.0, solution.objectives()[1], 1e-15);
      assertEquals(0.0, solution.constraints()[0]);
    }
  }
}
