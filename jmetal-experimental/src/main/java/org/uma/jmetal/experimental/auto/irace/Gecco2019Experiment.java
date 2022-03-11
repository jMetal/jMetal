package org.uma.jmetal.experimental.auto.irace;

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
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7_2D;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.problem.multiobjective.wfg.WFG3;
import org.uma.jmetal.problem.multiobjective.wfg.WFG4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG5;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.problem.multiobjective.wfg.WFG8;
import org.uma.jmetal.problem.multiobjective.wfg.WFG9;
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
 * This class reproduce the experimental study detailed in:
 * Antonio J. Nebro, Manuel López-Ibáñez, Cristóbal Barba-González, José García-Nieto
 * Automatic configuration of NSGA-II and MOPSO with jMetal and irace.
 * GECCO (Companion) 2019: 1374-1381
 * DOI: https://doi.org/10.1145/3319619.3326832
 *
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class Gecco2019Experiment {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    Check.that(args.length == 1, "Missing argument: experimentBaseDirectory") ;

    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new WFG1()).setReferenceFront("WFG1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG2()).setReferenceFront("WFG2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG3()).setReferenceFront("WFG3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG4()).setReferenceFront("WFG4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG5()).setReferenceFront("WFG5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG6()).setReferenceFront("WFG6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG7()).setReferenceFront("WFG7.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG8()).setReferenceFront("WFG8.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG9()).setReferenceFront("WFG9.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ1_2D()).setReferenceFront("DTLZ1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ2_2D()).setReferenceFront("DTLZ2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ3_2D()).setReferenceFront("DTLZ3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ4_2D()).setReferenceFront("DTLZ4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ5_2D()).setReferenceFront("DTLZ5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ6_2D()).setReferenceFront("DTLZ6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ7_2D()).setReferenceFront("DTLZ7.2D.csv"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("Gecco2019Study")
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
                .setMaxIterations(250)
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
                        + "--maximumNumberOfEvaluations 25000 "
                        + "--algorithmResult externalArchive "
                        + "--populationSizeWithArchive 79 "
                        + "--populationSize 100 "
                        + "--offspringPopulationSize 158 "
                        + "--createInitialSolutions scatterSearch "
                        + "--variation crossoverAndMutationVariation "
                        + "--selection tournament "
                        + "--selectionTournamentSize 8 "
                        + "--rankingForSelection dominanceRanking "
                        + "--densityEstimatorForSelection crowdingDistance "
                        + "--crossover BLX_ALPHA "
                        + "--crossoverProbability 0.9562 "
                        + "--crossoverRepairStrategy bounds "
                        + "--blxAlphaCrossoverAlphaValue 0.6316 "
                        + "--sbxDistributionIndex 20.0 "
                        + "--mutation uniform "
                        + "--mutationProbability 0.004 "
                        + "--mutationRepairStrategy round "
                        + "--uniformMutationPerturbation 0.6972 "
                        + "--polynomialMutationDistributionIndex 20.0 "
                        + "--externalArchive crowdingDistanceArchive ")
                        .split("\\s+");

        AutoNSGAII autoNSGAII = new AutoNSGAII();
        autoNSGAII.parseAndCheckParameters(parameters);
        EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

        algorithms.add(new ExperimentAlgorithm<>(nsgaII, "AutoNSGAII", experimentProblem, run));



        /* AutoMOPSO */
        String[] parametersAutoMOPSO = ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                + "--maximumNumberOfEvaluations 25000 "
                + "--swarmSize 72 "
                + "--archiveSize 100 "
                + "--swarmInitialization random "
                + "--velocityInitialization defaultVelocityInitialization "
                + "--externalArchive crowdingDistanceArchive "
                + "--localBestInitialization defaultLocalBestInitialization "
                + "--globalBestInitialization defaultGlobalBestInitialization "
                + "--globalBestSelection binaryTournament "
                + "--perturbation frequencySelectionMutationBasedPerturbation "
                + "--frequencyOfApplicationOfMutationOperator 6 "
                + "--mutation polynomial "
                + "--mutationProbability 0.0108 "
                + "--mutationRepairStrategy round "
                + "--polynomialMutationDistributionIndex 84.965 "
                + "--positionUpdate defaultPositionUpdate "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--velocityUpdate defaultVelocityUpdate "
                + "--c1Min 1.6003 "
                + "--c1Max 2.4511 "
                + "--c2Min 1.2224 "
                + "--c2Max 2.1119 "
                + "--wMin 0.1041  "
                + "--wMax 0.288 "
        )
                .split("\\s+");
        AutoMOPSO AutoMOPSO = new AutoMOPSO();
        AutoMOPSO.parseAndCheckParameters(parametersAutoMOPSO);
        ParticleSwarmOptimizationAlgorithm automopso = AutoMOPSO.create();

        algorithms.add(new ExperimentAlgorithm<>(automopso, "AutoMOPSO", experimentProblem, run));

        /* OMOPSO */
        String[] parametersOMOPSO = ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                + "--maximumNumberOfEvaluations 25000 "
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
                + "--c1Min 1.5 "
                + "--c1Max 2.0 "
                + "--c2Min 1.5 "
                + "--c2Max 2.0 "
                + "--wMin 0.1  "
                + "--wMax 0.5 "
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
