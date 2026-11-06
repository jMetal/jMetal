package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

@DisplayName("Unit tests for class ConstraintViolation")
class ConstraintViolationTest {
  private static final double EPSILON = 1e-12;

  private static DoubleSolution solutionWithConstraints(double... constraints) {
    DoubleSolution solution = new DefaultDoubleSolution(new ArrayList<>(), 2, constraints.length);
    System.arraycopy(constraints, 0, solution.constraints(), 0, constraints.length);
    return solution;
  }

  @Nested
  @DisplayName("When computing the constraint violation")
  class WhenComputingTheConstraintViolation {

    @Test
    @DisplayName("given negative constraints, when computing, then the CV is their absolute sum")
    void givenNegativeConstraints_whenComputing_thenTheViolationIsTheirAbsoluteSum() {
      // Arrange
      DoubleSolution solution = solutionWithConstraints(-1.0, 2.0, -0.5);

      // Act & Assert
      assertEquals(1.5, ConstraintViolation.of(solution), EPSILON);
      assertFalse(ConstraintViolation.isFeasible(solution));
    }

    @Test
    @DisplayName("given non-negative constraints, when computing, then the solution is feasible")
    void givenNonNegativeConstraints_whenComputing_thenTheSolutionIsFeasible() {
      // Arrange
      DoubleSolution solution = solutionWithConstraints(0.0, 3.0);

      // Act & Assert
      assertEquals(0.0, ConstraintViolation.of(solution), EPSILON);
      assertTrue(ConstraintViolation.isFeasible(solution));
    }
  }
}
