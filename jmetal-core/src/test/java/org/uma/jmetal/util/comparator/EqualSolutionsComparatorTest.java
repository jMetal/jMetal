package org.uma.jmetal.util.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class EqualSolutionsComparator")
class EqualSolutionsComparatorTest {
  private final EqualSolutionsComparator<DoubleSolution> subject = new EqualSolutionsComparator<>();

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution = new FakeDoubleProblem(2, objectives.length, 0).createSolution();
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  @Test
  @DisplayName("given solutions with the same objectives, then they are equal")
  void givenTheSameObjectives_whenComparing_thenTheyAreEqual() {
    assertThat(subject.compare(solution(0.3, 0.7), solution(0.3, 0.7))).isZero();
  }

  @Test
  @DisplayName("given solutions that differ in one objective, then they are not equal")
  void givenADifferentObjective_whenComparing_thenTheyAreNotEqual() {
    assertThat(subject.compare(solution(0.3, 0.7), solution(0.3, 0.8))).isNotZero();
  }

  @Test
  @DisplayName("given zero and minus zero, then they are the same value")
  void givenZeroAndMinusZero_whenComparing_thenTheyAreEqual() {
    assertThat(subject.compare(solution(0.0, 1.0), solution(-0.0, 1.0))).isZero();
    assertThat(subject.compare(solution(-0.0, 1.0), solution(0.0, 1.0))).isZero();
  }

  @Test
  @DisplayName("given a NaN and a number, then they are not equal")
  void givenANaN_whenComparing_thenItIsNotEqualToANumber() {
    assertThat(subject.compare(solution(Double.NaN, 1.0), solution(0.5, 1.0))).isNotZero();
  }

  @Test
  @DisplayName("given a null solution, then it is ordered after any solution")
  void givenANullSolution_whenComparing_thenItIsOrderedAfter() {
    assertThat(subject.compare(null, solution(0.5, 0.5))).isEqualTo(1);
    assertThat(subject.compare(solution(0.5, 0.5), null)).isEqualTo(-1);
  }
}
