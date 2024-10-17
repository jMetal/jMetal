package org.uma.jmetal.auto.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEADPermutation;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAIIPermutation;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.*;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class TriObjectiveTSPStudy {

  private static final int INDEPENDENT_RUNS = 8;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<PermutationSolution<Integer>>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new KroABC100TSP()));
    problemList.add(new ExperimentProblem<>(new KroABD100TSP()));
    problemList.add(new ExperimentProblem<>(new KroACD100TSP()));
    problemList.add(new ExperimentProblem<>(new KroACE100TSP()));

    var algorithmList = configureAlgorithmList(problemList);

    var experiment =
            new ExperimentBuilder<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>("TriObjectiveTSP")
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setReferenceFrontDirectory(experimentBaseDirectory + "/TriObjectiveTSP/referenceFronts")
                    .setOutputParetoFrontFileName("FUN")
                    .setOutputParetoSetFileName("VAR")
                    .setIndicatorList(Arrays.asList(
                            new Epsilon(),
                            new PISAHypervolume(),
                            new NormalizedHypervolume(),
                            new InvertedGenerationalDistancePlus()))
                    .setIndependentRuns(INDEPENDENT_RUNS)
                    .setNumberOfCores(8)
                    .build();

    new ExecuteAlgorithms<>(experiment).run();

    new GenerateReferenceParetoFront(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(2).setDisplayNotch().run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> configureAlgorithmList(
      List<ExperimentProblem<PermutationSolution<Integer>>> problemList) throws IOException {
    List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms = new ArrayList<>();

    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        nsgaII(algorithms, run, experimentProblem);
        moead(algorithms, run, experimentProblem);
      }
    }

    return algorithms;
  }

  private static void nsgaII(
      List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms, int run,
      ExperimentProblem<PermutationSolution<Integer>> experimentProblem) throws IOException {

    String referenceFrontFileName = null ;
    String problemName = experimentProblem.getProblem().getClass().getName() ;

    String[] parameters =
            ("--problemName " + problemName + " "
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

    EvolutionaryAlgorithm<PermutationSolution<Integer>> algorithm = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void moead(
      List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms, int run,
      ExperimentProblem<PermutationSolution<Integer>> experimentProblem) {

    String referenceFrontFileName = null ;
    String problemName = experimentProblem.getProblem().getClass().getName() ;

    String[] parameters =
            ("--problemName " + problemName + " "
                    + "--referenceFrontFileName " + referenceFrontFileName + " "
                    + "--randomGeneratorSeed 124 "
                    + "--maximumNumberOfEvaluations 200000 "
                    + "--offspringPopulationSize 1 "
                    + "--algorithmResult population "
                    + "--populationSize 100 "
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

    EvolutionaryAlgorithm<PermutationSolution<Integer>> moead = autoMOEAD.create();

    algorithms.add(
        new ExperimentAlgorithm<>(moead, experimentProblem, run));
  }
}
