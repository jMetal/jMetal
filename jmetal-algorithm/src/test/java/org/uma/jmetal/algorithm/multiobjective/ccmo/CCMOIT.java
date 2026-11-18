package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMO.VariationType;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.cdtlz.C1_DTLZ1;
import org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP2;
import org.uma.jmetal.problem.multiobjective.mw.MW3;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Integration tests of CCMO on constrained problems. The IGD is the mean distance from the points
 * of the reference front to the result (as in the paper of CCMO and in PlatEMO), on the objectives
 * without normalization. Each threshold is the worst value of 10 to 15 runs with different seeds
 * plus a margin of at least 25%.
 */
@DisplayName("Integration tests for class CCMO")
class CCMOIT {

  @BeforeEach
  void setUp() {
    // The results of the runs are random: a fixed seed keeps the thresholds from failing by chance
    JMetalRandom.getInstance().setSeed(12);
  }

  private static CCMO<DoubleSolution> ccmo(
      Problem<DoubleSolution> problem, int maxEvaluations, VariationType variationType) {
    CrossoverOperator<DoubleSolution> crossover =
        variationType == VariationType.GA
            ? new SBXCrossover(1.0, 20.0)
            : new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    return new CCMOBuilder<>(problem, crossover, mutation)
        .setPopulationSize(100)
        .setMaxEvaluations(maxEvaluations)
        .setVariationType(variationType)
        .build();
  }

  private static double igd(List<DoubleSolution> front, String referenceFrontFile)
      throws IOException {
    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    return new InvertedGenerationalDistance(referenceFront, 1.0)
        .compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
  }

  private static void assertFeasibleResultOfSize(List<DoubleSolution> result, int size) {
    assertEquals(size, result.size());
    assertTrue(result.stream().allMatch(ConstraintHandling::isFeasible));
  }

  @Test
  @DisplayName(
      "given MW3 and the GA variation, when running 100000 evaluations, then the result is"
          + " feasible and close to the front")
  void givenMW3AndTheGaVariation_whenRunning_thenTheResultIsFeasibleAndCloseToTheFront()
      throws IOException {
    // Arrange
    CCMO<DoubleSolution> algorithm = ccmo(new MW3(), 100000, VariationType.GA);

    // Act
    algorithm.run();

    // Assert
    // Rationale: the IGD of 14 runs ranged from 4.55e-3 to 5.55e-3 (4.69e-3 in the paper)
    List<DoubleSolution> result = algorithm.result();
    assertFeasibleResultOfSize(result, 100);
    assertTrue(igd(result, "../resources/referenceFronts/MW3.csv") < 7.0e-3);
  }

  @Test
  @DisplayName(
      "given C1_DTLZ1 with three objectives, when running 100000 evaluations, then the result is"
          + " feasible and close to the front")
  void givenC1Dtlz1_whenRunning_thenTheResultIsFeasibleAndCloseToTheFront() throws IOException {
    // Arrange
    CCMO<DoubleSolution> algorithm = ccmo(new C1_DTLZ1(), 100000, VariationType.GA);

    // Act
    algorithm.run();

    // Assert
    // Rationale: the IGD of 10 runs ranged from 1.98e-2 to 2.02e-2 (1.99e-2 in the paper)
    List<DoubleSolution> result = algorithm.result();
    assertFeasibleResultOfSize(result, 100);
    assertTrue(igd(result, "../resources/referenceFronts/C1_DTLZ1.3D.csv") < 2.5e-2);
  }

  @Test
  @DisplayName(
      "given LIRCMOP2 and the DE variation, when running 50000 evaluations, then the result is"
          + " feasible and near the front")
  void givenLircmop2AndTheDeVariation_whenRunning_thenTheResultIsFeasibleAndNearTheFront()
      throws IOException {
    // Arrange
    CCMO<DoubleSolution> algorithm = ccmo(new LIRCMOP2(), 50000, VariationType.DE);

    // Act
    algorithm.run();

    // Assert
    // Rationale: LIR-CMOP2 has large infeasible regions; with 30 variables, the IGD of 15 runs
    // ranged from 0.084 to 0.244
    List<DoubleSolution> result = algorithm.result();
    assertFeasibleResultOfSize(result, 100);
    assertTrue(igd(result, "../resources/referenceFronts/LIRCMOP2.csv") < 0.35);
  }
}
