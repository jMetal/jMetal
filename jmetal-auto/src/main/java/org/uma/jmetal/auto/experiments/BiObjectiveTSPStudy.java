package org.uma.jmetal.auto.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEADPermutation;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAIIPermutation;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADDEBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMPSOBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMSEMOABuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.NaryTournamentGlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.SPSO2007VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.LinkedPolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.KroAB100TSP;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.KroAC100TSP;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.KroAD100TSP;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance.KroAE100TSP;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT1;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT10;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT11;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT12;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT13;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT14;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT15;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT16;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT17;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT18;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT19;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT2;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT20;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT3;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT4;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT5;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT6;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT7;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT8;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT9;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class BiObjectiveTSPStudy {

  private static final int INDEPENDENT_RUNS = 8;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<PermutationSolution<Integer>>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new KroAB100TSP()));
    problemList.add(new ExperimentProblem<>(new KroAC100TSP()));
    problemList.add(new ExperimentProblem<>(new KroAD100TSP()));
    problemList.add(new ExperimentProblem<>(new KroAE100TSP()));

    var algorithmList = configureAlgorithmList(problemList);

    var experiment =
            new ExperimentBuilder<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>("BiObjectiveTSP")
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setReferenceFrontDirectory(experimentBaseDirectory + "/BiObjectiveTSP/referenceFronts")
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

    /*
    new GenerateReferenceParetoFront(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(5).setColumns(4).setDisplayNotch().run();

     */
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
