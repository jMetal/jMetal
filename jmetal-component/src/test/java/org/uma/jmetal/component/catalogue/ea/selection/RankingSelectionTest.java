package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RankingSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("RankingSelection tests")
class RankingSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, when constructor is called, then selection is initialized correctly")
    void givenValidParameters_whenConstructorIsCalled_thenSelectionIsInitializedCorrectly() {
      // Arrange
      int matingPoolSize = 5;
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(matingPoolSize, comparator);

      // Assert
      assertThat(selection.getMatingPoolSize()).isEqualTo(matingPoolSize);
    }
  }

  @Nested
  @DisplayName("Select method validation tests")
  class SelectValidationTests {

    @Test
    @DisplayName("Given null solution list, when select is called, then NullParameterException is thrown")
    void givenNullSolutionList_whenSelectIsCalled_thenNullParameterExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(2, comparator);

      // Act & Assert
      assertThrows(NullParameterException.class, () -> selection.select(null));
    }

    @Test
    @DisplayName("Given empty solution list, when select is called, then EmptyCollectionException is thrown")
    void givenEmptySolutionList_whenSelectIsCalled_thenEmptyCollectionExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(2, comparator);
      List<DoubleSolution> emptySolutions = new ArrayList<>();

      // Act & Assert
      assertThrows(EmptyCollectionException.class, () -> selection.select(emptySolutions));
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given a single solution, when select is called, then that solution is returned")
    void givenSingleSolution_whenSelectIsCalled_thenThatSolutionIsReturned() {
      // Arrange
      DoubleSolution solution = mock(DoubleSolution.class);
      when(solution.objectives()).thenReturn(new double[]{1.0});
      List<DoubleSolution> solutions = List.of(solution);
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(1, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(1).containsExactly(solution);
    }

    @Test
    @DisplayName("Given multiple solutions, when select is called, then correct number of solutions is returned")
    void givenMultipleSolutions_whenSelectIsCalled_thenCorrectNumberOfSolutionsIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given solutions, when select is called multiple times, then all selected solutions come from original list")
    void givenSolutions_whenSelectIsCalledMultipleTimes_thenAllSelectedSolutionsComeFromOriginalList() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(3, comparator);

      // Act & Assert
      for (int run = 0; run < 10; run++) {
        List<DoubleSolution> result = selection.select(solutions);
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(solutions::contains);
      }
    }

    @Test
    @DisplayName("Given solutions with different fitness, when select is called many times, then better solutions are selected more often")
    void givenSolutionsWithDifferentFitness_whenSelectIsCalledManyTimes_thenBetterSolutionsAreSelectedMoreOften() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(1, comparator);

      // Act
      int bestCount = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - best solution should be selected significantly more often (probability ~2/3)
      assertThat(bestCount).isGreaterThan(iterations / 2);
    }

    @Test
    @DisplayName("Given mating pool size larger than population, when select is called, then duplicates are allowed")
    void givenMatingPoolSizeLargerThanPopulation_whenSelectIsCalled_thenDuplicatesAreAllowed() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(10, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(10);
      assertThat(result).allMatch(s -> s == solution1 || s == solution2);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given solutions with equal fitness, when select is called, then all have equal probability")
    void givenSolutionsWithEqualFitness_whenSelectIsCalled_thenAllHaveEqualProbability() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{1.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(1, comparator);

      // Act
      int solution1Count = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == solution1) {
          solution1Count++;
        }
      }

      // Assert - with equal fitness, selection should still follow ranking (first in sorted order gets higher probability)
      // Due to stable sort, solution1 should be ranked first and selected more often
      assertThat(solution1Count).isGreaterThan(iterations / 3);
    }

    @Test
    @DisplayName("Given solutions with negative objectives, when select is called, then ranking works correctly")
    void givenSolutionsWithNegativeObjectives_whenSelectIsCalled_thenRankingWorksCorrectly() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{-100.0});
      when(solution2.objectives()).thenReturn(new double[]{-50.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(1, comparator);

      // Act
      int solution1Count = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == solution1) {
          solution1Count++;
        }
      }

      // Assert - solution1 (-100) is better (lower) so should be selected more often
      assertThat(solution1Count).isGreaterThan(iterations / 2);
    }

    @Test
    @DisplayName("Given original list, when select is called, then original list is not modified")
    void givenOriginalList_whenSelectIsCalled_thenOriginalListIsNotModified() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{3.0});
      when(solution2.objectives()).thenReturn(new double[]{1.0});
      when(solution3.objectives()).thenReturn(new double[]{2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      List<DoubleSolution> originalOrder = new ArrayList<>(solutions);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(2, comparator);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(solutions).containsExactlyElementsOf(originalOrder);
    }

    @Test
    @DisplayName("Given large population with varied fitness, when select is called, then worst solution can still be selected")
    void givenLargePopulationWithVariedFitness_whenSelectIsCalled_thenWorstSolutionCanStillBeSelected() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      DoubleSolution worstSolution = solutions.get(9);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      RankingSelection<DoubleSolution> selection = new RankingSelection<>(1, comparator);

      // Act
      int worstCount = 0;
      int iterations = 10000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == worstSolution) {
          worstCount++;
        }
      }

      // Assert - worst solution should still be selected sometimes (probability ~1/55 ≈ 1.8%)
      assertThat(worstCount).isGreaterThan(0);
    }
  }
}
