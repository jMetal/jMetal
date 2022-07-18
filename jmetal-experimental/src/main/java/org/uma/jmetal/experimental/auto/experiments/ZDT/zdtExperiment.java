package org.uma.jmetal.experimental.auto.experiments.ZDT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
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
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Automatic configuration of NSGA-II and MOPSO with jMetal and irace.
 *
 * @author Daniel Doblas
 */
public class zdtExperiment {

    private static final int INDEPENDENT_RUNS = 25;

    public static void main(String[] args) throws IOException {
        Check.that(args.length == 1, "Missing argument: experimentBaseDirectory");

        String experimentBaseDirectory = args[0];

        @NotNull List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
        problemList.add(new ExperimentProblem<>(new ZDT1()).setReferenceFront("ZDT1.csv"));
        problemList.add(new ExperimentProblem<>(new ZDT2()).setReferenceFront("ZDT2.csv"));
        problemList.add(new ExperimentProblem<>(new ZDT3()).setReferenceFront("ZDT3.csv"));
        problemList.add(new ExperimentProblem<>(new ZDT4()).setReferenceFront("ZDT4.csv"));
        problemList.add(new ExperimentProblem<>(new ZDT6()).setReferenceFront("ZDT6.csv"));


        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
                configureAlgorithmList(problemList);

        @NotNull Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZDTAutoAlgorithmExperiments")
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
    static @NotNull List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
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

            for (@NotNull ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
                double mutationProbability = 1.0 / experimentProblem.getProblem().getNumberOfVariables();
                double mutationDistributionIndex = 20.0;
                @NotNull Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
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

                /* OMOPSO */
                String @NotNull [] parametersOMOPSO = ("--problemName " + experimentProblem.getProblem().getClass()
                        .getName() + " "
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
                        + "--mutationProbabilityFactor 1.0 "
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
                @NotNull AutoMOPSO OMOPSO = new AutoMOPSO();
                OMOPSO.parseAndCheckParameters(parametersOMOPSO);
                @NotNull ParticleSwarmOptimizationAlgorithm omopso = OMOPSO.create();

                algorithms.add(new ExperimentAlgorithm<>(omopso, "OMOPSO", experimentProblem, run));

                /* AutoMOPSO With config.txt */
                String @NotNull [] parametersAutoMOPSOWithtConfig = ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                        + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
                        + "--maximumNumberOfEvaluations 25000 "
                        + "--swarmSize 22 " +
                        "--archiveSize 100 " +
                        "--externalArchive hypervolumeArchive " +
                        "--swarmInitialization random " +
                        "--velocityInitialization defaultVelocityInitialization " +
                        "--perturbation frequencySelectionMutationBasedPerturbation " +
                        "--mutation uniform " +
                        "--mutationProbabilityFactor 0.0638 " +
                        "--mutationRepairStrategy random " +
                        "--uniformMutationPerturbation 0.6053 " +
                        "--frequencyOfApplicationOfMutationOperator 8 " +
                        "--inertiaWeightComputingStrategy linearIncreasingValue " +
                        "--weightMin 0.1941 " +
                        "--weightMax 0.8233 " +
                        "--velocityUpdate constrainedVelocityUpdate " +
                        "--c1Min 1.7276 " +
                        "--c1Max 2.4922 " +
                        "--c2Min 1.3172 " +
                        "--c2Max 2.1464 " +
                        "--localBestInitialization defaultLocalBestInitialization " +
                        "--globalBestInitialization defaultGlobalBestInitialization " +
                        "--globalBestSelection binaryTournament " +
                        "--globalBestUpdate defaultGlobalBestUpdate " +
                        "--localBestUpdate defaultLocalBestUpdate " +
                        "--positionUpdate defaultPositionUpdate " +
                        "--velocityChangeWhenLowerLimitIsReached 0.1808 " +
                        "--velocityChangeWhenUpperLimitIsReached -0.3183"
                )
                        .split("\\s+");
                AutoMOPSO AutoMOPSOWithConfig = new AutoMOPSO();
                AutoMOPSOWithConfig.parseAndCheckParameters(parametersAutoMOPSOWithtConfig);
                ParticleSwarmOptimizationAlgorithm automopsoWithConfig = AutoMOPSOWithConfig.create();

                algorithms.add(new ExperimentAlgorithm<>(automopsoWithConfig, "AutoMOPSO", experimentProblem, run));

            }

        }
        return algorithms;
    }
}
