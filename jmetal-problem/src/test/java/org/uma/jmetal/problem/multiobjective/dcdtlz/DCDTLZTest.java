package org.uma.jmetal.problem.multiobjective.dcdtlz;

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
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the DC-DTLZ problems (Li, Chen, Fu and Yao, 2019)")
class DCDTLZTest {

  private static Class<?> problemClass(String name) throws ClassNotFoundException {
    return Class.forName("org.uma.jmetal.problem.multiobjective.dcdtlz." + name);
  }

  /** The class name of a problem code of {@code reference-values.csv}: 23 is DC2_DTLZ3. */
  private static String name(int code) {
    return "DC" + code / 10 + "_DTLZ" + code % 10;
  }

  private static DCDTLZ problem(String name) throws ReflectiveOperationException {
    return (DCDTLZ) problemClass(name).getConstructor().newInstance();
  }

  private static DCDTLZ problem(String name, int variables, int objectives)
      throws ReflectiveOperationException {
    return (DCDTLZ)
        problemClass(name)
            .getConstructor(int.class, int.class)
            .newInstance(variables, objectives);
  }

  private static DCDTLZ problem(
      String name, int variables, int objectives, double a, double b, double gFactor)
      throws ReflectiveOperationException {
    return (DCDTLZ)
        problemClass(name)
            .getConstructor(int.class, int.class, double.class, double.class, double.class)
            .newInstance(variables, objectives, a, b, gFactor);
  }

