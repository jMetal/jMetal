package org.uma.jmetal.auto.autoconfigurablealgorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

@DisplayName("Integration tests for class AutoRVEA")
class AutoRVEAIT {

  @ParameterizedTest
  @CsvSource({
    "org.uma.jmetal.problem.multiobjective.zdt.ZDT1, ZDT1.csv, rvea, 0.60",
    "org.uma.jmetal.problem.multiobjective.zdt.ZDT1, ZDT1.csv, rveaStar, 0.64",
    "org.uma.jmetal.problem.multiobjective.zdt.ZDT1, ZDT1.csv, iRVEA, 0.64",
    "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2, DTLZ2.3D.csv, rvea, 0.40",
    "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2, DTLZ2.3D.csv, rveaStar, 0.40",
    "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2, DTLZ2.3D.csv, iRVEA, 0.39"
  })
  @DisplayName(
      "given the default RVEA settings, when solving a benchmark problem, then the normalized"
          + " hypervolume is above a threshold")
  void givenDefaultSettings_whenSolvingProblem_thenHypervolumeIsAboveThreshold(
      String problemName, String referenceFrontFileName, String replacement, double threshold)
      throws IOException {
    // Arrange
    String[] parameters =
        ("--problemName "
                + problemName
                + " --randomGeneratorSeed 12"
                + " --referenceFrontFileName "
                + referenceFrontFileName
                + " --maximumNumberOfEvaluations 25000"
                + " --algorithmResult population"
                + " --createInitialSolutions random"
                + " --variation crossoverAndMutationVariation"
                + " --offspringPopulationSize 100"
                + " --crossover SBX"
                + " --crossoverProbability 1.0"
                + " --crossoverRepairStrategy bounds"
                + " --sbxDistributionIndex 30.0"
                + " --mutation polynomial"
                + " --mutationProbabilityFactor 1.0"
                + " --mutationRepairStrategy bounds"
                + " --polynomialMutationDistributionIndex 20.0"
                + " --selection random"
                + " --replacement "
                + replacement
                + " --alpha 2.0"
                + " --fr 0.1"
                + " --numberOfSubregions 40"
                + " --lateStageFraction 0.8"
                + " --epsilonKappa 0.05")
            .split("\\s+");
    AutoRVEA autoRVEA = new AutoRVEA();
    autoRVEA.parse(parameters);
    EvolutionaryAlgorithm<DoubleSolution> rvea = autoRVEA.create();

    // Act
    rvea.run();
    List<DoubleSolution> population = rvea.result();

    // Assert
    double[][] referenceFront =
        VectorUtils.readVectors("../resources/referenceFrontsCSV/" + referenceFrontFileName, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));
    double hv = hypervolume.compute(normalizedFront);

    assertThat(hv).isGreaterThan(threshold);
  }
}
