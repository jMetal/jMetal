package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RandomReplacement;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("RandomReplacement tests")
class RandomReplacementTest {

  @Nested
  @DisplayName("Replace method behavior tests")
  class ReplaceBehaviorTests {

    @Test
    @DisplayName("Given populations, when replace is called, then result has correct size")
    void givenPopulations_whenReplaceIsCalled_thenResultHasCorrectSize() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        population.add(mock(DoubleSolution.class));
        offspring.add(mock(DoubleSolution.class));
      }
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
    }

    @Test
    @DisplayName("Given populations, when replace is called, then all solutions come from joint population")
    void givenPopulations_whenReplaceIsCalled_thenAllSolutionsComeFromJointPopulation() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      Set<DoubleSolution> allSolutions = new HashSet<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        population.add(p);
        offspring.add(o);
        allSolutions.add(p);
        allSolutions.add(o);
      }
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).allMatch(allSolutions::contains);
    }

    @Test
    @DisplayName("Given populations, when replace is called multiple times, then different results are possible")
    void givenPopulations_whenReplaceIsCalledMultipleTimes_thenDifferentResultsArePossible() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        population.add(mock(DoubleSolution.class));
        offspring.add(mock(DoubleSolution.class));
      }
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      Set<List<DoubleSolution>> uniqueResults = new HashSet<>();
      for (int i = 0; i < 100; i++) {
        uniqueResults.add(new ArrayList<>(replacement.replace(population, offspring)));
      }

      // Assert - should have multiple different results due to randomness
      assertThat(uniqueResults.size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("Given populations, when replace is called, then no duplicates in result")
    void givenPopulations_whenReplaceIsCalled_thenNoDuplicatesInResult() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        population.add(mock(DoubleSolution.class));
        offspring.add(mock(DoubleSolution.class));
      }
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      Set<DoubleSolution> uniqueSolutions = new HashSet<>(result);
      assertThat(uniqueSolutions).hasSize(result.size());
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given empty offspring, when replace is called, then result contains only parent solutions")
    void givenEmptyOffspring_whenReplaceIsCalled_thenResultContainsOnlyParentSolutions() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        population.add(mock(DoubleSolution.class));
      }
      List<DoubleSolution> offspring = new ArrayList<>();
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
      assertThat(result).containsExactlyInAnyOrderElementsOf(population);
    }

    @Test
    @DisplayName("Given single solution populations, when replace is called, then result has one solution")
    void givenSingleSolutionPopulations_whenReplaceIsCalled_thenResultHasOneSolution() {
      // Arrange
      DoubleSolution parent = mock(DoubleSolution.class);
      DoubleSolution child = mock(DoubleSolution.class);
      List<DoubleSolution> population = new ArrayList<>(List.of(parent));
      List<DoubleSolution> offspring = new ArrayList<>(List.of(child));
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isIn(parent, child);
    }

    @Test
    @DisplayName("Given large populations, when replace is called, then result has correct size")
    void givenLargePopulations_whenReplaceIsCalled_thenResultHasCorrectSize() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        population.add(mock(DoubleSolution.class));
        offspring.add(mock(DoubleSolution.class));
      }
      RandomReplacement<DoubleSolution> replacement = new RandomReplacement<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(100);
    }
  }
}
