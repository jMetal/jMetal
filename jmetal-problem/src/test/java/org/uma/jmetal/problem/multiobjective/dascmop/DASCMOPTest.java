package org.uma.jmetal.problem.multiobjective.dascmop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the DAS-CMOP problems (Fan et al., 2020)")
class DASCMOPTest {

  private static final int VARIABLES = 10;

  private static AbstractDASCMOP problem(int number, Object... arguments)
      throws ReflectiveOperationException {
    Class<?> problemClass =
        Class.forName("org.uma.jmetal.problem.multiobjective.dascmop.DASCMOP" + number);
    Class<?>[] types =
        arguments.length == 0
            ? new Class<?>[0]
            : new Class<?>[] {int.class, double.class, double.class, double.class};
    return (AbstractDASCMOP) problemClass.getConstructor(types).newInstance(arguments);
  }

  private static DoubleSolution evaluated(AbstractDASCMOP problem, double... x) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }
    return problem.evaluate(solution);
  }

  /**
   * Rows of {@code dascmop/reference-values.csv}, computed with an independent transcription of
   * PlatEMO's code and Table 2 of the paper: problem, triplet, 10 variables, objectives and
   * constraints.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                DASCMOPTest.class.getResourceAsStream("/dascmop/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      List<double[]> rows =
          reader
              .lines()
              .filter(line -> !line.startsWith("#") && !line.isBlank())
              .map(line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
              .toList();
      return rows.stream().map(row -> Arguments.of((int) row[0], row));
    }
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class EvaluationTests {

    @ParameterizedTest(name = "DAS-CMOP{0}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.dascmop.DASCMOPTest#referenceValues")
    @DisplayName("given a solution, then its objectives and constraints are the reference ones")
    void givenASolution_whenEvaluated_thenItsValuesAreTheReferenceOnes(int number, double[] row)
        throws ReflectiveOperationException {
      // Arrange
      AbstractDASCMOP problem = problem(number, VARIABLES, row[1], row[2], row[3]);
      double[] x = Arrays.copyOfRange(row, 4, 4 + VARIABLES);

      // Act
      DoubleSolution solution = evaluated(problem, x);

      // Assert
      int m = problem.numberOfObjectives();
      double[] expected = Arrays.copyOfRange(row, 4 + VARIABLES, row.length);
      assertEquals(m + problem.numberOfConstraints(), expected.length);
      for (int i = 0; i < m; i++) {
        assertEquals(expected[i], solution.objectives()[i], 1e-9, "objective " + i);
      }
      for (int i = 0; i < problem.numberOfConstraints(); i++) {
        assertEquals(expected[m + i], solution.constraints()[i], 1e-9, "constraint " + i);
      }
    }
  }

  @Nested
  @DisplayName("When creating a problem with its default constructor")
  class DefaultConstructorTests {

    @ParameterizedTest(name = "DAS-CMOP{0}")
    @CsvSource({
      "1, 2, 11, 0.0",
      "2, 2, 11, 0.0",
      "3, 2, 11, 0.5",
      "4, 2, 11, 0.5",
      "5, 2, 11, 0.5",
      "6, 2, 11, 0.5",
      "7, 3, 7, 0.5",
      "8, 3, 7, 0.5",
      "9, 3, 7, 0.5"
    })
    @DisplayName("given a problem, then it has 30 variables, its name and PlatEMO's triplet")
    void givenAProblem_whenCreated_thenItHasItsDimensionsAndPlatEMOsTriplet(
        int number, int objectives, int constraints, double eta)
        throws ReflectiveOperationException {
      // Act
      AbstractDASCMOP problem = problem(number);

      // Assert
      assertEquals("DASCMOP" + number, problem.name());
      assertEquals(30, problem.numberOfVariables());
      assertEquals(objectives, problem.numberOfObjectives());
      assertEquals(constraints, problem.numberOfConstraints());
      assertEquals(eta, problem.eta());
      assertEquals(0.5, problem.zeta());
      assertEquals(0.5, problem.gamma());
    }
  }

  @Nested
  @DisplayName("When a component of the difficulty triplet is at a bound")
  class TripletTests {

    @Test
    @DisplayName("given eta = 0, then the Type-I constraint is always satisfied")
    void givenEtaZero_whenEvaluated_thenTheTypeIConstraintIsSatisfied() {
      // Arrange
      var problem = new DASCMOP4(VARIABLES, 0.0, 0.5, 0.5);

      // Act & Assert
      for (double x1 = 0.0; x1 <= 1.0; x1 += 0.01) {
        assertTrue(evaluated(problem, x1, 0.5).constraints()[0] >= 0.0, "x1 = " + x1);
      }
    }

    @Test
    @DisplayName("given zeta = 0, then the Type-II constraint is always satisfied")
    void givenZetaZero_whenEvaluated_thenTheTypeIIConstraintIsSatisfied() {
      // Arrange: with x1 = 0, g = x2^2 for DAS-CMOP1 with two variables
      var problem = new DASCMOP1(2, 0.5, 0.0, 0.5);

      // Act & Assert
      for (double x2 = 0.0; x2 <= 1.0; x2 += 0.01) {
        assertTrue(evaluated(problem, 0.0, x2).constraints()[1] >= 0.0, "x2 = " + x2);
      }
    }

    @Test
    @DisplayName("given zeta = 1, then the Type-II constraint is satisfied only with g = 0.5")
    void givenZetaOne_whenEvaluated_thenOnlyGEqualToDIsFeasible() {
      // Arrange: with x1 = 0, g = x2^2 for DAS-CMOP1 with two variables
      var problem = new DASCMOP1(2, 0.5, 1.0, 0.5);

      // Act
      double onTheBand = evaluated(problem, 0.0, Math.sqrt(0.5)).constraints()[1];
      double offTheBand = evaluated(problem, 0.0, 0.9).constraints()[1];

      // Assert (PlatEMO reverses this case, and accepts every g but those near 0.5)
      assertTrue(onTheBand >= 0.0, "g = 0.5: " + onTheBand);
      assertTrue(offTheBand < 0.0, "g = 0.81: " + offTheBand);
    }

    @ParameterizedTest(name = "DAS-CMOP{0}")
    @CsvSource({"1", "7"})
    @DisplayName("given gamma = 0, then the Type-III constraints are always satisfied")
    void givenGammaZero_whenEvaluated_thenTheTypeIIIConstraintsAreSatisfied(int number)
        throws ReflectiveOperationException {
      // Arrange
      AbstractDASCMOP problem = problem(number, VARIABLES, 0.5, 0.5, 0.0);
      int firstTypeIII = problem.numberOfObjectives() == 2 ? 2 : 3;
      var random = new java.util.Random(1);

      // Act & Assert
      for (int sample = 0; sample < 1000; sample++) {
        double[] x = random.doubles(VARIABLES).toArray();
        double[] constraints = evaluated(problem, x).constraints();
        for (int i = firstTypeIII; i < constraints.length; i++) {
          assertTrue(constraints[i] >= 0.0, Arrays.toString(x));
        }
      }
    }
  }

  @Nested
  @DisplayName("When creating a problem with invalid arguments")
  class InvalidArgumentTests {

    @ParameterizedTest(name = "triplet ({0}, {1}, {2})")
    @CsvSource({"-0.1, 0.5, 0.5", "0.5, 1.5, 0.5", "0.5, 0.5, 2.0"})
    @DisplayName("given a component of the triplet out of [0, 1], then the problem is rejected")
    void givenATripletOutOfRange_whenCreated_thenItIsRejected(
        double eta, double zeta, double gamma) {
      // Act & Assert
      var thrown =
          assertThrows(
              InvocationTargetException.class, () -> problem(2, VARIABLES, eta, zeta, gamma));
      assertInstanceOf(RuntimeException.class, thrown.getCause());
    }

    @Test
    @DisplayName("given fewer variables than objectives, then the problem is rejected")
    void givenFewerVariablesThanObjectives_whenCreated_thenItIsRejected() {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> new DASCMOP7(2, 0.5, 0.5, 0.5));
    }
  }
}
