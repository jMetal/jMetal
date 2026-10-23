package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByComputingTime;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;

@DisplayName("Unit tests for the progress of the termination conditions")
class TerminationProgressTest {
  @Nested
  @DisplayName("When terminating by evaluations")
  class WhenTerminatingByEvaluations {
    @ParameterizedTest(name = "{0} evaluations of 1000")
    @CsvSource({"0, 0.0", "100, 0.1", "1000, 1.0", "1100, 1.0"})
    @DisplayName("given some evaluations, then the progress is their fraction of the maximum")
    void givenSomeEvaluations_whenAskingTheProgress_thenItIsTheirFraction(
        int evaluations, double expected) {
      // Act
      var progress =
          new TerminationByEvaluations(1000).progress(Map.of("EVALUATIONS", evaluations));

      // Assert
      assertThat(progress).isPresent();
      assertThat(progress.getAsDouble()).isCloseTo(expected, within(1e-12));
    }

    @Test
    @DisplayName("given no evaluations in the status, then the progress is unknown")
    void givenNoEvaluations_whenAskingTheProgress_thenItIsUnknown() {
      // Act & Assert
      assertThat(new TerminationByEvaluations(1000).progress(Map.of())).isEmpty();
    }
  }

  @Nested
  @DisplayName("When terminating by computing time")
  class WhenTerminatingByComputingTime {
    @Test
    @DisplayName("given a computing time, then the progress is its fraction of the maximum")
    void givenAComputingTime_whenAskingTheProgress_thenItIsItsFraction() {
      // Arrange
      var termination = new TerminationByComputingTime(2000);

      // Act & Assert
      assertThat(termination.progress(Map.of("COMPUTING_TIME", 500L)).getAsDouble())
          .isCloseTo(0.25, within(1e-12));
      assertThat(termination.progress(Map.of("COMPUTING_TIME", 3000L)).getAsDouble())
          .isEqualTo(1.0);
      assertThat(termination.progress(Map.of())).isEmpty();
    }
  }

  @Nested
  @DisplayName("When the termination does not know the progress")
  class WhenTheTerminationDoesNotKnowTheProgress {
    @Test
    @DisplayName("given another termination, then the progress is unknown")
    void givenAnotherTermination_whenAskingTheProgress_thenItIsUnknown() {
      // Arrange
      Termination termination = status -> false;

      // Act & Assert
      assertThat(termination.progress(Map.of("EVALUATIONS", 10))).isEmpty();
    }
  }
}
