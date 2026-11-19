package org.uma.jmetal.algorithm.multiobjective.indm2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class KeyboardReferencePointReader")
class KeyboardReferencePointReaderTest {
  private InDM2<DoubleSolution> indm2;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    indm2 = mock(InDM2.class);
    DynamicProblem<DoubleSolution, Integer> problem = mock(DynamicProblem.class);
    when(problem.numberOfObjectives()).thenReturn(2);
    when(indm2.problem()).thenReturn(problem);
  }

  private void read(String text) throws InterruptedException {
    var input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    Thread thread = new KeyboardReferencePointReader(indm2, input).start();
    thread.join(TimeUnit.SECONDS.toMillis(10));
    assertThat(thread.isAlive()).isFalse();
  }

  @Nested
  @DisplayName("When a line is parsed")
  class WhenALineIsParsed {
    @Test
    @DisplayName("given four values and two objectives, then there are two points")
    void givenFourValuesAndTwoObjectives_thenThereAreTwoPoints() {
      assertThat(KeyboardReferencePointReader.parse(" 0.2, 0.5 ,0.4, 1.0", 2))
          .containsExactly(List.of(0.2, 0.5), List.of(0.4, 1.0));
    }

    @Test
    @DisplayName("given a number of values that is not a multiple of the objectives, then it raises an exception")
    void givenANumberOfValuesThatIsNotAMultiple_thenItRaisesAnException() {
      assertThatThrownBy(() -> KeyboardReferencePointReader.parse("0.2, 0.5, 0.4", 2))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a value that is not a number, then it raises an exception")
    void givenAValueThatIsNotANumber_thenItRaisesAnException() {
      assertThatThrownBy(() -> KeyboardReferencePointReader.parse("0.2, a", 2))
          .isInstanceOf(NumberFormatException.class);
    }
  }

  @Nested
  @DisplayName("When the input is read")
  class WhenTheInputIsRead {
    @Test
    @DisplayName("given points, a wrong line and q, then the points are given, the wrong line is skipped and the algorithm is stopped")
    void givenPointsAWrongLineAndQ_thenThePointsAreGivenAndTheAlgorithmIsStopped()
        throws InterruptedException {
      read("0.6, 0.4\nwrong\n\n0.1,0.2\nq\n0.3,0.3\n");

      InOrder order = inOrder(indm2);
      order.verify(indm2).updateReferencePoints(List.of(List.of(0.6, 0.4)));
      order.verify(indm2).updateReferencePoints(List.of(List.of(0.1, 0.2)));
      order.verify(indm2).requestStop();
      order.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("given points that the algorithm rejects, then the reader goes on")
    void givenPointsThatTheAlgorithmRejects_thenTheReaderGoesOn() throws InterruptedException {
      doThrow(new InvalidConditionException("rejected"))
          .when(indm2)
          .updateReferencePoints(List.of(List.of(0.1, 0.2), List.of(0.3, 0.4)));

      read("0.1, 0.2, 0.3, 0.4\nq\n");

      InOrder order = inOrder(indm2);
      order.verify(indm2).updateReferencePoints(any());
      order.verify(indm2).requestStop();
    }
  }
}