  /**
   * Rows of {@code dcdtlz/reference-values.csv}, computed with dcdtlz_reference_fronts.py of
   * Evolver, a transcription of PlatEMO's problems in NumPy: problem code (10 DC + DTLZ), number
   * of variables n, number of objectives m, a, b, factor of g, the n variables, the m objectives
   * and the constraints.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                DCDTLZTest.class.getResourceAsStream("/dcdtlz/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      List<double[]> rows =
          reader
              .lines()
              .filter(line -> !line.startsWith("#") && !line.isBlank())
              .map(
                  line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
              .toList();
      return rows.stream()
          .map(
              row ->
                  Arguments.of(
                      name((int) row[0]), (int) row[1], (int) row[2], row[3], row[4], row[5], row));
    }
  }

  private static void assertClose(double expected, double actual, String message) {
    assertEquals(expected, actual, 1e-10 * Math.max(1.0, Math.abs(expected)), message);
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class EvaluationTests {

    @ParameterizedTest(name = "{0} with {1} variables, {2} objectives, a = {3}, b = {4}, g x {5}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.dcdtlz.DCDTLZTest#referenceValues")
    @DisplayName("given a solution, then its objectives and constraints are the reference ones")
    void givenASolution_whenEvaluated_thenItsValuesAreTheReferenceOnes(
        String name,
        int variables,
        int objectives,
        double a,
        double b,
        double gFactor,
        double[] row)
        throws ReflectiveOperationException {
      // Arrange
      DCDTLZ problem = problem(name, variables, objectives, a, b, gFactor);
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < variables; i++) {
        solution.variables().set(i, row[6 + i]);
      }

      // Act
      problem.evaluate(solution);

      // Assert
      int constraints = problem.numberOfConstraints();
      double[] expected = Arrays.copyOfRange(row, 6 + variables, row.length);
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
  @DisplayName("When creating a problem with its default constructor")
  class DefaultConstructorTests {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
      "DC1_DTLZ1, 7, 1, 100",
      "DC1_DTLZ3, 12, 1, 10",
      "DC2_DTLZ1, 7, 2, 100",
      "DC2_DTLZ3, 12, 2, 10",
      "DC3_DTLZ1, 7, 3, 100",
      "DC3_DTLZ3, 12, 3, 10"
    })
    @DisplayName("given a problem, then it has 3 objectives and PlatEMO's dimensions and constants")
    void givenAProblem_whenCreated_thenItHasPlatEMOsDimensionsAndConstants(
        String name, int variables, int constraints, double gFactor)
        throws ReflectiveOperationException {
      // Act
      DCDTLZ problem = problem(name);

      // Assert
      assertEquals(name, problem.name());
      assertEquals(variables, problem.numberOfVariables());
      assertEquals(3, problem.numberOfObjectives());
      assertEquals(constraints, problem.numberOfConstraints());
      assertEquals(3.0, problem.a());
      assertEquals(0.5, problem.b());
      assertEquals(gFactor, problem.gFactor());
      for (var bounds : problem.variableBounds()) {
        assertEquals(0.0, bounds.getLowerBound().doubleValue());
        assertEquals(1.0, bounds.getUpperBound().doubleValue());
      }
    }
  }

  @Nested
  @DisplayName("When creating a problem with other numbers of objectives")
  class ScalableTests {

    @ParameterizedTest(name = "{0} objectives")
    @ValueSource(ints = {2, 4, 6})
    @DisplayName("given a number of objectives, then DC3 has one constraint per objective")
    void givenANumberOfObjectives_whenDC3IsCreated_thenItHasOneConstraintPerObjective(
        int objectives) {
      // Act
      var dtlz1 = new DC3_DTLZ1(objectives + 4, objectives);
      var dtlz3 = new DC3_DTLZ3(objectives + 9, objectives);

      // Assert
      assertEquals(objectives, dtlz1.numberOfObjectives());
      assertEquals(objectives, dtlz1.numberOfConstraints());
      assertEquals(objectives, dtlz3.numberOfObjectives());
      assertEquals(objectives, dtlz3.numberOfConstraints());
    }

    @ParameterizedTest(name = "{0} objectives")
    @ValueSource(ints = {2, 3, 5, 8})
    @DisplayName("given the distance variables at 0.5, then the objectives are on the front")
    void givenTheDistanceVariablesAtTheirOptimum_whenEvaluated_thenTheObjectivesAreOnTheFront(
        int objectives) {
      // Arrange
      var dtlz1 = new DC2_DTLZ1(objectives + 4, objectives);
      var dtlz3 = new DC2_DTLZ3(objectives + 9, objectives);
      var random = new Random(objectives);

      for (DCDTLZ problem : List.of(dtlz1, dtlz3)) {
        DoubleSolution solution = problem.createSolution();
        for (int i = 0; i < problem.numberOfVariables(); i++) {
          solution.variables().set(i, i < objectives - 1 ? random.nextDouble() : 0.5);
        }

        // Act
        problem.evaluate(solution);

        // Assert
        double[] f = solution.objectives();
        double level =
            problem == dtlz1
                ? Arrays.stream(f).sum() - 0.5
                : Math.sqrt(Arrays.stream(f).map(v -> v * v).sum()) - 1.0;
        assertEquals(0.0, level, 1e-12, problem.name());
        assertTrue(Arrays.stream(solution.constraints()).allMatch(c -> c >= 0), problem.name());
      }
    }
  }

  @Nested
  @DisplayName("When creating a problem without distance variables")
  class WithoutDistanceVariablesTests {

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"DC1_DTLZ3", "DC2_DTLZ3", "DC3_DTLZ3"})
    @DisplayName(
        "given 3 objectives and 2 variables, as in the STEP benchmark, then g is 0 and every"
            + " constraint on g is satisfied")
    void givenOneVariableLessThanObjectives_whenEvaluated_thenTheSolutionIsOnTheFront(String name)
        throws ReflectiveOperationException {
      // Arrange
      DCDTLZ problem = problem(name, 2, 3, 3.0, 0.5, 100.0);
      DoubleSolution solution = problem.createSolution();
      solution.variables().set(0, 0.05);
      solution.variables().set(1, 0.7);

      // Act
      problem.evaluate(solution);

      // Assert
      double[] f = solution.objectives();
      assertEquals(1.0, Math.sqrt(Arrays.stream(f).map(v -> v * v).sum()), 1e-12);
      assertTrue(Arrays.stream(solution.constraints()).allMatch(c -> c >= 0), name);
    }
  }

  @Nested
  @DisplayName("When creating a problem with invalid arguments")
  class InvalidArgumentTests {

    @ParameterizedTest(name = "{0} with {1} variables and {2} objectives")
    @CsvSource({
      "DC1_DTLZ1, 1, 3",
      "DC2_DTLZ3, 3, 5",
      "DC3_DTLZ1, 6, 1",
      "DC3_DTLZ3, 6, 0"
    })
    @DisplayName(
        "given fewer variables than position variables or under two objectives, then rejected")
    void givenInvalidDimensions_whenCreated_thenItIsRejected(
        String name, int variables, int objectives) {
      // Act & Assert
      var thrown =
          assertThrows(InvocationTargetException.class, () -> problem(name, variables, objectives));
      assertTrue(thrown.getCause() instanceof InvalidConditionException);
    }

    @ParameterizedTest(name = "a = {0}, b = {1}, factor of g = {2}")
    @CsvSource({"0, 0.5, 100", "-3, 0.5, 100", "3, 1.5, 100", "3, -1.5, 100", "3, 0.5, 0"})
    @DisplayName("given a non-positive a or factor of g, or b outside [-1, 1], then rejected")
    void givenInvalidConstants_whenCreated_thenTheyAreRejected(double a, double b, double gFactor) {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> new DC1_DTLZ1(7, 3, a, b, gFactor));
    }
  }
}
