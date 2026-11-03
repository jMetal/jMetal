package org.uma.jmetal.util.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

@DisplayName("The rankings and the search of the best solution only look at the sign of a comparator")
class ComparatorMagnitudeTest {

  private static List<DoubleSolution> population() {
    double[][] objectives = {{1, 1}, {2, 2}, {3, 3}, {1, 3}, {3, 1}};
    List<DoubleSolution> solutions = new ArrayList<>();
    for (double[] values : objectives) {
      DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
      solution.objectives()[0] = values[0];
      solution.objectives()[1] = values[1];
      solutions.add(solution);
    }
    return solutions;
  }

  /** A dominance comparator that returns -7, 0 or 7 and not -1, 0 or 1. */
  private static Comparator<DoubleSolution> scaledDominance() {
    DefaultDominanceComparator<DoubleSolution> dominance = new DefaultDominanceComparator<>();
    return (a, b) -> 7 * dominance.compare(a, b);
  }

  @Test
  @DisplayName("given a comparator with large values, when ranking, then FastNonDominatedSortRanking gives the same ranks")
  void givenAScaledComparator_whenRanking_thenFastNonDominatedSortGivesTheSameRanks() {
    List<DoubleSolution> expectedPopulation = population();
    List<DoubleSolution> scaledPopulation = population();
    var expected = new FastNonDominatedSortRanking<DoubleSolution>();
    var actual = new FastNonDominatedSortRanking<DoubleSolution>(scaledDominance()::compare);

    expected.compute(expectedPopulation);
    actual.compute(scaledPopulation);

    for (int i = 0; i < expectedPopulation.size(); i++) {
      assertThat(actual.getRank(scaledPopulation.get(i)))
          .as("solution %d", i)
          .isEqualTo(expected.getRank(expectedPopulation.get(i)));
    }
  }

  @Test
  @DisplayName("given a comparator with large values, when ranking, then StrengthRanking gives the same ranks")
  void givenAScaledComparator_whenRanking_thenStrengthRankingGivesTheSameRanks() {
    List<DoubleSolution> expectedPopulation = population();
    List<DoubleSolution> scaledPopulation = population();
    var expected = new StrengthRanking<DoubleSolution>(new DefaultDominanceComparator<>());
    var actual = new StrengthRanking<DoubleSolution>(scaledDominance()::compare);

    expected.compute(expectedPopulation);
    actual.compute(scaledPopulation);

    for (int i = 0; i < expectedPopulation.size(); i++) {
      assertThat(actual.getRank(scaledPopulation.get(i)))
          .as("solution %d", i)
          .isEqualTo(expected.getRank(expectedPopulation.get(i)));
    }
  }

  @Test
  @DisplayName("given a comparator with large values, when searching, then the best and the worst are found")
  void givenAScaledComparator_whenSearching_thenTheBestAndTheWorstAreFound() {
    List<DoubleSolution> solutions = population();
    Comparator<DoubleSolution> byFirstObjective =
        (a, b) -> (int) (100 * (a.objectives()[0] - b.objectives()[0]));

    assertThat(SolutionListUtils.findIndexOfBestSolution(solutions, byFirstObjective)).isEqualTo(0);
    assertThat(SolutionListUtils.findIndexOfWorstSolution(solutions, byFirstObjective)).isEqualTo(2);
  }
}
