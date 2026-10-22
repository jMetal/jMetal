package org.uma.jmetal.lab.experiment.component.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class GenerateLatexTablesWithStatistics")
class GenerateLatexTablesWithStatisticsTest {
  private static final String PROBLEM = "PROB";
  private static final String EXPERIMENT = "test";
  private static final String BEST = "\\cellcolor{gray95}";
  private static final String SECOND_BEST = "\\cellcolor{gray25}";

  @TempDir Path directory;

  private static Algorithm<List<DoubleSolution>> algorithm(String name) {
    return new Algorithm<>() {
      @Override
      public void run() {}

      @Override
      public List<DoubleSolution> result() {
        return List.of();
      }

      @Override
      public String name() {
        return name;
      }

      @Override
      public String description() {
        return name;
      }
    };
  }

  /** An indicator that is better when higher and can have any value. */
  private static class HigherIsBetterIndicator extends QualityIndicator {
    @Override
    public double compute(double[][] front) {
      return 0.0;
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
      return false;
    }

    @Override
    public QualityIndicator newInstance() {
      return new HigherIsBetterIndicator();
    }

    @Override
    public String name() {
      return "HIGHER";
    }

    @Override
    public String description() {
      return "An indicator that is better when higher";
    }
  }

  private Experiment<DoubleSolution, List<DoubleSolution>> experiment(QualityIndicator indicator) {
    var problem = new ExperimentProblem<DoubleSolution>(new ZDT1(), PROBLEM);
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (String tag : List.of("A", "B", "C")) {
      algorithms.add(new ExperimentAlgorithm<>(algorithm(tag), tag, problem, 0));
    }

    return new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(EXPERIMENT)
        .setAlgorithmList(algorithms)
        .setProblemList(new ArrayList<>(List.of(problem)))
        .setExperimentBaseDirectory(directory.toString())
        .setIndicatorList(List.of(indicator))
        .setIndependentRuns(3)
        .build();
  }

  private void writeValues(String algorithm, String indicator, double... values)
      throws IOException {
    Path path = directory.resolve(EXPERIMENT).resolve("data").resolve(algorithm).resolve(PROBLEM);
    Files.createDirectories(path);
    StringBuilder content = new StringBuilder();
    for (double value : values) {
      content.append(value).append('\n');
    }
    Files.writeString(path.resolve(indicator), content.toString());
  }

  /** The cells of the row of the problem in the first table: the text between the "&". */
  private List<String> cellsOfTheFirstTable() throws IOException {
    Path file = directory.resolve(EXPERIMENT).resolve("latex").resolve(EXPERIMENT + ".tex");
    String latex = Files.readString(file);
    String row =
        latex.lines().filter(line -> line.startsWith(PROBLEM + " & ")).findFirst().orElseThrow();
    String[] cells = row.replace("\\\\", "").split(" & ");
    return List.of(cells).subList(1, cells.length);
  }

