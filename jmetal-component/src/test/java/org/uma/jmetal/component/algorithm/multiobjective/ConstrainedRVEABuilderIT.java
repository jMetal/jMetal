package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Integration tests for RVEA (C-RVEA) on classic problems with constraints")
class ConstrainedRVEABuilderIT {
  private static final String REFERENCE_FRONTS_DIRECTORY = "../resources/referenceFronts/";
  private static final int POPULATION_SIZE = 100;
  private static final int MAX_EVALUATIONS = 25000;

  @BeforeEach
  void setUp() {
    // The results of the runs are random: a fixed seed keeps the thresholds from failing by chance
    JMetalRandom.getInstance().setSeed(12);
  }

  private static EvolutionaryAlgorithm<DoubleSolution> rvea(DoubleProblem problem) {
    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    return new RVEABuilder<>(
            problem,
            POPULATION_SIZE,
            MAX_EVALUATIONS,
            crossover,
            mutation,
            2.0,
            0.1,
            POPULATION_SIZE - 1)
        .build();
  }

  /** Hypervolume of the front relative to that of the reference front, both normalized. */
  private static double relativeHypervolume(List<DoubleSolution> front, String problemName)
      throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors(REFERENCE_FRONTS_DIRECTORY + problemName + ".csv", ",");
    double[] minimum = NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront);
    double[] maximum = NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront);
    double[][] normalizedReferenceFront =
        NormalizeUtils.normalize(referenceFront, minimum, maximum);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(front), minimum, maximum);

    var hypervolume = new PISAHypervolume(normalizedReferenceFront);
    return hypervolume.compute(normalizedFront) / hypervolume.compute(normalizedReferenceFront);
  }

  @ParameterizedTest(name = "{0}")
  // Thresholds a margin below the values obtained with seed 12: 0.90, 0.92, 0.99, 0.88 and 0.74
  @CsvSource({
    "Srinivas, 0.85",
    "ConstrEx, 0.85",
    "Golinski, 0.95",
    "Tanaka, 0.80",
    "Osyczka2, 0.65"
  })
  @DisplayName(
      "given a classic problem with constraints, when running RVEA, then the result is feasible"
          + " and its relative hypervolume reaches the threshold")
  void givenAClassicConstrainedProblem_whenRunningRVEA_thenTheResultIsFeasibleAndGood(
      String problemName, double threshold) throws IOException {
    // Arrange
    var problem =
        (DoubleProblem)
            ProblemFactory.<DoubleSolution>loadProblem(
                "org.uma.jmetal.problem.multiobjective." + problemName);
    var algorithm = rvea(problem);

    // Act
    algorithm.run();
    List<DoubleSolution> result = algorithm.result();

    // Assert
    assertThat(result).isNotEmpty().allMatch(ConstraintHandling::isFeasible);
    double hypervolume = relativeHypervolume(result, problemName);
    JMetalLogger.logger.info(
        problemName + ": " + result.size() + " solutions, relative HV = " + hypervolume);
    assertThat(hypervolume).isGreaterThan(threshold);
  }
}
