package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RveaTrajectoryFixtures.TaggedSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * The constraint handling of RVEA and RVEA*, as PlatEMO's RVEA and RVEAa: each reference vector
 * keeps its feasible solution with the smallest APD or, without feasible solutions, its solution
 * with the smallest overall constraint violation.
 */
@DisplayName("RVEA-family selection with constraints")
class RveaConstraintSelectionTest {
  private static final List<double[]> AXES = List.of(new double[] {1, 0}, new double[] {0, 1});

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(71);
  }

  private static TaggedSolution solution(int id, double x, double y, double... constraints) {
    return new TaggedSolution(id, new double[] {x, y}, constraints);
  }

  private static RVEAEnvironmentalSelection<TaggedSolution> rvea(List<double[]> vectors) {
    return new RVEAEnvironmentalSelection<>(2, 24, 2, 0.2, vectors);
  }

  @Nested
  @DisplayName("When RVEA selects one solution per reference vector")
  class RveaTests {

    @Test
    @DisplayName("given a feasible and an infeasible solution in a vector, then the feasible one stays")
    void givenFeasibleAndInfeasibleInAVector_whenSelecting_thenTheFeasibleOneStays() {
      // Arrange: the infeasible solution is closer to the ideal point, so it has the smaller APD
      var feasible = solution(0, 0, 2, 0);
      var infeasible = solution(1, 0, 1, -1);
      var other = solution(2, 3, 0, 0);

      // Act
      var survivors = rvea(AXES).execute(List.of(feasible, infeasible, other), 2);

      // Assert
      assertEquals(List.of(other, feasible), survivors);
    }

    @Test
    @DisplayName(
        "given only infeasible solutions in a vector, then the least violated stays, whatever"
            + " its APD")
    void givenOnlyInfeasibleInAVector_whenSelecting_thenTheLeastViolatedStays() {
      // Arrange: the violation is the sum of the negative constraint values (-1.5 and -3);
      // positive slack does not cancel a violation
      var leastViolated = solution(0, 0, 2, -1, 2, -0.5);
      var mostViolated = solution(1, 0, 1, -3, 0, 0);
      var other = solution(2, 3, 0, 0, 0, 0);

      // Act
      var survivors = rvea(AXES).execute(List.of(mostViolated, leastViolated, other), 2);

      // Assert
      assertEquals(-1.5, ConstraintHandling.overallConstraintViolationDegree(leastViolated));
      assertEquals(List.of(other, leastViolated), survivors);
    }

    @Test
    @DisplayName("given two equally violated solutions in a vector, then the first one stays")
    void givenEquallyViolatedSolutions_whenSelecting_thenTheFirstOneStays() {
      // Arrange: the second one has the smaller APD, which does not count between infeasible ones
      var first = solution(0, 0, 2, -1);
      var second = solution(1, 0, 1, -1);

      // Act
      var survivors = rvea(AXES).execute(List.of(first, second, solution(2, 3, 0, 0)), 2);

      // Assert
      assertEquals(first, survivors.get(1));
    }

    @Test
    @DisplayName(
        "given an infeasible solution beyond the ideal point, then it takes part in the"
            + " translation")
    void givenAnInfeasibleSolutionBeyondTheIdeal_whenSelecting_thenItTakesPartInTheTranslation() {
      // Arrange: with three vectors, a and b fall in different vectors from the ideal (2, 2), but
      // both in the diagonal one from the ideal (0, 0) of the infeasible solution c
      List<double[]> vectors =
          List.of(new double[] {1, 0}, new double[] {1, 1}, new double[] {0, 1});
      var a = solution(0, 2, 3, 0);
      var b = solution(1, 3, 2, 0);
      var c = solution(2, 0, 0, -1);

      // Act
      var withoutC = rvea(vectors).execute(List.of(a, b), 3);
      var withC = rvea(vectors).execute(List.of(a, b, c), 3);

      // Assert: c is alone in its vector, so it stays; a and b share one, so only a stays
      assertEquals(List.of(b, a), withoutC);
      assertEquals(List.of(c, a), withC);
    }

    @Test
    @DisplayName("given precomputed constraint attributes, then the selection uses them")
    void givenPrecomputedAttributes_whenSelecting_thenTheSelectionUsesThem() {
      // Arrange: the constraint values say infeasible, the precomputed attributes say feasible
      var feasibleByAttributes = solution(0, 0, 2, -20);
      ConstraintHandling.numberOfViolatedConstraints(feasibleByAttributes, 0);
      ConstraintHandling.overallConstraintViolationDegree(feasibleByAttributes, 0.0);
      var infeasible = solution(1, 0, 1, -1);

      // Act
      var survivors =
          rvea(AXES).execute(List.of(infeasible, feasibleByAttributes, solution(2, 3, 0, 0)), 2);

      // Assert
      assertEquals(feasibleByAttributes, survivors.get(1));
    }
  }

  @Nested
  @DisplayName("When RVEA* selects")
  class RveaStarTests {

    @Test
    @DisplayName(
        "given an infeasible solution that dominates a feasible one, then the feasible one stays"
            + " (PlatEMO's RVEAa would filter it out)")
    void givenAnInfeasibleDominatingAFeasible_whenSelecting_thenTheFeasibleOneStays() {
      // Arrange
      var feasible = solution(0, 0, 2, 0);
      var infeasible = solution(1, 0, 1, -1);
      var other = solution(2, 3, 0, 0);
      var selection = new RVEAStarEnvironmentalSelection<TaggedSolution>(2, 24, 2, 0.2, AXES);

      // Act
      var survivors = selection.execute(List.of(feasible, infeasible, other), 2);

      // Assert
      assertEquals(Set.of(feasible, other), Set.copyOf(survivors));
    }

    @Test
    @DisplayName("given a feasible solution dominated by another feasible one, then it is filtered out")
    void givenAFeasibleDominatedByAnotherFeasible_whenSelecting_thenItIsFilteredOut() {
      // Arrange: alone in its vector, the dominated solution would otherwise stay
      var dominated = solution(0, 1, 3, 0);
      var dominating = solution(1, 0, 2, 0);
      var other = solution(2, 3, 0, 0);
      var selection = new RVEAStarEnvironmentalSelection<TaggedSolution>(2, 24, 2, 0.2, AXES);

      // Act
      var survivors = selection.execute(List.of(dominated, dominating, other), 2);

      // Assert
      assertFalse(survivors.contains(dominated));
    }
  }

  @Nested
  @DisplayName("When iRVEA selects")
  class IrveaTests {

    @Test
    @DisplayName("given solutions with constraints, then it rejects them")
    void givenSolutionsWithConstraints_whenSelecting_thenTheyAreRejected() {
      // Arrange
      var selection = new IRVEAEnvironmentalSelection<TaggedSolution>(2, 24, 2, 0.2, AXES, 2);
      var candidates = List.of(solution(0, 0, 2, 0), solution(1, 3, 0, 0));

      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> selection.execute(candidates, 2));
    }
  }
}
