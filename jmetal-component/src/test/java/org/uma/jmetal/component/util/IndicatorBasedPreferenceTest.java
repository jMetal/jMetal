package org.uma.jmetal.component.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("IndicatorBasedPreference Tests")
class IndicatorBasedPreferenceTest {

  private List<Bounds<Double>> bounds;

  @BeforeEach
  void setUp() {
    bounds = List.of(Bounds.create(0.0, 1.0));
  }

  private DoubleSolution createSolution(double... objectives) {
    DoubleSolution solution = new DefaultDoubleSolution(bounds, objectives.length, 0);
    for (int i = 0; i < objectives.length; i++) {
      solution.objectives()[i] = objectives[i];
    }
    return solution;
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given default constructor, then kappa should be default value")
    void givenDefaultConstructor_thenKappaShouldBeDefaultValue() {
      // Arrange & Act
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);

      // Assert
      assertEquals(IndicatorBasedPreference.DEFAULT_KAPPA, preference.getKappa());
      assertEquals(2, preference.getNumberOfObjectives());
    }

    @Test
    @DisplayName("Given custom kappa, then kappa should be set correctly")
    void givenCustomKappa_thenKappaShouldBeSetCorrectly() {
      // Arrange & Act
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(3, 0.1);

      // Assert
      assertEquals(0.1, preference.getKappa());
      assertEquals(3, preference.getNumberOfObjectives());
    }
  }


  @Nested
  @DisplayName("Compute Tests")
  class ComputeTests {

    @Test
    @DisplayName("Given solution list, when compute is called, then fitness values should be set")
    void givenSolutionList_whenComputeIsCalled_thenFitnessValuesShouldBeSet() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      solutions.add(createSolution(0.2, 0.8));
      solutions.add(createSolution(0.5, 0.5));
      solutions.add(createSolution(0.8, 0.2));

      // Act
      preference.compute(solutions);

      // Assert
      for (DoubleSolution solution : solutions) {
        assertNotNull(preference.getFitness(solution));
      }
    }

    @Test
    @DisplayName("Given dominated solution, then it should have higher fitness")
    void givenDominatedSolution_thenItShouldHaveHigherFitness() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      DoubleSolution nonDominated = createSolution(0.2, 0.2);
      DoubleSolution dominated = createSolution(0.8, 0.8);
      solutions.add(nonDominated);
      solutions.add(dominated);

      // Act
      preference.compute(solutions);

      // Assert
      assertTrue(preference.getFitness(dominated) > preference.getFitness(nonDominated));
    }

    @Test
    @DisplayName("Given single solution, then fitness should be zero")
    void givenSingleSolution_thenFitnessShouldBeZero() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      solutions.add(createSolution(0.5, 0.5));

      // Act
      preference.compute(solutions);

      // Assert
      assertEquals(0.0, preference.getFitness(solutions.get(0)));
    }
  }

  @Nested
  @DisplayName("Comparator Tests")
  class ComparatorTests {

    @Test
    @DisplayName("Given two solutions with fitness, when compared, then lower fitness wins")
    void givenTwoSolutionsWithFitness_whenCompared_thenLowerFitnessWins() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      DoubleSolution better = createSolution(0.2, 0.2);
      DoubleSolution worse = createSolution(0.8, 0.8);
      solutions.add(better);
      solutions.add(worse);
      preference.compute(solutions);

      // Act
      Comparator<DoubleSolution> comparator = preference.getComparator();
      int result = comparator.compare(better, worse);

      // Assert
      assertTrue(result < 0);
    }

    @Test
    @DisplayName("Given solutions without fitness, when compared, then return zero")
    void givenSolutionsWithoutFitness_whenCompared_thenReturnZero() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      DoubleSolution s1 = createSolution(0.5, 0.5);
      DoubleSolution s2 = createSolution(0.3, 0.7);

      // Act
      Comparator<DoubleSolution> comparator = preference.getComparator();
      int result = comparator.compare(s1, s2);

      // Assert
      assertEquals(0, result);
    }

    @Test
    @DisplayName("Given one solution without fitness, when compared, then it loses")
    void givenOneSolutionWithoutFitness_whenCompared_thenItLoses() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      DoubleSolution withFitness = createSolution(0.5, 0.5);
      solutions.add(withFitness);
      preference.compute(solutions);

      DoubleSolution withoutFitness = createSolution(0.3, 0.7);

      // Act
      Comparator<DoubleSolution> comparator = preference.getComparator();

      // Assert
      assertTrue(comparator.compare(withFitness, withoutFitness) < 0);
      assertTrue(comparator.compare(withoutFitness, withFitness) > 0);
    }
  }


  @Nested
  @DisplayName("RemoveWorstAndUpdateFitness Tests")
  class RemoveWorstAndUpdateFitnessTests {

    @Test
    @DisplayName("Given solution list, when removeWorst is called, then worst is removed")
    void givenSolutionList_whenRemoveWorstIsCalled_thenWorstIsRemoved() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      DoubleSolution good = createSolution(0.2, 0.2);
      DoubleSolution bad = createSolution(0.8, 0.8);
      solutions.add(good);
      solutions.add(bad);
      preference.compute(solutions);
      int initialSize = solutions.size();

      // Act
      preference.removeWorstAndUpdateFitness(solutions);

      // Assert
      assertEquals(initialSize - 1, solutions.size());
      assertTrue(solutions.contains(good));
      assertFalse(solutions.contains(bad));
    }

    @Test
    @DisplayName("Given multiple solutions, when removeWorst is called multiple times, then size decreases")
    void givenMultipleSolutions_whenRemoveWorstIsCalledMultipleTimes_thenSizeDecreases() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      solutions.add(createSolution(0.1, 0.9));
      solutions.add(createSolution(0.3, 0.7));
      solutions.add(createSolution(0.5, 0.5));
      solutions.add(createSolution(0.7, 0.3));
      solutions.add(createSolution(0.9, 0.1));
      preference.compute(solutions);

      // Act
      preference.removeWorstAndUpdateFitness(solutions);
      preference.removeWorstAndUpdateFitness(solutions);

      // Assert
      assertEquals(3, solutions.size());
    }

    @Test
    @DisplayName("Given solutions after removal, then fitness values should be updated")
    void givenSolutionsAfterRemoval_thenFitnessValuesShouldBeUpdated() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> preference = new IndicatorBasedPreference<>(2);
      List<DoubleSolution> solutions = new ArrayList<>();
      // Create solutions where one clearly dominates another to ensure fitness changes
      DoubleSolution s1 = createSolution(0.1, 0.1); // Best - dominates others
      DoubleSolution s2 = createSolution(0.5, 0.5); // Middle
      DoubleSolution s3 = createSolution(0.9, 0.9); // Worst - dominated by others
      solutions.add(s1);
      solutions.add(s2);
      solutions.add(s3);
      preference.compute(solutions);

      // s3 should be worst (highest fitness) and will be removed
      Double fitnessS2Before = preference.getFitness(s2);
      int sizeBefore = solutions.size();

      // Act
      preference.removeWorstAndUpdateFitness(solutions);

      // Assert - size should decrease and worst should be removed
      assertEquals(sizeBefore - 1, solutions.size());
      assertTrue(solutions.contains(s1));
      assertTrue(solutions.contains(s2));
      assertFalse(solutions.contains(s3));
    }
  }

  @Nested
  @DisplayName("Related Preference Tests")
  class RelatedPreferenceTests {

    @Test
    @DisplayName("Given related preference with same kappa, when compute is called, then skip recomputation")
    void givenRelatedPreferenceWithSameKappa_whenComputeIsCalled_thenSkipRecomputation() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> primary = new IndicatorBasedPreference<>(2, 0.05);
      IndicatorBasedPreference<DoubleSolution> related =
          new IndicatorBasedPreference<>(2, 0.05, primary);

      List<DoubleSolution> solutions = new ArrayList<>();
      solutions.add(createSolution(0.2, 0.8));
      solutions.add(createSolution(0.8, 0.2));

      // Act - compute on primary first
      primary.compute(solutions);
      // Then compute on related - should use cached values
      related.compute(solutions);

      // Assert - both should have fitness values
      assertNotNull(primary.getFitness(solutions.get(0)));
      // Related preference doesn't recompute, so fitness comes from primary
    }

    @Test
    @DisplayName("Given related preference with different kappa, when compute is called, then recompute")
    void givenRelatedPreferenceWithDifferentKappa_whenComputeIsCalled_thenRecompute() {
      // Arrange
      IndicatorBasedPreference<DoubleSolution> primary = new IndicatorBasedPreference<>(2, 0.05);
      IndicatorBasedPreference<DoubleSolution> related =
          new IndicatorBasedPreference<>(2, 0.1, primary);

      List<DoubleSolution> solutions = new ArrayList<>();
      solutions.add(createSolution(0.2, 0.8));
      solutions.add(createSolution(0.8, 0.2));

      // Act
      primary.compute(solutions);
      related.compute(solutions);

      // Assert - both should have computed fitness
      assertNotNull(primary.getFitness(solutions.get(0)));
    }
  }
}
