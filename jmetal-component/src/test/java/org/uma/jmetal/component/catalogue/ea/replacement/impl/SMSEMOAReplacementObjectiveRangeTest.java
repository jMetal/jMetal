package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

@DisplayName("Unit tests for the objective range of class SMSEMOAReplacement")
class SMSEMOAReplacementObjectiveRangeTest {
  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  /**
   * Two extreme points and two points that are almost the same, with the extra objectives at
   * constant values; the extreme points are the last ones of the front, which is where a
   * replacement that cannot tell the contributions apart removes.
   */
  private static List<DoubleSolution> front(int objectives, double shift, double scale) {
    double[][] base = {{0.4, 0.6}, {0.41, 0.59}, {0.0, 1.0}, {1.0, 0.0}};
    List<DoubleSolution> front = new ArrayList<>();
    for (double[] point : base) {
      double[] values = new double[objectives];
      values[0] = point[0] * scale + shift;
      values[1] = point[1] * scale + shift;
      for (int j = 2; j < objectives; j++) {
        values[j] = 0.5 * scale + shift;
      }
      front.add(solution(values));
    }
    return front;
  }

  @Nested
  @DisplayName("When the objectives are not in the range [0, 1]")
  class WhenTheObjectivesAreNotInTheUnitRange {
    @ParameterizedTest(name = "{0} objectives, shifted by {1}, scaled by {2}")
    @CsvSource({
      "2, 0.0, 1.0", "2, -5.0, 1.0", "2, -100.0, 1.0", "2, 100.0, 1.0", "2, -5.0, 50.0",
      "3, 0.0, 1.0", "3, -5.0, 1.0", "3, -100.0, 1.0", "3, 100.0, 1.0", "3, -5.0, 50.0",
      "4, 0.0, 1.0", "4, -5.0, 1.0", "4, -100.0, 1.0", "4, 100.0, 1.0", "4, 0.0, 1000.0",
      "5, 0.0, 1.0", "5, -5.0, 1.0", "5, 100.0, 1.0"
    })
    @DisplayName("given near-duplicated and extreme points, then it removes a near-duplicate")
    void givenNearDuplicatedAndExtremePoints_whenReplacing_thenItRemovesANearDuplicate(
        int objectives, double shift, double scale) {
      // Arrange
      List<DoubleSolution> front = front(objectives, shift, scale);
      DoubleSolution nearDuplicate = front.get(0);
      DoubleSolution otherNearDuplicate = front.get(1);
      DoubleSolution firstExtreme = front.get(2);
      DoubleSolution lastExtreme = front.get(3);
      var replacement = new SMSEMOAReplacement<DoubleSolution>(new FastNonDominatedSortRanking<>());

      // Act
      List<DoubleSolution> result =
          replacement.replace(
              new ArrayList<>(List.of(nearDuplicate, otherNearDuplicate, firstExtreme)),
              List.of(lastExtreme));

      // Assert: the extreme points have the largest contributions, so they are kept
      assertThat(result).hasSize(3).contains(firstExtreme, lastExtreme);
      assertThat(result).containsAnyOf(nearDuplicate, otherNearDuplicate);
    }
  }
}
