package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("Unit tests for class R2RankingNormalized of MOMBI")
class R2RankingNormalizedTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] WEIGHTS = {{1.0, 0.0}, {0.0, 1.0}};

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  /** The solutions A = (0, 1), B = (1, 0) and C = (0.6, 0.6), with the objectives scaled. */
  private static List<DoubleSolution> population(double scale1, double scale2) {
    List<DoubleSolution> population = new ArrayList<>();
    population.add(solution(0.0 * scale1, 1.0 * scale2));
    population.add(solution(1.0 * scale1, 0.0 * scale2));
    population.add(solution(0.6 * scale1, 0.6 * scale2));
    return population;
  }

  /** As MOMBI-II, the normalizer is given to the utility functions and to the ranking. */
  private static R2RankingNormalized<DoubleSolution> ranking(Normalizer normalizer) {
    var functions = new ASFUtilityFunctionSet<DoubleSolution>(WEIGHTS);
    functions.setNormalizer(normalizer);
    return new R2RankingNormalized<>(functions, normalizer);
  }

  private static double utility(DoubleSolution solution) {
    return new R2RankingAttribute<DoubleSolution>().getAttribute(solution).utility;
  }

  @Nested
  @DisplayName("When ranking solutions with the objectives on the same scale")
  class WhenRankingWithTheSameScale {
    @Test
    @DisplayName("given extreme and central solutions, then the extremes are the first rank")
    void givenExtremeAndCentralSolutions_whenRanking_thenTheExtremesAreTheFirstRank() {
      // Arrange
      List<DoubleSolution> population = population(1.0, 1.0);
      DoubleSolution a = population.get(0);
      DoubleSolution b = population.get(1);
      DoubleSolution c = population.get(2);

      // Act
      var result = ranking(null).computeRanking(population);

      // Assert: each extreme is the best for a utility function, and C is the second for both
      assertEquals(2, result.getNumberOfSubfronts());
      assertEquals(2, result.getSubfront(0).size());
      assertTrue(result.getSubfront(0).containsAll(List.of(a, b)));
      assertEquals(List.of(c), result.getSubfront(1));
    }
  }

  @Nested
  @DisplayName("When ranking solutions with the objectives on different scales")
  class WhenRankingWithDifferentScales {
    @Test
    @DisplayName("given a normalizer that undoes the scales, then the ranking is the unit one")
    void givenANormalizerThatUndoesTheScales_whenRanking_thenTheRankingIsTheUnitOne() {
      // Arrange: the ranking sorts the list, so the solutions are kept apart
      List<DoubleSolution> population = population(1000.0, 0.001);
      DoubleSolution a = population.get(0);
      DoubleSolution b = population.get(1);
      DoubleSolution c = population.get(2);
      var normalizer = new Normalizer(List.of(0.0, 0.0), List.of(1000.0, 0.001));

      // Act
      var result = ranking(normalizer).computeRanking(population);

      // Assert
      assertEquals(2, result.getNumberOfSubfronts());
      assertTrue(result.getSubfront(0).containsAll(List.of(a, b)));
      assertEquals(List.of(c), result.getSubfront(1));
    }

    @Test
    @DisplayName("given no normalizer, then the objective with the largest scale decides")
    void givenNoNormalizer_whenRanking_thenTheObjectiveWithTheLargestScaleDecides() {
      // Arrange
      List<DoubleSolution> population = population(1000.0, 0.001);
      DoubleSolution a = population.get(0);
      DoubleSolution b = population.get(1);
      DoubleSolution c = population.get(2);

      // Act
      var result = ranking(null).computeRanking(population);

      // Assert: A is the best for both utility functions, then C, then B
      assertEquals(3, result.getNumberOfSubfronts());
      assertEquals(List.of(a), result.getSubfront(0));
      assertEquals(List.of(c), result.getSubfront(1));
      assertEquals(List.of(b), result.getSubfront(2));
    }

    @Test
    @DisplayName("given translated objectives, then a normalizer with the new bounds keeps it")
    void givenTranslatedObjectives_whenRankingWithTheNewBounds_thenTheRankingIsTheSame() {
      // Arrange: the objectives are shifted by -50 and -7, and so is the normalizer
      List<DoubleSolution> population = new ArrayList<>();
      for (DoubleSolution original : population(1.0, 1.0)) {
        population.add(solution(original.objectives()[0] - 50.0, original.objectives()[1] - 7.0));
      }
      DoubleSolution c = population.get(2);
      var normalizer = new Normalizer(List.of(-50.0, -7.0), List.of(-49.0, -6.0));

      // Act
      var result = ranking(normalizer).computeRanking(population);

      // Assert
      assertEquals(2, result.getNumberOfSubfronts());
      assertEquals(List.of(c), result.getSubfront(1));
    }
  }

  @Nested
  @DisplayName("When computing the utility of the solutions")
  class WhenComputingTheUtility {
    @Test
    @DisplayName("given a normalizer, then the utility is the norm of the normalized objectives")
    void givenANormalizer_whenRanking_thenTheUtilityIsTheNormOfTheNormalizedObjectives() {
      // Arrange
      List<DoubleSolution> population = population(1000.0, 0.001);
      DoubleSolution a = population.get(0);
      DoubleSolution b = population.get(1);
      DoubleSolution c = population.get(2);
      var normalizer = new Normalizer(List.of(0.0, 0.0), List.of(1000.0, 0.001));

      // Act
      ranking(normalizer).computeRanking(population);

      // Assert: the normalized solutions are (0, 1), (1, 0) and (0.6, 0.6)
      assertEquals(1.0, utility(a), EPSILON);
      assertEquals(1.0, utility(b), EPSILON);
      assertEquals(Math.sqrt(0.72), utility(c), EPSILON);
    }

    @Test
    @DisplayName("given no normalizer, then the utility is the norm of the objectives")
    void givenNoNormalizer_whenRanking_thenTheUtilityIsTheNormOfTheObjectives() {
      // Arrange
      List<DoubleSolution> population = population(3.0, 4.0);
      DoubleSolution a = population.get(0);
      DoubleSolution b = population.get(1);
      DoubleSolution c = population.get(2);

      // Act
      ranking(null).computeRanking(population);

      // Assert: A = (0, 4), B = (3, 0) and C = (1.8, 2.4)
      assertEquals(4.0, utility(a), EPSILON);
      assertEquals(3.0, utility(b), EPSILON);
      assertEquals(3.0, utility(c), EPSILON);
    }

    @Test
    @DisplayName("given a normalizer with negative bounds, then the utility uses their range")
    void givenNegativeBounds_whenRanking_thenTheUtilityUsesTheirRange() {
      // Arrange: (-8, -4) is (0, 0) and (-4, 0) is (1, 1) in the ranges [-8, -4] and [-4, 0]
      DoubleSolution lower = solution(-8.0, -4.0);
      DoubleSolution upper = solution(-4.0, 0.0);
      List<DoubleSolution> population = new ArrayList<>(List.of(lower, upper));
      var normalizer = new Normalizer(List.of(-8.0, -4.0), List.of(-4.0, 0.0));

      // Act
      ranking(normalizer).computeRanking(population);

      // Assert
      assertEquals(0.0, utility(lower), EPSILON);
      assertEquals(Math.sqrt(2.0), utility(upper), EPSILON);
    }
  }
}
