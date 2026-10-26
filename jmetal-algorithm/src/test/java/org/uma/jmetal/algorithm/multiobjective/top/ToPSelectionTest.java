package org.uma.jmetal.algorithm.multiobjective.top;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;

@DisplayName("Unit tests for class ToPSelection")
class ToPSelectionTest {
  private static final double EPSILON = 1e-12;
  private static final double INF = Double.POSITIVE_INFINITY;

  /** A feasible solution with the given objectives. */
  private static DoubleSolution feasible(double... objectives) {
    return solution(0.0, objectives);
  }

  /** A solution with the given objectives and overall constraint violation. */
  private static DoubleSolution solution(double violation, double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 1);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    solution.constraints()[0] = -violation;
    return solution;
  }

  @Nested
  @DisplayName("When comparing by constrained dominance")
  class WhenComparingByConstrainedDominance {
    @Test
    @DisplayName("given two feasible solutions, when comparing, then Pareto dominance decides")
    void givenTwoFeasibleSolutions_whenComparing_thenParetoDominanceDecides() {
      // Arrange
      DoubleSolution a = feasible(1.0, 2.0);
      DoubleSolution b = feasible(2.0, 3.0);
      DoubleSolution c = feasible(0.5, 4.0);

      // Act and Assert
      assertEquals(-1, ToPSelection.constrainedDominance(a, b));
      assertEquals(1, ToPSelection.constrainedDominance(b, a));
      assertEquals(0, ToPSelection.constrainedDominance(a, c));
    }

    @Test
    @DisplayName("given a feasible and an infeasible solution, then the feasible one dominates")
    void givenAFeasibleAndAnInfeasibleSolution_whenComparing_thenTheFeasibleOneDominates() {
      // Arrange
      DoubleSolution feasible = feasible(5.0, 5.0);
      DoubleSolution infeasible = solution(0.1, 0.0, 0.0);

      // Act and Assert
      assertEquals(-1, ToPSelection.constrainedDominance(feasible, infeasible));
      assertEquals(1, ToPSelection.constrainedDominance(infeasible, feasible));
    }

    @Test
    @DisplayName("given two infeasible solutions, then the smaller violation dominates")
    void givenTwoInfeasibleSolutions_whenComparing_thenTheSmallerViolationDominates() {
      // Arrange
      DoubleSolution smaller = solution(0.1, 5.0, 5.0);
      DoubleSolution larger = solution(0.3, 0.0, 0.0);

      // Act and Assert
      assertEquals(-1, ToPSelection.constrainedDominance(smaller, larger));
      assertEquals(1, ToPSelection.constrainedDominance(larger, smaller));
    }

    @Test
    @DisplayName("given two infeasible solutions with the same violation, then none dominates")
    void givenTwoInfeasibleSolutionsWithTheSameViolation_whenComparing_thenNoneDominates() {
      // Arrange: unlike DominanceWithConstraintsComparator, the objectives are not compared
      DoubleSolution a = solution(0.5, 0.0, 0.0);
      DoubleSolution b = solution(0.5, 1.0, 1.0);

      // Act and Assert
      assertEquals(0, ToPSelection.constrainedDominance(a, b));
      assertEquals(0, ToPSelection.constrainedDominance(b, a));
    }
  }

  @Nested
  @DisplayName("When sorting into fronts")
  class WhenSortingIntoFronts {
    @Test
    @DisplayName("given feasible and infeasible solutions, when sorting, then CDP gives the fronts")
    void givenFeasibleAndInfeasibleSolutions_whenSorting_thenTheFrontsFollowTheCdp() {
      // Arrange
      List<DoubleSolution> solutions =
          List.of(
              feasible(1.0, 4.0),
              feasible(2.0, 2.0),
              feasible(3.0, 3.0),
              solution(0.5, 0.0, 0.0),
              solution(0.5, 5.0, 5.0),
              solution(1.0, 0.0, 0.0));

      // Act
      int[] frontNumbers = ToPSelection.frontNumbers(solutions);

      // Assert
      assertArrayEquals(new int[] {0, 0, 1, 2, 2, 3}, frontNumbers);
    }
  }

  @Nested
  @DisplayName("When computing crowding distances")
  class WhenComputingCrowdingDistances {
    @Test
    @DisplayName("given a front, when computing, then extremes are infinite and inner ones add up")
    void givenAFront_whenComputing_thenTheExtremesAreInfiniteAndTheInnerOnesAddUp() {
      // Arrange
      List<DoubleSolution> front =
          List.of(feasible(0.0, 5.0), feasible(1.0, 3.0), feasible(3.0, 1.0), feasible(4.0, 0.0));

      // Act
      double[] distances = ToPSelection.crowdingDistances(front, new int[] {0, 0, 0, 0});

      // Assert: (3 - 0) / 4 + (5 - 1) / 5 and (4 - 1) / 4 + (3 - 0) / 5
      assertArrayEquals(new double[] {INF, 1.55, 1.35, INF}, distances, EPSILON);
    }

    @Test
    @DisplayName("given two fronts, when computing, then each front is computed on its own")
    void givenTwoFronts_whenComputing_thenEachFrontIsComputedOnItsOwn() {
      // Arrange
      List<DoubleSolution> solutions =
          List.of(
              feasible(0.0, 2.0),
              feasible(1.0, 3.0),
              feasible(1.0, 1.0),
              feasible(2.0, 2.0),
              feasible(2.0, 0.0),
              feasible(3.0, 1.0));

      // Act
      double[] distances =
          ToPSelection.crowdingDistances(solutions, new int[] {0, 1, 0, 1, 0, 1});

      // Assert: the inner solution of each front gets (2 - 0) / 2 + (2 - 0) / 2
      assertArrayEquals(new double[] {INF, INF, 2.0, 2.0, INF, INF}, distances, EPSILON);
    }

    @Test
    @DisplayName("given fronts of one and two solutions, when computing, then all are infinite")
    void givenFrontsOfOneAndTwoSolutions_whenComputing_thenAllAreInfinite() {
      // Arrange
      List<DoubleSolution> solutions =
          List.of(feasible(0.0, 1.0), feasible(1.0, 2.0), feasible(2.0, 1.0));

      // Act
      double[] distances = ToPSelection.crowdingDistances(solutions, new int[] {0, 1, 1});

      // Assert
      assertArrayEquals(new double[] {INF, INF, INF}, distances);
    }

    @Test
    @DisplayName("given ties in an objective, when computing, then they keep the order of the list")
    void givenTiesInAnObjective_whenComputing_thenTheyKeepTheOrderOfTheList() {
      // Arrange: solutions 0 and 2 have the same first objective; solution 0 goes first
      List<DoubleSolution> front =
          List.of(
              solution(1.0, 1.0, 2.0),
              solution(1.0, 0.0, 5.0),
              solution(1.0, 1.0, 1.0),
              solution(1.0, 4.0, 0.0),
              solution(1.0, 3.0, 4.0));

      // Act
      double[] distances = ToPSelection.crowdingDistances(front, new int[] {0, 0, 0, 0, 0});

      // Assert: 1/4 + 3/5, 2/4 + 2/5 and 3/4 + 3/5
      assertArrayEquals(new double[] {0.85, INF, 0.9, INF, 1.35}, distances, EPSILON);
    }

    @Test
    @DisplayName("given an objective with a single value, when computing, then inner ones are NaN")
    void givenAnObjectiveWithASingleValue_whenComputing_thenTheInnerDistancesAreNaN() {
      // Arrange: as in PlatEMO, the range of the first objective is zero
      List<DoubleSolution> front =
          List.of(solution(1.0, 1.0, 0.0), solution(1.0, 1.0, 1.0), solution(1.0, 1.0, 2.0));

      // Act
      double[] distances = ToPSelection.crowdingDistances(front, new int[] {0, 0, 0});

      // Assert
      assertArrayEquals(new double[] {INF, Double.NaN, INF}, distances);
    }
  }

  @Nested
  @DisplayName("When selecting the next population")
  class WhenSelectingTheNextPopulation {
    private final DoubleSolution a = feasible(0.0, 4.0);
    private final DoubleSolution b = feasible(2.0, 2.0);
    private final DoubleSolution c = feasible(4.0, 0.0);
    private final DoubleSolution d = feasible(1.0, 5.0);
    private final DoubleSolution e = feasible(3.0, 3.0);
    private final DoubleSolution f = feasible(5.0, 1.0);

    @Test
    @DisplayName("given two fronts, when selecting, then the most crowded of the last one are left")
    void givenTwoFronts_whenSelecting_thenTheMostCrowdedOfTheLastFrontAreLeftOut() {
      // Arrange: a, b and c are the first front; e is the inner solution of the second one

      // Act
      List<DoubleSolution> selected = ToPSelection.select(List.of(d, a, e, b, f, c), 5);

      // Assert: the selected solutions keep the order of the list
      assertEquals(List.of(d, a, b, f, c), selected);
    }

    @Test
    @DisplayName("given equal distances in the last front, when selecting, then the first one wins")
    void givenEqualDistancesInTheLastFront_whenSelecting_thenTheFirstOneIsSelected() {
      // Act: d and f have infinite distances
      List<DoubleSolution> selected = ToPSelection.select(List.of(d, a, e, b, f, c), 4);

      // Assert
      assertEquals(List.of(d, a, b, c), selected);
    }

    @Test
    @DisplayName("given a NaN distance in the last front, when selecting, then it goes first")
    void givenANaNDistanceInTheLastFront_whenSelecting_thenItIsSelectedFirst() {
      // Arrange: the second front has distances {inf, NaN, inf}
      DoubleSolution first = feasible(0.0, 0.0);
      DoubleSolution extreme1 = solution(1.0, 1.0, 0.0);
      DoubleSolution inner = solution(1.0, 1.0, 1.0);
      DoubleSolution extreme2 = solution(1.0, 1.0, 2.0);

      // Act
      List<DoubleSolution> selected =
          ToPSelection.select(List.of(first, extreme1, inner, extreme2), 2);

      // Assert: MATLAB's descending sort puts NaN first
      assertEquals(List.of(first, inner), selected);
    }

    @Test
    @DisplayName("given as many solutions as the size, when selecting, then all are selected")
    void givenAsManySolutionsAsTheSize_whenSelecting_thenAllAreSelectedInOrder() {
      // Act
      List<DoubleSolution> selected = ToPSelection.select(List.of(e, a, f), 3);

      // Assert
      assertEquals(List.of(e, a, f), selected);
    }

    @Test
    @DisplayName("given fewer solutions than the size, when selecting, then an exception is thrown")
    void givenFewerSolutionsThanTheSize_whenSelecting_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(ValueOutOfRangeException.class, () -> ToPSelection.select(List.of(a, b), 3));
    }
  }

  @Nested
  @DisplayName("When running a binary tournament")
  class WhenRunningABinaryTournament {
    private final int[] frontNumbers = {0, 1, 0, 0, 0};
    private final double[] crowdingDistances = {0.5, INF, 1.5, Double.NaN, 1.5};

    @Test
    @DisplayName("given two fronts, when competing, then the lower front number wins")
    void givenTwoFronts_whenCompeting_thenTheLowerFrontNumberWins() {
      // Act and Assert: solution 1 has an infinite distance, but a worse front
      assertEquals(0, ToPSelection.binaryTournament(0, 1, frontNumbers, crowdingDistances));
      assertEquals(0, ToPSelection.binaryTournament(1, 0, frontNumbers, crowdingDistances));
    }

    @Test
    @DisplayName("given the same front, when competing, then the larger crowding distance wins")
    void givenTheSameFront_whenCompeting_thenTheLargerCrowdingDistanceWins() {
      // Act and Assert
      assertEquals(2, ToPSelection.binaryTournament(0, 2, frontNumbers, crowdingDistances));
      assertEquals(2, ToPSelection.binaryTournament(2, 0, frontNumbers, crowdingDistances));
    }

    @Test
    @DisplayName("given a NaN crowding distance, when competing, then it loses")
    void givenANaNCrowdingDistance_whenCompeting_thenItLoses() {
      // Act and Assert
      assertEquals(0, ToPSelection.binaryTournament(3, 0, frontNumbers, crowdingDistances));
      assertEquals(0, ToPSelection.binaryTournament(0, 3, frontNumbers, crowdingDistances));
    }

    @Test
    @DisplayName("given a tie, when competing, then the lower index wins")
    void givenATie_whenCompeting_thenTheLowerIndexWins() {
      // Act and Assert
      assertEquals(2, ToPSelection.binaryTournament(4, 2, frontNumbers, crowdingDistances));
      assertEquals(2, ToPSelection.binaryTournament(2, 4, frontNumbers, crowdingDistances));
      assertEquals(4, ToPSelection.binaryTournament(4, 4, frontNumbers, crowdingDistances));
    }
  }
}
