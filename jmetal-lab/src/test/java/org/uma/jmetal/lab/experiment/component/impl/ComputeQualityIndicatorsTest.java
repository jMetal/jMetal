package org.uma.jmetal.lab.experiment.component.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class ComputeQualityIndicators")
class ComputeQualityIndicatorsTest {
  private static final double EPSILON = 1e-9;
  private static final String ALGORITHM = "ALG";
  private static final String PROBLEM = "PROB";
  private static final String EXPERIMENT = "test";

  @TempDir Path directory;

  /** The experiment writes its data in a directory with its name inside the base directory. */
  private Path experimentDirectory;

  private Path runDirectory;
  private Path referenceFrontDirectory;

  @BeforeEach
  void setUp() throws IOException {
    experimentDirectory = directory.resolve(EXPERIMENT);
    runDirectory =
        Files.createDirectories(
            experimentDirectory.resolve("data").resolve(ALGORITHM).resolve(PROBLEM));
    referenceFrontDirectory = Files.createDirectories(directory.resolve("referenceFronts"));
  }

  private void write(Path path, String content) throws IOException {
    Files.writeString(path, content);
  }

  /** Its normalized points are (0, 1), (0.5, 0.5) and (1, 0). */
  private void writeReferenceFront(String content) throws IOException {
    write(referenceFrontDirectory.resolve(PROBLEM + ".csv"), content);
  }

  private void writeRun(int run, String front) throws IOException {
    write(runDirectory.resolve("FUN" + run + ".csv"), front);
    write(runDirectory.resolve("VAR" + run + ".csv"), "variables of the run " + run + "\n");
  }

  private Experiment<DoubleSolution, List<DoubleSolution>> experiment(
      int runs, QualityIndicator... indicators) {
    var problem =
        new ExperimentProblem<DoubleSolution>(new ZDT1(), PROBLEM)
            .setReferenceFront(PROBLEM + ".csv");
    Algorithm<List<DoubleSolution>> algorithm =
        new Algorithm<>() {
          @Override
          public void run() {}

          @Override
          public List<DoubleSolution> result() {
            return List.of();
          }

          @Override
          public String name() {
            return ALGORITHM;
          }

          @Override
          public String description() {
            return ALGORITHM;
          }
        };
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    algorithms.add(new ExperimentAlgorithm<>(algorithm, ALGORITHM, problem, 0));

    return new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(EXPERIMENT)
        .setAlgorithmList(algorithms)
        .setProblemList(new ArrayList<>(List.of(problem)))
        .setExperimentBaseDirectory(directory.toString())
        .setReferenceFrontDirectory(referenceFrontDirectory.toString())
        .setOutputParetoFrontFileName("FUN")
        .setOutputParetoSetFileName("VAR")
        .setIndicatorList(List.of(indicators))
        .setIndependentRuns(runs)
        .build();
  }

  private List<Double> values(String indicator) throws IOException {
    return Files.readAllLines(runDirectory.resolve(indicator)).stream()
        .map(Double::parseDouble)
        .toList();
  }

  @Nested
  @DisplayName("When computing the indicators of the runs")
  class WhenComputingTheIndicatorsOfTheRuns {
    @BeforeEach
    void setUp() throws IOException {
      writeReferenceFront("0.0,10.0\n5.0,5.0\n10.0,0.0\n");
    }

    @Test
    @DisplayName("given fronts of the runs, then a value of the indicator is stored for each run")
    void givenTheFrontsOfTheRuns_whenRunning_thenAValueIsStoredForEachRun() throws Exception {
      // Arrange: normalized with the reference front, (10, 10) is (1, 1) and (5, 5) is (.5, .5)
      writeRun(0, "10.0,10.0\n");
      writeRun(1, "5.0,5.0\n");

      // Act
      new ComputeQualityIndicators<>(experiment(2, new Epsilon())).run();

      // Assert
      List<Double> values = values("EP");
      assertEquals(2, values.size());
      assertEquals(1.0, values.get(0), EPSILON);
      assertEquals(0.5, values.get(1), EPSILON);
    }

    @Test
    @DisplayName("given a reference front with negative objectives, then its range is used")
    void givenNegativeObjectives_whenRunning_thenTheRangeOfTheReferenceFrontIsUsed()
        throws Exception {
      // Arrange: the same fronts as above, shifted by -100
      writeReferenceFront("-100.0,-90.0\n-95.0,-95.0\n-90.0,-100.0\n");
      writeRun(0, "-90.0,-90.0\n");
      writeRun(1, "-95.0,-95.0\n");

      // Act
      new ComputeQualityIndicators<>(experiment(2, new Epsilon())).run();

      // Assert
      List<Double> values = values("EP");
      assertEquals(1.0, values.get(0), EPSILON);
      assertEquals(0.5, values.get(1), EPSILON);
    }

