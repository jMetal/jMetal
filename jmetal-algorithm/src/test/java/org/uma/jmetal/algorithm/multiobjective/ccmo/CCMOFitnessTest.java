package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

@DisplayName("Unit tests for class CCMOFitness")
class CCMOFitnessTest {
  private static final double EPSILON = 1e-12;
  private static final double ROOT_2 = Math.sqrt(2.0);
  private static final double ROOT_5 = Math.sqrt(5.0);
  private static final double ROOT_17 = Math.sqrt(17.0);

  /**
   * Five vectors: a, b and c are non-dominated, b dominates d, and the other four dominate e. The
   * second nearest neighbour (k = floor(sqrt(5)) = 2) of all of them except e is at distance
   * sqrt(5), and the one of e at sqrt(17).
   */
  private static final double[][] OBJECTIVES = {{1, 4}, {2, 2}, {4, 1}, {3, 3}, {5, 5}};

  private static double density(double distance) {
    return 1.0 / (distance + 2.0);
  }

  @Nested
  @DisplayName("When the vectors have no constraint violation")
  class WhenThereAreNoViolations {

    @Test
    @DisplayName("given five vectors, when computing, then the fitness is the SPEA2 fitness")
    void givenFiveVectors_whenComputing_thenTheFitnessIsTheSpea2Fitness() {
      // Arrange
      double[] violations = new double[5];
      // Strengths: a = 1, b = 2, c = 1, d = 1, e = 0
      double[] expected = {
        density(ROOT_5),
        density(ROOT_5),
        density(ROOT_5),
        2 + density(ROOT_5),
        1 + 2 + 1 + 1 + density(ROOT_17)
      };

      // Act
      double[] fitness = CCMOFitness.compute(OBJECTIVES, violations);

      // Assert
      assertArrayEquals(expected, fitness, EPSILON);
    }

    @Test
    @DisplayName("given one vector, when computing, then its fitness is zero")
    void givenOneVector_whenComputing_thenItsFitnessIsZero() {
      // Arrange
      double[][] objectives = {{1.0, 2.0}};

      // Act
      double[] fitness = CCMOFitness.compute(objectives, new double[1]);

      // Assert
      assertArrayEquals(new double[] {0.0}, fitness, EPSILON);
    }

    @Test
    @DisplayName("given two equal vectors, when computing, then neither dominates the other")
    void givenTwoEqualVectors_whenComputing_thenNeitherDominatesTheOther() {
      // Arrange
      double[][] objectives = {{1.0, 2.0}, {1.0, 2.0}};

      // Act
      double[] fitness = CCMOFitness.compute(objectives, new double[2]);

      // Assert
      assertArrayEquals(new double[] {density(0.0), density(0.0)}, fitness, EPSILON);
    }
  }

  @Nested
  @DisplayName("When the vectors have constraint violations")
  class WhenThereAreViolations {

    @Test
    @DisplayName(
        "given an infeasible vector, when computing, then all the feasible ones dominate it")
    void givenAnInfeasibleVector_whenComputing_thenAllTheFeasibleOnesDominateIt() {
      // Arrange
      double[] violations = {1.0, 0.0, 0.0, 0.0, 0.0};
      // a no longer dominates e and is dominated by all; strengths: b = 3, c = 2, d = 2, e = 1
      double[] expected = {
        3 + 2 + 2 + 1 + density(ROOT_5),
        density(ROOT_5),
        density(ROOT_5),
        3 + density(ROOT_5),
        3 + 2 + 2 + density(ROOT_17)
      };

      // Act
      double[] fitness = CCMOFitness.compute(OBJECTIVES, violations);

      // Assert
      assertArrayEquals(expected, fitness, EPSILON);
    }

    @Test
    @DisplayName(
        "given infeasible vectors, when computing, then a smaller violation dominates and equal"
            + " violations are compared by Pareto dominance")
    void givenInfeasibleVectors_whenComputing_thenViolationFirstAndThenParetoDominance() {
      // Arrange
      double[][] objectives = {{1, 1}, {2, 2}, {10, 10}};
      double[] violations = {2.0, 2.0, 0.5};
      // The third dominates both (strength 2), the first dominates the second (strength 1)
      double[] expected = {
        2 + density(ROOT_2), 2 + 1 + density(ROOT_2), density(Math.sqrt(128.0))
      };

      // Act
      double[] fitness = CCMOFitness.compute(objectives, violations);

      // Assert
      assertArrayEquals(expected, fitness, EPSILON);
    }
  }

  @Nested
  @DisplayName("When computing the fitness of solutions")
  class WhenComputingTheFitnessOfSolutions {
    private final FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 1);

    private DoubleSolution solution(double f1, double f2, double constraint) {
      DoubleSolution solution = problem.createSolution();
      solution.objectives()[0] = f1;
      solution.objectives()[1] = f2;
      solution.constraints()[0] = constraint;
      return solution;
    }

    @Test
    @DisplayName(
        "given a violated constraint, when computing with constraints, then the feasible solution"
            + " is the best one")
    void givenAViolatedConstraint_whenComputingWithConstraints_thenTheFeasibleSolutionIsBest() {
      // Arrange
      List<DoubleSolution> solutions = List.of(solution(1, 1, -1.0), solution(2, 2, 0.5));

      // Act
      double[] fitness = CCMOFitness.compute(solutions, true);

      // Assert
      assertTrue(fitness[0] >= 1.0);
      assertTrue(fitness[1] < 1.0);
    }

    @Test
    @DisplayName(
        "given a violated constraint, when computing without constraints, then the dominating"
            + " solution is the best one")
    void givenAViolatedConstraint_whenComputingWithoutConstraints_thenTheConstraintIsIgnored() {
      // Arrange
      List<DoubleSolution> solutions = List.of(solution(1, 1, -1.0), solution(2, 2, 0.5));

      // Act
      double[] fitness = CCMOFitness.compute(solutions, false);

      // Assert
      assertTrue(fitness[0] < 1.0);
      assertTrue(fitness[1] >= 1.0);
    }

    @Test
    @DisplayName(
        "given constraint values, when computing the violations, then they are the sums of the"
            + " negative values or the precomputed values")
    void givenConstraintValues_whenComputingTheViolations_thenTheyFollowTheJMetalConvention() {
      // Arrange
      DoubleSolution violated = solution(1, 1, -1.5);
      DoubleSolution satisfied = solution(1, 1, 2.0);
      DoubleSolution precomputed = solution(1, 1, 0.0);
      ConstraintHandling.overallConstraintViolationDegree(precomputed, -3.0);

      // Act
      double[] violations =
          CCMOFitness.constraintViolations(List.of(violated, satisfied, precomputed));

      // Assert
      assertArrayEquals(new double[] {1.5, 0.0, 3.0}, violations, EPSILON);
    }
  }

  @Test
  @DisplayName("given two vectors, when computing the distances, then the diagonal is infinite")
  void givenTwoVectors_whenComputingTheDistances_thenTheDiagonalIsInfinite() {
    // Arrange
    double[][] objectives = {{0, 0}, {3, 4}};

    // Act
    double[][] distances = CCMOFitness.distances(objectives);

    // Assert
    assertEquals(Double.POSITIVE_INFINITY, distances[0][0]);
    assertEquals(Double.POSITIVE_INFINITY, distances[1][1]);
    assertEquals(5.0, distances[0][1], EPSILON);
    assertEquals(5.0, distances[1][0], EPSILON);
  }
}
