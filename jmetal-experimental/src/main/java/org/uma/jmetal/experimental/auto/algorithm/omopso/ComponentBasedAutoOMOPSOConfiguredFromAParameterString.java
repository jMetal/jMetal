package org.uma.jmetal.experimental.auto.algorithm.omopso;


/**
 * @author Daniel Doblas
 */
public class ComponentBasedAutoOMOPSOConfiguredFromAParameterString {
    public static void main(String[] args){
        String referenceFrontFileName = "ZDT1.csv";

        String[] parameters = ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
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
                + "--perturbation mutationBasedPerturbation "
                + "--mutation polynomial "
                + "--mutationProbability 0.01 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 "
                + "--positionUpdate defaultPositionUpdate "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--velocityUpdate defaultVelocityUpdate "
                + "--c1Min 1.0 "
                + "--c1Max 2.0 "
                + "--c2Min 1.0 "
                + "--c2Max 2.0 "
                + "--wMin 0.1 "
                + "--wMax 0.5 "
        )
                .split("\\s+");
    }

    AutoOMOPSO OMOPSO = new AutoOMOPSO();

}
