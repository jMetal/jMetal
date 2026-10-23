package org.uma.jmetal.problem.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM01;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM02;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM03;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM04;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM05;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM06;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM07;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM08;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM09;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM10;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM11;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM12;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM13;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM14;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM15;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM16;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM18;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM19;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM20;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM21;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM22;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM23;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM24;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM25;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM26;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM27;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM28;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM29;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM30;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM31;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM32;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM33;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM34;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM35;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM36;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM37;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM38;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM39;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM40;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM41;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM42;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM43;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM44;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM45;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM46;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM47;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM48;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM49;
import org.uma.jmetal.problem.multiobjective.cec2021.RCM50;
import org.uma.jmetal.problem.multiobjective.cre.CRE21;
import org.uma.jmetal.problem.multiobjective.cre.CRE22;
import org.uma.jmetal.problem.multiobjective.cre.CRE23;
import org.uma.jmetal.problem.multiobjective.cre.CRE24;
import org.uma.jmetal.problem.multiobjective.cre.CRE25;
import org.uma.jmetal.problem.multiobjective.cre.CRE31;
import org.uma.jmetal.problem.multiobjective.cre.CRE32;
import org.uma.jmetal.problem.multiobjective.cre.CRE51;
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
          Map.entry("RCM01", RCM01::new),
          Map.entry("RCM02", RCM02::new),
          Map.entry("RCM03", RCM03::new),
          Map.entry("RCM04", RCM04::new),
          Map.entry("RCM05", RCM05::new),
          Map.entry("RCM06", RCM06::new),
          Map.entry("RCM07", RCM07::new),
          Map.entry("RCM08", RCM08::new),
          Map.entry("RCM09", RCM09::new),
          Map.entry("RCM10", RCM10::new),
          Map.entry("RCM11", RCM11::new),
          Map.entry("RCM12", RCM12::new),
          Map.entry("RCM13", RCM13::new),
          Map.entry("RCM14", RCM14::new),
          Map.entry("RCM15", RCM15::new),
          Map.entry("RCM16", RCM16::new),
          Map.entry("RCM18", RCM18::new),
          Map.entry("RCM19", RCM19::new),
          Map.entry("RCM20", RCM20::new),
          Map.entry("RCM21", RCM21::new),
          Map.entry("RCM22", RCM22::new),
          Map.entry("RCM23", RCM23::new),
          Map.entry("RCM24", RCM24::new),
          Map.entry("RCM25", RCM25::new),
          Map.entry("RCM26", RCM26::new),
          Map.entry("RCM27", RCM27::new),
          Map.entry("RCM28", RCM28::new),
          Map.entry("RCM29", RCM29::new),
          Map.entry("RCM30", RCM30::new),
          Map.entry("RCM31", RCM31::new),
          Map.entry("RCM32", RCM32::new),
          Map.entry("RCM33", RCM33::new),
          Map.entry("RCM34", RCM34::new),
          Map.entry("RCM35", RCM35::new),
          Map.entry("RCM36", RCM36::new),
          Map.entry("RCM37", RCM37::new),
          Map.entry("RCM38", RCM38::new),
          Map.entry("RCM39", RCM39::new),
          Map.entry("RCM40", RCM40::new),
          Map.entry("RCM41", RCM41::new),
          Map.entry("RCM42", RCM42::new),
          Map.entry("RCM43", RCM43::new),
          Map.entry("RCM44", RCM44::new),
          Map.entry("RCM45", RCM45::new),
          Map.entry("RCM46", RCM46::new),
          Map.entry("RCM47", RCM47::new),
          Map.entry("RCM48", RCM48::new),
          Map.entry("RCM49", RCM49::new),
          Map.entry("RCM50", RCM50::new));

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

  /** Whether a point of the front is the given objective vector, with a relative tolerance. */
  private static boolean isThePoint(double[] point, double[] objectives) {
    for (int j = 0; j < objectives.length; j++) {
      if (Math.abs(point[j] - objectives[j]) > 1e-9 * Math.max(1.0, Math.abs(point[j]))) {
        return false;
      }
    }
    return true;
  }

  private static boolean dominates(double[] a, double[] b) {
    boolean noWorse = true;
    boolean better = false;
    for (int j = 0; j < a.length; j++) {
      noWorse &= a[j] <= b[j];
      better |= a[j] < b[j];
    }
    return noWorse && better;
  }

  @Nested
  @DisplayName("When evaluating the sampled solutions of a front")
  class WhenEvaluatingTheSampledSolutions {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.RealWorldReferenceFrontsTest#problems")
    @DisplayName("given a sample, when evaluated, then each solution is feasible and in the front")
    void givenASample_whenEvaluated_thenEachSolutionIsFeasibleAndGivesAPointOfTheFront(
        String name) throws IOException {
      // Arrange
      DoubleProblem problem = PROBLEMS.get(name).get();
      List<double[]> samples = rows(SAMPLES.resolve(name + ".csv"), true);
      List<double[]> front = rows(FRONTS.resolve(name + ".csv"), false);
      assertThat(samples).as("samples of " + name).isNotEmpty();

      for (int row = 0; row < samples.size(); row++) {
        double[] values = samples.get(row);
        DoubleSolution solution = problem.createSolution();
        for (int i = 0; i < problem.numberOfVariables(); i++) {
          solution.variables().set(i, values[i]);
        }

        // Act
        problem.evaluate(solution);

        // Assert
        double[] objectives = solution.objectives();
        assertThat(ConstraintHandling.isFeasible(solution))
            .as(name + " row " + row + " is feasible")
            .isTrue();
        assertThat(front.stream().anyMatch(point -> isThePoint(point, objectives)))
            .as(name + " row " + row + " gives " + Arrays.toString(objectives)
                + ", a point of the front")
            .isTrue();
      }
    }
  }

  @Nested
  @DisplayName("When checking the points of a front")
  class WhenCheckingThePointsOfTheFront {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.RealWorldReferenceFrontsTest#problems")
    @DisplayName("given a front, when comparing its points, then none dominates another")
    void givenAFront_whenComparingItsPoints_thenNoneDominatesAnother(String name)
        throws IOException {
      // Arrange
      List<double[]> front = rows(FRONTS.resolve(name + ".csv"), false);
      assertThat(front).as("front of " + name).isNotEmpty();

      // Act
      List<String> dominatedPairs = new ArrayList<>();
      for (double[] a : front) {
        for (double[] b : front) {
          if (dominates(a, b)) {
            dominatedPairs.add(Arrays.toString(a) + " dominates " + Arrays.toString(b));
          }
        }
      }

      // Assert
      assertThat(dominatedPairs).isEmpty();
    }
  }
}
