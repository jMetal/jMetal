package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

@DisplayName("PopulationAndNeighborhoodSelection tests")
class PopulationAndNeighborhoodSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters without current solution, when constructor is called, then selection is created")
    void givenValidParametersWithoutCurrentSolution_whenConstructorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int matingPoolSize = 3;
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      double neighborhoodSelectionProbability = 0.9;

      // Act
      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(
              matingPoolSize, generator, neighborhood, neighborhoodSelectionProbability, false);

      // Assert
      assertThat(selection).isNotNull();
      assertThat(selection.getSolutionIndexGenerator()).isSameAs(generator);
    }

    @Test
    @DisplayName("Given valid parameters with current solution, when constructor is called, then selection is created")
    void givenValidParametersWithCurrentSolution_whenConstructorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int matingPoolSize = 3;
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      double neighborhoodSelectionProbability = 0.9;

      // Act
      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(
              matingPoolSize, generator, neighborhood, neighborhoodSelectionProbability, true);

      // Assert
      assertThat(selection).isNotNull();
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given probability 1.0, when select is called, then neighborhood is always used")
    void givenProbability1_whenSelectIsCalled_thenNeighborhoodIsAlwaysUsed() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
      }
      for (int i = 0; i < 5; i++) {
        neighbors.add(solutions.get(i));
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 1.0, false);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(selection.getNeighborType()).isEqualTo(Neighborhood.NeighborType.NEIGHBOR);
    }

    @Test
    @DisplayName("Given probability 0.0, when select is called, then population is always used")
    void givenProbability0_whenSelectIsCalled_thenPopulationIsAlwaysUsed() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
      }
      for (int i = 0; i < 5; i++) {
        neighbors.add(solutions.get(i));
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 0.0, false);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(selection.getNeighborType()).isEqualTo(Neighborhood.NeighborType.POPULATION);
    }

    @Test
    @DisplayName("Given selectCurrentSolution true, when select is called, then current solution is included")
    void givenSelectCurrentSolutionTrue_whenSelectIsCalled_thenCurrentSolutionIsIncluded() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
      }
      for (int i = 0; i < 5; i++) {
        neighbors.add(solutions.get(i));
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DoubleSolution currentSolution = solutions.get(generator.getValue());

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 0.0, true);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result).contains(currentSolution);
    }

    @Test
    @DisplayName("Given intermediate probability, when select is called many times, then both sources are used")
    void givenIntermediateProbability_whenSelectIsCalledManyTimes_thenBothSourcesAreUsed() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
      }
      for (int i = 0; i < 5; i++) {
        neighbors.add(solutions.get(i));
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 0.5, false);

      // Act
      int neighborCount = 0;
      int populationCount = 0;
      int iterations = 100;

      for (int i = 0; i < iterations; i++) {
        selection.select(solutions);
        if (selection.getNeighborType() == Neighborhood.NeighborType.NEIGHBOR) {
          neighborCount++;
        } else {
          populationCount++;
        }
      }

      // Assert - both should be used with probability 0.5
      assertThat(neighborCount).isGreaterThan(0);
      assertThat(populationCount).isGreaterThan(0);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given getNeighborType before select, when called after select, then returns correct type")
    void givenGetNeighborTypeBeforeSelect_whenCalledAfterSelect_thenReturnsCorrectType() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 1.0, false);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(selection.getNeighborType()).isNotNull();
    }

    @Test
    @DisplayName("Given getSolutionIndexGenerator, when called, then returns the generator")
    void givenGetSolutionIndexGenerator_whenCalled_thenReturnsTheGenerator() {
      // Arrange
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(3, generator, neighborhood, 0.5, false);

      // Act & Assert
      assertThat(selection.getSolutionIndexGenerator()).isSameAs(generator);
    }

    @Test
    @DisplayName("Given mating pool size of 1 with current solution, when select is called, then only current solution is returned")
    void givenMatingPoolSizeOf1WithCurrentSolution_whenSelectIsCalled_thenOnlyCurrentSolutionIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DoubleSolution currentSolution = solutions.get(generator.getValue());

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(1, generator, neighborhood, 0.5, true);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(1);
      assertThat(result).containsExactly(currentSolution);
    }

    @Test
    @DisplayName("Given large mating pool, when select is called, then correct size is returned")
    void givenLargeMatingPool_whenSelectIsCalled_thenCorrectSizeIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      List<DoubleSolution> neighbors = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
        DoubleSolution solution = mock(DoubleSolution.class);
        solutions.add(solution);
        neighbors.add(solution);
      }

      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(100);

      @SuppressWarnings("unchecked")
      Neighborhood<DoubleSolution> neighborhood = mock(Neighborhood.class);
      when(neighborhood.getNeighbors(anyList(), anyInt())).thenReturn(neighbors);

      PopulationAndNeighborhoodSelection<DoubleSolution> selection =
          new PopulationAndNeighborhoodSelection<>(50, generator, neighborhood, 0.5, false);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(50);
    }
  }
}
