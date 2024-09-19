package org.uma.jmetal.auto.autoconfigurablealgorithm.examples;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEAD;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEADPermutation;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
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
public class MOEADConfiguredFromToSolveAMultiObjectiveTSP {
  public static void main(String[] args) {
    String referenceFrontFileName = null ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.KroA100KroB100TSP "
            + "--referenceFrontFileName DTLZ1.3D.csv "
            + "--randomGeneratorSeed 124 "
            + "--maximumNumberOfEvaluations 200000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--offspringPopulationSize 1 "
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
            + "--crossover PMX "
            + "--crossoverProbability 0.9 "
            + "--mutation swap "
            + "--mutationProbabilityFactor 1.0 ")
            .split("\\s+");

    AutoMOEADPermutation autoMOEAD = new AutoMOEADPermutation();
    autoMOEAD.parse(parameters);

    AutoNSGAII.print(autoMOEAD.fixedParameterList);
    AutoNSGAII.print(autoMOEAD.autoConfigurableParameterList);

    EvolutionaryAlgorithm<PermutationSolution<Integer>> moead = autoMOEAD.create();

    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "MOEAD", 80, 5000,referenceFrontFileName);

    moead.observable().register(runTimeChartObserver);

    moead.run();

    JMetalLogger.logger.info("Total computing time: " + moead.totalComputingTime()); ;

    new SolutionListOutput(moead.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
