package org.uma.jmetal.util.comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.EpsilonDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class EpsilonDominanceComparator")
class EpsilonDominanceComparatorTest {
  private static final double EPSILON = 0.1;

  private EpsilonDominanceComparator<DoubleSolution> subject;

  @BeforeEach
  void setUp() {
    subject = new EpsilonDominanceComparator<>(EPSILON);
  }

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution = new FakeDoubleProblem(2, objectives.length, 0).createSolution();
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  @Nested
  @DisplayName("When creating the comparator")
  class WhenCreatingTheComparator {
    @ParameterizedTest(name = "epsilon = {0}")
    @ValueSource(doubles = {0.0, -0.1, Double.NaN})
    @DisplayName("given an epsilon that is not positive, when created, then it is rejected")
    void givenAnEpsilonThatIsNotPositive_whenCreated_thenItIsRejected(double epsilon) {
      assertThrows(
          InvalidConditionException.class, () -> new EpsilonDominanceComparator<>(epsilon));
    }
  }

  @Nested
  @DisplayName("When comparing solutions that are not in the same box")
  class WhenTheSolutionsAreNotInTheSameBox {
    @Test
    @DisplayName("given a solution whose box dominates the other one, then it is preferred")
    void givenABoxThatDominates_whenComparing_thenThatSolutionIsPreferred() {
      // Arrange: the boxes are (0, 0) and (2, 2)
      DoubleSolution better = solution(0.05, 0.05);
      DoubleSolution worse = solution(0.25, 0.25);

      // Act & Assert
      assertThat(subject.compare(better, worse)).isEqualTo(-1);
      assertThat(subject.compare(worse, better)).isEqualTo(1);
    }

    @Test
    @DisplayName("given boxes that do not dominate each other, then neither is preferred")
    void givenBoxesThatDoNotDominate_whenComparing_thenNeitherIsPreferred() {
      // Arrange: the boxes are (0, 2) and (2, 0)
      DoubleSolution solution1 = solution(0.05, 0.25);
      DoubleSolution solution2 = solution(0.25, 0.05);

      // Act & Assert
      assertThat(subject.compare(solution1, solution2)).isZero();
      assertThat(subject.compare(solution2, solution1)).isZero();
    }
  }

  @Nested
  @DisplayName("When comparing solutions in the same box")
  class WhenTheSolutionsAreInTheSameBox {
    @Test
    @DisplayName("given two solutions in a box, when comparing, then the closer to its corner wins")
    void givenTwoSolutionsInABox_whenComparing_thenTheCloserToTheCornerWins() {
      // Arrange: both are in the box (1, 1), whose lower corner is (0.1, 0.1)
      DoubleSolution closer = solution(0.11, 0.12);
      DoubleSolution farther = solution(0.17, 0.18);

      // Act & Assert
      assertThat(subject.compare(closer, farther)).isEqualTo(-1);
      assertThat(subject.compare(farther, closer)).isEqualTo(1);
    }

    @Test
    @DisplayName("given two equal solutions, when comparing, then the first one is the dominated")
    void givenEqualSolutions_whenComparing_thenTheFirstOneIsTheDominated() {
      // Arrange
      DoubleSolution solution1 = solution(0.13, 0.14);
      DoubleSolution solution2 = solution(0.13, 0.14);

      // Act & Assert: an archive keeps the solution that it already has
      assertThat(subject.compare(solution1, solution2)).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @Test
    @DisplayName("given a null solution, when comparing, then an exception is raised")
    void givenANullSolution_whenComparing_thenAnExceptionIsRaised() {
      assertThrows(NullParameterException.class, () -> subject.compare(null, solution(0.1, 0.1)));
      assertThrows(NullParameterException.class, () -> subject.compare(solution(0.1, 0.1), null));
    }

    @Test
    @DisplayName("given solutions with different numbers of objectives, then an exception is raised")
    void givenDifferentNumbersOfObjectives_whenComparing_thenAnExceptionIsRaised() {
      assertThrows(
          InvalidConditionException.class,
          () -> subject.compare(solution(0.1, 0.1), solution(0.1, 0.1, 0.1)));
    }
  }
}
