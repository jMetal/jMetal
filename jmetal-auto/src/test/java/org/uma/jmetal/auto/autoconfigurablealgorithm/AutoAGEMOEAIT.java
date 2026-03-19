package org.uma.jmetal.auto.autoconfigurablealgorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

class AutoAGEMOEAIT {

  @Test
  void autoAGEMOEAHasSixFirstLevelConfigurableParameters() {
    assertThat(new AutoAGEMOEA().configurableParameterList()).hasSize(6);
  }

  @Test
  void autoAGEMOEAHasFiveFixedParameters() {
    assertThat(new AutoAGEMOEA().fixedParameterList()).hasSize(5);
  }

  @Test
  void autoAGEMOEAHasTwentyFlattenedConfigurableParameters() {
    assertThat(
            AutoConfigurableAlgorithm.parameterFlattening(
                new AutoAGEMOEA().configurableParameterList()))
        .hasSize(20);
  }

  @Test
  void autoAGEMOEAWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT1()
      throws IOException {
    String referenceFrontFileName = "ZDT1.csv";

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--randomGeneratorSeed 12 "
            + "--maximumNumberOfEvaluations 25000 "
            + "--populationSize 100 "
            + "--algorithmVariant agemoea "
            + "--environmentalSelection agemoea "
            + "--replacement agemoeaReplacement "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 100 "
            + "--selection tournament "
            + "--selectionTournamentSize 2 "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoAGEMOEA autoAGEMOEA = new AutoAGEMOEA();
    autoAGEMOEA.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> ageMoea = autoAGEMOEA.create();
    ageMoea.run();

    List<DoubleSolution> population = ageMoea.result();

    String referenceFrontFile = "../resources/referenceFrontsCSV/" + referenceFrontFileName;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 95);
    assertTrue(hv > 0.65);
  }
}
