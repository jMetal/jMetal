package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("NaryTournamentSelection tests")
class NaryTournamentSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, when constructor with comparator is called, then selection is created")
    void givenValidParameters_whenConstructorWithComparatorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int tournamentSize = 2;
      int matingPoolSize = 5;
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(tournamentSize, matingPoolSize, comparator);

      // Assert
      assertThat(selection).isNotNull();
    }

    @Test
    @DisplayName("Given operator injection, when constructor is called, then selection wraps the operator")
    void givenOperatorInjection_whenConstructorIsCalled_thenSelectionWrapsTheOperator() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      var coreOperator =
          new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<DoubleSolution>(
              2, comparator);
      int matingPoolSize = 5;

      // Act
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(coreOperator, matingPoolSize);

      // Assert
      assertThat(selection).isNotNull();
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given a single solution with tournament size 1, when select is called, then that solution is returned")
    void givenSingleSolutionWithTournamentSize1_whenSelectIsCalled_thenThatSolutionIsReturned() {
      // Arrange
      DoubleSolution solution = mock(DoubleSolution.class);
      when(solution.objectives()).thenReturn(new double[] {1.0});
      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(1, 1, comparator);

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
        when(solution.objectives()).thenReturn(new double[] {(double) i});
        solutions.add(solution);
      }
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given solutions with different fitness, when binary tournament is used many times, then better solutions are selected more often")
    void givenSolutionsWithDifferentFitness_whenBinaryTournamentIsUsedManyTimes_thenBetterSolutionsAreSelectedMoreOften() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[] {0.0});
      when(worstSolution.objectives()).thenReturn(new double[] {100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 1, comparator);

      // Act
      int bestCount = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - with binary tournament on 2 solutions, best should always win
      assertThat(bestCount).isEqualTo(iterations);
    }

    @Test
    @DisplayName("Given larger tournament size, when select is called, then selection pressure increases")
    void givenLargerTournamentSize_whenSelectIsCalled_thenSelectionPressureIncreases() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[] {(double) i});
        solutions.add(solution);
      }
      DoubleSolution bestSolution = solutions.get(0);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> smallTournament =
          new NaryTournamentSelection<>(2, 1, comparator);
      NaryTournamentSelection<DoubleSolution> largeTournament =
          new NaryTournamentSelection<>(5, 1, comparator);

      // Act
      int smallTournamentBestCount = 0;
      int largeTournamentBestCount = 0;
      int iterations = 1000;

      for (int i = 0; i < iterations; i++) {
        if (smallTournament.select(solutions).get(0) == bestSolution) {
          smallTournamentBestCount++;
        }
        if (largeTournament.select(solutions).get(0) == bestSolution) {
          largeTournamentBestCount++;
        }
      }

      // Assert - larger tournament should select best solution more often
      assertThat(largeTournamentBestCount).isGreaterThan(smallTournamentBestCount);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given mating pool size larger than population, when select is called, then duplicates are allowed")
    void givenMatingPoolSizeLargerThanPopulation_whenSelectIsCalled_thenDuplicatesAreAllowed() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[] {1.0});
      when(solution2.objectives()).thenReturn(new double[] {2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 10, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(10);
      assertThat(result).allMatch(s -> s == solution1 || s == solution2);
    }

    @Test
    @DisplayName("Given solutions with equal fitness, when select is called, then any solution can be selected")
    void givenSolutionsWithEqualFitness_whenSelectIsCalled_thenAnySolutionCanBeSelected() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[] {1.0});
      when(solution2.objectives()).thenReturn(new double[] {1.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 1, comparator);

      // Act
      int solution1Count = 0;
      int iterations = 100;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == solution1) {
          solution1Count++;
        }
      }

      // Assert - both solutions should be selected at least once
      assertThat(solution1Count).isGreaterThan(0).isLessThan(iterations);
    }

    @Test
    @DisplayName("Given solutions with negative objectives, when select is called, then ranking works correctly")
    void givenSolutionsWithNegativeObjectives_whenSelectIsCalled_thenRankingWorksCorrectly() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[] {-100.0});
      when(solution2.objectives()).thenReturn(new double[] {-50.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 1, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert - solution1 (-100) is better (lower) so should be selected
      assertThat(result.get(0)).isSameAs(solution1);
    }

    @Test
    @DisplayName("Given original list, when select is called, then original list is not modified")
    void givenOriginalList_whenSelectIsCalled_thenOriginalListIsNotModified() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      DoubleSolution solution3 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[] {3.0});
      when(solution2.objectives()).thenReturn(new double[] {1.0});
      when(solution3.objectives()).thenReturn(new double[] {2.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2, solution3));
      List<DoubleSolution> originalOrder = new ArrayList<>(solutions);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(2, 2, comparator);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(solutions).containsExactlyElementsOf(originalOrder);
    }

    @Test
    @DisplayName("Given tournament size equal to population size, when select is called, then best solution is always selected")
    void givenTournamentSizeEqualToPopulationSize_whenSelectIsCalled_thenBestSolutionIsAlwaysSelected() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        when(solution.objectives()).thenReturn(new double[] {(double) i});
        solutions.add(solution);
      }
      DoubleSolution bestSolution = solutions.get(0);

      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      NaryTournamentSelection<DoubleSolution> selection =
          new NaryTournamentSelection<>(5, 1, comparator);

      // Act
      int bestCount = 0;
      int iterations = 100;
      for (int i = 0; i < iterations; i++) {
        if (selection.select(solutions).get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - when tournament size equals population, best is always selected
      assertThat(bestCount).isEqualTo(iterations);
    }
  }
}
