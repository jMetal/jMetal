package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RveaTrajectoryFixtures.TaggedSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for the feasibility-first selection of iRVEA")
class RveaConstraintSelectionTest {
  private FeasibilityFirstSelection<TaggedSolution> policy;

  @BeforeEach
  void setUp() {
    policy = new FeasibilityFirstSelection<>();
    JMetalRandom.getInstance().setSeed(71);
  }

  private static TaggedSolution solution(int id, double x, double y, double... constraints) {
    return new TaggedSolution(id, new double[] {x, y}, constraints);
  }

  private static IRVEAEnvironmentalSelection<TaggedSolution> selector() {
    var vectors = List.of(new double[] {1, 0}, new double[] {0, 1});
    return new IRVEAEnvironmentalSelection<>(2, 24, 2, 0.2, vectors, 2);
  }

  @Nested
  @DisplayName("When ordering infeasible candidates")
  class WhenOrderingInfeasibleCandidates {
    @Test
    @DisplayName(
        "given several violated constraints, when supplementing, then candidates are ordered by"
            + " the sum of their violations, keeping the input order on ties")
    void givenSeveralViolatedConstraints_whenSupplementing_thenOrderByTheSumOfViolations() {
      // Arrange
      var worse = solution(0, 0, 0, -2, 100, -3);
      var firstTie = solution(1, 100, 100, -1, 0, -1);
      var secondTie = solution(2, -100, -100, -2, 10, 0);
      var best = solution(3, 1000, 1000, -0.1, 5, 0);
      var partition = policy.partition(List.of(worse, firstTie, secondTie, best));

      // Act
      var result = policy.supplement(partition, List.of(), 3);

      // Assert
      assertThat(ConstraintHandling.overallConstraintViolationDegree(worse)).isEqualTo(-5);
      assertThat(ConstraintHandling.numberOfViolatedConstraints(worse)).isEqualTo(2);
      assertThat(result).containsExactly(best, firstTie, secondTie);
    }

    @Test
    @DisplayName(
        "given precomputed constraint attributes, when selecting, then they decide the"
            + " feasibility and the order")
    void givenPrecomputedAttributes_whenSelecting_thenTheyDecideFeasibilityAndOrder() {
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
      assertThat(partition.feasible()).containsExactly(feasible);
      assertThat(result).containsExactly(feasible, best);
      var statistics = policy.statistics();
      assertThat(statistics.candidateVisits()).isEqualTo(3);
      assertThat(statistics.feasibleVisits()).isEqualTo(1);
      assertThat(statistics.infeasibleVisits()).isEqualTo(2);
      assertThat(statistics.affectedSelections()).isEqualTo(1);
      assertThat(statistics.infeasibleSurvivors()).isEqualTo(1);
      assertThat(statistics.violationComparisons()).isPositive();
    }
  }

  @Nested
  @DisplayName("When there are enough feasible candidates")
  class WhenThereAreEnoughFeasibleCandidates {
    @Test
    @DisplayName(
        "given an infeasible ideal point, when selecting, then only feasible candidates survive")
    void givenAnInfeasibleIdeal_whenSelecting_thenOnlyFeasibleCandidatesSurvive() {
      // Arrange
      var selection = selector();
      List<TaggedSolution> candidates = new ArrayList<>();
      for (int i = 0; i < 8; i++) {
        candidates.add(solution(i, 5, 5, 0, 1));
      }
      candidates.add(solution(99, 0, 0, -0.001));

      // Act
      var survivors = selection.execute(candidates, 2);

      // Assert: identical feasible points occupy one niche, and the population is not refilled
      assertThat(survivors).hasSize(1).allMatch(ConstraintHandling::isFeasible);
      assertThat(selection.constraintStatistics().infeasibleSurvivors()).isZero();
    }
  }

  @Nested
  @DisplayName("When there are not enough feasible candidates")
  class WhenThereAreNotEnoughFeasibleCandidates {
    @Test
    @DisplayName(
        "given infeasible candidates, when selecting, then the least violating ones follow the"
            + " feasible survivors in a stable order")
    void givenInfeasibleCandidates_whenSelecting_thenTheLeastViolatingOnesFollow() {
      // Arrange
      var selection = selector();
      var feasible = solution(0, 5, 5, 0);
      var worse = solution(1, 1, 10, -3);
      var best = solution(2, 10, 1, -1);
      var tied = solution(3, 9, 2, -1);

      // Act
      var survivors = selection.execute(List.of(feasible, worse, best, tied), 2);

      // Assert
      assertThat(survivors).isIn(List.of(feasible, best), List.of(feasible, best, tied));
    }

    @Test
    @DisplayName(
        "given feasible and infeasible candidates in one niche, when selecting, then only the"
            + " feasible one survives")
    void givenMixedCandidatesInOneNiche_whenSelecting_thenOnlyTheFeasibleOneSurvives() {
      // Arrange
      var selection = selector();
      var feasible = solution(1, 1, 1, 1);
      var candidates = List.of(feasible, solution(2, 1, 1, -1), solution(3, 1, 1, -2));

      // Act
      var survivors = selection.execute(candidates, 2);

      // Assert
      assertThat(survivors).containsExactly(feasible);
    }
  }

  @Nested
  @DisplayName("When every candidate is infeasible")
  class WhenEveryCandidateIsInfeasible {
    @Test
    @DisplayName(
        "given candidates in one niche, when selecting, then only the least violating survives"
            + " and the vectors do not change")
    void givenCandidatesInOneNiche_whenSelecting_thenOnlyTheLeastViolatingSurvives() {
      // Arrange
      var selection = selector();
      var best = solution(1, 1, 1, -1);
      var tied = solution(2, 1, 1, -1);
      var worse = solution(3, 1, 1, -10);
      String vectors = Arrays.deepToString(selection.referenceVectors());
      String adaptive = Arrays.deepToString(selection.adaptiveReferenceVectors());

      // Act
      var survivors = selection.execute(List.of(worse, best, tied), 2);

      // Assert
      assertThat(survivors).containsExactly(best);
      assertThat(Arrays.deepToString(selection.referenceVectors())).isEqualTo(vectors);
      assertThat(selection.currentGeneration()).isZero();
      assertThat(Arrays.deepToString(selection.adaptiveReferenceVectors())).isEqualTo(adaptive);
      assertThat(selection.archive()).isEmpty();
    }

    @Test
    @DisplayName(
        "given an iRVEA archive with feasible solutions, when selecting, then copies of the"
            + " archive survive")
    void givenAFeasibleIRVEAArchive_whenSelecting_thenCopiesOfTheArchiveSurvive() {
      // Arrange
      var selection = selector();
      selection.execute(List.of(solution(0, 0, 10, 0), solution(1, 10, 0, 0)), 2);
      var expected = selection.archive();

      // Act
      var survivors = selection.execute(List.of(solution(99, -100, -100, -1)), 2);
      survivors.getFirst().objectives()[0] = 999;

      // Assert
      assertThat(survivors).isEqualTo(expected).allMatch(ConstraintHandling::isFeasible);
      assertThat(selection.archive()).noneMatch(solution -> solution.objectives()[0] == 999);
    }
  }
}
