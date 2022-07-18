package org.uma.jmetal.experimental.auto.algorithm.mopso;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOConfiguredFromAParameterString {

  public static void main(String[] args) {
    String referenceFrontFileName = "ZDT1.csv";

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--referenceFrontFileName "
            + referenceFrontFileName
            + " "
            + "--maximumNumberOfEvaluations 20000 "
            + "--swarmSize 71 "
            + "--archiveSize 100 "
            + "--swarmInitialization scatterSearch "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--externalArchive hypervolumeArchive "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--frequencyOfApplicationOfMutationOperator 1 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy round "
            + "--polynomialMutationDistributionIndex 17.19 "
            + "--positionUpdate defaultPositionUpdate "
            + "--velocityChangeWhenLowerLimitIsReached -1.0 "
            + "--velocityChangeWhenUpperLimitIsReached -1.0 "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--inertiaWeightComputingStrategy randomSelectedValue "
            + "--c1Min 1.6495 "
            + "--c1Max 2.779 "
            + "--c2Min 1.0244 "
            + "--c2Max 2.0143 "
            + "--weightMin 0.1  "
            + "--weightMax 0.5 ")
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    AutoMOPSO.print(autoMOPSO.fixedParameterList);
    AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

    @NotNull ParticleSwarmOptimizationAlgorithm mopso = autoMOPSO.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    @NotNull RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "irace.MOPSO", 80, "resources/referenceFrontsCSV/" + referenceFrontFileName);

    mopso.getObservable().register(runTimeChartObserver);

    mopso.run();
  }
}
