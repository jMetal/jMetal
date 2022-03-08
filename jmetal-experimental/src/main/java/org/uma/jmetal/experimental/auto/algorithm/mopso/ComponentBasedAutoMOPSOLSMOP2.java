package org.uma.jmetal.experimental.auto.algorithm.mopso;

import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOLSMOP2 {

    public static void main(String[] args) {
        String referenceFrontFileName = "LSMOP1.2D.csv";

        String[] parameters =
                ("--problemName org.uma.jmetal.problem.multiobjective.lsmop.LSMOP2_2_200 "
                        + "--referenceFrontFileName "
                        + referenceFrontFileName
                        + " "
                        + "--maximumNumberOfEvaluations 200000 "
                        + "--swarmSize 88 "
                        + "--archiveSize 100 "
                        + "--swarmInitialization scatterSearch "
                        + "--velocityInitialization defaultVelocityInitialization "
                        + "--externalArchive crowdingDistanceArchive "
                        + "--localBestInitialization defaultLocalBestInitialization "
                        + "--globalBestInitialization defaultGlobalBestInitialization "
                        + "--globalBestSelection binaryTournament "
                        + "--perturbation frequencySelectionMutationBasedPerturbation "
                        + "--frequencyOfApplicationOfMutationOperator 1 "
                        + "--mutation polynomial "
                        + "--mutationProbability 0.5754 "
                        + "--mutationRepairStrategy round "
                        + "--polynomialMutationDistributionIndex 115.3242 "
                        + "--positionUpdate defaultPositionUpdate "
                        + "--globalBestUpdate defaultGlobalBestUpdate "
                        + "--localBestUpdate defaultLocalBestUpdate "
                        + "--velocityUpdate constrainedVelocityUpdate "
                        + "--c1Min 1.5546 "
                        + "--c1Max 2.1638 "
                        + "--c2Min 1.1976 "
                        + "--c2Max 2.0482 "
                        + "--wMin 0.1552  "
                        + "--wMax 0.1222 ")
                        .split("\\s+");

        AutoMOPSO autoMOPSO = new AutoMOPSO();
        autoMOPSO.parseAndCheckParameters(parameters);

        AutoMOPSO.print(autoMOPSO.fixedParameterList);
        AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

        ParticleSwarmOptimizationAlgorithm mopso = autoMOPSO.create();

        EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
        RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
                new RunTimeChartObserver<>(
                        "irace.MOPSO", 80, "resources/referenceFrontsCSV/" + referenceFrontFileName);

        mopso.getObservable().register(runTimeChartObserver);

        mopso.run();
    }
}
