package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Construction of AdaptiveGrid and GridDensityEstimator")
class AdaptiveGridConstructionTest {

  @ParameterizedTest(name = "{0} bisections, {1} objectives")
  @CsvSource({"5, 7", "10, 4", "31, 1", "6, 6"})
  @DisplayName("given a grid with more than 2^30 hypercubes, when created, then an exception names the problem")
  void givenAGridThatIsTooLarge_whenCreated_thenAnExceptionIsRaised(int bisections, int objectives) {
    assertThrows(InvalidConditionException.class, () -> new AdaptiveGrid<DoubleSolution>(bisections, objectives));
  }

  @Test
  @DisplayName("given a grid of 2^15 hypercubes, when created, then it has that many hypercubes")
  void givenAValidGrid_whenCreated_thenItHasTheHypercubes() {
    assertThat(new AdaptiveGrid<DoubleSolution>(5, 3).getHypercubes()).hasSize(1 << 15);
  }

  @Test
  @DisplayName("given a single solution, when the density is computed, then its value is one")
  void givenASingleSolution_whenTheDensityIsComputed_thenItsValueIsOne() {
    // Arrange
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = 0.3;
    solution.objectives()[1] = 0.7;
    List<DoubleSolution> list = new ArrayList<>(List.of(solution));
    var estimator = new GridDensityEstimator<DoubleSolution>(5, 2);

    // Act
    estimator.compute(list);

    // Assert
    assertThat(estimator.value(solution)).isEqualTo(1.0);
  }
}
