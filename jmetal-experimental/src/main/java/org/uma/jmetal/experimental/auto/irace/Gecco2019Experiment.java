package org.uma.jmetal.experimental.auto.irace;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.nsgaii.AutoNSGAII;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.wfg.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gecco2019Experiment {
  private static final int INDEPENDENT_RUNS = 15;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
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

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("WFGStudy")
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
        String[] parameters =
                ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
                        + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
                        + "--maximumNumberOfEvaluations 75000 "
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
                        + "--mutationProbability 0.01 "
                        + "--mutationRepairStrategy bounds "
                        + "--polynomialMutationDistributionIndex 20.0 ")
                        .split("\\s+");

        AutoNSGAII autoNSGAII = new AutoNSGAII();
        autoNSGAII.parseAndCheckParameters(parameters);
        EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

        algorithms.add(new ExperimentAlgorithm<>(nsgaII, experimentProblem, run));
      }
    }
    return algorithms;
  }
}
