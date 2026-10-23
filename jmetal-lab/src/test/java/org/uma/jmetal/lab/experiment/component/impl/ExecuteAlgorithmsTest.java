package org.uma.jmetal.lab.experiment.component.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.ThreadLocalRandomGenerator;

@DisplayName("Unit tests for class ExecuteAlgorithms")
class ExecuteAlgorithmsTest {
  private static final int RUNS = 3;

  @TempDir Path directory;

  private PseudoRandomGenerator previousGenerator;

  @BeforeEach
  void setUp() {
    previousGenerator = JMetalRandom.getInstance().getRandomGenerator();
    JMetalRandom.getInstance().setRandomGenerator(new JavaRandomGenerator(1));
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setRandomGenerator(previousGenerator);
  }

  private static ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> nsgaII(
      ExperimentProblem<DoubleSolution> problem, String tag, int run) {
    var algorithm =
        new NSGAIIBuilder<>(
                problem.getProblem(),
                new SBXCrossover(0.9, 20.0),
                new PolynomialMutation(1.0 / problem.getProblem().numberOfVariables(), 20.0),
                20)
            .setMaxEvaluations(1000)
            .build();
    return new ExperimentAlgorithm<>(algorithm, tag, problem, run);
  }

  private Experiment<DoubleSolution, List<DoubleSolution>> experiment(
      String name, int cores, Long seed) {
    var problem = new ExperimentProblem<DoubleSolution>(new ZDT1(10));
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < RUNS; run++) {
      algorithms.add(nsgaII(problem, "NSGAIIa", run));
      algorithms.add(nsgaII(problem, "NSGAIIb", run));
    }
    var builder =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(name)
            .setAlgorithmList(algorithms)
            .setProblemList(List.of(problem))
            .setExperimentBaseDirectory(directory.toString())
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndependentRuns(RUNS)
            .setNumberOfCores(cores);
    if (seed != null) {
      builder.setSeed(seed);
    }
    return builder.build();
  }

  /** The contents of the FUN files of an experiment, by their path inside its data directory. */
  private Map<String, String> fronts(String name) throws IOException {
    Path data = directory.resolve(name).resolve("data");
    Map<String, String> fronts = new TreeMap<>();
    try (Stream<Path> files = Files.walk(data)) {
      for (Path file : files.filter(f -> f.getFileName().toString().startsWith("FUN")).toList()) {
        fronts.put(data.relativize(file).toString(), Files.readString(file));
      }
    }
    return fronts;
  }

  private Map<String, String> run(String name, int cores, Long seed) throws IOException {
    new ExecuteAlgorithms<>(experiment(name, cores, seed)).run();
    return fronts(name);
  }

  @Nested
  @DisplayName("When the experiment has a seed")
  class WhenTheExperimentHasASeed {
    @Test
    @DisplayName("given several cores, when running twice, then the fronts are the same")
    void givenSeveralCores_whenRunningTwice_thenTheFrontsAreTheSame() throws IOException {
      // Act
      Map<String, String> first = run("first", 4, 5L);
      Map<String, String> second = run("second", 4, 5L);

      // Assert
      assertEquals(2 * RUNS, first.size());
      assertEquals(first, second);
    }

    @Test
    @DisplayName("given one or several cores, when running, then the fronts are the same")
    void givenOneOrSeveralCores_whenRunning_thenTheFrontsAreTheSame() throws IOException {
      // Act
      Map<String, String> sequential = run("sequential", 1, 5L);
      Map<String, String> parallel = run("parallel", 4, 5L);

      // Assert
      assertEquals(sequential, parallel);
    }

    @Test
    @DisplayName("given another seed, when running, then the fronts differ")
    void givenAnotherSeed_whenRunning_thenTheFrontsDiffer() throws IOException {
      // Act
      Map<String, String> first = run("first", 4, 5L);
      Map<String, String> other = run("other", 4, 6L);

      // Assert: every run, of each algorithm, has its own seed
      assertEquals(first.size(), new HashSet<>(first.values()).size());
      assertNotEquals(first, other);
    }
  }

  @Nested
  @DisplayName("When choosing the random number generator")
  class WhenChoosingTheRandomNumberGenerator {
    @Test
    @DisplayName("given several cores, when running, then each thread has its own generator")
    void givenSeveralCores_whenRunning_thenEachThreadHasItsOwnGenerator() throws IOException {
      // Act
      run("parallel", 2, null);

      // Assert
      assertInstanceOf(
          ThreadLocalRandomGenerator.class, JMetalRandom.getInstance().getRandomGenerator());
    }

    @Test
    @DisplayName("given one core, when running, then the generator does not change")
    void givenOneCore_whenRunning_thenTheGeneratorDoesNotChange() throws IOException {
      // Arrange
      PseudoRandomGenerator generator = JMetalRandom.getInstance().getRandomGenerator();

      // Act
      run("sequential", 1, null);

      // Assert
      assertSame(generator, JMetalRandom.getInstance().getRandomGenerator());
    }
  }
}
