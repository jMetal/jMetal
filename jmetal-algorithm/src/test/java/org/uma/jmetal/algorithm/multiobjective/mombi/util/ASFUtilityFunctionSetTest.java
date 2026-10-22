package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;

@DisplayName("Unit tests for class ASFUtilityFunctionSet of MOMBI")
class ASFUtilityFunctionSetTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] WEIGHTS = {{1.0, 0.0}, {0.5, 0.5}};

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Nested
  @DisplayName("When evaluating a solution without a normalizer")
  class WhenEvaluatingWithoutANormalizer {
    private final ASFUtilityFunctionSet<DoubleSolution> functions =
        new ASFUtilityFunctionSet<>(WEIGHTS);

    @Test
    @DisplayName("given a solution, then the value is the largest weighted distance to the origin")
    void givenASolution_whenEvaluating_thenTheValueIsTheLargestWeightedDistance() {
      // Arrange
      DoubleSolution solution = solution(0.4, 0.8);

      // Act & Assert: a weight of zero is replaced by 0.01
      assertEquals(80.0, functions.evaluate(solution, 0), EPSILON);
      assertEquals(1.6, functions.evaluate(solution, 1), EPSILON);
    }

    @Test
    @DisplayName("given a reference point, then the distances are measured to it")
    void givenAReferencePoint_whenEvaluating_thenTheDistancesAreMeasuredToIt() {
      // Arrange
      var withReference = new ASFUtilityFunctionSet<DoubleSolution>(WEIGHTS, List.of(0.4, 0.8));

      // Act & Assert
      assertEquals(0.0, withReference.evaluate(solution(0.4, 0.8), 1), EPSILON);
      assertEquals(1.0, withReference.evaluate(solution(0.9, 0.8), 1), EPSILON);
      assertEquals(0.8, withReference.evaluate(solution(0.0, 0.8), 1), EPSILON);
    }

    @Test
    @DisplayName("given a vector that does not exist, then it is rejected")
    void givenAVectorThatDoesNotExist_whenEvaluating_thenItIsRejected() {
      // Act & Assert
      assertThrows(JMetalException.class, () -> functions.evaluate(solution(0.1, 0.1), -1));
      assertThrows(JMetalException.class, () -> functions.evaluate(solution(0.1, 0.1), 2));
    }
  }

  @Nested
  @DisplayName("When evaluating a solution with a normalizer")
  class WhenEvaluatingWithANormalizer {
    @Test
    @DisplayName("given a normalizer, then the objectives are normalized before the distance")
    void givenANormalizer_whenEvaluating_thenTheObjectivesAreNormalizedFirst() {
      // Arrange: the objectives (0.4, 0.8) are (0.2, 0.2) in the ranges [0, 2] and [0, 4]
      var functions = new ASFUtilityFunctionSet<DoubleSolution>(WEIGHTS);
      functions.setNormalizer(new Normalizer(List.of(0.0, 0.0), List.of(2.0, 4.0)));

      // Act & Assert
      assertEquals(20.0, functions.evaluate(solution(0.4, 0.8), 0), EPSILON);
      assertEquals(0.4, functions.evaluate(solution(0.4, 0.8), 1), EPSILON);
    }

    @Test
    @DisplayName("given a normalizer with negative ranges, then their lower bound is the origin")
    void givenNegativeRanges_whenEvaluating_thenTheLowerBoundIsTheOrigin() {
      // Arrange
      var functions = new ASFUtilityFunctionSet<DoubleSolution>(WEIGHTS);
      functions.setNormalizer(new Normalizer(List.of(-10.0, -4.0), List.of(10.0, 4.0)));

      // Act & Assert: (-10, -4) is (0, 0), and (0, 0) is (0.5, 0.5)
      assertEquals(0.0, functions.evaluate(solution(-10.0, -4.0), 1), EPSILON);
      assertEquals(1.0, functions.evaluate(solution(0.0, 0.0), 1), EPSILON);
    }

    @Test
    @DisplayName("given a normalizer with equal bounds, then the value is not finite")
    void givenEqualBounds_whenEvaluating_thenTheValueIsNotFinite() {
      // Arrange
      var functions = new ASFUtilityFunctionSet<DoubleSolution>(WEIGHTS);
      functions.setNormalizer(new Normalizer(List.of(1.0, 0.0), List.of(1.0, 4.0)));

      // Act & Assert
      assertFalse(Double.isFinite(functions.evaluate(solution(2.0, 1.0), 0)));
    }
  }
}
