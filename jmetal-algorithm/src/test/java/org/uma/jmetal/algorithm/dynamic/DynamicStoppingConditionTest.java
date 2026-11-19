package org.uma.jmetal.algorithm.dynamic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for interface DynamicStoppingCondition")
class DynamicStoppingConditionTest {

  private static DynamicStatus status(
      long iterations, long evaluations, int rounds, int problemChanges, long computingTime) {
    return new DynamicStatus(iterations, evaluations, rounds, problemChanges, computingTime);
  }

  @Nested
  @DisplayName("When it is checked")
  class WhenItIsChecked {
    @ParameterizedTest
    @CsvSource({"99, false", "100, true", "101, true"})
    @DisplayName("given afterIterations(100), then it is met from 100 iterations")
    void givenAfterIterations_thenItIsMetFromTheLimit(long iterations, boolean expected) {
      assertThat(DynamicStoppingCondition.afterIterations(100).isMet(status(iterations, 0, 0, 0, 0)))
          .isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"999, false", "1000, true"})
    @DisplayName("given afterEvaluations(1000), then it is met from 1000 evaluations")
    void givenAfterEvaluations_thenItIsMetFromTheLimit(long evaluations, boolean expected) {
      assertThat(
              DynamicStoppingCondition.afterEvaluations(1000)
                  .isMet(status(0, evaluations, 0, 0, 0)))
          .isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"2, false", "3, true"})
    @DisplayName("given afterRounds(3), then it is met from 3 rounds")
    void givenAfterRounds_thenItIsMetFromTheLimit(int rounds, boolean expected) {
      assertThat(DynamicStoppingCondition.afterRounds(3).isMet(status(0, 0, rounds, 0, 0)))
          .isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"29, false", "30, true"})
    @DisplayName("given afterProblemChanges(30), then it is met from 30 changes")
    void givenAfterProblemChanges_thenItIsMetFromTheLimit(int changes, boolean expected) {
      assertThat(
              DynamicStoppingCondition.afterProblemChanges(30).isMet(status(0, 0, 0, changes, 0)))
          .isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"4999, false", "5000, true"})
    @DisplayName("given afterComputingTime(5000), then it is met from 5000 ms")
    void givenAfterComputingTime_thenItIsMetFromTheLimit(long time, boolean expected) {
      assertThat(
              DynamicStoppingCondition.afterComputingTime(5000).isMet(status(0, 0, 0, 0, time)))
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("given never(), then it is never met")
    void givenNever_thenItIsNeverMet() {
      assertThat(
              DynamicStoppingCondition.never()
                  .isMet(status(Long.MAX_VALUE, Long.MAX_VALUE, 1000, 1000, Long.MAX_VALUE)))
          .isFalse();
    }

    @ParameterizedTest
    @CsvSource({"9, 1, false", "10, 1, true", "9, 2, true"})
    @DisplayName("given two conditions joined with or, then it is met when any of them is met")
    void givenTwoConditionsJoinedWithOr_thenItIsMetWhenAnyIsMet(
        long iterations, int rounds, boolean expected) {
      DynamicStoppingCondition condition =
          DynamicStoppingCondition.afterIterations(10).or(DynamicStoppingCondition.afterRounds(2));

      assertThat(condition.isMet(status(iterations, 0, rounds, 0, 0))).isEqualTo(expected);
    }
  }

  @Nested
  @DisplayName("When it is created")
  class WhenItIsCreated {
    @Test
    @DisplayName("given a limit that is not positive, then it raises an exception")
    void givenALimitThatIsNotPositive_thenItRaisesAnException() {
      assertThatThrownBy(() -> DynamicStoppingCondition.afterIterations(0))
          .isInstanceOf(InvalidConditionException.class);
      assertThatThrownBy(() -> DynamicStoppingCondition.afterEvaluations(-1))
          .isInstanceOf(InvalidConditionException.class);
      assertThatThrownBy(() -> DynamicStoppingCondition.afterProblemChanges(0))
          .isInstanceOf(InvalidConditionException.class);
    }
  }
}
