package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RveaTrajectoryFixtures.TaggedSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("RVEA-family feasibility-first selection")
class RveaConstraintSelectionTest {
  private FeasibilityFirstSelection<TaggedSolution> policy;

  @BeforeEach
  void setUp() {
    policy = new FeasibilityFirstSelection<>();
    JMetalRandom.getInstance().setSeed(71);
  }

  private TaggedSolution solution(int id, double x, double y, double... constraints) {
    return new TaggedSolution(id, new double[] {x, y}, constraints);
  }

  private RVEAEnvironmentalSelection<TaggedSolution> selector(String variant) {
    var vectors = List.of(new double[] {1, 0}, new double[] {0, 1});
    return switch (variant) {
      case "RVEA" -> new RVEAEnvironmentalSelection<>(2, 24, 2, 0.2, vectors);
      case "RVEAStar" -> new RVEAStarEnvironmentalSelection<>(2, 24, 2, 0.2, vectors);
      case "IRVEA" -> new IRVEAEnvironmentalSelection<>(2, 24, 2, 0.2, vectors, 2);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  @Nested
  @DisplayName("When aggregating canonical constraints")
  class CanonicalConstraints {
    @Test
    @DisplayName("given multiple constraints, when ordering fallback, then use negative sum")
    void givenMultipleConstraints_whenOrdering_thenUseNegativeSum() {
      // Arrange
      var worse = solution(0, 0, 0, -2, 100, -3);
      var firstTie = solution(1, 100, 100, -1, 0, -1);
      var secondTie = solution(2, -100, -100, -2, 10, 0);
      var best = solution(3, 1000, 1000, -0.1, 5, 0);
      var partition = policy.partition(List.of(worse, firstTie, secondTie, best));

      // Act
      var result = policy.supplement(partition, List.of(), 3);

      // Assert
      assertEquals(-5, ConstraintHandling.overallConstraintViolationDegree(worse));
      assertEquals(2, ConstraintHandling.numberOfViolatedConstraints(worse));
      assertEquals(List.of(best, firstTie, secondTie), result);
    }

    @Test
    @DisplayName("given precomputed attributes, when selecting, then honor jMetal feasibility")
    void givenPrecomputedAttributes_whenSelecting_thenHonorCanonicalValues() {
      // Arrange
      var feasible = solution(0, 5, 5, -20);
      ConstraintHandling.numberOfViolatedConstraints(feasible, 0);
      ConstraintHandling.overallConstraintViolationDegree(feasible, 0.0);
      var best = solution(1, 5, 5, -100);
      ConstraintHandling.overallConstraintViolationDegree(best, -0.1);
      var worse = solution(2, 0, 0, -1);

      // Act
      var partition = policy.partition(List.of(worse, feasible, best));
      var result = policy.supplement(partition, partition.feasible(), 2);

      // Assert
      assertEquals(List.of(feasible), partition.feasible());
      assertEquals(List.of(feasible, best), result);
      assertEquals(3, policy.statistics().candidateVisits());
      assertEquals(1, policy.statistics().feasibleVisits());
      assertEquals(2, policy.statistics().infeasibleVisits());
      assertEquals(1, policy.statistics().affectedSelections());
      assertEquals(1, policy.statistics().infeasibleSurvivors());
      assertTrue(policy.statistics().violationComparisons() > 0);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName("given enough feasible candidates, when selecting, then exclude infeasible ideals")
  void givenEnoughFeasibleCandidates_whenSelecting_thenExcludeInfeasibleIdeals(String variant) {
    // Arrange
    var selection = selector(variant);
    List<TaggedSolution> candidates = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      candidates.add(solution(i, 5, 5, 0, 1));
    }
    candidates.add(solution(99, 0, 0, -0.001));

    // Act
    var survivors = selection.execute(candidates, 2);

    // Assert: identical feasible points occupy only one niche; do not fill back to N.
    assertEquals(1, survivors.size());
    assertTrue(survivors.stream().allMatch(ConstraintHandling::isFeasible));
    assertEquals(0, selection.constraintStatistics().infeasibleSurvivors());
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName(
      "given insufficient feasible candidates, when supplementing, then keep stable CV order")
  void givenInsufficientFeasibleCandidates_whenSupplementing_thenKeepStableOrder(String variant) {
    // Arrange
    var selection = selector(variant);
    var feasible = solution(0, 5, 5, 0);
    var worse = solution(1, 1, 10, -3);
    var best = solution(2, 10, 1, -1);
    var tied = solution(3, 9, 2, -1);

    // Act
    var survivors = selection.execute(List.of(feasible, worse, best, tied), 2);

    // Assert
    assertEquals(feasible, survivors.getFirst());
    assertTrue(survivors.size() >= 2);
    assertEquals(best, survivors.get(1));
    if (survivors.size() == 3) {
      assertEquals(tied, survivors.get(2));
    }
    assertFalse(survivors.contains(worse));
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName("given all infeasible and empty niches, when selecting, then retain best CV only")
  void givenAllInfeasibleAndEmptyNiches_whenSelecting_thenRetainBestOnly(String variant) {
    // Arrange
    var selection = selector(variant);
    var best = solution(1, 1, 1, -1);
    var tied = solution(2, 1, 1, -1);
    var worse = solution(3, 1, 1, -10);
    String vectors = Arrays.deepToString(selection.referenceVectors());
    String adaptive =
        selection instanceof RVEAStarEnvironmentalSelection<?> star
            ? Arrays.deepToString(star.adaptiveReferenceVectors())
            : "";

    // Act
    var survivors = selection.execute(List.of(worse, best, tied), 2);

    // Assert
    assertEquals(List.of(best), survivors);
    assertEquals(vectors, Arrays.deepToString(selection.referenceVectors()));
    assertEquals(0, selection.currentGeneration());
    if (selection instanceof RVEAStarEnvironmentalSelection<?> star) {
      assertEquals(adaptive, Arrays.deepToString(star.adaptiveReferenceVectors()));
    }
    if (selection instanceof IRVEAEnvironmentalSelection<?> improved) {
      assertTrue(improved.archive().isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"RVEA", "RVEAStar", "IRVEA"})
  @DisplayName("given mixed candidates in one niche, when selecting, then preserve shrinkage")
  void givenMixedCandidatesInOneNiche_whenSelecting_thenPreserveShrinkage(String variant) {
    // Arrange
    var selection = selector(variant);
    var feasible = solution(1, 1, 1, 1);

    // Act
    var survivors =
        selection.execute(List.of(feasible, solution(2, 1, 1, -1), solution(3, 1, 1, -2)), 2);

    // Assert
    assertEquals(List.of(feasible), survivors);
  }

  @Test
  @DisplayName(
      "given an iRVEA feasible archive, when all new inputs violate, then reuse safe copies")
  void givenFeasibleArchive_whenNewInputsViolate_thenReuseSafeCopies() {
    // Arrange
    var selection = (IRVEAEnvironmentalSelection<TaggedSolution>) selector("IRVEA");
    selection.execute(List.of(solution(0, 0, 10, 0), solution(1, 10, 0, 0)), 2);
    var expected = selection.archive();

    // Act
    var survivors = selection.execute(List.of(solution(99, -100, -100, -1)), 2);
    survivors.getFirst().objectives()[0] = 999;

    // Assert
    assertEquals(expected, survivors);
    assertTrue(survivors.stream().allMatch(ConstraintHandling::isFeasible));
    assertTrue(selection.archive().stream().noneMatch(s -> s.objectives()[0] == 999));
  }
}
