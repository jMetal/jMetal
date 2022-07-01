package org.uma.jmetal.experimental.auto.experiments.escalability;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.IOException;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.termination.impl.TerminationByQualityIndicator;

/**
 * @author Daniel Doblas
 */
public class AMOPSO {

  public static void main(String[] args) throws IOException {
    var problem = new ZDT4(4096) ;
    String referenceFrontFileName = "resources/referenceFrontsCSV/ZDT4.csv";

    String[] parameters =
        ("--problemName " + problem.getClass().getName() + " "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--swarmSize 22 " +
            "--archiveSize 100 " +
            "--externalArchive hypervolumeArchive " +
            "--swarmInitialization random " +
            "--velocityInitialization defaultVelocityInitialization " +
            "--perturbation frequencySelectionMutationBasedPerturbation " +
            "--mutation uniform " +
            "--mutationProbabilityFactor 0.0638 " +
            "--mutationRepairStrategy random " +
            "--uniformMutationPerturbation 0.6053 " +
            "--frequencyOfApplicationOfMutationOperator 8 " +
            "--inertiaWeightComputingStrategy linearIncreasingValue " +
            "--weightMin 0.1941 " +
            "--weightMax 0.8233 " +
            "--velocityUpdate constrainedVelocityUpdate " +
            "--c1Min 1.7276 " +
            "--c1Max 2.4922 " +
            "--c2Min 1.3172 " +
            "--c2Max 2.1464 " +
            "--localBestInitialization defaultLocalBestInitialization " +
            "--globalBestInitialization defaultGlobalBestInitialization " +
            "--globalBestSelection binaryTournament " +
            "--globalBestUpdate defaultGlobalBestUpdate " +
            "--localBestUpdate defaultLocalBestUpdate " +
            "--positionUpdate defaultPositionUpdate " +
            "--velocityChangeWhenLowerLimitIsReached 0.1808 " +
            "--velocityChangeWhenUpperLimitIsReached -0.3183"
        )
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO() {
      @Override protected Problem<DoubleSolution> getProblem() {
        return problem ;
      }
    };

    autoMOPSO.parseAndCheckParameters(parameters);

    AutoMOPSO.print(autoMOPSO.fixedParameterList);
    AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

    ParticleSwarmOptimizationAlgorithm mopso = autoMOPSO.create();
    mopso.setTermination(new TerminationByQualityIndicator(new PISAHypervolume(),
        readVectors(referenceFrontFileName, ","),
        0.95, 100000000));

    mopso.run();

    System.out.println("Total computing time: " + mopso.getTotalComputingTime());
    System.out.println("Evaluations: " + mopso.getNumberOfEvaluations());
    System.out.println("Problem: " + problem.getName()) ;
    System.out.println("Number of variables: " + problem.getNumberOfVariables()) ;

    new SolutionListOutput(mopso.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

  }
}
