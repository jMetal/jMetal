package org.uma.jmetal.experimental.auto.experiments.LSMOP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.experimental.auto.algorithm.nsgaii.AutoNSGAII;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.lsmop.LSMOP1_2_20;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Automatic configuration of NSGA-II and MOPSO with jMetal and irace.
 *
 * @author Daniel Doblas
 */
public class lsmop1With20VariablesExperimentAutoMOPSO {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    Check.that(args.length == 1, "Missing argument: experimentBaseDirectory");

    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new LSMOP1_2_20()).setReferenceFront("LSMOP1.2D.csv"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(
            "LSMOP1With20VariablesAutoAlgorithmExperiments")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setReferenceFrontDirectory("resources/referenceFrontsCSV")
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndicatorList(Arrays.asList(
                new Epsilon(),
                new Spread(),
                new GenerationalDistance(),
                new PISAHypervolume(),
                new NormalizedHypervolume(),
                new InvertedGenerationalDistance(),
                new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();
    new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(
            experimentProblem.getProblem(),
            new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / experimentProblem.getProblem().getNumberOfVariables(),
                20.0),
            100)
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
      }

      for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
        double mutationProbability = 1.0 / experimentProblem.getProblem().getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
            (DoubleProblem) experimentProblem.getProblem(),
            new CrowdingDistanceArchive<DoubleSolution>(100))
            .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
            .setMaxIterations(300)
            .setSwarmSize(100)
            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
      }

      for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {

        /* AutoNSGAII */
        String[] parameters =
            ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                + "--maximumNumberOfEvaluations 30000 "
                + "--algorithmResult population "
                + "--populationSizeWithArchive 100 "
                + "--populationSize 100 "
                + "--offspringPopulationSize 2 "
                + "--createInitialSolutions scatterSearch "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 8 "
                + "--rankingForSelection dominanceRanking "
                + "--densityEstimatorForSelection crowdingDistance "
                + "--crossover SBX "
                + "--crossoverProbability 0.0754 "
                + "--crossoverRepairStrategy random "
                + "--sbxDistributionIndex 40.6351 "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation linkedPolynomial "
                + "--mutationProbability 0.8267 "
                + "--mutationRepairStrategy bounds "
                + "--linkedPolynomialMutationDistributionIndex 163.3098 "
                + "--externalArchive crowdingDistanceArchive ")
                .split("\\s+");

        AutoNSGAII autoNSGAII = new AutoNSGAII();
        autoNSGAII.parseAndCheckParameters(parameters);
        EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

        algorithms.add(new ExperimentAlgorithm<>(nsgaII, "AutoNSGAII", experimentProblem, run));



        /* AutoMOPSO */
        String[] parametersAutoMOPSO = ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                + "--maximumNumberOfEvaluations 300000 "
                + "--swarmSize 72 --archiveSize 100 "
                + "--externalArchive crowdingDistanceArchive "
                + "--swarmInitialization scatterSearch "
                + "--velocityInitialization defaultVelocityInitialization "
                + "--perturbation frequencySelectionMutationBasedPerturbation "
                + "--mutation polynomial "
                + "--mutationProbability 0.0445 "
                + "--mutationRepairStrategy round "
                + "--polynomialMutationDistributionIndex 314.4437 "
                + "--frequencyOfApplicationOfMutationOperator 1 "
                + "--inertiaWeightComputingStrategy linearIncreasingValue "
                + "--weightMin 0.155 "
                + "--weightMax 0.7226 "
                + "--velocityUpdate defaultVelocityUpdate "
                + "--c1Min 1.3994 "
                + "--c1Max 2.5065 "
                + "--c2Min 1.03 "
                + "--c2Max 2.0143 "
                + "--localBestInitialization defaultLocalBestInitialization "
                + "--globalBestInitialization defaultGlobalBestInitialization "
                + "--globalBestSelection random "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--positionUpdate defaultPositionUpdate "
                + "--velocityChangeWhenLowerLimitIsReached -0.132 "
                + "--velocityChangeWhenUpperLimitIsReached 0.9436"
        )
            .split("\\s+");
        AutoMOPSO AutoMOPSO = new AutoMOPSO();
        AutoMOPSO.parseAndCheckParameters(parametersAutoMOPSO);
        ParticleSwarmOptimizationAlgorithm automopso = AutoMOPSO.create();

        algorithms.add(new ExperimentAlgorithm<>(automopso, "AutoMOPSO", experimentProblem, run));

        /* OMOPSO */
        String[] parametersOMOPSO = ("--problemName " + experimentProblem.getProblem().getClass()
                .getName() + " "
                + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                + "--maximumNumberOfEvaluations 30000 "
                + "--swarmSize 100 "
                + "--archiveSize 100 "
                + "--swarmInitialization random "
                + "--velocityInitialization defaultVelocityInitialization "
                + "--externalArchive crowdingDistanceArchive "
                + "--localBestInitialization defaultLocalBestInitialization "
                + "--globalBestInitialization defaultGlobalBestInitialization "
                + "--globalBestSelection binaryTournament "
                + "--perturbation frequencySelectionMutationBasedPerturbation "
                + "--frequencyOfApplicationOfMutationOperator 7 "
                + "--mutation polynomial "
                + "--mutationProbability 1.0 "
                + "--mutationRepairStrategy round "
                + "--polynomialMutationDistributionIndex 20.0 "
                + "--positionUpdate defaultPositionUpdate "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--velocityUpdate defaultVelocityUpdate "
                + "--inertiaWeightComputingStrategy randomSelectedValue "
                + "--c1Min 1.5 "
                + "--c1Max 2.0 "
                + "--c2Min 1.5 "
                + "--c2Max 2.0 "
                + "--weightMin 0.1  "
                + "--weightMax 0.5 "
                + "--velocityChangeWhenLowerLimitIsReached -1.0 "
                + "--velocityChangeWhenUpperLimitIsReached -1.0 "
        )
            .split("\\s+");
        AutoMOPSO OMOPSO = new AutoMOPSO();
        OMOPSO.parseAndCheckParameters(parametersOMOPSO);
        ParticleSwarmOptimizationAlgorithm omopso = OMOPSO.create();

        algorithms.add(new ExperimentAlgorithm<>(omopso, "OMOPSO", experimentProblem, run));
      }

    }
    return algorithms;
  }
}
