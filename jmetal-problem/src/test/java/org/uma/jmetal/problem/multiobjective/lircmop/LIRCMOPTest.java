package org.uma.jmetal.problem.multiobjective.lircmop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

  /** A solution of the Pareto set of LIRCMOP5 and LIRCMOP6 (g1 = g2 = 0) with x1 = 0.5. */
  private static DoubleSolution paretoOptimalSolution(DoubleProblem problem) {
    DoubleSolution solution = problem.createSolution();
    int n = problem.numberOfVariables();
    solution.variables().set(0, 0.5);
    for (int i = 1; i < n; i++) {
      double angle = 0.5 * i / n * Math.PI * 0.5;
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
