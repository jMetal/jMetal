package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NeighborhoodSelection;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

@DisplayName("NeighborhoodSelection tests")
class NeighborhoodSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, when constructor is called, then selection is created")
    void givenValidParameters_whenConstructorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int matingPoolSize = 2;
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);

      // Act
      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(
              matingPoolSize, generator, neighborhood, selectionOperator, false);

      // Assert
      assertThat(selection).isNotNull();
      assertThat(selection.getSolutionIndexGenerator()).isSameAs(generator);
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given valid setup, when select is called, then correct number of solutions is returned")
    void givenValidSetup_whenSelectIsCalled_thenCorrectNumberOfSolutionsIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();
      DoubleSolution selectedSolution = mock(DoubleSolution.class);

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);
      when(selectionOperator.execute(anyList())).thenReturn(selectedSolution);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(3, generator, neighborhood, selectionOperator, false);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result).containsOnly(selectedSolution);
    }

    @Test
    @DisplayName("Given updateCurrentSolutionIndex true, when select is called, then index is updated")
    void givenUpdateCurrentSolutionIndexTrue_whenSelectIsCalled_thenIndexIsUpdated() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();
      DoubleSolution selectedSolution = mock(DoubleSolution.class);

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      int initialIndex = generator.getValue();

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);
      when(selectionOperator.execute(anyList())).thenReturn(selectedSolution);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(3, generator, neighborhood, selectionOperator, true);

      // Act
      selection.select(solutions);

      // Assert - index should have been updated 3 times
      assertThat(generator.getValue()).isNotEqualTo(initialIndex);
    }

    @Test
    @DisplayName("Given updateCurrentSolutionIndex false, when select is called, then index is not updated")
    void givenUpdateCurrentSolutionIndexFalse_whenSelectIsCalled_thenIndexIsNotUpdated() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();
      DoubleSolution selectedSolution = mock(DoubleSolution.class);

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      int initialIndex = generator.getValue();

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);
      when(selectionOperator.execute(anyList())).thenReturn(selectedSolution);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(3, generator, neighborhood, selectionOperator, false);

      // Act
      selection.select(solutions);

      // Assert - index should remain the same
      assertThat(generator.getValue()).isEqualTo(initialIndex);
    }

    @Test
    @DisplayName("Given selection, when select is called, then neighborhood is queried for each selection")
    void givenSelection_whenSelectIsCalled_thenNeighborhoodIsQueriedForEachSelection() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();
      DoubleSolution selectedSolution = mock(DoubleSolution.class);

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);
      when(selectionOperator.execute(anyList())).thenReturn(selectedSolution);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(3, generator, neighborhood, selectionOperator, false);

      // Act
      selection.select(solutions);

      // Assert
      verify(neighborhood, times(3)).getNeighbors(anyList(), anyInt());
      verify(selectionOperator, times(3)).execute(anyList());
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given mating pool size of 1, when select is called, then single solution is returned")
    void givenMatingPoolSizeOf1_whenSelectIsCalled_thenSingleSolutionIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();
      DoubleSolution selectedSolution = mock(DoubleSolution.class);

      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(5);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);
      when(selectionOperator.execute(anyList())).thenReturn(selectedSolution);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(1, generator, neighborhood, selectionOperator, false);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(1).containsExactly(selectedSolution);
    }

    @Test
    @DisplayName("Given getSolutionIndexGenerator, when called, then returns the generator")
    void givenGetSolutionIndexGenerator_whenCalled_thenReturnsTheGenerator() {
      // Arrange
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);

      @SuppressWarnings("unchecked")
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
          mock(SelectionOperator.class);

      NeighborhoodSelection<DoubleSolution> selection =
          new NeighborhoodSelection<>(2, generator, neighborhood, selectionOperator, false);

      // Act & Assert
      assertThat(selection.getSolutionIndexGenerator()).isSameAs(generator);
    }
  }
}
