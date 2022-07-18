package org.uma.jmetal.experimental.auto.algorithm.mopso;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOV2WFG {

  public static void main(String[] args) {
    @NotNull String referenceFrontFileName = "WFG4.2D.csv";

    var parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.wfg.WFG4 "
            + "--referenceFrontFileName "
            + referenceFrontFileName
            + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--swarmSize 134 --archiveSize 100 --externalArchive crowdingDistanceArchive --swarmInitialization scatterSearch --velocityInitialization defaultVelocityInitialization --perturbation frequencySelectionMutationBasedPerturbation --mutation uniform --mutationProbabilityFactor 0.7024 --mutationRepairStrategy round --uniformMutationPerturbation 0.0013 --frequencyOfApplicationOfMutationOperator 9 --inertiaWeightComputingStrategy constantValue --weight 0.1128 --velocityUpdate defaultVelocityUpdate --c1Min 1.8446 --c1Max 2.4326 --c2Min 1.3979 --c2Max 2.0621 --localBestInitialization defaultLocalBestInitialization --globalBestInitialization defaultGlobalBestInitialization --globalBestSelection binaryTournament --globalBestUpdate defaultGlobalBestUpdate --localBestUpdate defaultLocalBestUpdate --positionUpdate defaultPositionUpdate --velocityChangeWhenLowerLimitIsReached -0.6967 --velocityChangeWhenUpperLimitIsReached 0.5508"
        )
            .split("\\s+");

    var autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    AutoMOPSO.print(autoMOPSO.fixedParameterList);
    AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

    var mopso = autoMOPSO.create();

    @NotNull EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    @NotNull RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "irace.MOPSO", 80, "resources/referenceFrontsCSV/" + referenceFrontFileName);

    mopso.getObservable().register(runTimeChartObserver);

    mopso.run();
  }
}
