package org.uma.jmetal.auto.autoconfigurablealgorithm.examples;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEAD;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * Class configuring NSGA-II using arguments in the form <key, value> and the {@link AutoNSGAII} class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class MOEADConfiguredToSolveProblemZDT110F2 {
  public static void main(String[] args) {
    String referenceFrontFileName = "ZDT1.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT110F2 "
            + "--referenceFrontFileName ZDT1.csv "
            + "--randomGeneratorSeed 124 "
            + "--maximumNumberOfEvaluations 50000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--offspringPopulationSize 1 "
            + "--createInitialSolutions random "
            + "--normalizeObjectives true "
            + "--neighborhoodSize 20 "
            + "--maximumNumberOfReplacedSolutions 2 "
            + "--aggregationFunction penaltyBoundaryIntersection "
            + "--pbiTheta 5.0 "
            + "--neighborhoodSelectionProbability 0.9 "
            + "--variation crossoverAndMutationVariation "
            + "--selection populationAndNeighborhoodMatingPoolSelection "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoMOEAD autoNSGAII = new AutoMOEAD();
    autoNSGAII.parse(parameters);

    AutoNSGAII.print(autoNSGAII.fixedParameterList);
    AutoNSGAII.print(autoNSGAII.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "MOEAD", 80, 1000,"resources/referenceFrontsCSV/" + referenceFrontFileName);

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