  private static String marked(List<String> cells, String mark) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < cells.size(); i++) {
      if (cells.get(i).contains(mark)) {
        indices.add(i);
      }
    }
    assertEquals(1, indices.size(), "cells marked with " + mark + ": " + indices);
    return "ABC".substring(indices.get(0), indices.get(0) + 1);
  }

  @Nested
  @DisplayName("When the indicator is better when higher")
  class WhenTheIndicatorIsBetterWhenHigher {
    @Test
    @DisplayName("given positive values, then the highest is the best and the next the second")
    void givenPositiveValues_whenGenerating_thenTheHighestIsTheBest() throws Exception {
      // Arrange
      writeValues("A", "HV", 0.5, 0.5, 0.5);
      writeValues("B", "HV", 0.7, 0.7, 0.7);
      writeValues("C", "HV", 0.6, 0.6, 0.6);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new PISAHypervolume())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("B", marked(cells, BEST));
      assertEquals("C", marked(cells, SECOND_BEST));
    }

    @Test
    @DisplayName("given values that are all zero, then an algorithm is still marked as the best")
    void givenAllZeroValues_whenGenerating_thenAnAlgorithmIsMarkedAsTheBest() throws Exception {
      // Arrange
      for (String algorithm : List.of("A", "B", "C")) {
        writeValues(algorithm, "HV", 0.0, 0.0, 0.0);
      }

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new PISAHypervolume())).run();

      // Assert: with the same value and dispersion, the first one is the best and the next second
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("A", marked(cells, BEST));
      assertEquals("B", marked(cells, SECOND_BEST));
    }

    @Test
    @DisplayName("given negative values, then the highest, the closest to zero, is the best")
    void givenNegativeValues_whenGenerating_thenTheClosestToZeroIsTheBest() throws Exception {
      // Arrange
      writeValues("A", "HIGHER", -0.5, -0.5, -0.5);
      writeValues("B", "HIGHER", -0.2, -0.2, -0.2);
      writeValues("C", "HIGHER", -0.9, -0.9, -0.9);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new HigherIsBetterIndicator())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("B", marked(cells, BEST));
      assertEquals("A", marked(cells, SECOND_BEST));
    }

    @Test
    @DisplayName("given the same mean, then the lower dispersion is the best")
    void givenTheSameMean_whenGenerating_thenTheLowerDispersionIsTheBest() throws Exception {
      // Arrange: the means are 0.5, and the standard deviations are 0.2, 0.1 and 0.3
      writeValues("A", "HV", 0.3, 0.5, 0.7);
      writeValues("B", "HV", 0.4, 0.5, 0.6);
      writeValues("C", "HV", 0.2, 0.5, 0.8);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new PISAHypervolume())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("B", marked(cells, BEST));
      assertEquals("A", marked(cells, SECOND_BEST));
    }
  }

  @Nested
  @DisplayName("When the indicator is better when lower")
  class WhenTheIndicatorIsBetterWhenLower {
    @Test
    @DisplayName("given values, then the lowest is the best and the next the second")
    void givenValues_whenGenerating_thenTheLowestIsTheBest() throws Exception {
      // Arrange
      writeValues("A", "EP", 0.5, 0.5, 0.5);
      writeValues("B", "EP", 0.1, 0.1, 0.1);
      writeValues("C", "EP", 0.3, 0.3, 0.3);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new Epsilon())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("B", marked(cells, BEST));
      assertEquals("C", marked(cells, SECOND_BEST));
    }

    @Test
    @DisplayName("given negative values, then the lowest is the best")
    void givenNegativeValues_whenGenerating_thenTheLowestIsTheBest() throws Exception {
      // Arrange
      writeValues("A", "EP", -0.5, -0.5, -0.5);
      writeValues("B", "EP", -0.9, -0.9, -0.9);
      writeValues("C", "EP", 0.2, 0.2, 0.2);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new Epsilon())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertEquals("B", marked(cells, BEST));
      assertEquals("A", marked(cells, SECOND_BEST));
    }
  }

  @Nested
  @DisplayName("When writing the tables")
  class WhenWritingTheTables {
    @Test
    @DisplayName("given the values, then the table has the mean in scientific notation")
    void givenTheValues_whenGenerating_thenTheTableHasTheMeanAndTheDeviation() throws Exception {
      // Arrange
      writeValues("A", "HV", 0.25, 0.25, 0.25);
      writeValues("B", "HV", 0.5, 0.5, 0.5);
      writeValues("C", "HV", 1.0, 1.0, 1.0);

      // Act
      new GenerateLatexTablesWithStatistics(experiment(new PISAHypervolume())).run();

      // Assert
      List<String> cells = cellsOfTheFirstTable();
      assertTrue(cells.get(0).contains("2.50e-01"));
      assertTrue(cells.get(2).contains("1.00e+00"));
      assertFalse(cells.get(0).contains(BEST));
    }
  }
}
