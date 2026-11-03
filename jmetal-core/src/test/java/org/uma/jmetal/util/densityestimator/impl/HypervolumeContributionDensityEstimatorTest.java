package org.uma.jmetal.util.densityestimator.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class HypervolumeContributionDensityEstimator")
class HypervolumeContributionDensityEstimatorTest {
  private HypervolumeContributionDensityEstimator<DoubleSolution> subject;

  @BeforeEach
  void setUp() {
    subject = new HypervolumeContributionDensityEstimator<>(new double[] {1.0, 1.0});
  }

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Nested
  @DisplayName("When the contributions are computed")
  class WhenTheContributionsAreComputed {
    @Test
    @DisplayName("given a front, when computed, then the interior point contributes the most")
    void givenAFront_whenComputed_thenTheValuesAreTheContributions() {
      // Arrange
      // The legacy hypervolume sorts the list that it receives, so it must be modifiable
      List<DoubleSolution> front =
          new ArrayList<>(List.of(solution(0.2, 0.8), solution(0.5, 0.5), solution(0.8, 0.2)));

      // Act
      subject.compute(front);

      // Assert: the value is the hypervolume contribution, not the default 0.0
      assertThat(front).allSatisfy(s -> assertThat(subject.value(s)).isPositive());
    }

    @Test
    @DisplayName("given a front, when computed, then the comparator puts first the lowest value")
    void givenAFront_whenComputed_thenTheComparatorOrdersByTheContribution() {
      // Arrange
      DoubleSolution crowded = solution(0.5, 0.5);
      DoubleSolution isolated = solution(0.1, 0.9);
      List<DoubleSolution> front =
          new ArrayList<>(List.of(isolated, crowded, solution(0.52, 0.48)));

      // Act
      subject.compute(front);

      // Assert
      assertThat(subject.value(crowded)).isLessThan(subject.value(isolated));
      assertThat(subject.comparator().compare(crowded, isolated)).isNegative();
    }

    @Test
    @DisplayName("given an empty list, when computed, then nothing happens")
    void givenAnEmptyList_whenComputed_thenNothingHappens() {
      subject.compute(List.of());

      assertThat(subject.value(solution(0.5, 0.5))).isZero();
    }
  }
}
