package org.uma.jmetal.auto.autoconfigurablealgorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.auto.parameter.CategoricalIntegerParameter;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class AutoRVEA")
class AutoRVEATest {
  private static final String VECTORS_2D = "../resources/weightVectorFiles/moead/W2D_300.dat";
  private static final String VECTORS_3D = "../resources/weightVectorFiles/moead/W3D_100.dat";

  private AutoRVEA subject;

  @BeforeEach
  void setUp() {
    subject = new AutoRVEA();
  }

  private static String parameters(String problemName, String specific) {
    return "--problemName "
        + problemName
        + " --randomGeneratorSeed 1"
        + " --referenceFrontFileName ZDT1.csv"
        + " --maximumNumberOfEvaluations 1000"
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
        + " --alpha 2.0"
        + " --fr 0.1 "
        + specific;
  }

  private static String[] zdt1(String specific) {
    return parameters("org.uma.jmetal.problem.multiobjective.zdt.ZDT1", specific).split("\\s+");
  }

  private static final String RVEA_DEFAULTS =
      "--algorithmResult population --selection random --replacement rvea";

  @Nested
  @DisplayName("When inspecting the parameter space")
  class ParameterSpace {
    @Test
    @DisplayName("given a new instance, when listing parameters, then there are four fixed ones")
    void givenNewInstance_whenListingFixedParameters_thenThereAreFour() {
      // Act
      var fixed = subject.fixedParameterList();

      // Assert
      assertThat(fixed).hasSize(4);
      assertThat(fixed).extracting(parameter -> parameter.name()).doesNotContain("populationSize");
    }

    @Test
    @DisplayName(
        "given a new instance, when flattening the configurable parameters, then there are 5"
            + " first-level and 25 parameters in total")
    void givenNewInstance_whenFlatteningConfigurableParameters_thenThereAre25() {
      // Act
      var configurable = subject.configurableParameterList();
      var flattened = AutoConfigurableAlgorithm.parameterFlattening(configurable);

      // Assert
      assertThat(configurable).hasSize(5);
      assertThat(flattened).hasSize(25);
      assertThat(flattened)
          .extracting(parameter -> parameter.name())
          .doesNotContain("populationSize", "populationSizeWithArchive")
          .contains("replacement", "alpha", "fr", "numberOfSubregions", "epsilonKappa");
    }
  }

