package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.IterativeSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

@DisplayName("IterativeSelection tests")
class IterativeSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, when constructor is called, then selection is created")
    void givenValidParameters_whenConstructorIsCalled_thenSelectionIsCreated() {
      // Arrange
      int numberOfElementsToSelect = 5;
      SequenceGenerator<Integer> generator = new CyclicIntegerSequence(5);

      // Act
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(numberOfElementsToSelect, generator);

      // Assert
      assertThat(selection.getNumberOfElementsToSelect()).isEqualTo(numberOfElementsToSelect);
    }
  }

  @Nested
  @DisplayName("Select method behavior tests")
  class SelectBehaviorTests {

    @Test
    @DisplayName("Given a cyclic sequence, when select is called repeatedly, then solutions are returned in population order")
    void givenACyclicSequence_whenSelectIsCalledRepeatedly_thenSolutionsAreReturnedInPopulationOrder() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(1, new CyclicIntegerSequence(5));

      // Act
      List<DoubleSolution> firstCycle = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        firstCycle.addAll(selection.select(solutions));
      }
      List<DoubleSolution> secondCycle = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        secondCycle.addAll(selection.select(solutions));
      }

      // Assert
      assertThat(firstCycle).containsExactlyElementsOf(solutions);
      assertThat(secondCycle).containsExactlyElementsOf(solutions);
    }

    @Test
    @DisplayName("Given a cyclic sequence, when the whole population is selected in one call, then every solution is returned exactly once in population order")
    void givenACyclicSequence_whenTheWholePopulationIsSelectedInOneCall_thenEverySolutionIsReturnedExactlyOnceInPopulationOrder() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(5, new CyclicIntegerSequence(5));

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).containsExactlyElementsOf(solutions);
    }

    @Test
    @DisplayName("Given a random permutation cycle, when the whole population is selected in one call, then every solution is returned exactly once")
    void givenARandomPermutationCycle_whenTheWholePopulationIsSelectedInOneCall_thenEverySolutionIsReturnedExactlyOnce() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        solutions.add(mock(DoubleSolution.class));
      }
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(5, new RandomPermutationCycle(5));

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).containsExactlyInAnyOrderElementsOf(solutions);
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given a sequence generator whose length does not match the population size, when select is called, then an exception is thrown")
    void givenASequenceGeneratorWhoseLengthDoesNotMatchThePopulationSize_whenSelectIsCalled_thenAnExceptionIsThrown() {
      // Arrange
      List<DoubleSolution> solutions =
          List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(1, new CyclicIntegerSequence(5));

      // Act & Assert
      assertThatThrownBy(() -> selection.select(solutions)).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Given a population of a single solution, when select is called, then that solution is returned")
    void givenAPopulationOfASingleSolution_whenSelectIsCalled_thenThatSolutionIsReturned() {
      // Arrange
      List<DoubleSolution> solutions = List.of(mock(DoubleSolution.class));
      IterativeSelection<DoubleSolution> selection =
          new IterativeSelection<>(1, new CyclicIntegerSequence(1));

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(1).first().isSameAs(solutions.get(0));
    }
  }
}
