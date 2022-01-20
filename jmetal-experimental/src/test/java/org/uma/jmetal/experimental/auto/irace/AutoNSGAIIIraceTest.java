package org.uma.jmetal.experimental.auto.irace;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

class AutoNSGAIIIraceTest {
  @Test
  public void shouldTheProgramRunProperlyV2() throws IOException {
    String[] arguments = ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--referenceFrontFileName ZDT1.csv "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--offspringPopulationSize 100 "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--selection tournament "
            + "--selectionTournamentSize 2 "
            + "--rankingForSelection dominanceRanking "
            + "--densityEstimatorForSelection crowdingDistance "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbability 0.01 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAIIIrace autoNSGAIIIrace = new AutoNSGAIIIrace();
    autoNSGAIIIrace.parseAndCheckParameters(arguments);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAIIIrace.create();
    nsgaII.run();

    double[][] referenceFront = VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");
    double[][] front = getMatrixWithObjectiveValues(nsgaII.getResult());

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront);
    double value = qualityIndicator.compute(normalizedFront);

    assertNotNull(nsgaII.getResult());
    assertTrue(value < 0.015);
  }

}