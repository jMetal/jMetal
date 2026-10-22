package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

@DisplayName("Unit tests for the normalization of class NSGAIIIReplacement")
class NSGAIIIReplacementNormalizationTest {
  /** Five non-dominated points of the line f1 + f2 = 1. */
  private static final double[][] FRONT = {
    {0.0, 1.0}, {0.2, 0.8}, {0.5, 0.5}, {0.7, 0.3}, {1.0, 0.0}
  };

  private static final List<double[]> REFERENCE_POINTS =
      List.of(new double[] {1.0, 0.0}, new double[] {0.5, 0.5}, new double[] {0.0, 1.0});

  /** The points that are the closest to each of the reference points, in the normalized space. */
  private static final List<Integer> CLOSEST_TO_EACH_REFERENCE_POINT = List.of(0, 2, 4);

  private NSGAIIIReplacement<DoubleSolution> replacement;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(23);
    replacement =
        new NSGAIIIReplacement<>(
            new FastNonDominatedSortRanking<>(), REFERENCE_POINTS, 2, 3);
  }

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  /** The solutions of the front with its objectives scaled and then translated. */
  private static List<DoubleSolution> front(
      double scale1, double scale2, double shift1, double shift2) {
    List<DoubleSolution> solutions = new ArrayList<>();
    for (double[] point : FRONT) {
      solutions.add(solution(point[0] * scale1 + shift1, point[1] * scale2 + shift2));
    }
    return solutions;
  }

  /** Replaces the first two solutions by the rest; the indices of the selected ones. */
  private List<Integer> selectedIndices(List<DoubleSolution> solutions) {
    JMetalRandom.getInstance().setSeed(23);
    List<DoubleSolution> selected =
        replacement.replace(solutions.subList(0, 2), solutions.subList(2, solutions.size()));
    return selected.stream().map(solutions::indexOf).sorted().toList();
  }

  @Nested
  @DisplayName("When the objectives are on the scale of the reference points")
  class WhenTheObjectivesAreOnTheScaleOfTheReferencePoints {
    @Test
    @DisplayName("given a front, then the closest point to each reference point is selected")
    void givenAFront_whenReplacing_thenTheClosestPointToEachReferencePointIsSelected() {
      // Act
      List<Integer> selected = selectedIndices(front(1.0, 1.0, 0.0, 0.0));

      // Assert
      assertThat(selected).isEqualTo(CLOSEST_TO_EACH_REFERENCE_POINT);
    }
  }

  @Nested
  @DisplayName("When the objectives are scaled or translated")
  class WhenTheObjectivesAreScaledOrTranslated {
    @ParameterizedTest(name = "scales ({0}, {1})")
    @CsvSource({"1000.0, 1.0", "1.0, 0.001", "1.0, 1000.0", "0.5, 0.5", "2.0, 2.0", "10.0, 0.1"})
    @DisplayName("given objectives of other scales, then the same solutions are selected")
    void givenObjectivesOfOtherScales_whenReplacing_thenTheSameSolutionsAreSelected(
        double scale1, double scale2) {
      // Act
      List<Integer> selected = selectedIndices(front(scale1, scale2, 0.0, 0.0));

      // Assert
      assertThat(selected).isEqualTo(CLOSEST_TO_EACH_REFERENCE_POINT);
    }

    @ParameterizedTest(name = "shifts ({0}, {1})")
    @CsvSource({"5000.0, 3.0", "-5000.0, -3.0", "-1.0, 0.0", "0.0, -1000000.0"})
    @DisplayName("given translated objectives, even negative ones, then the same are selected")
    void givenTranslatedObjectives_whenReplacing_thenTheSameSolutionsAreSelected(
        double shift1, double shift2) {
      // Act
      List<Integer> selected = selectedIndices(front(1.0, 1.0, shift1, shift2));

      // Assert
      assertThat(selected).isEqualTo(CLOSEST_TO_EACH_REFERENCE_POINT);
    }

    @Test
    @DisplayName("given objectives scaled and translated, then the same solutions are selected")
    void givenScaledAndTranslatedObjectives_whenReplacing_thenTheSameSolutionsAreSelected() {
      // Act
      List<Integer> selected = selectedIndices(front(250.0, 0.004, -9000.0, -17.0));

      // Assert
      assertThat(selected).isEqualTo(CLOSEST_TO_EACH_REFERENCE_POINT);
    }
  }

  @Nested
  @DisplayName("When the front is degenerate")
  class WhenTheFrontIsDegenerate {
    @Test
    @DisplayName("given an objective with the same value in every solution, then they are selected")
    void givenAConstantObjective_whenReplacing_thenTheNormalizationDoesNotFail() {
      // Arrange: the third objective is constant, so its intercept is zero
      var threeObjectives =
          new NSGAIIIReplacement<DoubleSolution>(
              new FastNonDominatedSortRanking<>(),
              List.of(new double[] {1.0, 0.0, 0.0}, new double[] {0.0, 1.0, 0.0},
                  new double[] {0.0, 0.0, 1.0}),
              3,
              3);
      List<DoubleSolution> solutions = new ArrayList<>();
      for (double value : new double[] {0.0, 0.25, 0.5, 0.75, 1.0}) {
        DoubleSolution solution =
            new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 3, 0);
        solution.objectives()[0] = value;
        solution.objectives()[1] = 1.0 - value;
        solution.objectives()[2] = 7.0;
        solutions.add(solution);
      }

      // Act
      JMetalRandom.getInstance().setSeed(23);
      List<DoubleSolution> selected =
          threeObjectives.replace(solutions.subList(0, 2), solutions.subList(2, 5));

      // Assert
      assertThat(selected).hasSize(3).doesNotHaveDuplicates();
      assertThat(solutions).containsAll(selected);
    }

    @Test
    @DisplayName("given identical solutions, then as many as the population size are selected")
    void givenIdenticalSolutions_whenReplacing_thenThePopulationSizeIsKept() {
      // Arrange
      List<DoubleSolution> solutions = new ArrayList<>();
      for (int i = 0; i < 6; i++) {
        solutions.add(solution(0.5, 0.5));
      }

      // Act
      JMetalRandom.getInstance().setSeed(23);
      List<DoubleSolution> selected =
          replacement.replace(solutions.subList(0, 3), solutions.subList(3, 6));

      // Assert
      assertThat(selected).hasSize(3).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("given the points of a front on a single reference direction, then all are kept")
    void givenPointsOnASingleDirection_whenReplacing_thenThePopulationSizeIsKept() {
      // Arrange: points on the line f1 = f2 are all associated with the same reference point
      List<DoubleSolution> solutions = new ArrayList<>();
      for (double value : new double[] {0.1, 0.2, 0.3, 0.4, 0.5}) {
        solutions.add(solution(value, 1.0 - value));
      }
      solutions.add(solution(1.0, 0.0));

      // Act
      JMetalRandom.getInstance().setSeed(23);
      List<DoubleSolution> selected =
          replacement.replace(solutions.subList(0, 3), solutions.subList(3, 6));

      // Assert
      assertThat(selected).hasSize(3).doesNotHaveDuplicates();
      assertThat(solutions).containsAll(selected);
    }
  }
}
