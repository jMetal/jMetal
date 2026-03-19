package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.TruncationSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("TruncationSelection tests")
class TruncationSelectionTest {

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
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(matingPoolSize, comparator);

      // Assert
      assertThat(selection.getMatingPoolSize()).isEqualTo(matingPoolSize);
    }
  }

  @Nested
  @DisplayName("Select method tests")
  class SelectTests {

    @Test
    @DisplayName("Given a single solution, when select is called with matingPoolSize 1, then that solution is returned")
    void givenSingleSolution_whenSelectIsCalled_thenThatSolutionIsReturned() {
      // Arrange
      DoubleSolution solution = mock(DoubleSolution.class);
      when(solution.objectives()).thenReturn(new double[]{1.0});
      List<DoubleSolution> solutions = List.of(solution);
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(1, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(1).containsExactly(solution);
    }

    @Test
    @DisplayName("Given multiple solutions, when select is called, then best solutions are returned in order")
    void givenMultipleSolutions_whenSelectIsCalled_thenBestSolutionsAreReturnedInOrder() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{3.0});
      when(solution2.objectives()).thenReturn(new double[]{1.0});
      when(solution3.objectives()).thenReturn(new double[]{2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(2, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isSameAs(solution2); // Best (1.0)
      assertThat(result.get(1)).isSameAs(solution3); // Second best (2.0)
    }

    @Test
    @DisplayName("Given matingPoolSize larger than population, when select is called, then all solutions are returned")
    void givenMatingPoolSizeLargerThanPopulation_whenSelectIsCalled_thenAllSolutionsAreReturned() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(10, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Given solutions with same fitness, when select is called, then selection is deterministic")
    void givenSolutionsWithSameFitness_whenSelectIsCalled_thenSelectionIsDeterministic() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{1.0});
      when(solution3.objectives()).thenReturn(new double[]{1.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(2, comparator);

      // Act
      List<DoubleSolution> result1 = selection.select(new ArrayList<>(solutions));
      List<DoubleSolution> result2 = selection.select(new ArrayList<>(solutions));

      // Assert
      assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("Given five solutions, when selecting three, then top three by comparator are returned")
    void givenFiveSolutions_whenSelectingThree_thenTopThreeByComparatorAreReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      double[] objectives = {5.0, 2.0, 4.0, 1.0, 3.0};
      for (double obj : objectives) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{obj});
        solutions.add(solution);
      }

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(3, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result.get(0).objectives()[0]).isEqualTo(1.0);
      assertThat(result.get(1).objectives()[0]).isEqualTo(2.0);
      assertThat(result.get(2).objectives()[0]).isEqualTo(3.0);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given empty list, when select is called, then empty list is returned")
    void givenEmptyList_whenSelectIsCalled_thenEmptyListIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given matingPoolSize of zero, when select is called, then empty list is returned")
    void givenMatingPoolSizeOfZero_whenSelectIsCalled_thenEmptyListIsReturned() {
      // Arrange
      DoubleSolution solution = mock(DoubleSolution.class);
      when(solution.objectives()).thenReturn(new double[]{1.0});
      List<DoubleSolution> solutions = List.of(solution);
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(0, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given solutions with negative objective values, when select is called, then correct ordering is maintained")
    void givenSolutionsWithNegativeObjectives_whenSelectIsCalled_thenCorrectOrderingIsMaintained() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{-10.0});
      when(solution2.objectives()).thenReturn(new double[]{5.0});
      when(solution3.objectives()).thenReturn(new double[]{-5.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(2, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isSameAs(solution1); // -10.0
      assertThat(result.get(1)).isSameAs(solution3); // -5.0
    }

    @Test
    @DisplayName("Given reversed comparator, when select is called, then worst solutions by original metric are selected")
    void givenReversedComparator_whenSelectIsCalled_thenWorstSolutionsByOriginalMetricAreSelected() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{2.0});
      when(solution3.objectives()).thenReturn(new double[]{3.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble((DoubleSolution s) -> s.objectives()[0]).reversed();
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(2, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isSameAs(solution3); // 3.0 (highest)
      assertThat(result.get(1)).isSameAs(solution2); // 2.0
    }

    @Test
    @DisplayName("Given large population, when selecting small mating pool, then only requested number is returned")
    void givenLargePopulation_whenSelectingSmallMatingPool_thenOnlyRequestedNumberIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[]{(double) i});
        solutions.add(solution);
      }
      Collections.shuffle(solutions);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(3, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result.get(0).objectives()[0]).isEqualTo(0.0);
      assertThat(result.get(1).objectives()[0]).isEqualTo(1.0);
      assertThat(result.get(2).objectives()[0]).isEqualTo(2.0);
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
      TruncationSelection<DoubleSolution> selection = new TruncationSelection<>(2, comparator);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(solutions).containsExactlyElementsOf(originalOrder);
    }
  }
}
