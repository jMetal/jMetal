package org.uma.jmetal.auto.autoconfigurablealgorithm.examples;

import java.io.IOException;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAIIPermutation;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * Class configuring NSGA-II using arguments in the form <key, value> and the {@link AutoNSGAII}
 * class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class NSGAIIConfiguredToSolveAMultiObjectiveTSP {

  public static void main(String[] args) throws IOException {
    String referenceFrontFileName = null ;

    String[] parameters =
            ("--problemName org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.EuclidA300EuclidB300 "
                + "--randomGeneratorSeed 12 "
                + "--referenceFrontFileName " + referenceFrontFileName + " "
                + "--maximumNumberOfEvaluations 200000 "
                + "--populationSize 100 "
                + "--algorithmResult population  "
                + "--createInitialSolutions random "
                + "--offspringPopulationSize 100 "
                + "--variation crossoverAndMutationVariation "
                + "--crossover PMX "
                + "--crossoverProbability 1.0 "
                + "--mutation swap "
                + "--mutationProbabilityFactor 1.0 "
                + "--selection tournament "
                + "--selectionTournamentSize 2 \n")
            .split("\\s+");

    AutoNSGAIIPermutation autoNSGAII = new AutoNSGAIIPermutation();
    autoNSGAII.parse(parameters);

    AutoNSGAII.print(autoNSGAII.fixedParameterList());
    AutoNSGAII.print(autoNSGAII.configurableParameterList());

    EvolutionaryAlgorithm<PermutationSolution<Integer>> nsgaII = autoNSGAII.create();

    RunTimeChartObserver<PermutationSolution<Integer>> runTimeChartObserver =
        new RunTimeChartObserver<>("NSGA-II", 80, 5000, referenceFrontFileName, "F1", "F2");

    nsgaII.observable().register(runTimeChartObserver);

    nsgaII.run();

    JMetalLogger.logger.info("Total computing time: " + nsgaII.totalComputingTime());
    ;

    new SolutionListOutput(nsgaII.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
