package org.uma.jmetal.problem.multiobjective;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cec2021.*;
import org.uma.jmetal.problem.multiobjective.cre.*;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * The reference fronts of the constrained real-world problems, CRE and RCM (CEC 2021), in
 * {@code resources/referenceFronts/NAME.csv}. Their Pareto fronts are not known: the fronts are the
 * non-dominated feasible solutions of many runs. For each front, {@code
 * src/test/resources/realworld/NAME.csv} has a sample of 100 of its points (the extreme ones
 * included) with the solutions that give them (variables, then objectives): each solution must be
 * feasible and give its point, and the front must not contain dominated points. RCM17 has no
 * front, because its first objective has no lower bound in the feasible region.
 */
@DisplayName("Reference fronts of the constrained real-world problems")
class RealWorldReferenceFrontsTest {

  private static final Path FRONTS = Path.of("../resources/referenceFronts");
  private static final Path SAMPLES = Path.of("src/test/resources/realworld");

  private static final Map<String, Supplier<DoubleProblem>> PROBLEMS =
      Map.ofEntries(
          Map.entry("CRE21", CRE21::new),
          Map.entry("CRE22", CRE22::new),
          Map.entry("CRE23", CRE23::new),
          Map.entry("CRE24", CRE24::new),
          Map.entry("CRE25", CRE25::new),
          Map.entry("CRE31", CRE31::new),
          Map.entry("CRE32", CRE32::new),
          Map.entry("CRE51", CRE51::new),
          Map.entry("RCM01", RCM01PressureVesselProblem::new),
          Map.entry("RCM02", RCM02VibratingPlatformProblem::new),
          Map.entry("RCM03", RCM03TwoBarTrussDesignProblem::new),
          Map.entry("RCM04", RCM04WeldedBeamDesignProblem::new),
          Map.entry("RCM05", RCM05DiscBrakeDesignProblem::new),
          Map.entry("RCM06", RCM06SpeedReducerDesignProblem::new),
          Map.entry("RCM07", RCM07GearTrainDesignProblem::new),
          Map.entry("RCM08", RCM08CarSideImpactDesignProblem::new),
          Map.entry("RCM09", RCM09FourBarPlaneTrussProblem::new),
          Map.entry("RCM10", RCM10TwoBarPlaneTrussProblem::new),
          Map.entry("RCM11", RCM11WaterResourceManagementProblem::new),
          Map.entry("RCM12", RCM12SimplySupportedIBeamDesignProblem::new),
          Map.entry("RCM13", RCM13GearBoxDesignProblem::new),
          Map.entry("RCM14", RCM14MultipleDiskClutchBrakeDesignProblem::new),
          Map.entry("RCM15", RCM15SpringDesignProblem::new),
          Map.entry("RCM16", RCM16CantileverBeamDesignProblem::new),
          Map.entry("RCM18", RCM18FrontRailDesignProblem::new),
          Map.entry("RCM19", RCM19MultiProductBatchPlantProblem::new),
          Map.entry("RCM20", RCM20HydrostaticThrustBearingDesignProblem::new),
          Map.entry("RCM21", RCM21CrashEnergyManagementProblem::new),
          Map.entry("RCM22", RCM22HaverlyPoolingProblem::new),
          Map.entry("RCM23", RCM23ReactorNetworkDesignProblem::new),
          Map.entry("RCM24", RCM24HeatExchangerNetworkDesignProblem::new),
          Map.entry("RCM25", RCM25ProcessSynthesisProblem::new),
          Map.entry("RCM26", RCM26ProcessSynthesisAndDesignProblem::new),
          Map.entry("RCM27", RCM27ProcessFlowSheetingProblem::new),
          Map.entry("RCM28", RCM28TwoReactorProblem::new),
          Map.entry("RCM29", RCM29ProcessSynthesisProblem2::new),
          Map.entry("RCM30", RCM30SOPWMFor3LevelInvertersProblem::new),
          Map.entry("RCM31", RCM31SOPWMFor5LevelInvertersProblem::new),
          Map.entry("RCM32", RCM32SOPWMFor7LevelInvertersProblem::new),
          Map.entry("RCM33", RCM33SOPWMFor9LevelInvertersProblem::new),
          Map.entry("RCM34", RCM34SOPWMFor11LevelInvertersProblem::new),
          Map.entry("RCM35", RCM35SOPWMFor13LevelInvertersProblem::new),
          Map.entry("RCM36", RCM36DistributedGenerationSizingPhaseBalanceActiveLossProblem::new),
          Map.entry("RCM37", RCM37DistributedGenerationSizingPhaseBalanceReactiveLossProblem::new),
          Map.entry("RCM38", RCM38DistributedGenerationSizingActiveReactiveLossProblem::new),
          Map.entry("RCM39", RCM39DistributedGenerationSizingPhaseBalanceActiveReactiveLossProblem::new),
          Map.entry("RCM40", RCM40OptimalPowerFlowActiveReactiveLossProblem::new),
          Map.entry("RCM41", RCM41OptimalPowerFlowVoltageDeviationActiveReactiveLossProblem::new),
          Map.entry("RCM42", RCM42OptimalPowerFlowVoltageDeviationActiveLossProblem::new),
          Map.entry("RCM43", RCM43OptimalPowerFlowFuelCostActiveLossProblem::new),
          Map.entry("RCM44", RCM44OptimalPowerFlowFuelCostActiveReactiveLossProblem::new),
          Map.entry("RCM45", RCM45OptimalPowerFlowFuelCostVoltageDeviationActiveLossProblem::new),
          Map.entry("RCM46", RCM46OptimalPowerFlowFuelCostVoltageDeviationActiveReactiveLossProblem::new),
          Map.entry("RCM47", RCM47IslandedMicrogridDroopControlActiveReactiveLossProblem::new),
          Map.entry("RCM48", RCM48IslandedMicrogridDroopControlActiveLossVoltageDeviationProblem::new),
          Map.entry("RCM49", RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem::new),
          Map.entry("RCM50", RCM50PowerDistributionSystemPlanningProblem::new));

