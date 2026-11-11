package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByAnyOf;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class TerminationByAnyOf")
class TerminationByAnyOfTest {
  private Termination first;
  private Termination second;
  private Map<String, Object> status;
  private TerminationByAnyOf termination;

  @BeforeEach
  void setUp() {
    first = mock(Termination.class);
    second = mock(Termination.class);
    status = new HashMap<>();
    termination = new TerminationByAnyOf(first, second);
  }

  @Nested
  @DisplayName("When it is created")
  class WhenItIsCreated {
    @Test
    @DisplayName("given no conditions, then it raises an exception")
    void givenNoConditions_thenItRaisesAnException() {
      assertThatThrownBy(TerminationByAnyOf::new).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a null condition, then it raises an exception")
    void givenANullCondition_thenItRaisesAnException() {
      assertThatThrownBy(() -> new TerminationByAnyOf(first, null))
          .isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given some conditions, then it keeps them in order")
    void givenSomeConditions_thenItKeepsThemInOrder() {
      assertThat(termination.terminations()).containsExactly(first, second);
    }
  }

  @Nested
  @DisplayName("When it is checked")
  class WhenItIsChecked {
    @ParameterizedTest
    @CsvSource({"false, false, false", "true, false, true", "false, true, true", "true, true, true"})
    @DisplayName("given the results of the conditions, then it is met if any of them is met")
    void givenTheResultsOfTheConditions_thenItIsMetIfAnyOfThemIsMet(
        boolean firstIsMet, boolean secondIsMet, boolean expected) {
      when(first.isMet(status)).thenReturn(firstIsMet);
      when(second.isMet(status)).thenReturn(secondIsMet);

      assertThat(termination.isMet(status)).isEqualTo(expected);
    }

    @Test
    @DisplayName("given the first condition is met, then the second one is also checked")
    void givenTheFirstConditionIsMet_thenTheSecondOneIsAlsoChecked() {
      when(first.isMet(status)).thenReturn(true);

      termination.isMet(status);

      verify(second).isMet(status);
    }
  }

  @Nested
  @DisplayName("When the progress is asked")
  class WhenTheProgressIsAsked {
    @Test
    @DisplayName("given conditions that know their progress, then it is the largest one")
    void givenConditionsThatKnowTheirProgress_thenItIsTheLargestOne() {
      when(first.progress(status)).thenReturn(OptionalDouble.of(0.3));
      when(second.progress(status)).thenReturn(OptionalDouble.of(0.7));

      assertThat(termination.progress(status)).hasValue(0.7);
    }

    @Test
    @DisplayName("given only one condition knows its progress, then it is its progress")
    void givenOnlyOneConditionKnowsItsProgress_thenItIsItsProgress() {
      when(first.progress(status)).thenReturn(OptionalDouble.empty());
      when(second.progress(status)).thenReturn(OptionalDouble.of(0.4));

      assertThat(termination.progress(status)).hasValue(0.4);
    }

    @Test
    @DisplayName("given no condition knows its progress, then it is empty")
    void givenNoConditionKnowsItsProgress_thenItIsEmpty() {
      when(first.progress(status)).thenReturn(OptionalDouble.empty());
      when(second.progress(status)).thenReturn(OptionalDouble.empty());

      assertThat(termination.progress(status)).isEmpty();
    }
  }
}
