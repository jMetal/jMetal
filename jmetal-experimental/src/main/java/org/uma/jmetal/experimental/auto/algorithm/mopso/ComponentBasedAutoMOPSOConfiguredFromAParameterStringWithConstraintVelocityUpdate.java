package org.uma.jmetal.experimental.auto.algorithm.mopso;


import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.nsgaii.AutoNSGAII;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoMOPSOConfiguredFromAParameterStringWithConstraintVelocityUpdate {

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
        + "--localBestInitialization defaultLocalBestInitialiation "
        + "--globalBestInitialization defaultGlobalBestInitialization "
        + "--selection tournament "
        + "--selectionTournamentSize 2 "
        + "--perturbation frequencySelectionMutationBasedPerturbation "
        + "--frequencyOfApplicationOfMutationOperator 6 "
        + "--mutation polynomial "
        + "--mutationProbability 0.01 "
        + "--mutationRepairStrategy bounds "
        + "--polynomialMutationDistributionIndex 20.0 "
        + "--positionUpdate defaultPositionUpdate "
        + "--globalBestUpdate defaultGlobalBestUpdate "
        + "--localBestUpdate defaultLocalBestUpdate "
        + "--velocityUpdate constrainedVelocityUpdate "
        + "--c1Min 1.0 "
        + "--c1Max 2.0 "
        + "--c2Min 1.0 "
        + "--c2Max 2.0 "
        + "--wMin 0.1 "
        + "--wMax 0.5 "
    )
        .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    AutoNSGAII.print(autoMOPSO.fixedParameterList);
    AutoNSGAII.print(autoMOPSO.autoConfigurableParameterList);

    ParticleSwarmOptimizationAlgorithm mopso = autoMOPSO.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "NSGA-II", 80, "resources/referenceFrontsCSV/" + referenceFrontFileName);
    //WriteSolutionsToFilesObserver writeSolutionsToFilesObserver = new WriteSolutionsToFilesObserver() ;

    //mopso.getObservable().register(evaluationObserver);
    mopso.getObservable().register(runTimeChartObserver);
    //nsgaII.getObservable().register(writeSolutionsToFilesObserver);

    mopso.run();

  }
}
