package org.uma.jmetal.problem.multiobjective.cdtlz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for the C-DTLZ problems (Jain and Deb, 2014)")
class CDTLZTest {

  private static DoubleSolution withObjectives(DoubleProblem problem, double... objectives) {
    DoubleSolution solution = problem.createSolution();
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  @Nested
  @DisplayName("When creating a problem with its default constructor")
  class DefaultConstructorTests {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
      "C1_DTLZ1, 7, 1",
      "C1_DTLZ3, 12, 1",
      "C2_DTLZ2, 12, 1",
      "ConvexC2_DTLZ2, 12, 1",
      "C3_DTLZ1, 7, 3",
      "C3_DTLZ4, 12, 3"
    })
    @DisplayName("given a problem, when created, then it has 3 objectives and its dimensions")
    void givenAProblem_whenCreated_thenItHasThreeObjectives(
        String name, int variables, int constraints) throws ReflectiveOperationException {
      // Act
      DoubleProblem problem =
          (DoubleProblem)
              Class.forName("org.uma.jmetal.problem.multiobjective.cdtlz." + name)
                  .getConstructor()
                  .newInstance();

      // Assert
      assertEquals(3, problem.numberOfObjectives());
      assertEquals(variables, problem.numberOfVariables());
      assertEquals(constraints, problem.numberOfConstraints());
    }
  }

  @Nested
  @DisplayName("When evaluating the constraint of C1-DTLZ1: 1 - f3 / 0.6 - (f1 + f2) / 0.5")
  class C1DTLZ1Tests {

    @Test
    @DisplayName("given a point of the front, when evaluated, then it is feasible")
    void givenAPointOfTheFront_whenEvaluated_thenItIsFeasible() {
      // Arrange
      var problem = new C1_DTLZ1();
      DoubleSolution solution = withObjectives(problem, 0.1, 0.1, 0.3);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      assertEquals(1 - 0.3 / 0.6 - 0.2 / 0.5, solution.constraints()[0], 1e-12);
      assertTrue(solution.constraints()[0] >= 0);
    }

    @Test
    @DisplayName("given a point far from the front, when evaluated, then it is infeasible")
    void givenAPointFarFromTheFront_whenEvaluated_thenItIsInfeasible() {
      // Arrange
      var problem = new C1_DTLZ1();
      DoubleSolution solution = withObjectives(problem, 0.5, 0.5, 0.5);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      assertEquals(1 - 0.5 / 0.6 - 1.0 / 0.5, solution.constraints()[0], 1e-12);
      assertTrue(solution.constraints()[0] < 0);
    }
  }

  @Nested
  @DisplayName("When evaluating the constraint of C1-DTLZ3: (|f|^2 - 16) (|f|^2 - r^2)")
  class C1DTLZ3Tests {

    @Test
    @DisplayName("given a point of the front, the unit sphere, when evaluated, then it is feasible")
    void givenAPointOfTheFront_whenEvaluated_thenItIsFeasible() {
      // Arrange
      var problem = new C1_DTLZ3();
      DoubleSolution solution = withObjectives(problem, 1.0, 0.0, 0.0);

      // Act
      problem.evaluateConstraints(solution);

      // Assert: r = 9 with 3 objectives
      assertEquals((1 - 16) * (1 - 81), solution.constraints()[0], 1e-12);
    }

    @Test
    @DisplayName("given a point between the radii 4 and r, when evaluated, then it is infeasible")
    void givenAPointInTheInfeasibleBand_whenEvaluated_thenItIsInfeasible() {
      // Arrange
      var problem = new C1_DTLZ3();
      DoubleSolution solution = withObjectives(problem, 5.0, 0.0, 0.0);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      assertEquals((25 - 16) * (25 - 81), solution.constraints()[0], 1e-12);
      assertTrue(solution.constraints()[0] < 0);
    }

    @ParameterizedTest(name = "{0} objectives: r = {1}")
    @CsvSource({"2, 6.0", "3, 9.0", "5, 12.5", "8, 12.5", "10, 15.0", "15, 15.0"})
    @DisplayName("given a number of objectives, then the radius is the one of the definition")
    void givenANumberOfObjectives_whenAsked_thenTheRadiusIsTheDefinedOne(int m, double r) {
      // Act & Assert
      assertEquals(r, C1_DTLZ3.radius(m));
    }
  }

  @Nested
  @DisplayName("When evaluating the constraint of C2-DTLZ2 (r = 0.4 with 3 objectives)")
  class C2DTLZ2Tests {

    @Test
    @DisplayName("given a corner of the front, when evaluated, then it is feasible")
    void givenACornerOfTheFront_whenEvaluated_thenItIsFeasible() {
      // Arrange
      var problem = new C2_DTLZ2();
      DoubleSolution solution = withObjectives(problem, 1.0, 0.0, 0.0);

      // Act
      problem.evaluateConstraints(solution);

      // Assert: inside the sphere of radius 0.4 centered at (1, 0, 0)
      assertEquals(0.16, solution.constraints()[0], 1e-12);
    }

    @Test
    @DisplayName("given the center of the front, when evaluated, then it is feasible")
    void givenTheCenterOfTheFront_whenEvaluated_thenItIsFeasible() {
      // Arrange
      var problem = new C2_DTLZ2();
      double c = 1.0 / Math.sqrt(3);
      DoubleSolution solution = withObjectives(problem, c, c, c);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      assertEquals(0.16, solution.constraints()[0], 1e-12);
    }

    @Test
    @DisplayName("given a point of the front between the spheres, when evaluated, then it is infeasible")
    void givenAPointBetweenTheSpheres_whenEvaluated_thenItIsInfeasible() {
      // Arrange
      var problem = new C2_DTLZ2();
      DoubleSolution solution = withObjectives(problem, 0.8, 0.6, 0.0);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      assertTrue(solution.constraints()[0] < 0);
    }
  }

  @Nested
  @DisplayName("When evaluating the constraints of C3-DTLZ1: sum_{i != j} f_i + f_j / 0.5 - 1")
  class C3DTLZ1Tests {

    @Test
    @DisplayName("given a point of the front, on the constraints, when evaluated, then they are zero")
    void givenAPointOfTheFront_whenEvaluated_thenTheConstraintsAreZero() {
      // Arrange
      var problem = new C3_DTLZ1();
      DoubleSolution solution = withObjectives(problem, 0.25, 0.25, 0.25);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      for (double constraint : solution.constraints()) {
        assertEquals(0.0, constraint, 1e-12);
      }
    }

    @Test
    @DisplayName("given a point below the front, when evaluated, then every constraint is violated")
    void givenAPointBelowTheFront_whenEvaluated_thenEveryConstraintIsViolated() {
      // Arrange
      var problem = new C3_DTLZ1();
      DoubleSolution solution = withObjectives(problem, 0.1, 0.1, 0.1);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      for (double constraint : solution.constraints()) {
        assertEquals(0.2 + 0.2 - 1.0, constraint, 1e-12);
      }
    }
  }

  @Nested
  @DisplayName("When evaluating the constraints of C3-DTLZ4: f_j^2 / 4 + sum_{i != j} f_i^2 - 1")
  class C3DTLZ4Tests {

    @Test
    @DisplayName("given a point of the front, on the constraints, when evaluated, then they are zero")
    void givenAPointOfTheFront_whenEvaluated_thenTheConstraintsAreZero() {
      // Arrange
      var problem = new C3_DTLZ4();
      DoubleSolution solution = withObjectives(problem, 2.0 / 3, 2.0 / 3, 2.0 / 3);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      for (double constraint : solution.constraints()) {
        assertEquals(0.0, constraint, 1e-12);
      }
    }

    @Test
    @DisplayName("given a point below the front, when evaluated, then every constraint is violated")
    void givenAPointBelowTheFront_whenEvaluated_thenEveryConstraintIsViolated() {
      // Arrange
      var problem = new C3_DTLZ4();
      DoubleSolution solution = withObjectives(problem, 0.5, 0.5, 0.5);

      // Act
      problem.evaluateConstraints(solution);

      // Assert
      for (double constraint : solution.constraints()) {
        assertEquals(0.0625 + 0.5 - 1.0, constraint, 1e-12);
      }
    }
  }

  @Nested
  @DisplayName("When evaluating convex C2-DTLZ2")
  class ConvexC2DTLZ2Tests {

    @Test
    @DisplayName("given a solution, when evaluated, then the objectives are those of convex DTLZ2")
    void givenASolution_whenEvaluated_thenTheObjectivesAreConvex() {
      // Arrange: on the front of DTLZ2 (g = 0), with f = (cos 45, 0, sin 45)
      var problem = new ConvexC2_DTLZ2();
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < problem.numberOfVariables(); i++) {
        solution.variables().set(i, 0.5);
      }
      solution.variables().set(1, 0.0);

      // Act
      problem.evaluate(solution);

      // Assert: f1^4, f2^4 and f3^2
      assertEquals(0.25, solution.objectives()[0], 1e-12);
      assertEquals(0.0, solution.objectives()[1], 1e-12);
      assertEquals(0.5, solution.objectives()[2], 1e-12);
    }
  }
}
