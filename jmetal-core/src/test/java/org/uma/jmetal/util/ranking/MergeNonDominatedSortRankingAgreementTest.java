package org.uma.jmetal.util.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

@DisplayName("Agreement of MergeNonDominatedSortRanking with FastNonDominatedSortRanking")
class MergeNonDominatedSortRankingAgreementTest {

  private static List<DoubleSolution> population(
      Random random, int size, int objectives, int levels) {
    List<DoubleSolution> solutions = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      DoubleSolution solution = new FakeDoubleProblem(2, objectives, 0).createSolution();
      for (int j = 0; j < objectives; j++) {
        solution.objectives()[j] = random.nextInt(levels);
      }
      solutions.add(solution);
    }
    return solutions;
  }

  /** Peels the fronts one by one with Pareto dominance: an independent reference. */
  private static int[] peelFronts(List<DoubleSolution> solutions) {
    int[] ranks = new int[solutions.size()];
    java.util.Arrays.fill(ranks, -1);
    int assigned = 0;
    for (int rank = 0; assigned < solutions.size(); rank++) {
      List<Integer> front = new ArrayList<>();
      for (int i = 0; i < solutions.size(); i++) {
        if (ranks[i] >= 0) {
          continue;
        }
        boolean dominated = false;
        for (int j = 0; j < solutions.size() && !dominated; j++) {
          dominated = ranks[j] < 0 && j != i && dominates(solutions.get(j), solutions.get(i));
        }
        if (!dominated) {
          front.add(i);
        }
      }
      for (int i : front) {
        ranks[i] = rank;
      }
      assigned += front.size();
    }
    return ranks;
  }

  private static boolean dominates(DoubleSolution a, DoubleSolution b) {
    boolean strictlyBetter = false;
    for (int k = 0; k < a.objectives().length; k++) {
      if (a.objectives()[k] > b.objectives()[k]) {
        return false;
      }
      strictlyBetter |= a.objectives()[k] < b.objectives()[k];
    }
    return strictlyBetter;
  }

  @ParameterizedTest(name = "{0} objectives, {1} levels")
  @CsvSource({"2, 3", "2, 6", "3, 3", "3, 5", "4, 3", "5, 4"})
  @DisplayName(
      "given populations with many ties and duplicates, when ranked, then both rankings agree")
  void givenPopulationsWithTiesAndDuplicates_whenRanked_thenBothRankingsAgree(
      int objectives, int levels) {
    Random random = new Random(20261103L);

    for (int trial = 0; trial < 300; trial++) {
      // Arrange
      List<DoubleSolution> solutions = population(random, 5 + random.nextInt(40), objectives, levels);
      Ranking<DoubleSolution> expected = new FastNonDominatedSortRanking<>();
      Ranking<DoubleSolution> actual = new MergeNonDominatedSortRanking<>();

      // Act
      expected.compute(solutions);
      actual.compute(solutions);

      // Assert
      int[] reference = peelFronts(solutions);
      for (int i = 0; i < solutions.size(); i++) {
        DoubleSolution solution = solutions.get(i);
        String description =
            String.format(
                "trial %d, solution %d with objectives %s",
                trial, i, java.util.Arrays.toString(solution.objectives()));
        assertThat(expected.getRank(solution)).as("fast, " + description).isEqualTo(reference[i]);
        assertThat(actual.getRank(solution)).as("merge, " + description).isEqualTo(reference[i]);
      }
    }
  }
}
