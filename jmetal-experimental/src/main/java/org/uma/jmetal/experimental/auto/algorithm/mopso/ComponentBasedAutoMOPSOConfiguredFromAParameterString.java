package org.uma.jmetal.experimental.auto.algorithm.mopso;

import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOConfiguredFromAParameterString {

  public static void main(String[] args) {
    String referenceFrontFileName = "LSMOP1.2D.csv";

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.lsmop.LSMOP1_2_20 "
                + "--referenceFrontFileName "
                + referenceFrontFileName
                + " "
                + "--maximumNumberOfEvaluations 200000 "
                + "--swarmSize 71 "
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
                + "--mutationProbability 0.0245 "
                + "--mutationRepairStrategy round "
                + "--polynomialMutationDistributionIndex 17.19 "
                + "--positionUpdate defaultPositionUpdate "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--velocityUpdate defaultVelocityUpdate "
                + "--c1Min 1.6495 "
                + "--c1Max 2.779 "
                + "--c2Min 1.0244 "
                + "--c2Max 2.0143 "
                + "--wMin 0.1278  "
                + "--wMax 0.1337 ")
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
