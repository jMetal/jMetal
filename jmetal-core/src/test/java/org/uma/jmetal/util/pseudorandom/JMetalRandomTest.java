package org.uma.jmetal.util.pseudorandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class JMetalRandom")
class JMetalRandomTest {
  @Nested
  @DisplayName("When getting the instance")
  class WhenGettingTheInstance {
    @Test
    @DisplayName("given many threads, when they get the instance at once, then it is the same one")
    void givenManyThreads_whenTheyGetTheInstanceAtOnce_thenItIsTheSameOne() throws Exception {
      // Arrange
      int threads = 16;
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      CountDownLatch start = new CountDownLatch(1);
      List<Future<JMetalRandom>> futures = new ArrayList<>();

      // Act
      try {
        for (int i = 0; i < threads; i++) {
          futures.add(
              executor.submit(
                  () -> {
                    start.await();
                    return JMetalRandom.getInstance();
                  }));
        }
        start.countDown();

        // Assert
        for (Future<JMetalRandom> future : futures) {
          assertThat(future.get()).isSameAs(JMetalRandom.getInstance());
        }
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Nested
  @DisplayName("When setting the generator")
  class WhenSettingTheGenerator {
    @Test
    @DisplayName("given a null generator, when setting it, then an exception is thrown")
    void givenANullGenerator_whenSettingIt_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThatThrownBy(() -> JMetalRandom.getInstance().setRandomGenerator(null))
          .isInstanceOf(NullParameterException.class);
      assertThat(JMetalRandom.getInstance().getRandomGenerator()).isNotNull();
    }
  }
}
