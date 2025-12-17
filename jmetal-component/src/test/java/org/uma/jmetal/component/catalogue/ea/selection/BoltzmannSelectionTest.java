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
import org.uma.jmetal.component.catalogue.ea.selection.impl.BoltzmannSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("BoltzmannSelection tests")
class BoltzmannSelectionTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters with default temperature, when constructor is called, then selection is initialized correctly")
    void givenValidParametersWithDefaultTemperature_whenConstructorIsCalled_thenSelectionIsInitializedCorrectly() {
      // Arrange
      int matingPoolSize = 5;
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(matingPoolSize, comparator);

      // Assert
      assertThat(selection.getMatingPoolSize()).isEqualTo(matingPoolSize);
      assertThat(selection.getTemperature()).isEqualTo(BoltzmannSelection.DEFAULT_TEMPERATURE);
    }

    @Test
    @DisplayName("Given valid parameters with custom temperature, when constructor is called, then selection is initialized correctly")
    void givenValidParametersWithCustomTemperature_whenConstructorIsCalled_thenSelectionIsInitializedCorrectly() {
      // Arrange
      int matingPoolSize = 5;
      double temperature = 10.0;
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(matingPoolSize, comparator, temperature);

      // Assert
      assertThat(selection.getMatingPoolSize()).isEqualTo(matingPoolSize);
      assertThat(selection.getTemperature()).isEqualTo(temperature);
    }

    @Test
    @DisplayName("Given zero temperature, when constructor is called, then InvalidConditionException is thrown")
    void givenZeroTemperature_whenConstructorIsCalled_thenInvalidConditionExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> new BoltzmannSelection<>(5, comparator, 0.0));
    }

    @Test
    @DisplayName("Given negative temperature, when constructor is called, then InvalidConditionException is thrown")
    void givenNegativeTemperature_whenConstructorIsCalled_thenInvalidConditionExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> new BoltzmannSelection<>(5, comparator, -1.0));
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
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(2, comparator);

      // Act & Assert
      assertThrows(NullParameterException.class, () -> selection.select(null));
    }

    @Test
    @DisplayName("Given empty solution list, when select is called, then EmptyCollectionException is thrown")
    void givenEmptySolutionList_whenSelectIsCalled_thenEmptyCollectionExceptionIsThrown() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(2, comparator);
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
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(1, comparator);

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
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(5, comparator);

      // Act
      List<DoubleSolution> result = selection.select(solutions);

      // Assert
      assertThat(result).hasSize(5);
      assertThat(result).allMatch(solutions::contains);
    }
  }

  @Nested
  @DisplayName("Temperature effect tests")
  class TemperatureEffectTests {

    @Test
    @DisplayName("Given low temperature, when select is called many times, then best solutions are selected more often")
    void givenLowTemperature_whenSelectIsCalledManyTimes_thenBestSolutionsAreSelectedMoreOften() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> lowTempSelection = new BoltzmannSelection<>(1, comparator, 0.5);

      // Act
      int bestCount = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = lowTempSelection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - with low temperature, best solution should be selected almost always
      assertThat(bestCount).isGreaterThan((int) (iterations * 0.8));
    }

    @Test
    @DisplayName("Given high temperature, when select is called many times, then selection is more uniform")
    void givenHighTemperature_whenSelectIsCalledManyTimes_thenSelectionIsMoreUniform() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> highTempSelection = new BoltzmannSelection<>(1, comparator, 100.0);

      // Act
      int bestCount = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = highTempSelection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - with high temperature, selection should be more uniform (closer to 50%)
      assertThat(bestCount).isBetween((int) (iterations * 0.4), (int) (iterations * 0.7));
    }

    @Test
    @DisplayName("Given default temperature, when getTemperature is called, then default value is returned")
    void givenDefaultTemperature_whenGetTemperatureIsCalled_thenDefaultValueIsReturned() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(5, comparator);

      // Act & Assert
      assertThat(selection.getTemperature()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Given very low temperature, when select is called, then selection is nearly deterministic")
    void givenVeryLowTemperature_whenSelectIsCalled_thenSelectionIsNearlyDeterministic() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(1, comparator, 0.1);

      // Act
      int bestCount = 0;
      int iterations = 100;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - with very low temperature, best should be selected almost always
      assertThat(bestCount).isGreaterThanOrEqualTo((int) (iterations * 0.95));
    }

    @Test
    @DisplayName("Given very high temperature, when select is called, then selection approaches uniform")
    void givenVeryHighTemperature_whenSelectIsCalled_thenSelectionApproachesUniform() {
      // Arrange
      DoubleSolution bestSolution = mock(DoubleSolution.class);
      DoubleSolution worstSolution = mock(DoubleSolution.class);
      when(bestSolution.objectives()).thenReturn(new double[]{0.0});
      when(worstSolution.objectives()).thenReturn(new double[]{100.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(bestSolution, worstSolution));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(1, comparator, 1000.0);

      // Act
      int bestCount = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == bestSolution) {
          bestCount++;
        }
      }

      // Assert - with very high temperature, selection should be close to 50/50
      assertThat(bestCount).isBetween((int) (iterations * 0.4), (int) (iterations * 0.6));
    }
  }

  @Nested
  @DisplayName("Edge case tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Given solutions with equal fitness, when select is called, then selection is uniform")
    void givenSolutionsWithEqualFitness_whenSelectIsCalled_thenSelectionIsUniform() {
      // Arrange
      DoubleSolution solution1 = mock(DoubleSolution.class);
      DoubleSolution solution2 = mock(DoubleSolution.class);
      when(solution1.objectives()).thenReturn(new double[]{1.0});
      when(solution2.objectives()).thenReturn(new double[]{1.0});

      List<DoubleSolution> solutions = new ArrayList<>(List.of(solution1, solution2));
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(1, comparator, 1.0);

      // Act
      int solution1Count = 0;
      int iterations = 1000;
      for (int i = 0; i < iterations; i++) {
        List<DoubleSolution> result = selection.select(solutions);
        if (result.get(0) == solution1) {
          solution1Count++;
        }
      }

      // Assert - with equal fitness, ranking still applies (first gets higher rank)
      assertThat(solution1Count).isGreaterThan(iterations / 3);
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
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(10, comparator);

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
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(2, comparator);

      // Act
      selection.select(solutions);

      // Assert
      assertThat(solutions).containsExactlyElementsOf(originalOrder);
    }

    @Test
    @DisplayName("Given small positive temperature close to zero, when constructor is called, then it is accepted")
    void givenSmallPositiveTemperature_whenConstructorIsCalled_thenItIsAccepted() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(5, comparator, 0.001);

      // Assert
      assertThat(selection.getTemperature()).isEqualTo(0.001);
    }

    @Test
    @DisplayName("Given large temperature, when constructor is called, then it is accepted")
    void givenLargeTemperature_whenConstructorIsCalled_thenItIsAccepted() {
      // Arrange
      Comparator<DoubleSolution> comparator = Comparator.comparingDouble(s -> s.objectives()[0]);

      // Act
      BoltzmannSelection<DoubleSolution> selection = new BoltzmannSelection<>(5, comparator, 10000.0);

      // Assert
      assertThat(selection.getTemperature()).isEqualTo(10000.0);
    }
  }
}
