package org.uma.jmetal.util.solutionattribute;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.PreferenceDistance;

@DisplayName("Unit tests for class PreferenceDistance")
class PreferenceDistanceTest {

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Test
  @DisplayName("given an objective whose values are all equal, when computed, then no distance is NaN")
  void givenAConstantObjective_whenComputed_thenNoDistanceIsNaN() {
    // Arrange
    var preferenceDistance = new PreferenceDistance<DoubleSolution>(List.of(0.2, 0.5), 0.001);
    List<DoubleSolution> front =
        new ArrayList<>(
            List.of(solution(0.1, 0.5), solution(0.4, 0.5), solution(0.7, 0.5), solution(0.9, 0.5)));

    // Act
    preferenceDistance.computeDensityEstimator(front);

    // Assert
    assertThat(front)
        .allSatisfy(s -> assertThat(preferenceDistance.getAttribute(s)).isNotNaN().isFinite());
  }

  @Test
  @DisplayName("given a front, when computed, then a solution closer to the interest point is closer")
  void givenAFront_whenComputed_thenTheCloserSolutionHasTheSmallerDistance() {
    // Arrange: the interest point is (0.2, 0.8)
    var preferenceDistance = new PreferenceDistance<DoubleSolution>(List.of(0.2, 0.8), 0.001);
    DoubleSolution close = solution(0.3, 0.7);
    DoubleSolution far = solution(0.6, 0.4);
    List<DoubleSolution> front =
        new ArrayList<>(List.of(solution(0.0, 1.0), close, far, solution(1.0, 0.0)));

    // Act
    preferenceDistance.computeDensityEstimator(front);

    // Assert
    assertThat(preferenceDistance.getAttribute(close))
        .isLessThan(preferenceDistance.getAttribute(far));
  }
}
