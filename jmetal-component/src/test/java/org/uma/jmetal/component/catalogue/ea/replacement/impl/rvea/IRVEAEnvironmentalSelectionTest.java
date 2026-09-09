package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class IRVEAEnvironmentalSelection")
class IRVEAEnvironmentalSelectionTest {
  private IRVEAEnvironmentalSelection<PointSolution> subject;
  private long previousSeed;

  @BeforeEach
  void setUp() {
    previousSeed = JMetalRandom.getInstance().getSeed();
    JMetalRandom.getInstance().setSeed(42);
    subject = new IRVEAEnvironmentalSelection<>(2, 10, 2, 1, 2);
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setSeed(previousSeed);
  }

  @Nested
  @DisplayName("When replacing inactive directions")
  class VectorReplacement {
    @Test
    @DisplayName(
        "given multiple inactive vectors, when replacing, then only one uses the least covered"
            + " direction")
    void givenMultipleInactiveVectors_whenReplacing_thenOnlyOneUsesLeastCoveredDirection() {
      // Arrange
      for (int i = 0; i < subject.populationSize(); i++) {
        subject.adaptiveVectors[i] = subject.referenceVectors()[i];
      }
      double[][] before = subject.adaptiveReferenceVectors();
      double[][] predefined = subject.referenceVectors();
      var farthest = point(0.7, 0.3);

      // Act
      subject.replaceInactiveVector(List.of(point(0, 1), point(1, 0), farthest));

      // Assert
      double[][] after = subject.adaptiveReferenceVectors();
      int[] changed =
          IntStream.range(0, before.length)
              .filter(i -> !Arrays.equals(before[i], after[i]))
              .toArray();
      assertEquals(1, changed.length);
      assertArrayEquals(RVEAGeometry.unit(farthest.objectives()), after[changed[0]], 1.0e-12);
      assertTrue(Arrays.deepEquals(predefined, subject.referenceVectors()));
    }

    @Test
    @DisplayName(
        "given an active adaptive vector, when replacing another, then its direction is unchanged")
    void givenActiveAdaptiveVector_whenReplacingAnother_thenItsDirectionIsUnchanged() {
      // Arrange
      subject.adaptiveVectors[0] = RVEAGeometry.unit(new double[] {2, 1});
      subject.adaptiveVectors[1] = new double[] {1, 0};
      subject.adaptiveVectors[2] = new double[] {0, 1};
      double[] active = subject.adaptiveVectors[0].clone();

      // Act
      subject.replaceInactiveVector(
          List.of(point(0, 1), point(1, 0), point(2, 1), point(0.2, 0.8)));

      // Assert
      assertArrayEquals(active, subject.adaptiveReferenceVectors()[0]);
    }

    @Test
    @DisplayName("given identical objectives, when replacing, then no zero direction is created")
    void givenIdenticalObjectives_whenReplacing_thenNoZeroDirectionIsCreated() {
      // Arrange
      double[][] before = subject.adaptiveReferenceVectors();

      // Act
      subject.replaceInactiveVector(List.of(point(4, 4), point(4, 4)));

      // Assert
      assertTrue(Arrays.deepEquals(before, subject.adaptiveReferenceVectors()));
    }
  }

  @Nested
  @DisplayName("When supplementing APD selection")
  class SecondarySelection {
    @ParameterizedTest
    @CsvSource({"6, 2", "7, 1", "10, 1"})
    @DisplayName(
        "given angular tradeoffs, when choosing secondary dominance, then switch above six"
            + " objectives")
    void givenAngularTradeoffs_whenChoosingSecondaryDominance_thenSwitchAboveSixObjectives(
        int objectives, int expected) {
      // Arrange
      subject = new IRVEAEnvironmentalSelection<>(objectives, 10, 2, 1, 1);
      double[] first = new double[objectives];
      first[objectives - 1] = 1;
      double[] second = new double[objectives];
      Arrays.fill(second, 1);
      second[objectives - 1] = 0;

      // Act
      List<PointSolution> selected =
          subject.secondarySelection(List.of(point(first), point(second)));

      // Assert
      assertEquals(expected, selected.size());
      assertTrue(selected.contains(point(first)));
    }

    @Test
    @DisplayName(
        "given a region with only dominated points, when selecting, then its nearest point"
            + " survives")
    void givenRegionWithOnlyDominatedPoints_whenSelecting_thenItsNearestPointSurvives() {
      // Arrange
      var best = point(0, 0);
      var promising = point(2, 0.1);
      var distant = point(3, 0.2);
      List<PointSolution> candidates = List.of(best, promising, distant);

      // Act
      List<PointSolution> protectedSolutions = subject.protectRegions(candidates, List.of(best));
      List<PointSolution> selected = subject.execute(candidates, 3);

      // Assert
      assertEquals(List.of(promising), protectedSolutions);
      assertTrue(selected.contains(promising));
      assertFalse(selected.contains(distant));
    }

    @Test
    @DisplayName(
        "given insufficient active niches late in evolution, when selecting, then epsilon fills"
            + " from candidates")
    void givenInsufficientNichesLate_whenSelecting_thenEpsilonFillsFromCandidates() {
      // Arrange
      subject =
          new IRVEAEnvironmentalSelection<>(
              2, 10, 2, 1, List.of(new double[] {1, 0}, new double[] {1, 0}, new double[] {1, 0}));
      var candidates = List.of(point(0, 1), point(0.5, 0.5), point(1, 0));
      List<PointSolution> selected = List.of();

      // Act
      for (int generation = 0; generation < 10; generation++) {
        for (int i = 0; i < subject.populationSize(); i++) {
          subject.adaptiveVectors[i] = new double[] {1, 0};
        }
        selected = subject.execute(candidates, 3);
      }

      // Assert
      assertEquals(3, selected.size());
      assertTrue(selected.containsAll(candidates));
    }
  }

  @Nested
  @DisplayName("When maintaining the epsilon archive")
  class Archive {
    @Test
    @DisplayName(
        "given old and new solutions, when updating, then keep bounded historical solutions as"
            + " copies")
    void givenOldAndNewSolutions_whenUpdating_thenKeepBoundedHistoryAsCopies() {
      // Arrange
      var old = point(0, 1);
      var recent = point(1, 0);
      subject.afterSelection(List.of(old));

      // Act
      subject.afterSelection(List.of(recent, point(3, 3), point(4, 4)));
      old.objectives()[0] = 99;
      List<PointSolution> exported = subject.archive();
      exported.getFirst().objectives()[0] = -99;

      // Assert
      assertEquals(3, subject.archive().size());
      assertTrue(subject.archive().contains(point(0, 1)));
      assertTrue(subject.archive().contains(recent));
      assertFalse(subject.archive().contains(point(4, 4)));
    }
  }

  private static PointSolution point(double... objectives) {
    return new PointSolution(objectives);
  }
}
