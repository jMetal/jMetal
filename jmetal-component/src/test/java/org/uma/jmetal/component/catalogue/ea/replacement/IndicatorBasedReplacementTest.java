package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.IndicatorBasedReplacement;
import org.uma.jmetal.component.util.IndicatorBasedPreference;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("IndicatorBasedReplacement Tests")
class IndicatorBasedReplacementTest {

  private List<Bounds<Double>> bounds;
  private IndicatorBasedPreference<DoubleSolution> preference;

  @BeforeEach
  void setUp() {
    bounds = List.of(Bounds.create(0.0, 1.0));
    preference = new IndicatorBasedPreference<>(2);
  }

  private DoubleSolution createSolution(double obj1, double obj2) {
    DoubleSolution solution = new DefaultDoubleSolution(bounds, 2, 0);
    solution.objectives()[0] = obj1;
    solution.objectives()[1] = obj2;
    return solution;
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given preference, then replacement should be created")
    void givenPreference_thenReplacementShouldBeCreated() {
      // Arrange & Act
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      // Assert
      assertNotNull(replacement);
      assertEquals(preference, replacement.getPreference());
    }
  }


  @Nested
  @DisplayName("Replace Tests")
  class ReplaceTests {

    @Test
    @DisplayName("Given populations, when replace is called, then result size equals parent size")
    void givenPopulations_whenReplaceIsCalled_thenResultSizeEqualsParentSize() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      List<DoubleSolution> population = new ArrayList<>();
      population.add(createSolution(0.3, 0.7));
      population.add(createSolution(0.5, 0.5));
      population.add(createSolution(0.7, 0.3));

      List<DoubleSolution> offspring = new ArrayList<>();
      offspring.add(createSolution(0.2, 0.8));
      offspring.add(createSolution(0.8, 0.2));

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(population.size(), result.size());
    }

    @Test
    @DisplayName("Given dominated offspring, when replace is called, then dominated solutions are removed")
    void givenDominatedOffspring_whenReplaceIsCalled_thenDominatedSolutionsAreRemoved() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      List<DoubleSolution> population = new ArrayList<>();
      DoubleSolution good1 = createSolution(0.2, 0.3);
      DoubleSolution good2 = createSolution(0.3, 0.2);
      population.add(good1);
      population.add(good2);

      List<DoubleSolution> offspring = new ArrayList<>();
      DoubleSolution dominated = createSolution(0.9, 0.9);
      offspring.add(dominated);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(2, result.size());
      assertFalse(result.contains(dominated));
    }

    @Test
    @DisplayName("Given better offspring, when replace is called, then better solutions survive")
    void givenBetterOffspring_whenReplaceIsCalled_thenBetterSolutionsSurvive() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      List<DoubleSolution> population = new ArrayList<>();
      DoubleSolution bad = createSolution(0.9, 0.9);
      population.add(bad);
      population.add(createSolution(0.5, 0.5));

      List<DoubleSolution> offspring = new ArrayList<>();
      DoubleSolution good = createSolution(0.1, 0.1);
      offspring.add(good);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(2, result.size());
      assertTrue(result.contains(good));
      assertFalse(result.contains(bad));
    }

    @Test
    @DisplayName("Given empty offspring, when replace is called, then return population")
    void givenEmptyOffspring_whenReplaceIsCalled_thenReturnPopulation() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      List<DoubleSolution> population = new ArrayList<>();
      population.add(createSolution(0.3, 0.7));
      population.add(createSolution(0.7, 0.3));

      List<DoubleSolution> offspring = new ArrayList<>();

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(population.size(), result.size());
    }

    @Test
    @DisplayName("Given equal size populations, when replace is called, then best half survives")
    void givenEqualSizePopulations_whenReplaceIsCalled_thenBestHalfSurvives() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      List<DoubleSolution> population = new ArrayList<>();
      population.add(createSolution(0.2, 0.8));
      population.add(createSolution(0.8, 0.2));

      List<DoubleSolution> offspring = new ArrayList<>();
      offspring.add(createSolution(0.3, 0.7));
      offspring.add(createSolution(0.7, 0.3));

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(2, result.size());
    }
  }


  @Nested
  @DisplayName("Kappa Parameter Tests")
  class KappaParameterTests {

    @Test
    @DisplayName("Given different kappa values, then selection pressure changes")
    void givenDifferentKappaValues_thenSelectionPressureChanges() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> lowKappa = new IndicatorBasedPreference<>(2, 0.01);
      IndicatorBasedPreference<DoubleSolution> highKappa = new IndicatorBasedPreference<>(2, 0.5);

      List<DoubleSolution> solutions1 = new ArrayList<>();
      solutions1.add(createSolution(0.2, 0.8));
      solutions1.add(createSolution(0.5, 0.5));
      solutions1.add(createSolution(0.8, 0.2));

      List<DoubleSolution> solutions2 = new ArrayList<>();
      solutions2.add(createSolution(0.2, 0.8));
      solutions2.add(createSolution(0.5, 0.5));
      solutions2.add(createSolution(0.8, 0.2));

      // Act
      lowKappa.compute(solutions1);
      highKappa.compute(solutions2);

      // Assert - different kappa should produce different fitness distributions
      Double lowKappaFitness = lowKappa.getFitness(solutions1.get(1));
      Double highKappaFitness = highKappa.getFitness(solutions2.get(1));
      assertNotEquals(lowKappaFitness, highKappaFitness);
    }
  }

  @Nested
  @DisplayName("Multi-Objective Tests")
  class MultiObjectiveTests {

    @Test
    @DisplayName("Given Pareto front solutions, when replace is called, then diversity is maintained")
    void givenParetoFrontSolutions_whenReplaceIsCalled_thenDiversityIsMaintained() {
      // Arrange
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(preference);

      // Create a well-distributed Pareto front
      List<DoubleSolution> population = new ArrayList<>();
      population.add(createSolution(0.0, 1.0));
      population.add(createSolution(0.5, 0.5));
      population.add(createSolution(1.0, 0.0));

      // Add some dominated solutions as offspring
      List<DoubleSolution> offspring = new ArrayList<>();
      offspring.add(createSolution(0.6, 0.6));
      offspring.add(createSolution(0.7, 0.7));

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert - non-dominated solutions should survive
      assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Given three objectives, when replace is called, then works correctly")
    void givenThreeObjectives_whenReplaceIsCalled_thenWorksCorrectly() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> pref3D = new IndicatorBasedPreference<>(3);
      IndicatorBasedReplacement<DoubleSolution> replacement =
          new IndicatorBasedReplacement<>(pref3D);

      List<DoubleSolution> population = new ArrayList<>();
      DoubleSolution s1 = new DefaultDoubleSolution(bounds, 3, 0);
      s1.objectives()[0] = 0.2;
      s1.objectives()[1] = 0.3;
      s1.objectives()[2] = 0.5;
      population.add(s1);

      DoubleSolution s2 = new DefaultDoubleSolution(bounds, 3, 0);
      s2.objectives()[0] = 0.5;
      s2.objectives()[1] = 0.2;
      s2.objectives()[2] = 0.3;
      population.add(s2);

      List<DoubleSolution> offspring = new ArrayList<>();
      DoubleSolution s3 = new DefaultDoubleSolution(bounds, 3, 0);
      s3.objectives()[0] = 0.9;
      s3.objectives()[1] = 0.9;
      s3.objectives()[2] = 0.9;
      offspring.add(s3);

      // Act
      List<DoubleSolution> result = replacement.replace(population, offspring);

      // Assert
      assertEquals(2, result.size());
      assertFalse(result.contains(s3));
    }
  }
}
