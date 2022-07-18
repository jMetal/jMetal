package org.uma.jmetal.auto.autoconfigurablealgorithm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

class AutoMOPSOIT {
  @Test
  void AutoMOPSOWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT4() throws IOException {
    var referenceFrontFileName = "ZDT4.csv";

    var parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT4 "
            + "--referenceFrontFileName "
            + referenceFrontFileName
            + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--swarmSize 100 "
            + "--archiveSize 100 "
            + "--swarmInitialization random "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--leaderArchive crowdingDistanceArchive "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--frequencyOfApplicationOfMutationOperator 7 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--positionUpdate defaultPositionUpdate "
            + "--velocityChangeWhenLowerLimitIsReached -1.0 "
            + "--velocityChangeWhenUpperLimitIsReached -1.0 "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--velocityUpdate constrainedVelocityUpdate "
            + "--inertiaWeightComputingStrategy randomSelectedValue "
            + "--c1Min 1.5 "
            + "--c1Max 2.5 "
            + "--c2Min 1.5 "
            + "--c2Max 2.5 "
            + "--weightMin 0.1 "
            + "--weightMax 0.5 ")
            .split("\\s+");

    var autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    var smpso = autoMOPSO.create() ;

    smpso.run();

    var population  = smpso.getResult() ;

    var referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 95) ;
    assertTrue(hv > 0.65) ;
  }
}