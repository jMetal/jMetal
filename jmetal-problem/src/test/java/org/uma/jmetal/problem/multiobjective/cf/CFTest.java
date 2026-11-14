package org.uma.jmetal.problem.multiobjective.cf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
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
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for the CF problems")
class CFTest {

  private static DoubleProblem createDefault(int id) throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.cf.CF" + id)
            .getConstructor()
            .newInstance();
  }

  /** Creates a CF problem with M objectives and M + 10 variables, as the default ones. */
  private static DoubleProblem create(int id, int numberOfObjectives)
      throws ReflectiveOperationException {
    return (DoubleProblem)
        Class.forName("org.uma.jmetal.problem.multiobjective.cf.CF" + id)
            .getConstructor(Integer.class, Integer.class)
            .newInstance(numberOfObjectives + 10, numberOfObjectives);
  }

  /** The C- and D-type problems (CF9-CF16) with 2, 3 and 5 objectives. */
  static Stream<Arguments> cAndDTypeProblems() {
    return IntStream.rangeClosed(9, 16)
        .boxed()
        .flatMap(id -> Stream.of(2, 3, 5).map(m -> Arguments.of(id, m)));
  }

  /** The number k of constrained angles of the paper: M - 1 up to 3 objectives, M / 2 up to 8. */
  private static int constrainedAngles(int numberOfObjectives) {
    if (numberOfObjectives <= 3) {
      return numberOfObjectives - 1;
    }
    return numberOfObjectives <= 8 ? numberOfObjectives / 2 : 3;
  }

  /** The open intervals that a constrained angle must avoid: C type (CF9-12), D type (CF13-16). */
  private static double[][] excludedIntervals(int id) {
    return id <= 12
        ? new double[][] {{1.0 / 4.0, 3.0 / 4.0}}
        : new double[][] {{1.0 / 10.0, 4.0 / 10.0}, {7.0 / 10.0, 8.0 / 10.0}};
  }

  /** The angles theta_1 to theta_(M-1) of a solution, scaled to [0, 1], as the problems compute. */
  private static double[] angles(DoubleSolution solution, int numberOfObjectives) {
    double[] squaredSums = new double[numberOfObjectives];
    double squaredSum = 0.0;
    for (int i = numberOfObjectives - 1; i >= 0; i--) {
      double x = solution.variables().get(i);
      squaredSum = squaredSum + x * x;
      squaredSums[i] = squaredSum;
    }
    double[] theta = new double[numberOfObjectives - 1];
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      theta[i] =
          2.0 / Math.PI * Math.atan(Math.sqrt(squaredSums[i + 1]) / solution.variables().get(i));
    }
    return theta;
  }

  /**
   * Sets the first M variables of a solution so that the sum of their squares is S and their angles
   * are theta, and the other ones to 0.2, where the distance function h is 0.
   */
  private static void place(DoubleSolution solution, double s, double[] theta) {
    double radius = Math.sqrt(s);
    for (int i = 0; i < theta.length; i++) {
      solution.variables().set(i, radius * Math.cos(Math.PI / 2.0 * theta[i]));
      radius *= Math.sin(Math.PI / 2.0 * theta[i]);
    }
    solution.variables().set(theta.length, radius);
    for (int i = theta.length + 1; i < solution.variables().size(); i++) {
      solution.variables().set(i, 0.2);
    }
  }

  @Nested
  @DisplayName("When creating a problem without arguments")
  class DefaultConstructorTests {

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    @DisplayName("given each problem, when created and evaluated, then it has 3 objectives")
    void givenEachProblem_whenCreatedWithoutArguments_thenItHasThreeObjectives(int id)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = createDefault(id);
      DoubleSolution solution = problem.createSolution();

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(3, problem.numberOfObjectives());
      assertEquals(3, solution.objectives().length);
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    @DisplayName("given each problem, when created, then it has 10 distance variables")
    void givenEachProblem_whenCreatedWithoutArguments_thenItHasTenDistanceVariables(int id)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = createDefault(id);

      // Assert
      assertEquals(13, problem.numberOfVariables());
    }

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    @DisplayName("given random solutions, when evaluated, then objectives and constraints are finite")
    void givenRandomSolutions_whenEvaluated_thenObjectivesAndConstraintsAreFinite(int id)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem = createDefault(id);
      JMetalRandom.getInstance().setSeed(1);

      for (int i = 0; i < 100; i++) {
        DoubleSolution solution = problem.createSolution();

        // Act
        problem.evaluate(solution);

        // Assert
        assertTrue(
            Arrays.stream(solution.objectives()).allMatch(Double::isFinite),
            "objectives " + Arrays.toString(solution.objectives()));
        assertTrue(
            Arrays.stream(solution.constraints()).allMatch(Double::isFinite),
            "constraints " + Arrays.toString(solution.constraints()));
      }
    }
  }

  @Nested
  @DisplayName("When evaluating a solution of a C- or D-type problem with r > 0")
  class RadiusConstraintTests {

    @ParameterizedTest(name = "CF{0}")
    @CsvSource({"10, 0.25", "11, 0.5", "12, 0.75", "14, 0.25", "15, 0.5", "16, 0.75"})
    @DisplayName("given a solution inside radius r, when evaluated, then only S + h >= r is violated")
    void givenASolutionInsideTheRadius_whenEvaluated_thenOnlyTheRadiusConstraintIsViolated(
        int id, double r) throws ReflectiveOperationException {
      // Arrange: h = 0 and both angles below 0.1, so only S + h >= r is violated
      DoubleProblem problem = createDefault(id);
      DoubleSolution solution = problem.createSolution();
      double[] position = {0.099, 0.014, 0.001};
      for (int i = 0; i < problem.numberOfVariables(); i++) {
        solution.variables().set(i, i < position.length ? position[i] : 0.2);
      }
      double s = Arrays.stream(position).map(x -> x * x).sum();

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(-(r - s), ConstraintHandling.overallConstraintViolationDegree(solution), 1e-12);
      assertEquals(1, ConstraintHandling.numberOfViolatedConstraints(solution));
    }
  }

  @Nested
  @DisplayName("When evaluating the angle constraints of a C- or D-type problem")
  class AngleConstraintTests {

    @ParameterizedTest(name = "CF{0} with {1} objectives")
    @CsvSource({
      "9, 2, 2", "9, 3, 3", "9, 5, 3",
      "10, 2, 3", "10, 3, 4", "10, 5, 4",
      "11, 2, 3", "11, 3, 4", "11, 5, 4",
      "12, 2, 3", "12, 3, 4", "12, 5, 4",
      "13, 2, 2", "13, 3, 3", "13, 5, 3",
      "14, 2, 3", "14, 3, 4", "14, 5, 4",
      "15, 2, 3", "15, 3, 4", "15, 5, 4",
      "16, 2, 3", "16, 3, 4", "16, 5, 4"
    })
    @DisplayName(
        "given a number of objectives, when created, then it has one constraint per constrained"
            + " angle besides those of the radius")
    void givenANumberOfObjectives_whenCreated_thenItHasOneConstraintPerConstrainedAngle(
        int id, int numberOfObjectives, int expectedConstraints)
        throws ReflectiveOperationException {
      // Act
      DoubleProblem problem = create(id, numberOfObjectives);
      DoubleSolution solution = problem.evaluate(problem.createSolution());

      // Assert
      assertEquals(expectedConstraints, problem.numberOfConstraints());
      assertEquals(expectedConstraints, solution.constraints().length);
    }

    @ParameterizedTest(name = "CF{0} with {1} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.cf.CFTest#cAndDTypeProblems")
    @DisplayName(
        "given a constrained angle in an excluded interval, when evaluated, then only its"
            + " constraint is negative")
    void givenAnAngleInAnExcludedInterval_whenEvaluated_thenOnlyItsConstraintIsNegative(
        int id, int numberOfObjectives) throws ReflectiveOperationException {
      // Arrange: S = 0.9 and h = 0 satisfy the radius constraints, the other constrained angles
      // are outside the excluded intervals, and the angles after the first k, which have no
      // constraint, are inside one
      DoubleProblem problem = create(id, numberOfObjectives);
      int k = constrainedAngles(numberOfObjectives);
      int firstAngleConstraint = problem.numberOfConstraints() - k;
      double allowed = id <= 12 ? 0.1 : 0.5;

      for (double[] interval : excludedIntervals(id)) {
        double inside = (interval[0] + interval[1]) / 2.0;
        double expected = -(interval[1] - interval[0]) / 2.0;
        for (int j = 0; j < k; j++) {
          double[] theta = new double[numberOfObjectives - 1];
          Arrays.fill(theta, inside);
          for (int i = 0; i < k; i++) {
            theta[i] = i == j ? inside : allowed;
          }
          DoubleSolution solution = problem.createSolution();
          place(solution, 0.9, theta);

          // Act
          problem.evaluate(solution);

          // Assert
          String point = "angles " + Arrays.toString(theta);
          double[] constraints = solution.constraints();
          assertEquals(expected, constraints[firstAngleConstraint + j], 1e-12, point);
          for (int i = 0; i < constraints.length; i++) {
            if (i != firstAngleConstraint + j) {
              assertTrue(constraints[i] >= 0.0, point + ", constraint " + i);
            }
          }
          assertEquals(1, ConstraintHandling.numberOfViolatedConstraints(solution), point);
          assertEquals(
              expected,
              ConstraintHandling.overallConstraintViolationDegree(solution),
              1e-12,
              point);
          assertFalse(
              solution
                  .attributes()
                  .containsKey(ConstraintHandling.PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION),
              point);
        }
      }
    }

    @ParameterizedTest(name = "CF{0} with {1} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.cf.CFTest#cAndDTypeProblems")
    @DisplayName(
        "given the constrained angles outside the excluded intervals, when evaluated, then no"
            + " constraint is negative")
    void givenTheAnglesOutsideTheExcludedIntervals_whenEvaluated_thenNoConstraintIsNegative(
        int id, int numberOfObjectives) throws ReflectiveOperationException {
      // Arrange: S = 0.9 and h = 0, and the angles after the first k inside an excluded interval
      DoubleProblem problem = create(id, numberOfObjectives);
      int k = constrainedAngles(numberOfObjectives);
      double[] allowedValues = id <= 12 ? new double[] {0.1, 0.9} : new double[] {0.05, 0.5, 0.9};
      double excluded = id <= 12 ? 0.5 : 0.25;

      for (double allowed : allowedValues) {
        double[] theta = new double[numberOfObjectives - 1];
        Arrays.fill(theta, excluded);
        Arrays.fill(theta, 0, k, allowed);
        DoubleSolution solution = problem.createSolution();
        place(solution, 0.9, theta);

        // Act
        problem.evaluate(solution);

        // Assert
        String point = "angles " + Arrays.toString(theta);
        assertTrue(Arrays.stream(solution.constraints()).allMatch(c -> c >= 0.0), point);
        assertTrue(ConstraintHandling.isFeasible(solution), point);
        assertEquals(0.0, ConstraintHandling.overallConstraintViolationDegree(solution), point);
      }
    }

    @ParameterizedTest(name = "CF{0} with {1} objectives")
    @MethodSource("org.uma.jmetal.problem.multiobjective.cf.CFTest#cAndDTypeProblems")
    @DisplayName(
        "given random solutions, when evaluated, then their feasibility and violation are those of"
            + " the former pairs of bounds per excluded interval")
    void givenRandomSolutions_whenEvaluated_thenFeasibilityAndViolationAreTheFormerOnes(
        int id, int numberOfObjectives) throws ReflectiveOperationException {
      // Arrange: half of the points are uniform in the bounds, and half have h = 0 and S in
      // (0.01, 1.1), around the radius constraints
      DoubleProblem problem = create(id, numberOfObjectives);
      int k = constrainedAngles(numberOfObjectives);
      int radiusConstraints = problem.numberOfConstraints() - k;
      Random random = new Random(100L * id + numberOfObjectives);
      int feasible = 0;
      int violatingAnAngle = 0;

      for (int point = 0; point < 1000; point++) {
        DoubleSolution solution = problem.createSolution();
        if (point % 2 == 0) {
          for (int i = 0; i < problem.numberOfVariables(); i++) {
            solution.variables().set(i, 1e-10 + (1.0 - 2e-10) * random.nextDouble());
          }
        } else {
          double[] theta = new double[numberOfObjectives - 1];
          Arrays.setAll(theta, i -> 0.001 + 0.998 * random.nextDouble());
          place(solution, 0.01 + 1.09 * random.nextDouble(), theta);
        }

        // Act
        problem.evaluate(solution);

        // Assert: the former implementation had the same radius constraints, read here from
        // constraints(), and two values per excluded interval (a, b) of an angle, theta - a and
        // b - theta; the interval was violated when both were positive, and the smaller one was
        // added to the violation. The paper counts one violated constraint per angle.
        double[] theta = angles(solution, numberOfObjectives);
        double formerViolation = 0.0;
        int violatedConstraints = 0;
        for (int i = 0; i < radiusConstraints; i++) {
          if (solution.constraints()[i] < 0.0) {
            formerViolation += solution.constraints()[i];
            violatedConstraints++;
          }
        }
        int violatedAngles = 0;
        for (int i = 0; i < k; i++) {
          for (double[] interval : excludedIntervals(id)) {
            double lower = theta[i] - interval[0];
            double upper = -(theta[i] - interval[1]);
            if (lower > 0.0 && upper > 0.0) {
              formerViolation -= Math.min(lower, upper);
              violatedAngles++;
            }
          }
        }
        violatedConstraints += violatedAngles;
        String message = "point " + point + ", angles " + Arrays.toString(theta);
        assertEquals(violatedConstraints == 0, ConstraintHandling.isFeasible(solution), message);
        assertEquals(
            formerViolation,
            ConstraintHandling.overallConstraintViolationDegree(solution),
            1e-14 * Math.max(1.0, Math.abs(formerViolation)),
            message);
        assertEquals(
            violatedConstraints, ConstraintHandling.numberOfViolatedConstraints(solution), message);
        feasible += violatedConstraints == 0 ? 1 : 0;
        violatingAnAngle += violatedAngles > 0 ? 1 : 0;
      }
      assertTrue(feasible > 0, "no feasible point");
      assertTrue(violatingAnAngle > 0, "no point with an angle in an excluded interval");
    }
  }

  @Nested
  @DisplayName("When creating a problem with an Ackley distance function")
  class AckleyDistanceTests {

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {3, 8, 9, 14})
    @DisplayName("given as many variables as objectives, when created, then it is rejected")
    void givenAsManyVariablesAsObjectives_whenCreated_thenItIsRejected(int id)
        throws ReflectiveOperationException {
      // Arrange
      var constructor =
          Class.forName("org.uma.jmetal.problem.multiobjective.cf.CF" + id)
              .getConstructor(Integer.class, Integer.class);

      // Act
      var thrown =
          assertThrows(InvocationTargetException.class, () -> constructor.newInstance(3, 3));

      // Assert
      assertInstanceOf(InvalidConditionException.class, thrown.getCause());
    }
  }
}
