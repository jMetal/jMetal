package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RveaTrajectoryFixtures.TaggedSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for the per-niche constraint handling of RVEA (C-RVEA)")
class RveaNicheConstraintSelectionTest {
  private static final List<double[]> VECTORS = List.of(new double[] {1, 0}, new double[] {0, 1});

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(71);
  }

  private static TaggedSolution solution(int id, double f1, double f2, double... constraints) {
    return new TaggedSolution(id, new double[] {f1, f2}, constraints);
  }

  @Nested
  @DisplayName("When selecting with RVEA")
  class WhenSelectingWithRVEA {
    private RVEAEnvironmentalSelection<TaggedSolution> selection;

    @BeforeEach
    void setUp() {
      selection = new RVEAEnvironmentalSelection<>(2, 24, 2, 0.2, VECTORS);
    }

    @Test
    @DisplayName(
        "given a niche with feasible and infeasible candidates, when selecting, then the feasible"
            + " one survives although the infeasible one has a lower APD")
    void givenAMixedNiche_whenSelecting_thenTheFeasibleCandidateSurvives() {
      // Arrange
      var feasibleFirstNiche = solution(0, 2, 0.1, 0);
      var infeasibleFirstNiche = solution(1, 1, 0.05, -1);
      var feasibleSecondNiche = solution(2, 0.1, 2, 0);
      var infeasibleSecondNiche = solution(3, 0.05, 1, -1);
      var candidates =
          List.of(
              feasibleFirstNiche, infeasibleFirstNiche, feasibleSecondNiche, infeasibleSecondNiche);

      // Act
      var survivors = selection.execute(candidates, 2);

      // Assert
      assertThat(survivors).containsExactly(feasibleFirstNiche, feasibleSecondNiche);
    }

    @Test
    @DisplayName(
        "given a niche with only infeasible candidates, when selecting, then the least violating"
            + " one survives although it has a higher APD")
    void givenAnInfeasibleNiche_whenSelecting_thenTheLeastViolatingCandidateSurvives() {
      // Arrange
      var feasible = solution(0, 1, 0, 0);
      var closerButMoreViolating = solution(1, 0, 1, -2);
      var fartherButLessViolating = solution(2, 0, 2, -0.5);

      // Act
      var survivors =
          selection.execute(List.of(feasible, closerButMoreViolating, fartherButLessViolating), 2);

      // Assert
      assertThat(survivors).containsExactly(feasible, fartherButLessViolating);
    }

    @Test
    @DisplayName(
        "given only infeasible candidates, when selecting, then the least violating one of each"
            + " niche survives, not the least violating ones overall")
    void givenOnlyInfeasibleCandidates_whenSelecting_thenOneSurvivesPerNiche() {
      // Arrange
      var leastViolatingFirstNiche = solution(0, 1, 0, -0.1);
      var secondLeastViolatingFirstNiche = solution(1, 2, 0, -0.2);
      var mostViolatingSecondNiche = solution(2, 0, 1, -5);
      var candidates =
          List.of(
              leastViolatingFirstNiche, secondLeastViolatingFirstNiche, mostViolatingSecondNiche);

      // Act
      var survivors = selection.execute(candidates, 2);

      // Assert
      assertThat(survivors).containsExactly(leastViolatingFirstNiche, mostViolatingSecondNiche);
    }

    @Test
    @DisplayName(
        "given as many feasible candidates as vectors in one niche, when selecting, then an"
            + " infeasible candidate keeps the other niche")
    void givenFeasibleCandidatesInOneNiche_whenSelecting_thenAnInfeasibleOneKeepsTheOther() {
      // Arrange
      var feasible = solution(0, 1, 0, 0);
      var otherFeasible = solution(1, 2, 0, 0);
      var infeasible = solution(2, 0, 1, -1);

      // Act
      var survivors = selection.execute(List.of(feasible, otherFeasible, infeasible), 2);

      // Assert
      assertThat(survivors).containsExactly(feasible, infeasible);
    }
  }
}
