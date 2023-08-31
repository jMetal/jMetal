package org.uma.jmetal.auto.autoconfigurablealgorithm.examples;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * Class configuring NSGA-II using arguments in the form <key, value> and the {@link AutoNSGAII}
 * class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class NSGAIIConfiguredFromAParameterString {

  public static void main(String[] args) {
    String referenceFrontFileName = "resources/referenceFrontsCSV/ZDT1.csv";

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--populationSize 100 "
            + "--algorithmResult population  "
            + "--createInitialSolutions random "
            + "--offspringPopulationSize 100 "
            + "--variation crossoverAndMutationVariation --crossover SBX "
            + "--crossoverProbability 0.7010787592319431 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--mutationRepairStrategy bounds "
            + "--selection tournament "
            + "--selectionTournamentSize 2 \n")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    AutoNSGAII.print(autoNSGAII.fixedParameterList());
    AutoNSGAII.print(autoNSGAII.configurableParameterList());

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "NSGA-II", 80, 100,
            referenceFrontFileName, "F1", "F2");

    nsgaII.observable().register(evaluationObserver);
    nsgaII.observable().register(runTimeChartObserver);

    nsgaII.run();

    JMetalLogger.logger.info("Total computing time: " + nsgaII.totalComputingTime()); ;

    new SolutionListOutput(nsgaII.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
