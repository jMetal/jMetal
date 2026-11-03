package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Edge cases of SolutionListUtils")
class SolutionListUtilsEdgeCasesTest {

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Test
  @DisplayName("given an empty list, when inverted, then the inverted front is empty")
  void givenAnEmptyList_whenInverted_thenTheInvertedFrontIsEmpty() {
    assertThat(SolutionListUtils.getInvertedFront(new ArrayList<DoubleSolution>())).isEmpty();
  }

  @Test
  @DisplayName("given a front, when inverted, then each objective is one minus its value and the front is not modified")
  void givenAFront_whenInverted_thenEachObjectiveIsOneMinusItsValue() {
    List<DoubleSolution> front = List.of(solution(0.25, 0.75), solution(1.5, -0.5));

    List<DoubleSolution> inverted = SolutionListUtils.getInvertedFront(front);

    // The values out of [0, 1] are clipped
    assertThat(inverted.get(0).objectives()).containsExactly(0.75, 0.25);
    assertThat(inverted.get(1).objectives()).containsExactly(0.0, 1.0);
    assertThat(front.get(0).objectives()).containsExactly(0.25, 0.75);
  }

  @Test
  @DisplayName("given a front with a constant objective, when normalized, then that objective is the midpoint")
  void givenAConstantObjective_whenNormalized_thenItIsTheMidpoint() {
    List<DoubleSolution> front = List.of(solution(1.0, 5.0), solution(3.0, 5.0));

    List<DoubleSolution> normalized = SolutionListUtils.normalizeSolutionList(front);

    assertThat(normalized.get(0).objectives()).containsExactly(0.0, 0.5);
    assertThat(normalized.get(1).objectives()).containsExactly(1.0, 0.5);
  }

  @Test
  @DisplayName("given a front, when the non-dominated solutions are taken, then the dominated ones are not there")
  void givenAFront_whenTheNonDominatedSolutionsAreTaken_thenTheDominatedOnesAreNotThere() {
    DoubleSolution dominated = solution(2.0, 3.5);
    List<DoubleSolution> front = List.of(solution(1.0, 3.0), dominated, solution(3.0, 1.0));

    assertThat(SolutionListUtils.getNonDominatedSolutions(front))
        .hasSize(2)
        .doesNotContain(dominated);
  }
}
