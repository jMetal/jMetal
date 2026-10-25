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
@DisplayName("Unit tests for class DiversityArchiveUpdate")
class DiversityArchiveUpdateTest {
  private static final double[][] WEIGHTS = {{1e-6, 1.0}, {0.5, 0.5}, {1.0, 1e-6}};

  private DiversityArchiveUpdate<DoubleSolution> update;

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
    update = new DiversityArchiveUpdate<>(WEIGHTS);
  }

  @Nested
  @DisplayName("When filling the subregions")
  class WhenFillingTheSubregions {

    @Test
    @DisplayName("given a crowded subregion of the CA, when updating, then the others are filled")
    void givenACrowdedSubregionOfTheCA_whenUpdating_thenTheOtherSubregionsAreFilled() {
      // Arrange: the CA has three solutions in the second subregion; p1 dominates p2 in the
      // first subregion, and the constraints are ignored (p1 is infeasible)
      List<DoubleSolution> convergenceArchive =
          List.of(
              solution(0.0, 0.5, 0.5), solution(0.0, 0.4, 0.4), solution(0.0, 0.6, 0.6));
      DoubleSolution p2 = solution(0.0, 0.1, 2.0);
      DoubleSolution r = solution(0.0, 0.45, 0.45);
      DoubleSolution q = solution(0.0, 1.0, 0.0);
      DoubleSolution p1 = solution(-5.0, 0.0, 1.0);

      // Act
      List<DoubleSolution> result =
          update.update(convergenceArchive, List.of(), List.of(p2, r, q, p1));

      // Assert: iteration 1 takes p1 and q; iteration 2 takes p2
      assertSameSolutions(List.of(p1, q, p2), result);
    }

    @Test
    @DisplayName("given non-dominated candidates, when updating, then the best Tchebycheff wins")
    void givenNonDominatedCandidates_whenUpdating_thenTheBestTchebycheffValueWins() {
      // Arrange: b and a are non-dominated in the first subregion; with the ideal point of both,
      // (0, 0.9), the Tchebycheff value of a is 0.1 and the one of b is 5e4
      List<DoubleSolution> convergenceArchive = List.of(solution(0.0, 0.5, 0.5));
      DoubleSolution b = solution(0.0, 0.05, 0.9);
      DoubleSolution a = solution(0.0, 0.0, 1.0);
      DoubleSolution q = solution(0.0, 1.0, 0.0);

      // Act
      List<DoubleSolution> result = update.update(convergenceArchive, List.of(b), List.of(a, q));

      // Assert
      assertSameSolutions(List.of(a, q, b), result);
    }

    @Test
    @DisplayName("given a CA in every subregion, when updating, then a second round is needed")
    void givenACAInEverySubregion_whenUpdating_thenTheIterationsGoOn() {
      // Arrange: the CA has one solution per subregion, so iteration 1 takes nothing; the
      // candidates are in the second subregion, and g2 dominates g1, which dominates g3
      List<DoubleSolution> convergenceArchive =
          List.of(
              solution(0.0, 0.0, 1.0), solution(0.0, 0.5, 0.5), solution(0.0, 1.0, 0.0));
      DoubleSolution g1 = solution(0.0, 0.3, 0.3);
      DoubleSolution g2 = solution(0.0, 0.2, 0.2);
      DoubleSolution g3 = solution(0.0, 0.4, 0.4);

      // Act
      List<DoubleSolution> result =
          update.update(convergenceArchive, List.of(g1), List.of(g2, g3));

      // Assert: iteration 2 takes g2, and iteration 3 takes g1 and g3
      assertSameSolutions(List.of(g2, g1, g3), result);
    }

    @Test
    @DisplayName("given fewer candidates than weight vectors, when updating, then all are taken")
    void givenFewerCandidatesThanWeightVectors_whenUpdating_thenAllAreTaken() {
      // Arrange
      DoubleSolution q = solution(0.0, 1.0, 0.0);

      // Act
      List<DoubleSolution> result = update.update(List.of(), List.of(), List.of(q));

      // Assert
      assertSameSolutions(List.of(q), result);
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
          InvalidConditionException.class, () -> new DiversityArchiveUpdate<>(new double[0][]));
    }

    @Test
    @DisplayName("given a null archive, when updating, then an exception is thrown")
    void givenANullArchive_whenUpdating_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(NullParameterException.class, () -> update.update(List.of(), null, List.of()));
    }
  }
}
