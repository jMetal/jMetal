package org.uma.jmetal.algorithm.multiobjective.ctaea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cdtlz.C1_DTLZ1;
import org.uma.jmetal.problem.multiobjective.cdtlz.C3_DTLZ4;
import org.uma.jmetal.problem.multiobjective.mw.MW3;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Integration tests of C-TAEA on constrained problems, with the operators of PlatEMO by default
 * (SBX with probability 1.0 and polynomial mutation with probability 1/n, both with distribution
 * index 20) and 100 requested weight vectors.
 *
 * <p>The thresholds of IGD+ are at least 1.5 times the worst value of 10 runs with different seeds
 * (C1-DTLZ1: 0.0162-0.0176; C3-DTLZ4: 0.0525-0.0588; MW3, 30 runs: 0.0032-0.0058).
 */
@DisplayName("Integration tests for class CTAEA")
class CTAEAIT {

  @BeforeEach
  void setUp() {
    // The results of the runs are random: a fixed seed keeps the thresholds from failing by chance
    JMetalRandom.getInstance().setSeed(12);
  }

  private static List<DoubleSolution> run(DoubleProblem problem, int maxEvaluations) {
    CTAEA<DoubleSolution> algorithm =
        new CTAEABuilder<>(
                problem,
                new SBXCrossover(1.0, 20.0),
                new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
            .setPopulationSize(100)
            .setMaxEvaluations(maxEvaluations)
            .build();
    algorithm.run();
    return algorithm.result();
  }

  private static double igdPlus(List<DoubleSolution> result, String referenceFrontFile)
      throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors("../resources/referenceFronts/" + referenceFrontFile, ",");
    return new InvertedGenerationalDistancePlus(referenceFront)
        .compute(SolutionListUtils.getMatrixWithObjectiveValues(result));
  }

  private static boolean allFeasible(List<DoubleSolution> result) {
    return result.stream().allMatch(ConstraintHandling::isFeasible);
  }

  @Test
  @DisplayName("given C1-DTLZ1 with three objectives, when running, then the front is good")
  void givenC1DTLZ1_whenRunning_thenTheFrontIsFeasibleAndClose() throws IOException {
    // Act: 500 generations of 91 solutions
    List<DoubleSolution> result = run(new C1_DTLZ1(), 91 * 500);

    // Assert
    assertEquals(91, result.size());
    assertTrue(allFeasible(result));
    assertTrue(igdPlus(result, "C1_DTLZ1.3D.csv") < 0.03);
  }

  @Test
  @DisplayName("given C3-DTLZ4 with three objectives, when running, then the front is good")
  void givenC3DTLZ4_whenRunning_thenTheFrontIsFeasibleAndClose() throws IOException {
    // Act: 750 generations of 91 solutions
    List<DoubleSolution> result = run(new C3_DTLZ4(), 91 * 750);

    // Assert
    assertEquals(91, result.size());
    assertTrue(allFeasible(result));
    assertTrue(igdPlus(result, "C3_DTLZ4.3D.csv") < 0.09);
  }

  @Test
  @DisplayName("given MW3 with two objectives, when running, then the front is good")
  void givenMW3_whenRunning_thenTheFrontIsFeasibleAndClose() throws IOException {
    // Act: 300 generations of 100 solutions
    List<DoubleSolution> result = run(new MW3(), 100 * 300);

    // Assert
    assertEquals(100, result.size());
    assertTrue(allFeasible(result));
    assertTrue(igdPlus(result, "MW3.csv") < 0.01);
  }
}
