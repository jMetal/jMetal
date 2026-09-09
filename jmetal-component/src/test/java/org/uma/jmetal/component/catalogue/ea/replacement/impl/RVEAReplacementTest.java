package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("RVEAReplacement tests")
class RVEAReplacementTest {

  @Test
  @DisplayName(
      "given empty niches, when replacing twice, then the nominal vector count survives population"
          + " shrinkage")
  void givenEmptyNiches_whenReplacingTwice_thenNominalVectorCountSurvivesPopulationShrinkage() {
    // Arrange
    RVEAReplacement<DoubleSolution> replacement =
        new RVEAReplacement<>(new RVEAEnvironmentalSelection<>(3, 10, 2.0, 0.5, 1));

    DoubleSolution solution1 = solutionWithObjectives(1.0, 10.0, 10.0);
    DoubleSolution solution2 = solutionWithObjectives(10.0, 1.0, 10.0);
    DoubleSolution solution3 = solutionWithObjectives(2.0, 2.0, 10.0);
    DoubleSolution solution4 = solutionWithObjectives(3.0, 2.5, 10.0);

    List<DoubleSolution> currentPopulation = List.of(solution1, solution2, solution3);
    List<DoubleSolution> offspringPopulation = List.of(solution4);
    List<DoubleSolution> jointPopulation = List.of(solution1, solution2, solution3, solution4);

    // Act
    List<DoubleSolution> result = replacement.replace(currentPopulation, offspringPopulation);
    List<DoubleSolution> nextResult = replacement.replace(result, offspringPopulation);

    // Assert
    assertThat(result).hasSize(2).allMatch(jointPopulation::contains).doesNotHaveDuplicates();
    assertThat(nextResult).containsExactly(solution3, solution4);
  }

  private DoubleSolution solutionWithObjectives(double... objectives) {
    DoubleSolution solution = mock(DoubleSolution.class);
    when(solution.objectives()).thenReturn(objectives);
    when(solution.constraints()).thenReturn(new double[0]);

    return solution;
  }
}
