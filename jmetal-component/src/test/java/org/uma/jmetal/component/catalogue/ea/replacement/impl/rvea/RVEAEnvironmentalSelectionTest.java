package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("RVEAEnvironmentalSelection tests")
class RVEAEnvironmentalSelectionTest {

  @Nested
  @DisplayName("Execute method behavior tests")
  class ExecuteMethodBehaviorTests {

    @Test
    @DisplayName("Given empty niches, when execute is called, then result size equals the number of occupied reference vectors")
    void givenEmptyNiches_whenExecuteIsCalled_thenResultSizeEqualsTheNumberOfOccupiedReferenceVectors() {
      // Arrange
      double[][] referenceVectors = {
          {0.0, 0.0, 1.0},
          {0.0, 1.0, 0.0},
          {1.0, 0.0, 0.0}
      };
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(referenceVectors, 10, 2.0, 0.5);

      DoubleSolution solution1 = solutionWithObjectives(1.0, 10.0, 10.0);
      DoubleSolution solution2 = solutionWithObjectives(10.0, 1.0, 10.0);
      DoubleSolution solution3 = solutionWithObjectives(2.0, 2.0, 10.0);
      DoubleSolution solution4 = solutionWithObjectives(3.0, 2.5, 10.0);
      List<DoubleSolution> jointPopulation = List.of(solution1, solution2, solution3, solution4);

      // Act
      List<DoubleSolution> result = environmentalSelection.execute(jointPopulation, 3);

      // Assert
      assertThat(result).hasSize(2);
      assertThat(result).allMatch(jointPopulation::contains);
    }

    @Test
    @DisplayName("Given an adaptation generation, when execute is called, then reference vectors adapt using the survivor nadir point")
    void givenAnAdaptationGeneration_whenExecuteIsCalled_thenReferenceVectorsAdaptUsingTheSurvivorNadirPoint() {
      // Arrange
      double[][] referenceVectors = {
          {0.0, 1.0},
          {0.5, 0.5},
          {1.0, 0.0}
      };
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(referenceVectors, 1, 2.0, 1.0);

      DoubleSolution idealSolution = solutionWithObjectives(1.0, 1.0);
      DoubleSolution xAxisSolution = solutionWithObjectives(11.0, 1.0);
      DoubleSolution yAxisSolution = solutionWithObjectives(1.0, 2.0);
      DoubleSolution diagonalSolution = solutionWithObjectives(2.0, 2.0);
      DoubleSolution discardedOutlier = solutionWithObjectives(101.0, 1.2);

      List<DoubleSolution> jointPopulation =
          List.of(idealSolution, xAxisSolution, yAxisSolution, diagonalSolution, discardedOutlier);

      // Act
      environmentalSelection.execute(jointPopulation, 3);
      double[][] adaptedReferenceVectors = environmentalSelection.referenceVectors();

      // Assert
      double expectedX = 10.0 / Math.sqrt(101.0);
      double expectedY = 1.0 / Math.sqrt(101.0);

      assertThat(adaptedReferenceVectors[1][0]).isCloseTo(expectedX, within(1.0e-12));
      assertThat(adaptedReferenceVectors[1][1]).isCloseTo(expectedY, within(1.0e-12));
    }
  }

  private DoubleSolution solutionWithObjectives(double... objectives) {
    DoubleSolution solution = mock(DoubleSolution.class);
    when(solution.objectives()).thenReturn(objectives);

    return solution;
  }
}
