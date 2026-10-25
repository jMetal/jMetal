package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;

@DisplayName("Unit tests for class MatingProportions")
class MatingProportionsTest {
  private static final double EPSILON = 1e-12;

  /** Creates a solution; a negative constraint value is a violation. */
  private static DoubleSolution solution(double constraint, double... objectives) {
    DoubleSolution solution = new DefaultDoubleSolution(new ArrayList<>(), objectives.length, 1);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    solution.constraints()[0] = constraint;
    return solution;
  }

  @Nested
  @DisplayName("When computing the proportions")
  class WhenComputingTheProportions {

    @Test
    @DisplayName("given two archives, when computing, then the shares follow PlatEMO")
    void givenTwoArchives_whenComputing_thenTheSharesFollowPlatEMO() {
      // Arrange: in the union, (0, 1), (1, 0) and (0.5, 0.5) are non-dominated; in the CA alone,
      // (0, 1) and (1, 0) are; the constraints are ignored
      List<DoubleSolution> convergenceArchive =
          List.of(solution(0.0, 0.0, 1.0), solution(-1.0, 1.0, 0.0), solution(0.0, 2.0, 2.0));
      List<DoubleSolution> diversityArchive =
          List.of(solution(-1.0, 0.5, 0.5), solution(0.0, 3.0, 3.0), solution(0.0, 4.0, 4.0));

      // Act
      MatingProportions proportions = MatingProportions.of(convergenceArchive, diversityArchive);

      // Assert
      assertEquals(2.0 / 6.0, proportions.convergenceShare(), EPSILON);
      assertEquals(1.0 / 6.0, proportions.diversityShare(), EPSILON);
      assertEquals(2.0 / 3.0, proportions.nonDominatedShareOfConvergenceArchive(), EPSILON);
    }

    @Test
    @DisplayName("given a dominated CA, when computing, then its share in the union is zero")
    void givenADominatedCA_whenComputing_thenItsShareInTheUnionIsZero() {
      // Arrange
      List<DoubleSolution> convergenceArchive =
          List.of(solution(0.0, 2.0, 2.0), solution(0.0, 3.0, 1.5));
      List<DoubleSolution> diversityArchive =
          List.of(solution(-1.0, 1.0, 1.0), solution(-1.0, 1.5, 1.2));

      // Act
      MatingProportions proportions = MatingProportions.of(convergenceArchive, diversityArchive);

      // Assert
      assertEquals(0.0, proportions.convergenceShare(), EPSILON);
      assertEquals(1.0 / 4.0, proportions.diversityShare(), EPSILON);
      assertEquals(1.0, proportions.nonDominatedShareOfConvergenceArchive(), EPSILON);
    }

    @Test
    @DisplayName("given an empty CA, when computing, then an exception is thrown")
    void givenAnEmptyCA_whenComputing_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(
          EmptyCollectionException.class,
          () -> MatingProportions.of(List.<DoubleSolution>of(), List.of(solution(0.0, 1.0))));
    }
  }
}
