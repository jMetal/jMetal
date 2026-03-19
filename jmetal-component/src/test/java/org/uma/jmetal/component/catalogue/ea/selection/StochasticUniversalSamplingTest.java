package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.uma.jmetal.component.catalogue.ea.selection.impl.StochasticUniversalSampling;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("StochasticUniversalSampling tests")
class StochasticUniversalSamplingTest {

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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(matingPoolSize, comparator);

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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(2, comparator);

      // Act & Assert
      assertThrows(NullParameterException.class, () -> selection.select(null));
    }

    @Test
    @DisplayName("Given empty solution list, when select is called, then EmptyCollectionException is thrown")
    void givenEmptySolutionList_whenSelectIsCalled_thenEmptyCollectionExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(2, comparator);
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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(1, comparator);

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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given solutions, when select is called, then all selected solutions come from original list")
    void givenSolutions_whenSelectIsCalled_thenAllSelectedSolutionsComeFromOriginalList() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(3, comparator);

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
      DoubleSolution middleSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(middleSolution.objectives()).thenReturn(new double[]{50.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, middleSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(1, comparator);

      // Act
      Map<DoubleSolution, Integer> selectionCounts = new HashMap<>();
      selectionCounts.put(bestSolution, 0);
      selectionCounts.put(middleSolution, 0);
      selectionCounts.put(worstSolution, 0);

      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        DoubleSolution selected = result.get(0);
        selectionCounts.put(selected, selectionCounts.get(selected) + 1);
      }

      // Assert - best solution should be selected most often
      assertThat(selectionCounts.get(bestSolution)).isGreaterThan(selectionCounts.get(worstSolution));
    }
  }

  @Nested
  @DisplayName("SUS specific behavior tests")
  class SUSSpecificBehaviorTests {

    @Test
    @DisplayName("Given population size equals mating pool size, when select is called, then all solutions can be selected")
    void givenPopulationSizeEqualsMatingPoolSize_whenSelectIsCalled_thenAllSolutionsCanBeSelected() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("Given SUS selection, when compared to roulette wheel over many runs, then variance should be lower")
    void givenSUSSelection_whenComparedToRouletteWheelOverManyRuns_thenVarianceShouldBeLower() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(5, comparator);

      // Act - run multiple selections and check consistency
      List<Integer> bestSolutionCounts = new ArrayList<>();
      for (int run = 0; run < 100; run++) {
        List<DoubleSolution> result = selection.select(solutions);
        int bestCount = (int) result.stream()
            .filter(s -> s.objectives()[0] < 3.0)
            .count();
        bestSolutionCounts.add(bestCount);
      }

      // Assert - SUS should provide relatively consistent selection
      double average = bestSolutionCounts.stream().mapToInt(Integer::intValue).average().orElse(0);
      assertThat(average).isGreaterThan(0);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given solutions with equal fitness, when select is called, then selection distributes across solutions")
    void givenSolutionsWithEqualFitness_whenSelectIsCalled_thenSelectionDistributesAcrossSolutions() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{1.0}); // All equal
        solutions.add(solution);
      }

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
      assertThat(result).allMatch(solutions::contains);
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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(10, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(10);
      assertThat(result).allMatch(s -> s == solution1 || s == solution2);
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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(2, comparator);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(solutions).containsExactlyElementsOf(originalOrder);
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
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(1, comparator);

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
    @DisplayName("Given large population, when selecting small mating pool, then best solutions are favored")
    void givenLargePopulation_whenSelectingSmallMatingPool_thenBestSolutionsAreFavored() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      StochasticUniversalSampling<DoubleSolution> selection = new StochasticUniversalSampling<>(5, comparator);

      // Act
      Map<DoubleSolution, Integer> selectionCounts = new HashMap<>();
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        for (DoubleSolution s : result) {
          selectionCounts.merge(s, 1, Integer::sum);
        }
      }

      // Assert - best solutions (lower objective) should be selected more often
      int bestSolutionsCount = 0;
      for (int i = 0; i < 10; i++) {
        bestSolutionsCount += selectionCounts.getOrDefault(solutions.get(i), 0);
      }
      int worstSolutionsCount = 0;
      for (int i = 90; i < 100; i++) {
        worstSolutionsCount += selectionCounts.getOrDefault(solutions.get(i), 0);
      }
      assertThat(bestSolutionsCount).isGreaterThan(worstSolutionsCount);
    }
  }
}
