package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.aggregationfunction.impl.WeightedSum;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

@DisplayName("Unit tests for the normalization of class MOEADReplacement")
class MOEADReplacementNormalizationTest {
  private static final int SUBPROBLEMS = 11;
  private static final int NEIGHBORS = 4;
  private static final int ITERATIONS = 150;
  private static final long SEED = 7;

  /** A solution with an identifier in its variable, which its copies keep. */
  private static DoubleSolution solution(int id, double f1, double f2) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1000.0)), 2, 0);
    solution.variables().set(0, (double) id);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  private static double[] transformed(double f1, double f2, double[] transformation) {
    return new double[] {f1 * transformation[0] + transformation[2],
        f2 * transformation[1] + transformation[3]};
  }

  /**
   * Runs the replacement of MOEA/D on a fixed sequence of offspring, whose objectives are
   * transformed by scales and shifts, and returns the identifiers of the final population.
   */
  private static List<Integer> run(
      AggregationFunction aggregationFunction, boolean normalize, double[] transformation) {
    aggregationFunction.epsilon(1e-12);
    JMetalRandom.getInstance().setSeed(SEED);

    var neighborhood = new WeightVectorNeighborhood<DoubleSolution>(SUBPROBLEMS, NEIGHBORS);
    SequenceGenerator<Integer> sequence = new RandomPermutationCycle(SUBPROBLEMS);
    var selection =
        new PopulationAndNeighborhoodSelection<DoubleSolution>(
            2, sequence, neighborhood, 1.0, false);
    var replacement =
        new MOEADReplacement<DoubleSolution>(
            selection, neighborhood, aggregationFunction, sequence, 2, normalize);

    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < SUBPROBLEMS; i++) {
      double f1 = 0.1 + 0.8 * i / (SUBPROBLEMS - 1);
      double[] objectives = transformed(f1, 1.0 - f1, transformation);
      population.add(solution(i, objectives[0], objectives[1]));
    }

    for (int iteration = 0; iteration < ITERATIONS; iteration++) {
      double f1 = (iteration * 0.6180339887) % 1.0;
      double f2 = 1.15 - f1 - 0.2 * ((iteration * 0.7548776662) % 1.0);
      double[] objectives = transformed(f1, f2, transformation);
      selection.select(population);
      replacement.replace(
          population, List.of(solution(100 + iteration, objectives[0], objectives[1])));
    }

    return population.stream().map(s -> s.variables().get(0).intValue()).toList();
  }

  @Nested
  @DisplayName("When the objectives are normalized")
  class WhenTheObjectivesAreNormalized {
    @ParameterizedTest(name = "scales ({0}, {1}) and shifts ({2}, {3})")
    @CsvSource({
      "1000.0, 1.0, 0.0, 0.0",
      "1.0, 0.001, 0.0, 0.0",
      "1.0, 1.0, 100.0, -50.0",
      "1.0, 1.0, -1000.0, -7.0",
      "250.0, 0.004, -9000.0, -17.0"
    })
    @DisplayName("given scaled or translated objectives, then the subproblems get the same ones")
    void givenScaledOrTranslatedObjectives_whenReplacing_thenTheSubproblemsGetTheSameSolutions(
        double scale1, double scale2, double shift1, double shift2) {
      // Arrange
      double[] identity = {1.0, 1.0, 0.0, 0.0};
      double[] other = {scale1, scale2, shift1, shift2};

      // Act
      List<Integer> original = run(new Tschebyscheff(true), true, identity);
      List<Integer> transformed = run(new Tschebyscheff(true), true, other);

      // Assert: the offspring replaced several solutions, and the same ones in both runs
      assertThat(original).filteredOn(id -> id >= 100).hasSizeGreaterThan(3);
      assertThat(transformed).isEqualTo(original);
    }

    @ParameterizedTest(name = "scales ({0}, {1}) and shifts ({2}, {3})")
    @CsvSource({"1000.0, 1.0, 0.0, 0.0", "1.0, 1.0, -1000.0, -7.0"})
    @DisplayName("given a weighted sum, then the subproblems get the same solutions as well")
    void givenAWeightedSum_whenReplacing_thenTheSubproblemsGetTheSameSolutions(
        double scale1, double scale2, double shift1, double shift2) {
      // Arrange
      double[] identity = {1.0, 1.0, 0.0, 0.0};
      double[] other = {scale1, scale2, shift1, shift2};

      // Act
      List<Integer> original = run(new WeightedSum(true), true, identity);
      List<Integer> transformed = run(new WeightedSum(true), true, other);

      // Assert
      assertThat(original).filteredOn(id -> id >= 100).hasSizeGreaterThan(3);
      assertThat(transformed).isEqualTo(original);
    }

    @ParameterizedTest(name = "scales ({0}, {1})")
    @CsvSource({"1000.0, 1.0", "1.0, 0.001"})
    @DisplayName("given objectives of other scales without normalizing, then the solutions differ")
    void givenOtherScalesWithoutNormalizing_whenReplacing_thenTheSolutionsDiffer(
        double scale1, double scale2) {
      // Arrange
      double[] identity = {1.0, 1.0, 0.0, 0.0};
      double[] other = {scale1, scale2, 0.0, 0.0};

      // Act
      List<Integer> original = run(new Tschebyscheff(false), false, identity);
      List<Integer> scaled = run(new Tschebyscheff(false), false, other);

      // Assert
      assertThat(scaled).isNotEqualTo(original);
    }
  }
}
