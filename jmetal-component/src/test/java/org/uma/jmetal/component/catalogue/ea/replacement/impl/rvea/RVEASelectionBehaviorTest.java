package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class RVEAEnvironmentalSelection")
class RVEASelectionBehaviorTest {
  private RVEAEnvironmentalSelection<PointSolution> subject;

  @BeforeEach
  void setUp() {
    subject =
        new RVEAEnvironmentalSelection<>(
            2, 10, 2.0, 1.0, List.of(new double[] {1, 0}, new double[] {0, 1}));
  }

  @Nested
  @DisplayName("When selecting by APD")
  class ApdSelection {
    @Test
    @DisplayName("given competing convergence and angle, when time passes, then the winner changes")
    void givenCompetingConvergenceAndAngle_whenTimePasses_thenWinnerChanges() {
      // Arrange
      var x = point(1, 0);
      var y = point(0, 1);
      var middle = point(0.5, 0.6);
      List<PointSolution> candidates = List.of(x, y, middle);

      // Act
      List<PointSolution> first = subject.execute(candidates, 2);
      List<PointSolution> last = first;
      for (int generation = 1; generation < 10; generation++) {
        last = subject.execute(candidates, 2);
      }

      // Assert
      assertEquals(List.of(x, middle), first);
      assertEquals(List.of(x, y), last);
      assertArrayEquals(new double[] {0.5, 0.6}, middle.objectives());
      assertEquals(9, subject.currentGeneration());
    }

    @Test
    @DisplayName("given an empty niche, when selecting, then no unrelated solution fills it")
    void givenEmptyNiche_whenSelecting_thenNoUnrelatedSolutionFillsIt() {
      // Arrange
      subject = new RVEAEnvironmentalSelection<>(2, 10, 2, 1, 2);
      List<PointSolution> candidates = List.of(point(0, 1), point(0, 2), point(0, 3));

      // Act
      List<PointSolution> result = subject.execute(candidates, 3);

      // Assert
      assertEquals(1, result.size());
      assertEquals(candidates.getFirst(), result.getFirst());
    }

    @Test
    @DisplayName(
        "given translated candidates, when selecting, then selection is translation invariant")
    void givenTranslatedCandidates_whenSelecting_thenSelectionIsTranslationInvariant() {
      // Arrange
      List<PointSolution> candidates = List.of(point(-3, 2), point(-4, 3), point(-3.5, 2.6));

      // Act
      List<PointSolution> result = subject.execute(candidates, 2);

      // Assert
      assertEquals(List.of(candidates.get(0), candidates.get(2)), result);
    }
  }

  @Nested
  @DisplayName("When adapting reference vectors")
  class VectorAdaptation {
    @Test
    @DisplayName(
        "given unequal ranges, when adapting, then original directions scale by survivor ranges")
    void givenUnequalRanges_whenAdapting_thenOriginalDirectionsScaleBySurvivorRanges() {
      // Arrange
      subject = new RVEAEnvironmentalSelection<>(2, 10, 2, 0.2, 2);
      var population = List.of(point(2, 0), point(0, 8), point(1, 1));

      // Act
      subject.execute(population, 3);
      double[][] adapted = subject.referenceVectors();
      subject.execute(List.of(point(6, 0), point(0, 3), point(1, 1)), 3);
      double[][] betweenAdaptations = subject.referenceVectors();
      subject.execute(population, 3);

      // Assert
      assertArrayEquals(new double[] {1 / Math.sqrt(17), 4 / Math.sqrt(17)}, adapted[1], 1.0e-12);
      assertArrayEquals(adapted[1], betweenAdaptations[1]);
      assertArrayEquals(adapted[1], subject.referenceVectors()[1], 1.0e-12);
    }

    @Test
    @DisplayName(
        "given collapsed ranges and duplicate vectors, when selecting, then directions stay finite")
    void givenCollapsedRangesAndDuplicateVectors_whenSelecting_thenDirectionsStayFinite() {
      // Arrange
      subject =
          new RVEAEnvironmentalSelection<>(
              2, 2, 0, 0.1, List.of(new double[] {1, 1}, new double[] {2, 2}));

      // Act
      List<PointSolution> result = subject.execute(List.of(point(7, 7), point(7, 7)), 2);

      // Assert
      assertEquals(1, result.size());
      assertTrue(
          Arrays.stream(subject.referenceVectors())
              .flatMapToDouble(Arrays::stream)
              .allMatch(Double::isFinite));
    }

    @Test
    @DisplayName(
        "given exported vectors, when a caller mutates them, then selection state is unchanged")
    void givenExportedVectors_whenCallerMutatesThem_thenStateIsUnchanged() {
      // Arrange
      double[][] vectors = subject.referenceVectors();

      // Act
      vectors[0][0] = 42;

      // Assert
      assertEquals(1, subject.referenceVectors()[0][0]);
    }
  }

  @Nested
  @DisplayName("When validating numerical inputs")
  class Validation {
    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, -1})
    @DisplayName("given invalid alpha, when creating selection, then reject the configuration")
    void givenInvalidAlpha_whenCreatingSelection_thenRejectConfiguration(double alpha) {
      // Arrange / Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new RVEAEnvironmentalSelection<>(2, 10, alpha, 0.1, 1));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.1, 1.1, Double.NaN, Double.POSITIVE_INFINITY})
    @DisplayName("given invalid frequency, when creating selection, then reject the configuration")
    void givenInvalidFrequency_whenCreatingSelection_thenRejectConfiguration(double frequency) {
      // Arrange / Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new RVEAEnvironmentalSelection<>(2, 10, 2, frequency, 1));
    }

    @Test
    @DisplayName(
        "given invalid directions or objectives, when selecting, then fail before state changes")
    void givenInvalidDirectionsOrObjectives_whenSelecting_thenFailBeforeStateChanges() {
      // Arrange / Act & Assert
      for (double[] vector :
          List.of(
              new double[] {0, 0},
              new double[] {-1, 1},
              new double[] {Double.NaN, 1},
              new double[] {1, 2, 3})) {
        assertThrows(
            InvalidConditionException.class,
            () -> new RVEAEnvironmentalSelection<>(2, 10, 2, 0.1, List.of(vector)));
      }
      assertThrows(
          InvalidConditionException.class, () -> subject.execute(List.of(point(Double.NaN, 1)), 2));
      assertThrows(InvalidConditionException.class, () -> subject.execute(List.of(), 2));
      assertEquals(-1, subject.currentGeneration());
    }

    @Test
    @DisplayName("given very large finite directions, when normalizing, then no overflow occurs")
    void givenLargeFiniteDirections_whenNormalizing_thenNoOverflowOccurs() {
      // Arrange
      subject =
          new RVEAEnvironmentalSelection<>(2, 1, 2, 1, List.of(new double[] {1.0e300, 1.0e300}));

      // Act
      double[] vector = subject.referenceVectors()[0];

      // Assert
      assertArrayEquals(new double[] {Math.sqrt(0.5), Math.sqrt(0.5)}, vector, 1.0e-12);
      assertFalse(Double.isNaN(RVEAGeometry.angle(vector, vector)));
    }
  }

  private static PointSolution point(double... objectives) {
    return new PointSolution(objectives);
  }
}
