package org.uma.jmetal.algorithm.dynamic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class PeriodicProblemUpdater")
class PeriodicProblemUpdaterTest {
  private DynamicAlgorithm<?> algorithm;

  @BeforeEach
  void setUp() {
    algorithm = mock(DynamicAlgorithm.class);
  }

  @Nested
  @DisplayName("When it runs")
  class WhenItRuns {
    @Test
    @DisplayName("given it is started, then it updates the problem with 1, 2, 3, ...")
    void givenItIsStarted_thenItUpdatesTheProblemWithConsecutiveValues() {
      try (var clock = new PeriodicProblemUpdater(algorithm, 5).start()) {
        verify(algorithm, timeout(5000)).updateProblem(3);
      }

      InOrder order = Mockito.inOrder(algorithm);
      order.verify(algorithm).updateProblem(1);
      order.verify(algorithm).updateProblem(2);
      order.verify(algorithm).updateProblem(3);
    }

    @Test
    @DisplayName("given it is started twice, then it raises an exception")
    void givenItIsStartedTwice_thenItRaisesAnException() {
      try (var clock = new PeriodicProblemUpdater(algorithm, 1000).start()) {
        assertThatThrownBy(clock::start).isInstanceOf(InvalidConditionException.class);
      }
    }
  }

  @Nested
  @DisplayName("When it is created")
  class WhenItIsCreated {
    @Test
    @DisplayName("given a period that is not positive, then it raises an exception")
    void givenAPeriodThatIsNotPositive_thenItRaisesAnException() {
      assertThatThrownBy(() -> new PeriodicProblemUpdater(algorithm, 0))
          .isInstanceOf(InvalidConditionException.class);
    }
  }
}
