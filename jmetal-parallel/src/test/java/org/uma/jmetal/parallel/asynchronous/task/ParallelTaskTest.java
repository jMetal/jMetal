package org.uma.jmetal.parallel.asynchronous.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Unit tests for ParallelTask")
class ParallelTaskTest {
  @Test
  @DisplayName("given an identifier and data, when a task is created, then it has them")
  void givenAnIdentifierAndData_whenATaskIsCreated_thenItHasThem() {
    ParallelTask<String> task = ParallelTask.create(5, "data");

    assertThat(task.getIdentifier()).isEqualTo(5);
    assertThat(task.getContents()).isEqualTo("data");
  }

  @Test
  @DisplayName("given null data, when a task is created, then it is rejected")
  void givenNullData_whenATaskIsCreated_thenItIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> ParallelTask.create(1, null));
  }
}
