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
    @DisplayName("Given empty niches, when execute is called, then the configured population size is preserved without duplicates")
    void givenEmptyNiches_whenExecuteIsCalled_thenTheConfiguredPopulationSizeIsPreservedWithoutDuplicates() {
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
      assertThat(result).hasSize(3).allMatch(jointPopulation::contains).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Given multiple executions, when execute is called, then the ideal point is recomputed from the current candidate population")
    void givenMultipleExecutions_whenExecuteIsCalled_thenTheIdealPointIsRecomputedFromTheCurrentCandidatePopulation() {
      // Arrange
      double[][] referenceVectors = {
          {0.0, 1.0},
          {0.5, 0.5},
          {1.0, 0.0}
      };
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(referenceVectors, 1, 2.0, 1.0);

      // Act
      environmentalSelection.execute(firstPopulation, 2);
      environmentalSelection.execute(secondPopulation, 2);

      // Assert
      assertThat(environmentalSelection.idealPoint()).containsExactly(5.0, 5.0);
    }

    @Test
    @DisplayName("Given adaptive reference vectors, when execute is called, then the adaptation uses the current survivor ideal and nadir points")
    void givenAdaptiveReferenceVectors_whenExecuteIsCalled_thenTheAdaptationUsesTheCurrentSurvivorIdealAndNadirPoints() {
      // Arrange
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(
              2,
              2,
              2.0,
              0.1,
              List.of(new double[] {1.0, 0.0}, new double[] {1.0, 1.0}, new double[] {0.0, 1.0}));

      List<DoubleSolution> firstPopulation = List.of(
          solutionWithObjectives(2.0, 1.0),
          solutionWithObjectives(1.0, 2.0),
          solutionWithObjectives(2.0, 2.0));
      List<DoubleSolution> secondPopulation = List.of(
          solutionWithObjectives(7.0, 5.0),
          solutionWithObjectives(5.0, 6.0),
          solutionWithObjectives(7.0, 6.0));

      // Act
      environmentalSelection.execute(firstPopulation, 3);
      environmentalSelection.execute(secondPopulation, 3);
      double[][] adaptedReferenceVectors = environmentalSelection.referenceVectors();

      // Assert
      double expectedX = 2.0 / Math.sqrt(5.0);
      double expectedY = 1.0 / Math.sqrt(5.0);

      assertThat(environmentalSelection.idealPoint()).containsExactly(5.0, 5.0);
      assertThat(environmentalSelection.nadirPoint()).containsExactly(7.0, 6.0);
      assertThat(adaptedReferenceVectors[1][0]).isCloseTo(expectedX, within(1.0e-12));
      assertThat(adaptedReferenceVectors[1][1]).isCloseTo(expectedY, within(1.0e-12));
    }

    @Test
    @DisplayName("Given the first generation, when execute is called, then reference vectors adapt on the first eligible generation")
    void givenTheFirstGeneration_whenExecuteIsCalled_thenReferenceVectorsAdaptOnTheFirstEligibleGeneration() {
      // Arrange
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(
              2,
              10,
              2.0,
              0.5,
              List.of(new double[] {1.0, 0.0}, new double[] {1.0, 1.0}, new double[] {0.0, 1.0}));

      List<DoubleSolution> population = List.of(
          solutionWithObjectives(7.0, 5.0),
          solutionWithObjectives(5.0, 6.0),
          solutionWithObjectives(7.0, 6.0));

      // Act
      environmentalSelection.execute(population, 3);
      double[][] adaptedReferenceVectors = environmentalSelection.referenceVectors();

      // Assert
      double expectedX = 2.0 / Math.sqrt(5.0);
      double expectedY = 1.0 / Math.sqrt(5.0);

      assertThat(environmentalSelection.currentGeneration()).isEqualTo(0);
      assertThat(adaptedReferenceVectors[1][0]).isCloseTo(expectedX, within(1.0e-12));
      assertThat(adaptedReferenceVectors[1][1]).isCloseTo(expectedY, within(1.0e-12));
    }

    @Test
    @DisplayName("Given two candidates in the same bi objective niche, when execute is called, then APD uses the number of objectives as the penalty factor")
    void givenTwoCandidatesInTheSameBiObjectiveNiche_whenExecuteIsCalled_thenAPDUsesTheNumberOfObjectivesAsThePenaltyFactor() {
      // Arrange
      RVEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
          new RVEAEnvironmentalSelection<>(
              2,
              1,
              1.0,
              1.0,
              List.of(new double[] {1.0, 0.0}, new double[] {0.0, 1.0}));

      List<DoubleSolution> warmUpPopulation =
          List.of(solutionWithObjectives(1.0, 0.0), solutionWithObjectives(0.0, 1.0));

      DoubleSolution idealSolution = solutionWithObjectives(0.0, 0.0);
      double gamma = Math.PI / 2.0;
      DoubleSolution widerAngleSolution =
          solutionWithObjectives(1.02 * Math.sin(gamma * 0.4), 1.02 * Math.cos(gamma * 0.4));
      DoubleSolution narrowerAngleSolution =
          solutionWithObjectives(1.5 * Math.sin(gamma * 0.05), 1.5 * Math.cos(gamma * 0.05));

      // Act
      environmentalSelection.execute(warmUpPopulation, 2);
      List<DoubleSolution> result = environmentalSelection.execute(
          List.of(idealSolution, widerAngleSolution, narrowerAngleSolution), 2);

      // Assert
      assertThat(result).contains(idealSolution, narrowerAngleSolution);
      assertThat(result).doesNotContain(widerAngleSolution);
    }
  }

  private DoubleSolution solutionWithObjectives(double... objectives) {
    DoubleSolution solution = mock(DoubleSolution.class);
    when(solution.objectives()).thenReturn(objectives);

    return solution;
  }
}
