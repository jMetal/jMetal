package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * The solutions of these tests have no variables, so all of them are equal according to {@code
 * equals}: the expected archives are checked by identity.
 */
@DisplayName("Unit tests for class ConvergenceArchiveUpdate")
class ConvergenceArchiveUpdateTest {
  private static final double[][] WEIGHTS = {{1e-6, 1.0}, {0.5, 0.5}, {1.0, 1e-6}};

  private ConvergenceArchiveUpdate<DoubleSolution> update;

  /** Creates a solution; a negative constraint value is a violation. */
  private static DoubleSolution solution(double constraint, double... objectives) {
    DoubleSolution solution = new DefaultDoubleSolution(new ArrayList<>(), objectives.length, 1);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    solution.constraints()[0] = constraint;
    return solution;
  }

  private static void assertSameSolutions(
      List<DoubleSolution> expected, List<DoubleSolution> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertSame(expected.get(i), actual.get(i), "Solution " + i);
    }
  }

  @BeforeEach
  void setUp() {
    update = new ConvergenceArchiveUpdate<>(WEIGHTS);
  }

  @Nested
  @DisplayName("When there are as many feasible candidates as weight vectors")
  class WhenThereAreAsManyFeasibleCandidatesAsWeightVectors {

    @Test
    @DisplayName("given N feasible candidates, when updating, then they are the new archive")
    void givenNFeasibleCandidates_whenUpdating_thenTheyAreTheNewArchive() {
      // Arrange
      DoubleSolution f1 = solution(0.0, 0.0, 1.0);
      DoubleSolution i1 = solution(-1.0, 0.5, 0.5);
      DoubleSolution f2 = solution(0.0, 3.0, 3.0);
      DoubleSolution f3 = solution(2.0, 1.0, 0.0);
      DoubleSolution i2 = solution(-0.1, 0.0, 0.0);

      // Act
      List<DoubleSolution> result = update.update(List.of(f1, i1), List.of(f2, f3, i2));

      // Assert: the dominated feasible solution is kept
      assertSameSolutions(List.of(f1, f2, f3), result);
    }
  }

  @Nested
  @DisplayName("When there are more feasible candidates than weight vectors")
  class WhenThereAreMoreFeasibleCandidatesThanWeightVectors {

    @Test
    @DisplayName("given a crowded subregion, when updating, then its worst solution is removed")
    void givenACrowdedSubregion_whenUpdating_thenItsWorstSolutionIsRemoved() {
      // Arrange: a and b are in the first subregion; b has the largest Tchebycheff value with
      // the ideal point of {a, b}, (0, 0.9); e is dominated and is not considered
      DoubleSolution a = solution(0.0, 0.0, 1.0);
      DoubleSolution b = solution(0.0, 0.1, 0.9);
      DoubleSolution c = solution(0.0, 0.5, 0.5);
      DoubleSolution d = solution(0.0, 1.0, 0.0);
      DoubleSolution e = solution(0.0, 0.6, 0.6);

      // Act
      List<DoubleSolution> result = update.update(List.of(a, b), List.of(c, d, e));

      // Assert
      assertSameSolutions(List.of(a, c, d), result);
    }

    @Test
    @DisplayName("given equal objective vectors, when updating, then zero distances are ignored")
    void givenEqualObjectiveVectors_whenUpdating_thenTheirZeroDistanceIsIgnored() {
      // Arrange: a and aCopy are at distance zero, so b, the closest one to them, is removed
      // first; then only a and aCopy share the crowded subregion, all their distances are
      // ignored, and the first one is removed
      DoubleSolution a = solution(0.0, 0.0, 1.0);
      DoubleSolution aCopy = solution(0.0, 0.0, 1.0);
      DoubleSolution b = solution(0.0, 0.05, 0.95);
      DoubleSolution c = solution(0.0, 0.5, 0.5);
      DoubleSolution d = solution(0.0, 1.0, 0.0);

      // Act
      List<DoubleSolution> result = update.update(List.of(), List.of(a, aCopy, b, c, d));

      // Assert
      assertSameSolutions(List.of(aCopy, c, d), result);
    }

    @Test
    @DisplayName("given the closest pair, when choosing the worst, then Tchebycheff decides")
    void givenTheClosestPair_whenChoosingTheWorst_thenTheTchebycheffValueDecides() {
      // Arrange: q and r are in the second subregion, the most crowded one, and r is the farthest
      // from the ideal point of the pair, (0.48, 0.5)
      DoubleSolution p = solution(0.0, 0.0, 1.0);
      DoubleSolution q = solution(0.0, 0.48, 0.5);
      DoubleSolution r = solution(0.0, 0.5, 0.52);
      DoubleSolution s = solution(0.0, 1.0, 0.0);

      // Act
      DoubleSolution worst = update.worstOfTheMostCrowdedSubregion(List.of(p, q, r, s));

      // Assert
      assertSame(r, worst);
    }
  }

  @Nested
  @DisplayName("When there are fewer feasible candidates than weight vectors")
  class WhenThereAreFewerFeasibleCandidatesThanWeightVectors {

    @Test
    @DisplayName("given infeasible candidates, when updating, then the last front loses the max CV")
    void givenInfeasibleCandidates_whenUpdating_thenTheLargestViolationOfTheLastFrontIsRemoved() {
      // Arrange: with the ideal point (0, 0), the values (CV, Tchebycheff) are p (1, 1),
      // q (0.5, 1), r (3, 1), t (0.2, 1.2) and u (0.1, 1.5); the first front is {q, t, u}, and
      // q, its solution with the largest CV, is removed
      DoubleSolution f = solution(0.0, 0.2, 0.2);
      DoubleSolution p = solution(-1.0, 0.0, 1.0);
      DoubleSolution q = solution(-0.5, 1.0, 0.0);
      DoubleSolution r = solution(-3.0, 0.5, 0.5);
      DoubleSolution t = solution(-0.2, 0.6, 0.6);
      DoubleSolution u = solution(-0.1, 0.75, 0.75);

      // Act
      List<DoubleSolution> result = update.update(List.of(f, p), List.of(q, r, t, u));

      // Assert
      assertSameSolutions(List.of(f, t, u), result);
    }

    @Test
    @DisplayName("given a tie in the CV, when trimming the last front, then the first one goes")
    void givenATieInTheViolation_whenTrimmingTheLastFront_thenTheFirstOneIsRemoved() {
      // Arrange: x, y and w have the same values (CV, Tchebycheff) = (1, 1)
      DoubleSolution f = solution(0.0, 0.2, 0.2);
      DoubleSolution x = solution(-1.0, 0.0, 1.0);
      DoubleSolution y = solution(-1.0, 1.0, 0.0);
      DoubleSolution w = solution(-1.0, 0.5, 0.5);

      // Act
      List<DoubleSolution> result = update.update(List.of(f), List.of(x, y, w));

      // Assert
      assertSameSolutions(List.of(f, y, w), result);
    }

    @Test
    @DisplayName("given fewer candidates than weight vectors, when updating, then all are kept")
    void givenFewerCandidatesThanWeightVectors_whenUpdating_thenAllAreKept() {
      // Arrange
      DoubleSolution x = solution(-2.0, 0.0, 1.0);
      DoubleSolution y = solution(-1.0, 1.0, 0.0);

      // Act
      List<DoubleSolution> result = update.update(List.of(), List.of(x, y));

      // Assert: y has a smaller CV and the same Tchebycheff value, so it is in the first front
      assertSameSolutions(List.of(y, x), result);
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {

    @Test
    @DisplayName("given no weight vectors, when creating the update, then an exception is thrown")
    void givenNoWeightVectors_whenCreatingTheUpdate_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(
          InvalidConditionException.class, () -> new ConvergenceArchiveUpdate<>(new double[0][]));
      assertThrows(NullParameterException.class, () -> new ConvergenceArchiveUpdate<>(null));
    }

    @Test
    @DisplayName("given a null archive, when updating, then an exception is thrown")
    void givenANullArchive_whenUpdating_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(NullParameterException.class, () -> update.update(null, List.of()));
    }
  }
}
