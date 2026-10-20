package org.uma.jmetal.problem.multiobjective.cf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for the CF problems")
class CFTest {

  @Nested
  @DisplayName("When creating a problem without arguments")
  class DefaultConstructorTests {

    @ParameterizedTest(name = "CF{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    @DisplayName("given each problem, when created and evaluated, then it has 3 objectives")
    void givenEachProblem_whenCreatedWithoutArguments_thenItHasThreeObjectives(int id)
        throws ReflectiveOperationException {
      // Arrange
      DoubleProblem problem =
          (DoubleProblem)
              Class.forName("org.uma.jmetal.problem.multiobjective.cf.CF" + id)
                  .getConstructor()
                  .newInstance();
      DoubleSolution solution = problem.createSolution();

      // Act
      problem.evaluate(solution);

      // Assert
      assertEquals(3, problem.numberOfObjectives());
      assertEquals(3, solution.objectives().length);
    }
  }
}
