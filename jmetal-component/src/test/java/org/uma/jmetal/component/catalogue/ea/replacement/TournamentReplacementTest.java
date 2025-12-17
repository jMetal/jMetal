package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.TournamentReplacement;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("TournamentReplacement tests")
class TournamentReplacementTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given default constructor, when getTournamentSize is called, then returns default value")
    void givenDefaultConstructor_whenGetTournamentSizeIsCalled_thenReturnsDefaultValue() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      TournamentReplacement<DoubleSolution> replacement = new TournamentReplacement<>(comparator);

      // Assert
      assertThat(replacement.getTournamentSize())
          .isEqualTo(TournamentReplacement.DEFAULT_TOURNAMENT_SIZE);
    }

    @Test
    @DisplayName("Given custom tournament size, when getTournamentSize is called, then returns custom value")
    void givenCustomTournamentSize_whenGetTournamentSizeIsCalled_thenReturnsCustomValue() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      int tournamentSize = 5;

      // Act
      TournamentReplacement<DoubleSolution> replacement =
          new TournamentReplacement<>(tournamentSize, comparator);

      // Assert
      assertThat(replacement.getTournamentSize()).isEqualTo(tournamentSize);
    }
  }

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
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {(double) i});
        when(o.objectives()).thenReturn(new double[] {(double) (i + 10)});
        population.add(p);
        offspring.add(o);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement = new TournamentReplacement<>(comparator);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
    }

    @Test
    @DisplayName("Given populations with different fitness, when replace is called with large tournament, then better solutions are favored")
    void givenPopulationsWithDifferentFitness_whenReplaceIsCalledWithLargeTournament_thenBetterSolutionsAreFavored() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {(double) i}); // 0-9
        when(o.objectives()).thenReturn(new double[] {(double) (i + 10)}); // 10-19
        population.add(p);
        offspring.add(o);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement =
          new TournamentReplacement<>(10, comparator);

      // Act
      Map<DoubleSolution, Integer> selectionCounts = new HashMap<>();
      int iterations = 100;
      for (int iter = 0; iter < iterations; iter++) {
        List<DoubleSolution> result = replacement.replace(population, offspring);
        for (DoubleSolution s : result) {
          selectionCounts.merge(s, 1, Integer::sum);
        }
      }

      // Assert - best solutions (lower objective) should be selected more often
      DoubleSolution bestSolution = population.get(0);
      DoubleSolution worstSolution = offspring.get(9);
      assertThat(selectionCounts.getOrDefault(bestSolution, 0))
          .isGreaterThan(selectionCounts.getOrDefault(worstSolution, 0));
    }

    @Test
    @DisplayName("Given larger tournament size, when replace is called, then selection pressure increases")
    void givenLargerTournamentSize_whenReplaceIsCalled_thenSelectionPressureIncreases() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {(double) i});
        when(o.objectives()).thenReturn(new double[] {(double) (i + 10)});
        population.add(p);
        offspring.add(o);
      }
      DoubleSolution bestSolution = population.get(0);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> smallTournament =
          new TournamentReplacement<>(2, comparator);
      TournamentReplacement<DoubleSolution> largeTournament =
          new TournamentReplacement<>(10, comparator);

      // Act
      int smallTournamentBestCount = 0;
      int largeTournamentBestCount = 0;
      int iterations = 100;

      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> smallResult = smallTournament.replace(population, offspring);
        List<DoubleSolution> largeResult = largeTournament.replace(population, offspring);

        smallTournamentBestCount += smallResult.stream().filter(s -> s == bestSolution).count();
        largeTournamentBestCount += largeResult.stream().filter(s -> s == bestSolution).count();
      }

      // Assert - larger tournament should select best solution more often
      assertThat(largeTournamentBestCount).isGreaterThan(smallTournamentBestCount);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given empty offspring, when replace is called, then result contains parent solutions")
    void givenEmptyOffspring_whenReplaceIsCalled_thenResultContainsParentSolutions() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {(double) i});
        population.add(p);
      }
      List<DoubleSolution> offspring = new ArrayList<>();
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement = new TournamentReplacement<>(comparator);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
      assertThat(result).allMatch(population::contains);
    }

    @Test
    @DisplayName("Given tournament size larger than joint population, when replace is called, then it adapts")
    void givenTournamentSizeLargerThanJointPopulation_whenReplaceIsCalled_thenItAdapts() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 3; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {(double) i});
        when(o.objectives()).thenReturn(new double[] {(double) (i + 3)});
        population.add(p);
        offspring.add(o);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement =
          new TournamentReplacement<>(100, comparator); // Much larger than population

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
    }

    @Test
    @DisplayName("Given single solution populations, when replace is called, then result has one solution")
    void givenSingleSolutionPopulations_whenReplaceIsCalled_thenResultHasOneSolution() {
      // Arrange
      DoubleSolution parent = mock(DoubleSolution.class);
      DoubleSolution child = mock(DoubleSolution.class);
      when(parent.objectives()).thenReturn(new double[] {1.0});
      when(child.objectives()).thenReturn(new double[] {2.0});
      List<DoubleSolution> population = new ArrayList<>(List.of(parent));
      List<DoubleSolution> offspring = new ArrayList<>(List.of(child));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement = new TournamentReplacement<>(comparator);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Given solutions with equal fitness, when replace is called, then any can be selected")
    void givenSolutionsWithEqualFitness_whenReplaceIsCalled_thenAnyCanBeSelected() {
      // Arrange
      List<DoubleSolution> population = new ArrayList<>();
      List<DoubleSolution> offspring = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution p = mock(DoubleSolution.class);
        DoubleSolution o = mock(DoubleSolution.class);
        when(p.objectives()).thenReturn(new double[] {1.0}); // All equal
        when(o.objectives()).thenReturn(new double[] {1.0}); // All equal
        population.add(p);
        offspring.add(o);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TournamentReplacement<DoubleSolution> replacement = new TournamentReplacement<>(comparator);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertThat(result).hasSize(population.size());
    }
  }
}
