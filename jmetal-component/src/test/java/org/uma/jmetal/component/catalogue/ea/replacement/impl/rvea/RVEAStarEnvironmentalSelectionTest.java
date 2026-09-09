package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RVEAReplacement;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class RVEAStarEnvironmentalSelection")
class RVEAStarEnvironmentalSelectionTest {
  private RVEAStarEnvironmentalSelection<PointSolution> subject;
  private long previousSeed;

  @BeforeEach
  void setUp() {
    previousSeed = JMetalRandom.getInstance().getSeed();
    JMetalRandom.getInstance().setSeed(42);
    subject = new RVEAStarEnvironmentalSelection<>(2, 10, 2, 1, 2);
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setSeed(previousSeed);
  }

  @Nested
  @DisplayName("When regenerating reference vectors")
  class Regeneration {
    @Test
    @DisplayName(
        "given an inactive direction, when regenerating, then active and predefined vectors stay")
    void givenInactiveDirection_whenRegenerating_thenActiveAndPredefinedVectorsStay() {
      // Arrange
      subject.adaptiveVectors[0] = new double[] {1, 0};
      subject.adaptiveVectors[1] = new double[] {0, 1};
      subject.adaptiveVectors[2] = RVEAGeometry.unit(new double[] {1, 1});
      double[][] before = subject.adaptiveReferenceVectors();
      double[][] predefined = subject.referenceVectors();

      // Act
      subject.afterSelection(List.of(point(0, 8), point(2, 0)));

      // Assert
      double[][] after = subject.adaptiveReferenceVectors();
      assertArrayEquals(before[0], after[0]);
      assertArrayEquals(before[1], after[1]);
      assertFalse(Arrays.equals(before[2], after[2]));
      assertEquals(1, RVEAGeometry.norm(after[2]), 1.0e-12);
      assertTrue(after[2][0] >= 0 && after[2][1] >= 0);
      assertTrue(Arrays.deepEquals(predefined, subject.referenceVectors()));
    }

    @Test
    @DisplayName(
        "given zero objective ranges, when regenerating, then existing vectors remain valid")
    void givenZeroObjectiveRanges_whenRegenerating_thenExistingVectorsRemainValid() {
      // Arrange
      double[][] before = subject.adaptiveReferenceVectors();

      // Act
      subject.afterSelection(List.of(point(1, 1), point(1, 1)));

      // Assert
      assertTrue(Arrays.deepEquals(before, subject.adaptiveReferenceVectors()));
    }
  }

  @Nested
  @DisplayName("When keeping a variable population")
  class VariablePopulation {
    @Test
    @DisplayName(
        "given a populated adaptive niche, when selecting, then it contributes an extra survivor")
    void givenPopulatedAdaptiveNiche_whenSelecting_thenItContributesExtraSurvivor() {
      // Arrange
      subject = new RVEAStarEnvironmentalSelection<>(2, 2, 2, 1, 1);
      subject.adaptiveVectors[0] = RVEAGeometry.unit(new double[] {1, 1});
      subject.adaptiveVectors[1] = RVEAGeometry.unit(new double[] {2, 1});
      var candidates = List.of(point(1, 0), point(0, 1), point(0.5, 0.5), point(2, 2));

      // Act
      List<PointSolution> first = subject.execute(candidates, 2);
      List<PointSolution> last = subject.execute(candidates, 2);

      // Assert
      assertEquals(3, first.size());
      assertFalse(first.contains(candidates.getLast()));
      assertEquals(2, last.size());
      assertEquals(2, last.stream().distinct().count());
    }

    @Test
    @DisplayName(
        "given a collapsed parent population, when replacing again, then the nominal size is"
            + " retained")
    void givenCollapsedParents_whenReplacingAgain_thenNominalSizeIsRetained() {
      // Arrange
      var replacement = new RVEAReplacement<>(subject);
      var initial = List.of(point(1, 1), point(2, 2), point(3, 3));

      // Act
      List<PointSolution> first = replacement.replace(initial, List.of(point(0, 0)));
      List<PointSolution> second = replacement.replace(first, List.of(point(0, 2), point(2, 0)));

      // Assert
      assertEquals(1, first.size());
      assertEquals(List.of(point(0, 0)), second);
      assertEquals(3, subject.populationSize());
    }

    @Test
    @DisplayName(
        "given exported adaptive vectors, when modified, then the algorithm state is isolated")
    void givenExportedAdaptiveVectors_whenModified_thenStateIsIsolated() {
      // Arrange
      double[][] before = subject.adaptiveReferenceVectors();

      // Act
      subject.adaptiveReferenceVectors()[0][0] = -1;

      // Assert
      assertArrayEquals(before[0], subject.adaptiveReferenceVectors()[0]);
    }
  }

  private static PointSolution point(double... objectives) {
    return new PointSolution(objectives);
  }
}
