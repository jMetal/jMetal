package org.uma.jmetal.util.errorchecking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for class JMetalException")
class JMetalExceptionTest {

  @Test
  @DisplayName("given a message, when created, then it keeps the message and has no cause")
  void givenAMessage_whenCreated_thenItKeepsTheMessage() {
    JMetalException exception = new JMetalException("wrong value");

    assertEquals("wrong value", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  @DisplayName("given a message and a cause, when created, then it keeps both")
  void givenAMessageAndACause_whenCreated_thenItKeepsBoth() {
    IOException cause = new IOException("disk full");

    JMetalException exception = new JMetalException("Error writing the file", cause);

    assertEquals("Error writing the file", exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  @Test
  @DisplayName("given only a cause, when created, then it keeps the cause and its message")
  void givenOnlyACause_whenCreated_thenItKeepsTheCause() {
    IOException cause = new IOException("disk full");

    JMetalException exception = new JMetalException(cause);

    assertSame(cause, exception.getCause());
    assertEquals(cause.toString(), exception.getMessage());
  }
}
