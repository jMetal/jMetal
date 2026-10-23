package org.uma.jmetal.util.pseudorandom.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

@DisplayName("Unit tests for class ThreadLocalRandomGenerator")
class ThreadLocalRandomGeneratorTest {
  private static final int DRAWS = 20;

  private PseudoRandomGenerator previousGenerator;

  @BeforeEach
  void setUp() {
    previousGenerator = JMetalRandom.getInstance().getRandomGenerator();
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setRandomGenerator(previousGenerator);
  }

  private static List<Double> draws(PseudoRandomGenerator generator) {
    return IntStream.range(0, DRAWS).mapToObj(i -> generator.nextDouble()).toList();
  }

  private static <T> List<T> inThreads(int threads, Callable<T> task) throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    try {
      CountDownLatch start = new CountDownLatch(1);
      List<Future<T>> futures = new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        futures.add(
            executor.submit(
                () -> {
                  start.await();
                  return task.call();
                }));
      }
      start.countDown();
      List<T> results = new ArrayList<>();
      for (Future<T> future : futures) {
        results.add(future.get());
      }
      return results;
    } finally {
      executor.shutdownNow();
    }
  }

  @Nested
  @DisplayName("When installing it")
  class WhenInstallingIt {
    @Test
    @DisplayName("given a seeded generator, when installing, then this thread keeps its sequence")
    void givenASeededGenerator_whenInstalling_thenThisThreadKeepsItsSequence() {
      // Arrange
      List<Double> expected = draws(new JavaRandomGenerator(42));
      JMetalRandom.getInstance().setRandomGenerator(new JavaRandomGenerator(42));

      // Act
      ThreadLocalRandomGenerator.install();

      // Assert
      assertThat(draws(JMetalRandom.getInstance().getRandomGenerator())).isEqualTo(expected);
    }

    @Test
    @DisplayName("given it is installed, when installing again, then the same generator is kept")
    void givenItIsInstalled_whenInstallingAgain_thenTheSameGeneratorIsKept() {
      // Arrange
      ThreadLocalRandomGenerator first = ThreadLocalRandomGenerator.install();

      // Act
      ThreadLocalRandomGenerator second = ThreadLocalRandomGenerator.install();

      // Assert
      assertThat(second).isSameAs(first);
      assertThat(JMetalRandom.getInstance().getRandomGenerator()).isSameAs(first);
    }

    @Test
    @DisplayName("given a factory, when another thread draws, then the factory makes its generator")
    void givenAFactory_whenAnotherThreadDraws_thenItGetsAGeneratorOfTheFactory() throws Exception {
      // Arrange
      ThreadLocalRandomGenerator generator =
          ThreadLocalRandomGenerator.install(MersenneTwisterGenerator::new);

      // Act
      List<String> names = inThreads(1, () -> generator.generatorOfThisThread().getName());

      // Assert
      assertThat(names).containsExactly("MersenneTwister");
      assertThat(generator.getName()).isEqualTo("ThreadLocalRandomGenerator (MersenneTwister)");
    }

    @Test
    @DisplayName("given invalid arguments, when creating it, then an exception is thrown")
    void givenInvalidArguments_whenCreatingIt_thenAnExceptionIsThrown() {
      // Arrange
      var generator = new ThreadLocalRandomGenerator(new JavaRandomGenerator(1));

      // Act & Assert
      assertThatThrownBy(() -> new ThreadLocalRandomGenerator(null))
          .isInstanceOf(NullParameterException.class);
      assertThatThrownBy(() -> new ThreadLocalRandomGenerator(new JavaRandomGenerator(1), null))
          .isInstanceOf(NullParameterException.class);
      assertThatThrownBy(() -> new ThreadLocalRandomGenerator(generator))
          .isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When drawing from several threads")
  class WhenDrawingFromSeveralThreads {
    @Test
    @DisplayName("given several threads, when they draw, then each thread has its own generator")
    void givenSeveralThreads_whenTheyDraw_thenEachThreadHasItsOwnGenerator() throws Exception {
      // Arrange
      ThreadLocalRandomGenerator generator =
          new ThreadLocalRandomGenerator(new JavaRandomGenerator(5));
      Set<PseudoRandomGenerator> generators = ConcurrentHashMap.newKeySet();

      // Act
      List<List<Double>> sequences =
          inThreads(
              8,
              () -> {
                generators.add(generator.generatorOfThisThread());
                return draws(generator);
              });

      // Assert: eight generators, with different seeds, so the sequences differ
      assertThat(generators).hasSize(8);
      assertThat(sequences.stream().distinct()).hasSize(8);
    }

    @Test
    @DisplayName("given a seed in each thread, when they draw, then the sequences are reproducible")
    void givenASeedInEachThread_whenTheyDraw_thenTheirSequencesAreReproducible() throws Exception {
      // Arrange
      ThreadLocalRandomGenerator generator =
          new ThreadLocalRandomGenerator(new JavaRandomGenerator(5));
      List<Double> expected = draws(new JavaRandomGenerator(123));

      // Act
      List<List<Double>> sequences =
          inThreads(
              8,
              () -> {
                generator.setSeed(123);
                return draws(generator);
              });

      // Assert
      assertThat(sequences).allSatisfy(sequence -> assertThat(sequence).isEqualTo(expected));
    }

    @Test
    @DisplayName("given a seed in another thread, when this thread draws, then it is not affected")
    void givenASeedInAnotherThread_whenThisThreadDraws_thenItIsNotAffected() throws Exception {
      // Arrange
      ThreadLocalRandomGenerator generator =
          new ThreadLocalRandomGenerator(new JavaRandomGenerator(42));
      List<Double> expected = draws(new JavaRandomGenerator(42));

      // Act
      inThreads(
          1,
          () -> {
            generator.setSeed(7);
            return generator.getSeed();
          });

      // Assert
      assertThat(generator.getSeed()).isEqualTo(42);
      assertThat(draws(generator)).isEqualTo(expected);
    }
  }

  @Nested
  @DisplayName("When serializing it")
  class WhenSerializingIt {
    @Test
    @DisplayName("given a generator, when deserialized, then it draws with a new generator")
    void givenAGenerator_whenDeserialized_thenItDrawsWithANewGenerator() throws Exception {
      // Arrange
      var generator = new ThreadLocalRandomGenerator(new JavaRandomGenerator(42));
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
        out.writeObject(generator);
      }

      // Act
      PseudoRandomGenerator copy;
      try (ObjectInputStream in =
          new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
        copy = (PseudoRandomGenerator) in.readObject();
      }

      // Assert
      assertThat(copy.getName()).isEqualTo(generator.getName());
      assertThat(copy.nextDouble()).isBetween(0.0, 1.0);
      copy.setSeed(9);
      assertThat(draws(copy)).isEqualTo(draws(new JavaRandomGenerator(9)));
    }
  }
}
