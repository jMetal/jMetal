package org.uma.jmetal.experimental.auto.algorithm.mopso;

import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSODTLZ {

    public static void main(String[] args) {
        String referenceFrontFileName = "DTLZ7.2D.csv";

        String[] parameters =
                ("--problemName org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7_2D "
                        + "--referenceFrontFileName "
                        + referenceFrontFileName
                        + " "
                        + "--maximumNumberOfEvaluations 25000 "
                        + "--swarmSize 59 "
                        + "--archiveSize 100 "
                        + "--swarmInitialization random "
                        + "--velocityInitialization defaultVelocityInitialization "
                        + "--externalArchive crowdingDistanceArchive "
                        + "--localBestInitialization defaultLocalBestInitialization "
                        + "--globalBestInitialization defaultGlobalBestInitialization "
                        + "--globalBestSelection binaryTournament "
                        + "--perturbation frequencySelectionMutationBasedPerturbation "
                        + "--frequencyOfApplicationOfMutationOperator 7 "
                        + "--mutation polynomial "
                        + "--mutationProbability 0.0152 "
                        + "--mutationRepairStrategy round "
                        + "--polynomialMutationDistributionIndex 285.3716 "
                        + "--positionUpdate defaultPositionUpdate "
                        + "--globalBestUpdate defaultGlobalBestUpdate "
                        + "--localBestUpdate defaultLocalBestUpdate "
                        + "--velocityUpdate constrainedVelocityUpdate "
                        + "--c1Min 1.7446 "
                        + "--c1Max 2.0257 "
                        + "--c2Min 1.9815 "
                        + "--c2Max 2.1373 "
                        + "--wMin 0.3580 "
                        + "--wMax 1.0000 ")
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
