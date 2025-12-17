package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

@DisplayName("DifferentialEvolutionSelection tests")
class DifferentialEvolutionSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, when constructor is called, then selection is created")
    void givenValidParameters_whenConstructorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int matingPoolSize = 6;
      int numberOfParentsToSelect = 3;
      boolean takeCurrentIndividualAsParent = true;
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);

      // Act
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(
              matingPoolSize, numberOfParentsToSelect, takeCurrentIndividualAsParent, generator);

      // Assert
      assertThat(selection).isNotNull();
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given population with enough solutions, when select is called, then correct number of parents is returned")
    void givenPopulationWithEnoughSolutions_whenSelectIsCalled_thenCorrectNumberOfParentsIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, true, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given selection with takeCurrentIndividualAsParent true, when select is called, then current individual is included")
    void givenSelectionWithTakeCurrentIndividualAsParentTrue_whenSelectIsCalled_thenCurrentIndividualIsIncluded() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, true, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      // Current individual (index 0 from generator) should be in the result
      assertThat(result).contains(solutions.get(0));
    }

    @Test
    @DisplayName("Given multiple selections, when select is called repeatedly, then different parents can be selected")
    void givenMultipleSelections_whenSelectIsCalledRepeatedly_thenDifferentParentsCanBeSelected() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, false, generator);

      // Act
      List<DoubleSolution> result1 = selection.select(solutions);
      generator.generateNext();
      List<DoubleSolution> result2 = selection.select(solutions);

      // Assert
      assertThat(result1).hasSize(3);
      assertThat(result2).hasSize(3);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given minimum population size of 3, when select is called, then selection works")
    void givenMinimumPopulationSizeOf3_whenSelectIsCalled_thenSelectionWorks() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 3; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(3);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, true, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Given large population, when select is called, then selection works efficiently")
    void givenLargePopulation_whenSelectIsCalled_thenSelectionWorksEfficiently() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(100);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, true, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given mating pool size larger than parents per selection, when select is called, then multiple selection rounds occur")
    void givenMatingPoolSizeLargerThanParentsPerSelection_whenSelectIsCalled_thenMultipleSelectionRoundsOccur() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      // Request 6 parents but only 3 per selection round
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(6, 3, true, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(6);
      assertThat(result).allMatch(solutions::contains);
    }

    @Test
    @DisplayName("Given selection without current individual, when select is called, then random parents are selected")
    void givenSelectionWithoutCurrentIndividual_whenSelectIsCalled_thenRandomParentsAreSelected() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(10);
      DifferentialEvolutionSelection selection =
          new DifferentialEvolutionSelection(3, 3, false, generator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(3);
      assertThat(result).allMatch(solutions::contains);
    }
  }
}
