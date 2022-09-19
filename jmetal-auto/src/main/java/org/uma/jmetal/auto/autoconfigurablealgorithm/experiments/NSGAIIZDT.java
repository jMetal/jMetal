package org.uma.jmetal.auto.autoconfigurablealgorithm.experiments;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.IOException;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByQualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class configuring NSGA-II using arguments in the form <key, value> and the {@link AutoNSGAII} class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class NSGAIIZDT {
  public static void main(String[] args) throws IOException {

    String referenceFrontFileName = "resources/referenceFrontsCSV/ZDT1.csv" ;
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1" ;

    String[] parameters =
        ("--problemName " + problemName + " "
                + "--referenceFrontFileName "+ referenceFrontFileName + " "
                + "--maximumNumberOfEvaluations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 2 "
                + "--rankingForSelection dominanceRanking "
                + "--densityEstimatorForSelection crowdingDistance "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbabilityFactor 1.0 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);

    AutoNSGAII.print(autoNSGAII.fixedParameterList);
    AutoNSGAII.print(autoNSGAII.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    TerminationByQualityIndicator termination = new TerminationByQualityIndicator(
        new PISAHypervolume(),
        readVectors(referenceFrontFileName, ","),
        0.95, 15000000);

    nsgaII.setTermination(termination);

    nsgaII.run();

    JMetalLogger.logger.info("Total computing time: " + nsgaII.getTotalComputingTime()); ;
    JMetalLogger.logger.info("Evaluations: " + nsgaII.getNumberOfEvaluations());

    new SolutionListOutput(nsgaII.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VARautoTuned.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUNautoTuned.csv", ","))
        .print();
  }
}
