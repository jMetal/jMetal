package org.uma.jmetal.component.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;

@DisplayName("Unit tests for class RunProgress")
class RunProgressTest {
  @Nested
  @DisplayName("When asking the progress")
  class WhenAskingTheProgress {
    @Test
    @DisplayName("given it is not bound, then the progress is NaN")
    void givenItIsNotBound_whenAskingTheProgress_thenItIsNaN() {
      // Act & Assert
      assertThat(new RunProgress().getAsDouble()).isNaN();
    }

    @Test
    @DisplayName("given it is bound, then it follows the status of the algorithm")
    void givenItIsBound_whenTheStatusChanges_thenTheProgressFollowsIt() {
      // Arrange
      Map<String, Object> status = new HashMap<>();
      var progress = new RunProgress();
      progress.bind(() -> new TerminationByEvaluations(200), () -> status);

      // Act
      status.put("EVALUATIONS", 50);
      double first = progress.getAsDouble();
      status.put("EVALUATIONS", 150);
      double second = progress.getAsDouble();

      // Assert
      assertThat(first).isEqualTo(0.25);
      assertThat(second).isEqualTo(0.75);
    }

    @Test
    @DisplayName("given a termination without progress, then the progress is NaN")
    void givenATerminationWithoutProgress_whenAskingTheProgress_thenItIsNaN() {
      // Arrange
      var progress = new RunProgress();
      progress.bind(() -> status -> false, HashMap::new);

      // Act & Assert
      assertThat(progress.getAsDouble()).isNaN();
    }
  }
}
