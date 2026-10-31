package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

@DisplayName("Unit tests for the selection and replacement of AsynchronousMultiThreadedNSGAII")
class AsynchronousMultiThreadedNSGAIITest {

  private static DoubleSolution solution(double f1, double f2, double... constraints) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, constraints.length);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    System.arraycopy(constraints, 0, solution.constraints(), 0, constraints.length);
    return solution;
  }

  /**
   * Replaces with the ranking and then runs many tournaments between the two survivors: alone in
   * their fronts, both have an infinite crowding distance, so only their ranks can tell them apart.
   */
  private static void assertTheTournamentAlwaysChoosesTheFirstOf(
      DoubleSolution better, DoubleSolution worse, DoubleSolution discarded, Ranking<DoubleSolution> ranking) {
    List<DoubleSolution> population =
        AsynchronousMultiThreadedNSGAII.replacement(ranking)
            .replace(List.of(better, worse), List.of(discarded));
    SelectionOperator<List<DoubleSolution>, DoubleSolution> tournament =
        AsynchronousMultiThreadedNSGAII.tournament(ranking);

    for (int i = 0; i < 100; i++) {
      assertSame(better, tournament.execute(population));
    }
  }

  @Nested
  @DisplayName("With the default ranking")
  class WithTheDefaultRanking {

    @Test
    @DisplayName("the binary tournament chooses a solution of a better front")
    void givenADominatedSolution_whenRunningTheTournament_thenTheNonDominatedOneIsChosen() {
      // Arrange
      DoubleSolution nonDominated = solution(1.0, 1.0);
      DoubleSolution dominated = solution(2.0, 2.0);
      DoubleSolution worst = solution(3.0, 3.0);

      // Act & Assert
      assertTheTournamentAlwaysChoosesTheFirstOf(
          nonDominated, dominated, worst, new MergeNonDominatedSortRanking<>());
    }
  }

  @Nested
  @DisplayName("With a ranking with constraint dominance")
  class WithARankingWithConstraintDominance {

    @Test
    @DisplayName("the binary tournament chooses a feasible solution over an infeasible one")
    void givenAnInfeasibleSolutionThatDominates_whenRunningTheTournament_thenTheFeasibleOneIsChosen() {
      // Arrange
      DoubleSolution feasible = solution(2.0, 2.0, 0.0);
      DoubleSolution infeasible = solution(1.0, 1.0, -1.0);
      DoubleSolution moreInfeasible = solution(1.0, 1.0, -2.0);

      // Act & Assert
      assertTheTournamentAlwaysChoosesTheFirstOf(
          feasible, infeasible, moreInfeasible,
          new FastNonDominatedSortRanking<>(new DominanceWithConstraintsComparator<>()));
    }
  }
}
