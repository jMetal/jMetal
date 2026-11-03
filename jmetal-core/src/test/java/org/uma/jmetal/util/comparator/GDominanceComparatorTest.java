package org.uma.jmetal.util.comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.GDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class GDominanceComparator (Molina et al., 2009)")
class GDominanceComparatorTest {
  private GDominanceComparator<DoubleSolution> subject;

  @BeforeEach
  void setUp() {
    subject = new GDominanceComparator<>(List.of(0.5, 0.5));
  }

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Test
  @DisplayName("given a solution that dominates the reference point and another that does not, then the first wins")
  void givenASolutionThatDominatesTheReferencePoint_whenComparing_thenItWins() {
    // Arrange: (0.4, 0.4) dominates (0.5, 0.5); (0.1, 0.9) is in neither region
    DoubleSolution inside = solution(0.4, 0.4);
    DoubleSolution outside = solution(0.1, 0.9);

    // Act & Assert: although neither dominates the other
    assertThat(subject.compare(inside, outside)).isEqualTo(-1);
    assertThat(subject.compare(outside, inside)).isEqualTo(1);
  }

  @Test
  @DisplayName("given a solution dominated by the reference point, then it counts as being in the region")
  void givenASolutionDominatedByTheReferencePoint_whenComparing_thenItCountsAsInTheRegion() {
    // Arrange: (0.8, 0.8) is dominated by the reference point, (0.1, 0.9) is in neither region
    DoubleSolution dominatedByReference = solution(0.8, 0.8);
    DoubleSolution outside = solution(0.1, 0.9);

    // Act & Assert
    assertThat(subject.compare(dominatedByReference, outside)).isEqualTo(-1);
  }

  @Test
  @DisplayName("given two solutions in the same situation, then Pareto dominance decides")
  void givenTwoSolutionsInTheSameSituation_whenComparing_thenParetoDominanceDecides() {
    assertThat(subject.compare(solution(0.3, 0.3), solution(0.4, 0.4))).isEqualTo(-1);
    assertThat(subject.compare(solution(0.1, 0.9), solution(0.9, 0.1))).isZero();
  }

  @Test
  @DisplayName("given a reference point of another size, when comparing, then an exception names the problem")
  void givenAReferencePointOfAnotherSize_whenComparing_thenAnExceptionIsRaised() {
    GDominanceComparator<DoubleSolution> shortReference = new GDominanceComparator<>(List.of(0.5));

    assertThrows(
        InvalidConditionException.class,
        () -> shortReference.compare(solution(0.1, 0.1), solution(0.2, 0.2)));
  }
}
