package org.uma.jmetal.problem.multiobjective.mw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the MW problems (Ma and Wang, 2019)")
class MWTest {

  private static final Set<Integer> SCALABLE = Set.of(4, 8, 14);

  private static DoubleProblem problem(int number) throws ReflectiveOperationException {
    return (DoubleProblem) problemClass(number).getConstructor().newInstance();
  }

  private static DoubleProblem problem(int number, int variables, int objectives)
      throws ReflectiveOperationException {
    Class<?> problemClass = problemClass(number);
    if (SCALABLE.contains(number)) {
      return (DoubleProblem)
          problemClass.getConstructor(int.class, int.class).newInstance(variables, objectives);
    }
    assertEquals(2, objectives);
    return (DoubleProblem) problemClass.getConstructor(int.class).newInstance(variables);
  }

  private static Class<?> problemClass(int number) throws ClassNotFoundException {
    return Class.forName("org.uma.jmetal.problem.multiobjective.mw.MW" + number);
  }

  private static DoubleSolution evaluated(DoubleProblem problem, double... x) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }
    return problem.evaluate(solution);
  }

  /** x_i = 1 - (x_{i-1} - 0.5)^2 for i &gt;= m: the minimum of g3, from the position variables. */
  private static double[] atTheMinimumOfG3(int variables, double... positionVariables) {
    double[] x = Arrays.copyOf(positionVariables, variables);
    for (int i = positionVariables.length; i < variables; i++) {
      x[i] = 1.0 - (x[i - 1] - 0.5) * (x[i - 1] - 0.5);
    }
    return x;
  }

  /** x_i = (i - 1) / n for i &gt;= m: the minimum of g2, from the position variables. */
  private static double[] atTheMinimumOfG2(int variables, double... positionVariables) {
    double[] x = Arrays.copyOf(positionVariables, variables);
    for (int i = positionVariables.length; i < variables; i++) {
      x[i] = i / (double) variables;
    }
    return x;
  }

  /**
   * Rows of {@code mw/reference-values.csv}, computed with mw_reference.py, an independent Python
   * transcription of the paper (MW1-MW6) and of pymoo (MW7-MW14): problem, number of variables n,
   * number of objectives m, the n variables, the m objectives and the constraints.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                MWTest.class.getResourceAsStream("/mw/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      List<double[]> rows =
          reader
              .lines()
              .filter(line -> !line.startsWith("#") && !line.isBlank())
              .map(line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
              .toList();
      return rows.stream()
          .map(row -> Arguments.of((int) row[0], (int) row[1], (int) row[2], row));
    }
  }

  private static void assertClose(double expected, double actual, String message) {
    assertEquals(expected, actual, 1e-10 * Math.max(1.0, Math.abs(expected)), message);
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class EvaluationTests {

    @ParameterizedTest(name = "MW{0} with {1} variables and {2} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.mw.MWTest#referenceValues")
    @DisplayName("given a solution, then its objectives and constraints are the reference ones")
    void givenASolution_whenEvaluated_thenItsValuesAreTheReferenceOnes(
        int number, int variables, int objectives, double[] row)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number, variables, objectives);
      double[] x = Arrays.copyOfRange(row, 3, 3 + variables);

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert
      int constraints = problem.numberOfConstraints();
      double[] expected = Arrays.copyOfRange(row, 3 + variables, row.length);
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

    @ParameterizedTest(name = "MW{0}")
    @CsvSource({
      "1, 2, 1, 1.0",
      "2, 2, 1, 1.0",
      "3, 2, 2, 1.0",
      "4, 3, 1, 1.0",
      "5, 2, 3, 1.0",
      "6, 2, 1, 1.1",
      "7, 2, 2, 1.0",
      "8, 3, 1, 1.0",
      "9, 2, 1, 1.0",
      "10, 2, 3, 1.0",
      "11, 2, 4, 1.4142135623730951",
      "12, 2, 2, 1.0",
      "13, 2, 2, 1.5",
      "14, 3, 1, 1.5"
    })
    @DisplayName("given a problem, then it has 15 variables, the paper's bounds and its dimensions")
    void givenAProblem_whenCreated_thenItHasThePapersDimensionsAndBounds(
        int number, int objectives, int constraints, double upperBound)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = problem(number);

      // Assert
      assertEquals("MW" + number, problem.name());
      assertEquals(15, problem.numberOfVariables());
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(constraints, problem.numberOfConstraints());
      for (var bounds : problem.variableBounds()) {
        assertEquals(0.0, bounds.getLowerBound().doubleValue());
        assertEquals(upperBound, bounds.getUpperBound().doubleValue());
      }
    }
  }

  @Nested
  @DisplayName("When creating a scalable problem with other numbers of objectives")
  class ScalableTests {

    @ParameterizedTest(name = "MW{0} with {1} objectives")
    @CsvSource({"4, 2", "4, 5", "8, 2", "8, 5", "14, 2", "14, 5"})
    @DisplayName("given a number of objectives, then the problem has it and one constraint")
    void givenANumberOfObjectives_whenCreated_thenTheProblemHasIt(int number, int objectives)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = problem(number, 12, objectives);

      // Assert
      assertEquals(12, problem.numberOfVariables());
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(1, problem.numberOfConstraints());
    }

    @ParameterizedTest(name = "{0} objectives")
    @ValueSource(ints = {2, 3, 4, 5, 6})
    @DisplayName("given g2 at its minimum, then the objectives of MW8 are on the unit sphere")
    void givenTheMinimumOfG2_whenMW8IsEvaluated_thenTheObjectivesAreOnTheUnitSphere(
        int objectives) {
      // Arrange
      var problem = new MW8(15, objectives);
      double[] position = new double[objectives - 1];
      for (int i = 0; i < position.length; i++) {
        position[i] = 0.1 + 0.8 * i / objectives;
      }

      // Act
      DoubleSolution solution = evaluated(problem, atTheMinimumOfG2(15, position));

      // Assert
      double squaredNorm = Arrays.stream(solution.objectives()).map(f -> f * f).sum();
      assertEquals(1.0, squaredNorm, 1e-12);
    }

    @ParameterizedTest(name = "{0} objectives")
    @ValueSource(ints = {2, 3, 4, 5, 6})
    @DisplayName("given g1 at its minimum, then the objectives of MW4 add up to 1 and are feasible")
    void givenTheMinimumOfG1_whenMW4IsEvaluated_thenTheObjectivesAreOnTheSimplex(int objectives) {
      // Arrange
      var problem = new MW4(15, objectives);
      int n = 15;
      double[] x = new double[n];
      for (int i = 0; i < objectives - 1; i++) {
        x[i] = 0.2 + 0.6 * i / objectives;
      }
      for (int i = objectives; i <= n; i++) {
        x[i - 1] = Math.pow(0.5 + (i - 1) / (2.0 * n), 1.0 / (n - objectives));
      }

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert
      assertEquals(1.0, Arrays.stream(solution.objectives()).sum(), 1e-12);
      assertTrue(solution.constraints()[0] >= -1e-12);
    }

    @ParameterizedTest(name = "{0} objectives")
    @ValueSource(ints = {2, 3, 4, 5, 6})
    @DisplayName("given g3 at its minimum, then the last objective of MW14 is the mean shape")
    void givenTheMinimumOfG3_whenMW14IsEvaluated_thenTheLastObjectiveIsTheMeanShape(
        int objectives) {
      // Arrange
      var problem = new MW14(15, objectives);
      double[] position = new double[objectives - 1];
      double mean = 0.0;
      for (int i = 0; i < position.length; i++) {
        position[i] = 0.1 + 1.3 * i / objectives;
        mean +=
            (6.0
                    - Math.exp(position[i])
                    - 1.5 * Math.sin(1.1 * Math.PI * position[i] * position[i]))
                / (objectives - 1);
      }

      // Act
      DoubleSolution solution = evaluated(problem, atTheMinimumOfG3(15, position));

      // Assert
      for (int i = 0; i < position.length; i++) {
        assertEquals(position[i], solution.objectives()[i]);
      }
      assertEquals(mean, solution.objectives()[objectives - 1], 1e-12);
      assertTrue(solution.constraints()[0] > 0.0);
    }
  }

  @Nested
  @DisplayName("When evaluating a solution at the bounds of the decision space")
  class BoundsTests {

    @ParameterizedTest(name = "MW{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14})
    @DisplayName("given the variables at a bound, then the objectives and constraints are finite")
    void givenTheVariablesAtABound_whenEvaluated_thenTheValuesAreFinite(int number)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = problem(number);
      double upper = problem.variableBounds().get(0).getUpperBound();
      double[] lower = new double[15];
      double[] upperCorner = new double[15];
      Arrays.fill(upperCorner, upper);

      // Act & Assert
      for (double[] x : List.of(lower, upperCorner)) {
        DoubleSolution solution = evaluated(problem, x);
        assertTrue(Arrays.stream(solution.objectives()).allMatch(Double::isFinite));
        assertTrue(Arrays.stream(solution.constraints()).allMatch(Double::isFinite));
      }
    }

    @Test
    @DisplayName("given x1 = sqrt(2) and g3 = 1 in MW11, then f2 is 0 and not NaN")
    void givenTheUpperBoundOfX1AndG3AtItsMinimum_whenMW11IsEvaluated_thenF2IsZero() {
      // Arrange: sqrt(2) * sqrt(2) = 2.0000000000000004 in floating point
      var problem = new MW11();
      double[] x = atTheMinimumOfG3(15, Math.sqrt(2.0));

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert
      assertEquals(Math.sqrt(2.0), solution.objectives()[0]);
      assertEquals(0.0, solution.objectives()[1]);
      assertFalse(Arrays.stream(solution.constraints()).anyMatch(Double::isNaN));
    }

    @Test
    @DisplayName("given x1 = 1.1 and g2 = 1 in MW6, then the end (1.1, 0) of the front is feasible")
    void givenTheUpperBoundOfX1AndG2AtItsMinimum_whenMW6IsEvaluated_thenTheEndIsFeasible() {
      // Arrange
      var problem = new MW6();
      double[] x = atTheMinimumOfG2(15, 1.1);

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert (PlatEMO's MW6, with f1 = 1.0999 g2 x1, does not reach this point)
      assertEquals(1.1, solution.objectives()[0]);
      assertEquals(0.0, solution.objectives()[1]);
      assertTrue(solution.constraints()[0] > 0.0);
    }
  }

  @Nested
  @DisplayName("When creating a problem with invalid arguments")
  class InvalidArgumentTests {

    @ParameterizedTest(name = "MW{0}")
    @ValueSource(ints = {1, 2, 3, 5, 6, 7, 9, 10, 11, 12, 13})
    @DisplayName("given a single variable, then a problem with two objectives is rejected")
    void givenASingleVariable_whenCreated_thenItIsRejected(int number) {
      // Act & Assert
      var thrown =
          assertThrows(
              InvocationTargetException.class,
              () -> problemClass(number).getConstructor(int.class).newInstance(1));
      assertTrue(thrown.getCause() instanceof InvalidConditionException);
    }

    @ParameterizedTest(name = "MW{0} with {1} variables and {2} objectives")
    @CsvSource({"4, 2, 3", "8, 4, 5", "14, 2, 3", "4, 15, 1", "8, 15, 1", "14, 15, 0"})
    @DisplayName("given fewer variables than objectives or fewer than two objectives, then rejected")
    void givenInvalidDimensions_whenAScalableProblemIsCreated_thenItIsRejected(
        int number, int variables, int objectives) {
      // Act & Assert
      var thrown =
          assertThrows(
              InvocationTargetException.class, () -> problem(number, variables, objectives));
      assertTrue(thrown.getCause() instanceof InvalidConditionException);
    }
  }
}
