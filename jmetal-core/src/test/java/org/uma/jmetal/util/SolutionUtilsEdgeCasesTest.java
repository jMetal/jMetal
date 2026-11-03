package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;

@DisplayName("Edge cases of SolutionUtils")
class SolutionUtilsEdgeCasesTest {

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution = new FakeDoubleProblem(2, objectives.length, 0).createSolution();
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  @Nested
  @DisplayName("When computing the normalized distance between objectives")
  class WhenComputingTheNormalizedDistance {
    @Test
    @DisplayName("given ranges of the objectives, when computed, then each difference is divided by its range")
    void givenRanges_whenComputed_thenEachDifferenceIsDividedByItsRange() {
      // Arrange: differences 1 and 2, ranges 2 and 4: (0.5, 0.5)
      DoubleSolution first = solution(1.0, 2.0);
      DoubleSolution second = solution(2.0, 4.0);

      // Act
      double distance =
          SolutionUtils.normalizedDistanceBetweenObjectives(
              first, second, new double[] {3.0, 6.0}, new double[] {1.0, 2.0});

      // Assert
      assertThat(distance).isCloseTo(Math.sqrt(0.5), org.assertj.core.data.Offset.offset(1e-12));
    }

    @Test
    @DisplayName("given an objective with a range of zero, when computed, then it adds nothing")
    void givenAZeroRange_whenComputed_thenThatObjectiveAddsNothing() {
      // Arrange: the second objective is the same in the whole front
      DoubleSolution first = solution(1.0, 5.0);
      DoubleSolution second = solution(3.0, 5.0);

      // Act
      double distance =
          SolutionUtils.normalizedDistanceBetweenObjectives(
              first, second, new double[] {3.0, 5.0}, new double[] {1.0, 5.0});

      // Assert: 2 / 2 = 1, and not NaN
      assertThat(distance).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
    }
  }

  @Nested
  @DisplayName("When computing the average distance to a list")
  class WhenComputingTheAverageDistance {
    @Test
    @DisplayName("given an empty list, when computed, then an exception is raised and not NaN")
    void givenAnEmptyList_whenComputed_thenAnExceptionIsRaised() {
      assertThrows(
          EmptyCollectionException.class,
          () -> SolutionUtils.averageDistanceToSolutionList(solution(1.0, 1.0), List.<DoubleSolution>of()));
    }
  }
}
