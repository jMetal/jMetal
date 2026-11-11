package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByKeyboard;

@DisplayName("Unit tests for class TerminationByKeyboard")
class TerminationByKeyboardTest {
  private static final long TIMEOUT_IN_MILLISECONDS = 10_000;

  private InputStream standardInput;
  private PipedOutputStream keyboard;
  private TerminationByKeyboard termination;

  @BeforeEach
  void setUp() throws Exception {
    standardInput = System.in;
    keyboard = new PipedOutputStream();
    System.setIn(new PipedInputStream(keyboard));
    termination = new TerminationByKeyboard();
  }

  @AfterEach
  void tearDown() throws Exception {
    keyboard.close();
    System.setIn(standardInput);
  }

  @Nested
  @DisplayName("When it is checked")
  class WhenItIsChecked {
    @Test
    @DisplayName("given no return has been hit, then it is not met")
    void givenNoReturnHasBeenHit_thenItIsNotMet() {
      assertThat(termination.isMet(Map.of("EVALUATIONS", 100))).isFalse();
      assertThat(termination.getEvaluations()).isZero();
    }

    @Test
    @DisplayName("given a return is hit, then it is met and keeps the evaluations")
    void givenAReturnIsHit_thenItIsMetAndKeepsTheEvaluations() throws Exception {
      keyboard.write("\n".getBytes(StandardCharsets.UTF_8));
      keyboard.flush();

      Map<String, Object> status = Map.of("EVALUATIONS", 2500);
      long deadline = System.currentTimeMillis() + TIMEOUT_IN_MILLISECONDS;
      boolean met = termination.isMet(status);
      while (!met && System.currentTimeMillis() < deadline) {
        Thread.sleep(10);
        met = termination.isMet(status);
      }

      assertThat(met).isTrue();
      assertThat(termination.getEvaluations()).isEqualTo(2500);
    }
  }
}
