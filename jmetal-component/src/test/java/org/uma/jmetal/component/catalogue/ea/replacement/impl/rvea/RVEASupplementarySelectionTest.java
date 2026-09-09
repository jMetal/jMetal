package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.pointsolution.PointSolution;

@DisplayName("Unit tests for RVEA supplementary selection strategies")
class RVEASupplementarySelectionTest {
  private static PointSolution point(double... objectives) {
    return new PointSolution(objectives);
  }

  @Nested
  @DisplayName("When selecting by additive epsilon")
  class EpsilonSelection {
    @Test
    @DisplayName(
        "given a dominance chain, when truncating, then the best converged solutions survive")
    void givenDominanceChain_whenTruncating_thenBestConvergedSolutionsSurvive() {
      // Arrange
      var candidates = List.of(point(2, 2), point(1, 1), point(0, 0));

      // Act
      List<PointSolution> selected = EpsilonIndicatorSelection.select(candidates, 2);

      // Assert
      assertEquals(List.of(point(1, 1), point(0, 0)), selected);
      assertEquals(3, candidates.size());
    }

    @Test
    @DisplayName(
        "given rescaled objectives, when selecting by epsilon, then survivor decisions agree")
    void givenRescaledObjectives_whenSelectingByEpsilon_thenSurvivorDecisionsAgree() {
      // Arrange
      var original = List.of(point(0, 1), point(1, 0), point(0.4, 0.4), point(0.8, 0.9));
      var scaled =
          original.stream()
              .map(s -> point(10 + 1000 * s.objectives()[0], -3 + 0.01 * s.objectives()[1]))
              .toList();

      // Act
      var first = EpsilonIndicatorSelection.select(original, 2);
      var second = EpsilonIndicatorSelection.select(scaled, 2);

      // Assert
      assertEquals(
          first.stream().map(original::indexOf).toList(),
          second.stream().map(scaled::indexOf).toList());
    }

    @Test
    @DisplayName(
        "given identical objectives, when truncating, then zero normalization ranges are supported")
    void givenIdenticalObjectives_whenTruncating_thenZeroRangesAreSupported() {
      // Arrange
      var candidates = List.of(point(3, 3), point(3, 3), point(3, 3));

      // Act
      var selected = EpsilonIndicatorSelection.select(candidates, 1);

      // Assert
      assertEquals(List.of(point(3, 3)), selected);
      assertTrue(EpsilonIndicatorSelection.select(candidates, 0).isEmpty());
    }
  }

  @Nested
  @DisplayName("When reducing populations by angular distance")
  class AngularReduction {
    @Test
    @DisplayName(
        "given two nearby directions, when clustering, then retain their nearest-to-ideal member")
    void givenNearbyDirections_whenClustering_thenRetainNearestToIdealMember() {
      // Arrange
      var candidates = List.of(point(1, 0), point(2, 0.1), point(0, 1));

      // Act
      var selected = RVEATruncation.cluster(candidates, 2);

      // Assert
      assertEquals(List.of(point(1, 0), point(0, 1)), selected);
      assertTrue(RVEATruncation.cluster(candidates, 0).isEmpty());
    }

    @Test
    @DisplayName(
        "given a duplicate direction, when deleting crowding, then preserve distinct extreme"
            + " directions")
    void givenDuplicateDirection_whenDeletingCrowding_thenPreserveDistinctExtremes() {
      // Arrange
      var candidates = List.of(point(0, 1), point(0.5, 0.5), point(0.5, 0.5), point(1, 0));

      // Act
      var selected = RVEATruncation.crowded(candidates, 3);

      // Assert
      assertEquals(3, selected.stream().distinct().count());
      assertTrue(selected.contains(point(0, 1)));
      assertTrue(selected.contains(point(1, 0)));
      assertFalse(selected.isEmpty());
    }
  }
}
