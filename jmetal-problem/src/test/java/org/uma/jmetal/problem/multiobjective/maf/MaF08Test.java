package org.uma.jmetal.problem.multiobjective.maf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class MaF08")
class MaF08Test {

  private static final double EPSILON = 1.0e-12;

  @Nested
  @DisplayName("When constructing the problem")
  class WhenConstructing {

    @Test
    @DisplayName("given the default constructor, then it has two variables and ten objectives")
    void givenDefaultConstructor_whenCreated_thenItHasTwoVariablesAndTenObjectives() {
      DoubleProblem problem = new MaF08();

      assertThat(problem.numberOfVariables()).isEqualTo(2);
      assertThat(problem.numberOfObjectives()).isEqualTo(10);
      assertThat(problem.numberOfConstraints()).isZero();
      assertThat(problem.name()).isEqualTo("MaF08");
    }

    @Test
    @DisplayName("given any number of objectives, then the decision space is the one of the MaF suite")
    void givenAnyNumberOfObjectives_whenCreated_thenDecisionSpaceIsTheOneOfTheMaFSuite() {
      for (int numberOfObjectives = 3; numberOfObjectives <= 15; numberOfObjectives++) {
        DoubleProblem problem = new MaF08(2, numberOfObjectives);

        for (int i = 0; i < problem.numberOfVariables(); i++) {
          assertThat(problem.variableBounds().get(i).getLowerBound()).isEqualTo(-10000.0);
          assertThat(problem.variableBounds().get(i).getUpperBound()).isEqualTo(10000.0);
        }
      }
    }

    @Test
    @DisplayName("given any number of objectives, then the whole polygon is inside the decision space")
    void givenAnyNumberOfObjectives_whenCreated_thenThePolygonIsInsideTheDecisionSpace() {
      for (int numberOfObjectives = 3; numberOfObjectives <= 15; numberOfObjectives++) {
        DoubleProblem problem = new MaF08(2, numberOfObjectives);

        for (double[] vertex : MaF08.polygonpoints(numberOfObjectives, 1.0)) {
          for (int i = 0; i < 2; i++) {
            assertThat(vertex[i])
                .isBetween(
                    problem.variableBounds().get(i).getLowerBound(),
                    problem.variableBounds().get(i).getUpperBound());
          }
        }
      }
    }
  }

  @Nested
  @DisplayName("When evaluating solutions")
  class WhenEvaluating {

    @Test
    @DisplayName("given a vertex of the polygon, when evaluated, then its distance to itself is zero")
    void givenAVertexOfThePolygon_whenEvaluated_thenTheDistanceToItselfIsZero() {
      int numberOfObjectives = 3;
      DoubleProblem problem = new MaF08(2, numberOfObjectives);
      double[][] vertices = MaF08.polygonpoints(numberOfObjectives, 1.0);

      for (int k = 0; k < numberOfObjectives; k++) {
        DoubleSolution solution = solutionAt(problem, vertices[k][0], vertices[k][1]);

        problem.evaluate(solution);

        assertThat(solution.objectives()[k]).isCloseTo(0.0, within(EPSILON));
      }
    }

    @Test
    @DisplayName("given the center of the polygon, when evaluated, then every objective is its radius")
    void givenTheCenterOfThePolygon_whenEvaluated_thenEveryObjectiveIsItsRadius() {
      DoubleProblem problem = new MaF08(2, 5);
      DoubleSolution solution = solutionAt(problem, 0.0, 0.0);

      problem.evaluate(solution);

      for (double objective : solution.objectives()) {
        assertThat(objective).isCloseTo(1.0, within(EPSILON));
      }
    }
  }

  private static DoubleSolution solutionAt(DoubleProblem problem, double x1, double x2) {
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, x1);
    solution.variables().set(1, x2);
    return solution;
  }
}