    @Test
    @DisplayName("given several indicators, then each has its own file")
    void givenSeveralIndicators_whenRunning_thenEachHasItsOwnFile() throws Exception {
      // Arrange
      writeRun(0, "10.0,10.0\n");

      // Act
      new ComputeQualityIndicators<>(
              experiment(1, new Epsilon(), new InvertedGenerationalDistance()))
          .run();

      // Assert
      assertEquals(1.0, values("EP").get(0), EPSILON);
      assertEquals(Math.sqrt(2.5) / 3.0, values("IGD").get(0), EPSILON);
    }

    @Test
    @DisplayName("given an indicator file from a previous execution, then it is replaced")
    void givenAPreviousIndicatorFile_whenRunning_thenItIsReplaced() throws Exception {
      // Arrange
      writeRun(0, "10.0,10.0\n");
      write(runDirectory.resolve("EP"), "99.0\n99.0\n");

      // Act
      new ComputeQualityIndicators<>(experiment(1, new Epsilon())).run();

      // Assert
      assertEquals(List.of(1.0), values("EP"));
    }
  }

  @Nested
  @DisplayName("When selecting the best and the median runs")
  class WhenSelectingTheBestAndTheMedianRuns {
    @BeforeEach
    void setUp() throws IOException {
      writeReferenceFront("0.0,10.0\n5.0,5.0\n10.0,0.0\n");
      writeRun(0, "10.0,10.0\n");
      writeRun(1, "5.0,5.0\n");
    }

    @Test
    @DisplayName("given an indicator that is better when lower, then the best run has the lowest")
    void givenALowerIsBetterIndicator_whenRunning_thenTheBestRunHasTheLowestValue()
        throws Exception {
      // Act
      new ComputeQualityIndicators<>(experiment(2, new Epsilon())).run();

      // Assert: the run 1 has the value 0.5, and the run 0 is the median one of the two
      assertEquals(
          Files.readString(runDirectory.resolve("FUN1.csv")),
          Files.readString(runDirectory.resolve("BEST_EP_FUN.csv")));
      assertEquals(
          Files.readString(runDirectory.resolve("VAR1.csv")),
          Files.readString(runDirectory.resolve("BEST_EP_VAR.csv")));
      assertEquals(
          Files.readString(runDirectory.resolve("FUN0.csv")),
          Files.readString(runDirectory.resolve("MEDIAN_EP_FUN.csv")));
    }
  }

  @Nested
  @DisplayName("When writing the summary")
  class WhenWritingTheSummary {
    @Test
    @DisplayName("given the runs, then the summary has a row for each indicator value")
    void givenTheRuns_whenRunning_thenTheSummaryHasARowForEachValue() throws Exception {
      // Arrange
      writeReferenceFront("0.0,10.0\n5.0,5.0\n10.0,0.0\n");
      writeRun(0, "10.0,10.0\n");
      writeRun(1, "5.0,5.0\n");

      // Act
      new ComputeQualityIndicators<>(experiment(2, new Epsilon())).run();

      // Assert
      List<String> rows =
          Files.readAllLines(experimentDirectory.resolve("QualityIndicatorSummary.csv"));
      assertEquals("Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue", rows.get(0));
      assertEquals(ALGORITHM + "," + PROBLEM + ",EP,0,1.0", rows.get(1));
      assertEquals(ALGORITHM + "," + PROBLEM + ",EP,1,0.5", rows.get(2));
      assertEquals(3, rows.size());
    }
  }

  @Nested
  @DisplayName("When the fronts are not valid")
  class WhenTheFrontsAreNotValid {
    @Test
    @DisplayName("given a front with an infinite value, then the computation is rejected")
    void givenAFrontWithAnInfiniteValue_whenRunning_thenItIsRejected() throws Exception {
      // Arrange
      writeReferenceFront("0.0,10.0\n5.0,5.0\n10.0,0.0\n");
      writeRun(0, "Infinity,5.0\n");

      // Act & Assert
      var component = new ComputeQualityIndicators<>(experiment(1, new Epsilon()));
      assertThrows(InvalidConditionException.class, component::run);
    }

    @Test
    @DisplayName("given a missing reference front, then the computation fails")
    void givenAMissingReferenceFront_whenRunning_thenItFails() throws Exception {
      // Arrange
      writeRun(0, "10.0,10.0\n");

      // Act & Assert
      var component = new ComputeQualityIndicators<>(experiment(1, new Epsilon()));
      assertThrows(IOException.class, component::run);
      assertTrue(Files.notExists(experimentDirectory.resolve("QualityIndicatorSummary.csv")));
    }
  }
}
