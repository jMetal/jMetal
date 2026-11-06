package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class CCMOEnvironmentalSelection")
class CCMOEnvironmentalSelectionTest {
  private static final double EPSILON = 1e-12;

  private FakeDoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(2, 2, 1);
  }

  private DoubleSolution solution(double f1, double f2, double constraint) {
    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = constraint;
    return solution;
  }

  private DoubleSolution solution(double f1, double f2) {
    return solution(f1, f2, 0.0);
  }

  @Nested
  @DisplayName("When selecting solutions")
  class WhenSelecting {
    private DoubleSolution a;
    private DoubleSolution b;
    private DoubleSolution c;
    private DoubleSolution d;
    private DoubleSolution e;

    @BeforeEach
    void setUp() {
      // a, b and c are non-dominated with the same fitness, b dominates d, all dominate e
      a = solution(1, 4);
      b = solution(2, 2);
      c = solution(4, 1);
      d = solution(3, 3);
      e = solution(5, 5);
    }

    @Test
    @DisplayName(
        "given fewer non-dominated solutions than the population size, when selecting, then the"
            + " population is filled by fitness and ties keep the order of the list")
    void givenFewerNonDominatedSolutions_whenSelecting_thenThePopulationIsFilledByFitness() {
      // Arrange
      List<DoubleSolution> solutions = List.of(c, a, d, b, e);

      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 4, true);

      // Assert
      assertEquals(List.of(c, a, b, d), result.population());
    }

    @Test
    @DisplayName(
        "given as many non-dominated solutions as the population size, when selecting, then they"
            + " are selected")
    void givenAsManyNonDominatedSolutions_whenSelecting_thenTheyAreSelected() {
      // Arrange
      List<DoubleSolution> solutions = List.of(e, d, a, b, c);

      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 3, true);

      // Assert
      assertEquals(List.of(a, b, c), result.population());
    }

    @Test
    @DisplayName(
        "given a selection, when selecting, then the fitness is the one computed on the whole list")
    void givenASelection_whenSelecting_thenTheFitnessIsTheOneComputedOnTheWholeList() {
      // Arrange
      List<DoubleSolution> solutions = List.of(a, b, c, d, e);
      double[] expected = CCMOFitness.compute(solutions, true);

      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 4, true);

      // Assert
      assertArrayEquals(
          new double[] {expected[0], expected[1], expected[2], expected[3]},
          result.fitness(),
          EPSILON);
    }

    @Test
    @DisplayName(
        "given more non-dominated solutions than the population size, when selecting, then the"
            + " truncation removes the most crowded ones")
    void givenMoreNonDominatedSolutions_whenSelecting_thenTheTruncationRemovesTheMostCrowded() {
      // Arrange
      DoubleSolution p0 = solution(0, 4);
      DoubleSolution p1 = solution(1, 3);
      DoubleSolution p2 = solution(1.5, 2.5);
      DoubleSolution p3 = solution(4, 0);
      List<DoubleSolution> solutions = List.of(p0, p1, p2, p3, solution(5, 5));

      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 3, true);

      // Assert
      assertEquals(3, result.population().size());
      assertEquals(List.of(p0, p2, p3), sortedByFirstObjective(result.population()));
    }
  }

  @Nested
  @DisplayName("When the solutions have constraints")
  class WhenThereAreConstraints {
    private DoubleSolution infeasible;
    private DoubleSolution first;
    private DoubleSolution second;
    private DoubleSolution third;
    private List<DoubleSolution> solutions;

    @BeforeEach
    void setUp() {
      // The infeasible solution dominates the others in the Pareto sense
      infeasible = solution(1, 1, -1.0);
      first = solution(2, 3);
      second = solution(3, 2);
      third = solution(4, 4);
      solutions = List.of(infeasible, first, second, third);
    }

    @Test
    @DisplayName(
        "given an infeasible solution, when selecting for the original problem, then the feasible"
            + " non-dominated solutions are selected")
    void givenAnInfeasibleSolution_whenSelectingForTheOriginalProblem_thenItIsDiscarded() {
      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 2, true);

      // Assert
      assertEquals(List.of(first, second), result.population());
    }

    @Test
    @DisplayName(
        "given an infeasible solution, when selecting for the helper problem, then the constraints"
            + " are ignored")
    void givenAnInfeasibleSolution_whenSelectingForTheHelperProblem_thenItIsSelected() {
      // Act
      var result = CCMOEnvironmentalSelection.execute(solutions, 2, false);

      // Assert
      assertEquals(List.of(infeasible, first), result.population());
    }
  }

  @Nested
  @DisplayName("When truncating")
  class WhenTruncating {
    // Sorted distances: p0 (1.41, 2.12, 5.66), p1 (0.71, 1.41, 4.24), p2 (0.71, 2.12, 3.54),
    // p3 (3.54, 4.24, 5.66)
    private final double[][] objectives = {{0, 4}, {1, 3}, {1.5, 2.5}, {4, 0}};

    @Test
    @DisplayName(
        "given one vector to remove, when truncating, then the one with the lexicographically"
            + " smallest sorted distances is removed")
    void givenOneVectorToRemove_whenTruncating_thenTheLexicographicallySmallestIsRemoved() {
      // Act
      boolean[] deleted = CCMOEnvironmentalSelection.truncation(objectives, 1);

      // Assert
      assertArrayEquals(new boolean[] {false, true, false, false}, deleted);
    }

    @Test
    @DisplayName(
        "given two vectors to remove, when truncating, then the distances are updated after each"
            + " removal")
    void givenTwoVectorsToRemove_whenTruncating_thenTheDistancesAreUpdatedAfterEachRemoval() {
      // Act
      boolean[] deleted = CCMOEnvironmentalSelection.truncation(objectives, 2);

      // Assert
      assertArrayEquals(new boolean[] {false, true, true, false}, deleted);
    }

    @Test
    @DisplayName("given no vector to remove, when truncating, then none is removed")
    void givenNoVectorToRemove_whenTruncating_thenNoneIsRemoved() {
      // Act
      boolean[] deleted = CCMOEnvironmentalSelection.truncation(objectives, 0);

      // Assert
      assertArrayEquals(new boolean[4], deleted);
    }

    @Test
    @DisplayName(
        "given two vectors with the same sorted distances, when truncating, then the first one is"
            + " removed")
    void givenATie_whenTruncating_thenTheFirstOneIsRemoved() {
      // Arrange: the two inner vectors have the sorted distances (sqrt 2, sqrt 2, sqrt 8)
      double[][] symmetric = {{0, 3}, {1, 2}, {2, 1}, {3, 0}};

      // Act
      boolean[] deleted = CCMOEnvironmentalSelection.truncation(symmetric, 1);

      // Assert
      assertArrayEquals(new boolean[] {false, true, false, false}, deleted);
    }

    @Test
    @DisplayName(
        "given duplicated vectors, when truncating, then the duplicates are removed first")
    void givenDuplicatedVectors_whenTruncating_thenTheDuplicatesAreRemovedFirst() {
      // Arrange
      double[][] duplicated = {{0, 2}, {1, 1}, {2, 0}, {1, 1}};

      // Act
      boolean[] deleted = CCMOEnvironmentalSelection.truncation(duplicated, 1);

      // Assert
      assertArrayEquals(new boolean[] {false, true, false, false}, deleted);
    }
  }

  private static List<DoubleSolution> sortedByFirstObjective(List<DoubleSolution> solutions) {
    return solutions.stream()
        .sorted((s1, s2) -> Double.compare(s1.objectives()[0], s2.objectives()[0]))
        .toList();
  }
}
