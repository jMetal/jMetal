package org.uma.jmetal.experimental.auto.algorithm.mopso;


import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOConfiguredFromAParameterStringWithNonUniforMutation {

  public static void main(String[] args) {
    String referenceFrontFileName = "ZDT4.csv";

    String[] parameters = ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT4 "
        + "--referenceFrontFileName " + referenceFrontFileName + " "
        + "--maximumNumberOfEvaluations 25000 "
        + "--swarmSize 100 "
        + "--archiveSize 100 "
        + "--swarmInitialization random "
        + "--velocityInitialization defaultVelocityInitialization "
        + "--externalArchive crowdingDistanceArchive "
        + "--localBestInitialization defaultLocalBestInitialization "
        + "--globalBestInitialization defaultGlobalBestInitialization "
        + "--globalBestSelection binaryTournament "
        + "--perturbation frequencySelectionMutationBasedPerturbation "
        + "--frequencyOfApplicationOfMutationOperator 7 "
        + "--mutation nonUniform "
        + "--mutationProbability 0.01 "
        + "--mutationRepairStrategy bounds "
        + "--nonUniformMutationPerturbation 0.5 "
        + "--positionUpdate defaultPositionUpdate "
        + "--globalBestUpdate defaultGlobalBestUpdate "
        + "--localBestUpdate defaultLocalBestUpdate "
        + "--velocityUpdate constrainedVelocityUpdate "
        + "--c1Min 1.5 "
        + "--c1Max 2.5 "
        + "--c2Min 1.5 "
        + "--c2Max 2.5 "
        + "--wMin 0.1 "
        + "--wMax 0.1 "
    )
        .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    AutoMOPSO.print(autoMOPSO.fixedParameterList);
    AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

    ParticleSwarmOptimizationAlgorithm mopso = autoMOPSO.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "MOPSO With Non Uniform Mutation", 80, "resources/referenceFrontsCSV/" + referenceFrontFileName);

    mopso.getObservable().register(runTimeChartObserver);

    mopso.run();

  }
}
