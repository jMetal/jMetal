package org.uma.jmetal.auto.autoconfigurablealgorithm.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
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
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
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
import org.uma.jmetal.util.errorchecking.JMetalException;

public class NSGAIIForSolvingZDTExperiment {
  private static final int INDEPENDENT_RUNS = 15;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT1()));
    problemList.add(new ExperimentProblem<>(new ZDT2()));
    problemList.add(new ExperimentProblem<>(new ZDT3()));
    problemList.add(new ExperimentProblem<>(new ZDT4()));
    problemList.add(new ExperimentProblem<>(new ZDT6()));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("NSGAIIStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("resources/referenceFrontsCSV")
            .setIndicatorList(
                List.of(
                    new Epsilon(),
                    new Spread(),
                    new GenerationalDistance(),
                    new PISAHypervolume(),
                    new NormalizedHypervolume(),
                    new InvertedGenerationalDistance(),
                    new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(10)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateHtmlPages<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(3).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
   * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this
   * example, where four variants of a same algorithm are defined.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        addNSGAII(algorithms, run, experimentProblem);
        //addSteadyStateNSGAII(algorithms, run, experimentProblem);
        addAutoNSGAIIConfiguration1(algorithms, run, experimentProblem);
        addAutoNSGAIIConfiguration2(algorithms, run, experimentProblem);
        addAutoNSGAIIConfiguration3(algorithms, run, experimentProblem);
      }
    }
    return algorithms;
  }

  private static void addSteadyStateNSGAII(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters = (
        "--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 1 "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy round "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--selection tournament "
            + "--selectionTournamentSize 2 ")
        .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);
    EvolutionaryAlgorithm<DoubleSolution> nsgaii = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(nsgaii, "ssNSGAII", experimentProblem, run));
  }

  private static void addAutoNSGAIIConfiguration1(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters = (
        "--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--populationSizeWithArchive 20 "
            + "--externalArchive crowdingDistanceArchive "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 200 "
            + "--crossover BLX_ALPHA "
            + "--crossoverProbability 0.8885 "
            + "--crossoverRepairStrategy bounds "
            + "--blxAlphaCrossoverAlphaValue 0.9408 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy round "
            + "--polynomialMutationDistributionIndex 158 "
            + "--selection tournament "
            + "--selectionTournamentSize 9 ")
        .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);
    EvolutionaryAlgorithm<DoubleSolution> nsgaii = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(nsgaii, "AutoNSGAII1", experimentProblem, run));
  }

  private static void addAutoNSGAIIConfiguration2(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters = (
        "--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--populationSizeWithArchive 56 "
            + "--externalArchive crowdingDistanceArchive "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 14 "
            + "--crossover BLX_ALPHA "
            + "--crossoverProbability 0.8885 "
            + "--crossoverRepairStrategy bounds "
            + "--blxAlphaCrossoverAlphaValue 0.9408 "
            + "--mutation nonUniform "
            + "--mutationProbabilityFactor 0.4534 "
            + "--mutationRepairStrategy round "
            + "--nonUniformMutationPerturbation 0.2995 "
            + "--selection tournament "
            + "--selectionTournamentSize 9 ")
        .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);
    EvolutionaryAlgorithm<DoubleSolution> nsgaii = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(nsgaii, "AutoNSGAII2", experimentProblem, run));
  }

  private static void addAutoNSGAIIConfiguration3(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters = (
        "--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--populationSizeWithArchive 35 "
            + "--externalArchive crowdingDistanceArchive "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 9 "
            + "--crossover BLX_ALPHA "
            + "--crossoverProbability 0.9955 "
            + "--crossoverRepairStrategy bounds "
            + "--blxAlphaCrossoverAlphaValue 0.9608 "
            + "--mutation nonUniform "
            + "--mutationProbabilityFactor 0.5084 "
            + "--mutationRepairStrategy round "
            + "--nonUniformMutationPerturbation 0.3486 "
            + "--selection tournament "
            + "--selectionTournamentSize 7 ")
        .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);
    EvolutionaryAlgorithm<DoubleSolution> nsgaii = autoNSGAII.create();

    algorithms.add(
        new ExperimentAlgorithm<>(nsgaii, "AutoNSGAII3", experimentProblem, run));
  }

  private static void addNSGAII(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {
    Algorithm<List<DoubleSolution>> algorithm;
    algorithm =
        new NSGAIIBuilder<>(
            experimentProblem.getProblem(),
            new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(
                1.0 / experimentProblem.getProblem().getNumberOfVariables(),
                20.0),
            100)
            .setMaxEvaluations(25000)
            .build();
    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "NSGAII", experimentProblem, run));
  }
}
