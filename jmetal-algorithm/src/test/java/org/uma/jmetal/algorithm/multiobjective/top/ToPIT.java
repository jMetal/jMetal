package org.uma.jmetal.algorithm.multiobjective.top;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 * Integration tests of ToP on constrained problems with reference fronts.
 *
 * <p>Each test runs ToP, with the default parameters, with the seeds 1 to n, and checks that every
 * final population is feasible and that the median IGD of the runs is below a threshold. The IGD is
 * the mean distance from the points of the reference front to the population, as in PlatEMO. The
 * thresholds come from the runs with the seeds 1 to 20, with a margin: on MW3 (25000 evaluations,
 * 15 seeds) the median IGD is 0.0129, although in 4 of the 15 runs the population collapses into a
 * single feasible point and its IGD is 0.918; on LIR-CMOP2 (100000 evaluations, 10 seeds) it is
 * 0.227 (0.153 to 0.265 in 20 runs); and on C1-DTLZ1 (100000 evaluations, 10 seeds) it is 0.0270
 * (0.0244 to 0.0289 in 20 runs).
 */
@DisplayName("Integration tests for class ToP")
class ToPIT {
  private static final String REFERENCE_FRONTS_DIRECTORY = "../resources/referenceFronts/";

  /** The quality of the final population of a run. */
  private record Run(double igd, double feasibilityRatio, int size) {}

  private static List<Run> run(
      Supplier<Problem<DoubleSolution>> problem,
      int maxEvaluations,
      int numberOfSeeds,
      String referenceFrontFile)
      throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors(REFERENCE_FRONTS_DIRECTORY + referenceFrontFile, ",");
    var igd = new InvertedGenerationalDistance(referenceFront, 1.0);

    List<Run> runs = new ArrayList<>();
    for (long seed = 1; seed <= numberOfSeeds; seed++) {
      JMetalRandom.getInstance().setSeed(seed);
      ToP algorithm = new ToPBuilder(problem.get()).setMaxEvaluations(maxEvaluations).build();
      algorithm.run();

      List<DoubleSolution> population = algorithm.result();
      List<DoubleSolution> feasible =
          population.stream().filter(ConstraintHandling::isFeasible).toList();
      double value =
          feasible.isEmpty()
              ? Double.POSITIVE_INFINITY
              : igd.compute(SolutionListUtils.getMatrixWithObjectiveValues(feasible));
      runs.add(
          new Run(value, ConstraintHandling.feasibilityRatio(population), population.size()));
    }
    return runs;
  }

  private static double medianIgd(List<Run> runs) {
    double[] values = runs.stream().mapToDouble(Run::igd).sorted().toArray();
    return values[values.length / 2];
  }

  private static void assertFeasibleAndClose(List<Run> runs, double maximumMedianIgd) {
    assertTrue(
        runs.stream().allMatch(r -> r.size() == 100 && r.feasibilityRatio() == 1.0),
        runs::toString);
    assertTrue(medianIgd(runs) < maximumMedianIgd, runs::toString);
  }

  @Test
  @DisplayName("given MW3, when running ToP, then the populations are feasible and close")
  void givenMW3_whenRunningToP_thenThePopulationsAreFeasibleAndCloseToTheFront()
      throws IOException {
    // Act
    List<Run> runs = run(MW3::new, 25000, 15, "MW3.csv");

    // Assert
    assertFeasibleAndClose(runs, 0.03);
  }

  @Test
  @DisplayName("given LIR-CMOP2, when running ToP, then the populations are feasible and close")
  void givenLIRCMOP2_whenRunningToP_thenThePopulationsAreFeasibleAndCloseToTheFront()
      throws IOException {
    // Act
    List<Run> runs = run(LIRCMOP2::new, 100000, 10, "LIRCMOP2.csv");

    // Assert
    assertFeasibleAndClose(runs, 0.30);
  }

  @Test
  @DisplayName("given C1-DTLZ1, when running ToP, then the populations are feasible and close")
  void givenC1DTLZ1_whenRunningToP_thenThePopulationsAreFeasibleAndCloseToTheFront()
      throws IOException {
    // Act
    List<Run> runs = run(C1_DTLZ1::new, 100000, 10, "C1_DTLZ1.3D.csv");

    // Assert
    assertFeasibleAndClose(runs, 0.04);
  }
}
