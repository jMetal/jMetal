package org.uma.jmetal.auto.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOPSO;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
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
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6_2D;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7_2D;
import org.uma.jmetal.problem.multiobjective.re.RE21;
import org.uma.jmetal.problem.multiobjective.re.RE22;
import org.uma.jmetal.problem.multiobjective.re.RE23;
import org.uma.jmetal.problem.multiobjective.re.RE24;
import org.uma.jmetal.problem.multiobjective.re.RE25;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.problem.multiobjective.wfg.WFG3;
import org.uma.jmetal.problem.multiobjective.wfg.WFG4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG5;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.problem.multiobjective.wfg.WFG8;
import org.uma.jmetal.problem.multiobjective.wfg.WFG9;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT3;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT6;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

public class SwarmIntelligenceStudy10000e3 {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    Check.that(args.length == 1, "Missing argument: experimentBaseDirectory");

    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();

    problemList.add(new ExperimentProblem<>(new ZDT1()).setReferenceFront("ZDT1.csv"));
    problemList.add(new ExperimentProblem<>(new ZDT2()).setReferenceFront("ZDT2.csv"));
    problemList.add(new ExperimentProblem<>(new ZDT3()).setReferenceFront("ZDT3.csv"));
    problemList.add(new ExperimentProblem<>(new ZDT4()).setReferenceFront("ZDT4.csv"));
    problemList.add(new ExperimentProblem<>(new ZDT6()).setReferenceFront("ZDT6.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ1_2D()).setReferenceFront("DTLZ1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ2_2D()).setReferenceFront("DTLZ2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ3_2D()).setReferenceFront("DTLZ3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ4_2D()).setReferenceFront("DTLZ4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ5_2D()).setReferenceFront("DTLZ5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ6_2D()).setReferenceFront("DTLZ6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ7_2D()).setReferenceFront("DTLZ7.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG1()).setReferenceFront("WFG1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG2()).setReferenceFront("WFG2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG3()).setReferenceFront("WFG3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG4()).setReferenceFront("WFG4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG5()).setReferenceFront("WFG5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG6()).setReferenceFront("WFG6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG7()).setReferenceFront("WFG7.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG8()).setReferenceFront("WFG8.2D.csv"));
    problemList.add(new ExperimentProblem<>(new WFG9()).setReferenceFront("WFG9.2D.csv"));
    problemList.add(new ExperimentProblem<>(new RE21()).setReferenceFront("RE21.csv"));
    problemList.add(new ExperimentProblem<>(new RE22()).setReferenceFront("RE22.csv"));
    problemList.add(new ExperimentProblem<>(new RE23()).setReferenceFront("RE23.csv"));
    problemList.add(new ExperimentProblem<>(new RE24()).setReferenceFront("RE24.csv"));
    problemList.add(new ExperimentProblem<>(new RE25()).setReferenceFront("RE25.csv"));
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("SwarmIntelligenceExperiment10000e3")
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
        nsgaII(algorithms, run, experimentProblem);
        smpso(algorithms, run, experimentProblem);
        omopso(algorithms, run, experimentProblem);
        autoMOPSOZ(algorithms, run, experimentProblem);
        autoMOPSOD(algorithms, run, experimentProblem);
        autoMOPSOW(algorithms, run, experimentProblem);
        autoMOPSORE(algorithms, run, experimentProblem);
      }
    }
    return algorithms;
  }

  private static void nsgaII(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName "+  experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
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

    EvolutionaryAlgorithm<DoubleSolution> algorithm = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "NSGAII", experimentProblem, run));
  }

  private static void smpso(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--algorithmResult leaderArchive "
            + "--swarmSize 100 "
            + "--archiveSize 100 "
            + "--swarmInitialization random "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--leaderArchive crowdingDistanceArchive "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--frequencyOfApplicationOfMutationOperator 7 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--positionUpdate defaultPositionUpdate "
            + "--velocityChangeWhenLowerLimitIsReached -1.0 "
            + "--velocityChangeWhenUpperLimitIsReached -1.0 "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--velocityUpdate constrainedVelocityUpdate "
            + "--inertiaWeightComputingStrategy constantValue "
            + "--c1Min 1.5 "
            + "--c1Max 2.5 "
            + "--c2Min 1.5 "
            + "--c2Max 2.5 "
            + "--weight 0.1 ")
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "SMPSO", experimentProblem, run));
  }

  private static void omopso(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--swarmSize 100 "
            + "--archiveSize 100 "
            + "--algorithmResult leaderArchive "
            + "--swarmInitialization random "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--leaderArchive crowdingDistanceArchive "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--frequencyOfApplicationOfMutationOperator 7 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy round "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--positionUpdate defaultPositionUpdate "
            + "--velocityChangeWhenLowerLimitIsReached -1.0 "
            + "--velocityChangeWhenUpperLimitIsReached -1.0 "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--inertiaWeightComputingStrategy randomSelectedValue "
            + "--c1Min 1.5 "
            + "--c1Max 2.0 "
            + "--c2Min 1.5 "
            + "--c2Max 2.0 "
            + "--weightMin 0.1 "
            + "--weightMax 0.5 ")
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "OMOPSO", experimentProblem, run));
  }

  private static void autoMOPSOZ(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--swarmSize 27 "
            + "--archiveSize 100 "
            + "--leaderArchive hypervolumeArchive "
            + "--algorithmResult leaderArchive "
            + "--swarmInitialization scatterSearch "
            + "--velocityInitialization SPSO2007VelocityInitialization "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--inertiaWeightComputingStrategy linearIncreasingValue "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--c1Min 1.5043 "
            + "--c1Max 2.1342 "
            + "--c2Min 1.9521 "
            + "--c2Max 2.7965 "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--positionUpdate defaultPositionUpdate "
            + "--mutation nonUniform "
            + "--frequencyOfApplicationOfMutationOperator 1 "
            + "--weightMin 0.1911 "
            + "--weightMax 0.8316 "
            + "--velocityChangeWhenLowerLimitIsReached 0.8786 "
            + "--velocityChangeWhenUpperLimitIsReached -0.2416 "
            + "--mutationProbabilityFactor 0.0732 "
            + "--mutationRepairStrategy round "
            + "--nonUniformMutationPerturbation 0.2749 "
)
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "AutoMOPSOZ", experimentProblem, run));
  }

  private static void autoMOPSOD(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--swarmSize 194 "
            + "--archiveSize 100 "
            + "--leaderArchive hypervolumeArchive "
            + "--algorithmResult leaderArchive "
            + "--swarmInitialization scatterSearch "
            + "--velocityInitialization SPSO2007VelocityInitialization "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--inertiaWeightComputingStrategy linearDecreasingValue "
            + "--velocityUpdate constrainedVelocityUpdate "
            + "--c1Min 1.1083 "
            + "--c1Max 2.2274 "
            + "--c2Min 1.5095 "
            + "--c2Max 2.4183 "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--positionUpdate defaultPositionUpdate "
            + "--mutation polynomial "
            + "--frequencyOfApplicationOfMutationOperator 8 "
            + "--weightMin 0.1188 "
            + "--weightMax 0.7594 "
            + "--velocityChangeWhenLowerLimitIsReached -0.0475 "
            + "--velocityChangeWhenUpperLimitIsReached 0.0035 "
            + "--mutationProbabilityFactor 0.7639 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 210.1730 "
        )
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "AutoMOPSOD", experimentProblem, run));
  }

  private static void autoMOPSOW(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--swarmSize 25 "
            + "--archiveSize 100 "
            + "--leaderArchive hypervolumeArchive "
            + "--algorithmResult leaderArchive "
            + "--swarmInitialization scatterSearch "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--inertiaWeightComputingStrategy linearIncreasingValue "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--c1Min 1.6244 "
            + "--c1Max 2.6552 "
            + "--c2Min 1.3221 "
            + "--c2Max 2.1361 "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--positionUpdate defaultPositionUpdate "
            + "--mutation uniform "
            + "--frequencyOfApplicationOfMutationOperator 6 "
            + "--weightMin 0.2085 "
            + "--weightMax 0.7378 "
            + "--velocityChangeWhenLowerLimitIsReached -0.9125 "
            + "--velocityChangeWhenUpperLimitIsReached -0.7409 "
            + "--mutationProbabilityFactor 0.1887 "
            + "--mutationRepairStrategy round "
            + "--uniformMutationPerturbation 0.1255 "
        )
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "AutoMOPSOW", experimentProblem, run));
  }

  private static void autoMOPSORE(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--swarmSize 156 "
            + "--archiveSize 100 "
            + "--leaderArchive hypervolumeArchive "
            + "--algorithmResult leaderArchive "
            + "--swarmInitialization latinHypercubeSampling "
            + "--velocityInitialization defaultVelocityInitialization "
            + "--perturbation frequencySelectionMutationBasedPerturbation "
            + "--inertiaWeightComputingStrategy constantValue "
            + "--velocityUpdate defaultVelocityUpdate "
            + "--c1Min 1.6567 "
            + "--c1Max 2.2514 "
            + "--c2Min 1.2255 "
            + "--c2Max 2.1769 "
            + "--localBestInitialization defaultLocalBestInitialization "
            + "--globalBestInitialization defaultGlobalBestInitialization "
            + "--globalBestSelection binaryTournament "
            + "--globalBestUpdate defaultGlobalBestUpdate "
            + "--localBestUpdate defaultLocalBestUpdate "
            + "--positionUpdate defaultPositionUpdate "
            + "--mutation nonUniform "
            + "--frequencyOfApplicationOfMutationOperator 6 "
            + "--weight 0.1016 "
            + "--velocityChangeWhenLowerLimitIsReached 0.4801 "
            + "--velocityChangeWhenUpperLimitIsReached 0.9951 "
            + "--mutationProbabilityFactor 0.2042 "
            + "--mutationRepairStrategy bounds "
            + "--nonUniformMutationPerturbation 0.7257 "
        )
            .split("\\s+");

    AutoMOPSO autoMOPSO = new AutoMOPSO();
    autoMOPSO.parseAndCheckParameters(parameters);

    ParticleSwarmOptimizationAlgorithm algorithm = autoMOPSO.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "AutoMOPSORE", experimentProblem, run));
  }
}