  static Stream<String> problems() {
    return PROBLEMS.keySet().stream().sorted();
  }

  private static List<double[]> rows(Path path, boolean header) throws IOException {
    List<double[]> rows = new ArrayList<>();
    List<String> lines = Files.readAllLines(path);
    for (String line : header ? lines.subList(1, lines.size()) : lines) {
      if (!line.isBlank()) {
        rows.add(Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray());
      }
    }
    return rows;
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("problems")
  @DisplayName("each sampled solution is feasible and gives its point of the front")
  void eachSampledSolutionIsFeasibleAndGivesItsPointOfTheFront(String name) throws IOException {
    DoubleProblem problem = PROBLEMS.get(name).get();
    int n = problem.numberOfVariables();
    int m = problem.numberOfObjectives();
    List<double[]> samples = rows(SAMPLES.resolve(name + ".csv"), true);
    List<double[]> front = rows(FRONTS.resolve(name + ".csv"), false);

    assertFalse(samples.isEmpty(), "no samples");
    for (int row = 0; row < samples.size(); row++) {
      double[] values = samples.get(row);
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < n; i++) {
        solution.variables().set(i, values[i]);
      }
      problem.evaluate(solution);
      assertTrue(ConstraintHandling.isFeasible(solution), name + " row " + row + " is infeasible");
      double[] objectives = solution.objectives();
      boolean inTheFront =
          front.stream()
              .anyMatch(
                  point -> {
                    for (int j = 0; j < m; j++) {
                      if (Math.abs(point[j] - objectives[j])
                          > 1e-9 * Math.max(1.0, Math.abs(point[j]))) {
                        return false;
                      }
                    }
                    return true;
                  });
      assertTrue(
          inTheFront,
          name + " row " + row + " gives " + Arrays.toString(objectives)
              + ", which is not a point of the front");
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("problems")
  @DisplayName("no point of the front dominates another")
  void noPointOfTheFrontDominatesAnother(String name) throws IOException {
    List<double[]> front = rows(FRONTS.resolve(name + ".csv"), false);

    assertFalse(front.isEmpty(), "empty front");
    for (double[] a : front) {
      for (double[] b : front) {
        boolean noWorse = true;
        boolean better = false;
        for (int j = 0; j < a.length; j++) {
          noWorse &= a[j] <= b[j];
          better |= a[j] < b[j];
        }
        assertFalse(noWorse && better, Arrays.toString(a) + " dominates " + Arrays.toString(b));
      }
    }
  }
}
