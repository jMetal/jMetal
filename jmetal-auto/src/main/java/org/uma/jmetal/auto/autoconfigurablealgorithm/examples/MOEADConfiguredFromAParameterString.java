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
public class MOEADConfiguredFromAParameterString {
  public static void main(String[] args) {
    String referenceFrontFileName = "DTLZ1.3D.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1 "
            + "--referenceFrontFileName DTLZ1.3D.csv "
            + "--randomGeneratorSeed 124 "
            + "--maximumNumberOfEvaluations 150000 "
            + "--algorithmResult population "
            + "--populationSize 91 "
            + "--sequenceGenerator integerSequence "
            + "--createInitialSolutions random "
            + "--normalizeObjectives false "
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

    AutoMOEAD autoMOEAD = new AutoMOEAD();
    autoMOEAD.parse(parameters);

    AutoNSGAII.print(autoMOEAD.fixedParameterList);
    AutoNSGAII.print(autoMOEAD.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> moead = autoMOEAD.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "MOEAD", 80, 5000,"resources/referenceFrontsCSV/" + referenceFrontFileName);

    moead.observable().register(evaluationObserver);
    moead.observable().register(runTimeChartObserver);

    moead.run();

    JMetalLogger.logger.info("Total computing time: " + moead.totalComputingTime()); ;

    new SolutionListOutput(moead.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