  @Nested
  @DisplayName("When inspecting the offspring population size")
  class OffspringPopulationSize {
    @Test
    @DisplayName(
        "given a new instance, when reading the offspring sizes, then they range from 10 to 200")
    void givenNewInstance_whenReadingOffspringSizes_thenTheyRangeFrom10To200() {
      // Act
      var parameter =
          (CategoricalIntegerParameter)
              AutoConfigurableAlgorithm.parameterFlattening(subject.configurableParameterList())
                  .stream()
                  .filter(p -> p.name().equals("offspringPopulationSize"))
                  .findFirst()
                  .orElseThrow();

      // Assert
      assertThat(parameter.validValues()).containsExactly(10, 20, 50, 100, 150, 200);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 400})
    @DisplayName("given an excluded offspring size, when parsing, then an exception is thrown")
    void givenExcludedOffspringSize_whenParsing_thenExceptionIsThrown(int offspringSize) {
      // Arrange
      String[] arguments =
          String.join(" ", zdt1(RVEA_DEFAULTS))
              .replace("--offspringPopulationSize 100", "--offspringPopulationSize " + offspringSize)
              .split("\\s+");

      // Act and Assert
      assertThatThrownBy(() -> subject.parse(arguments)).isInstanceOf(RuntimeException.class);
    }
  }

  @Nested
  @DisplayName("When obtaining the reference vectors")
  class ReferenceVectors {
    @ParameterizedTest
    @CsvSource({"2, 100", "3, 105", "4, 120", "5, 126", "8, 156", "10, 275"})
    @DisplayName(
        "given no vectors file, when generating vectors, then their number depends on the"
            + " objectives")
    void givenNoVectorsFile_whenGeneratingVectors_thenNumberDependsOnObjectives(
        int objectives, int expectedSize) {
      // Act
      List<double[]> vectors = AutoRVEA.defaultReferenceVectors(objectives);

      // Assert
      assertThat(vectors).hasSize(expectedSize);
      assertThat(vectors).allMatch(vector -> vector.length == objectives);
    }

    @Test
    @DisplayName("given a vectors file, when obtaining vectors, then the file sets their number")
    void givenVectorsFile_whenObtainingVectors_thenFileSetsTheirNumber() {
      // Arrange
      subject.parse(zdt1(RVEA_DEFAULTS + " --referenceVectorsFile " + VECTORS_2D));

      // Act
      List<double[]> vectors = subject.referenceVectors(2);

      // Assert
      assertThat(vectors).hasSize(300);
    }

    @Test
    @DisplayName(
        "given a vectors file with a wrong dimension, when obtaining vectors, then an exception"
            + " is thrown")
    void givenVectorsFileWithWrongDimension_whenObtainingVectors_thenExceptionIsThrown() {
      // Arrange
      subject.parse(zdt1(RVEA_DEFAULTS + " --referenceVectorsFile " + VECTORS_3D));

      // Act and Assert
      assertThatThrownBy(() -> subject.referenceVectors(2))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName(
        "given a second parse without vectors file, when obtaining vectors, then the default"
            + " ones are used")
    void givenSecondParseWithoutVectorsFile_whenObtainingVectors_thenDefaultOnesAreUsed() {
      // Arrange
      subject.parse(zdt1(RVEA_DEFAULTS + " --referenceVectorsFile " + VECTORS_2D));
      subject.parse(zdt1(RVEA_DEFAULTS));

      // Act
      List<double[]> vectors = subject.referenceVectors(2);

      // Assert
      assertThat(vectors).hasSize(100);
    }
  }

  @Nested
  @DisplayName("When creating and running the algorithm")
  class Creation {
    @ParameterizedTest
    @CsvSource({
      "rvea, RVEA",
      "rveaStar, RVEA*",
      "iRVEA, iRVEA"
    })
    @DisplayName(
        "given a replacement, when creating and running, then the variant has its name and"
            + " returns at most N solutions")
    void givenReplacement_whenCreatingAndRunning_thenVariantHasItsNameAndReturnsSolutions(
        String replacement, String expectedName) {
      // Arrange
      subject.parse(
          zdt1(
              "--algorithmResult population --selection random --replacement "
                  + replacement
                  + " --numberOfSubregions 40 --lateStageFraction 0.8 --epsilonKappa 0.05"));

      // Act
      EvolutionaryAlgorithm<DoubleSolution> algorithm = subject.create();
      algorithm.run();

      // Assert
      assertThat(algorithm.name()).isEqualTo(expectedName);
      assertThat(algorithm.result()).isNotEmpty().hasSizeLessThanOrEqualTo(100);
    }

    @ParameterizedTest
    @ValueSource(strings = {"crowdingDistanceArchive", "unboundedArchive"})
    @DisplayName(
        "given an external archive, when running, then the result has at most N solutions")
    void givenExternalArchive_whenRunning_thenResultHasAtMostNSolutions(String archive) {
      // Arrange
      subject.parse(
          zdt1(
              "--algorithmResult externalArchive --externalArchive "
                  + archive
                  + " --selection random --replacement rvea"));

      // Act
      EvolutionaryAlgorithm<DoubleSolution> algorithm = subject.create();
      algorithm.run();

      // Assert
      assertThat(algorithm.result()).isNotEmpty().hasSizeLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName(
        "given a population smaller than the tournament size, when running, then random"
            + " selection is used instead")
    void givenPopulationSmallerThanTournament_whenRunning_thenRandomSelectionIsUsed(
        @TempDir Path directory) throws IOException {
      // Arrange
      Path vectorsFile = directory.resolve("W2D_3.dat");
      Files.writeString(vectorsFile, "1.0 0.0\n0.5 0.5\n0.0 1.0\n");
      subject.parse(
          zdt1(
              "--algorithmResult population --selection tournament --selectionTournamentSize 10"
                  + " --replacement rvea --referenceVectorsFile "
                  + vectorsFile));

      // Act
      EvolutionaryAlgorithm<DoubleSolution> algorithm = subject.create();
      algorithm.run();

      // Assert
      assertThat(algorithm.result()).isNotEmpty().hasSizeLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("given a constrained problem, when creating, then an exception is thrown")
    void givenConstrainedProblem_whenCreating_thenExceptionIsThrown() {
      // Arrange
      subject.parse(
          parameters("org.uma.jmetal.problem.multiobjective.Srinivas", RVEA_DEFAULTS)
              .split("\\s+"));

      // Act and Assert
      assertThatThrownBy(() -> subject.create()).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName(
        "given a budget smaller than the population, when creating, then an exception is thrown")
    void givenBudgetSmallerThanPopulation_whenCreating_thenExceptionIsThrown() {
      // Arrange
      String[] arguments =
          String.join(" ", zdt1(RVEA_DEFAULTS + " --referenceVectorsFile " + VECTORS_2D))
              .replace("--maximumNumberOfEvaluations 1000", "--maximumNumberOfEvaluations 200")
              .split("\\s+");
      subject.parse(arguments);

      // Act and Assert
      assertThatThrownBy(() -> subject.create()).isInstanceOf(InvalidConditionException.class);
    }
  }
}
