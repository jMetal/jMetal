package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOPSO;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;

/**
 * Program to generate the irace configuration file for class {@link AutoMOPSO}
 * Note that the result configuration must be manually modified, replacing the lines of parameters
 * weightMin and weightMax by these:
 *
 * weightMin    "--weightMin "     r   (0.1, 0.5)      | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
 * weightMax    "--weightMax "     r    (0.5, 1.0)     | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class AutoMOPSOIraceFileGenerator {

  public static void main(String[] args) {
    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--referenceFrontFileName ZDT1.csv "
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
            + "--frequencyOfApplicationOfMutationOperator 6 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--positionUpdate defaultPositionUpdate "
            + "--velocityChangeWhenLowerLimitIsReached -1.0 "
            + "--velocityChangeWhenUpperLimitIsReached -1.0 "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--inertiaWeightComputingStrategy randomSelectedValue "
            + "--c1Min 1.0 "
            + "--c1Max 2.0 "
            + "--c2Min 1.0 "
            + "--c2Max 2.0 "
            + "--weightMin 0.1 "
            + "--weightMax 0.5 ")
            .split("\\s+");

    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator();
    parameterFileGenerator.generateConfigurationFile(new AutoMOPSO(), parameters);
  }
}
