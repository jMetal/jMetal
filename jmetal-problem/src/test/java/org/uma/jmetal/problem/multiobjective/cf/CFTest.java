package org.uma.jmetal.problem.multiobjective.cf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
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
