package org.uma.jmetal.auto.old.nsgaii;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.ExperimentBuilder;
import org.uma.jmetal.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.problem.multiobjective.wfg.*;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ExampleOfParameterFileGeneration of experimental study based on solving the ZDT problems with four versions of NSGA-II,
 * each of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AutoNSGAIIStudy {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();

    /*
    problemList.add(new ExperimentProblem<>(new ZDT1()));
    problemList.add(new ExperimentProblem<>(new ZDT2()));
    problemList.add(new ExperimentProblem<>(new ZDT3()));
    problemList.add(new ExperimentProblem<>(new ZDT4()));
    problemList.add(new ExperimentProblem<>(new ZDT6()));

    problemList.add(new ExperimentProblem<>(new WFG1()).changeReferenceFrontTo("WFG1.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG2()).changeReferenceFrontTo("WFG2.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG3()).changeReferenceFrontTo("WFG3.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG4()).changeReferenceFrontTo("WFG4.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG5()).changeReferenceFrontTo("WFG5.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG6()).changeReferenceFrontTo("WFG6.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG7()).changeReferenceFrontTo("WFG7.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG8()).changeReferenceFrontTo("WFG8.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG9()).changeReferenceFrontTo("WFG9.2D.pf"));

    problemList.add(new ExperimentProblem<>(new DTLZ1()).changeReferenceFrontTo("DTLZ1.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ2()).changeReferenceFrontTo("DTLZ2.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).changeReferenceFrontTo("DTLZ3.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ4()).changeReferenceFrontTo("DTLZ4.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ5()).changeReferenceFrontTo("DTLZ5.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ6()).changeReferenceFrontTo("DTLZ6.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ7()).changeReferenceFrontTo("DTLZ7.2D.pf"));
*/
    problemList.add(new ExperimentProblem<>(new DTLZ1()).changeReferenceFrontTo("DTLZ1.2D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).changeReferenceFrontTo("DTLZ3.2D.pf"));
    problemList.add(new ExperimentProblem<>(new WFG8()).changeReferenceFrontTo("WFG8.2D.pf"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("AutoNSGAIIStudy2")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("/pareto_fronts")
            .setIndicatorList(
                Arrays.asList(
                    new Epsilon<DoubleSolution>(),
                    new Spread<DoubleSolution>(),
                    // new GenerationalDistance<DoubleSolution>(),
                    new PISAHypervolume<DoubleSolution>(),
                    // new InvertedGenerationalDistance<DoubleSolution>(),
                    new InvertedGenerationalDistancePlus<DoubleSolution>()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    //new ExecuteAlgorithms<>(org.uma.jmetal.experiment).run();

    //new ComputeQualityIndicators<>(org.uma.jmetal.experiment).run();
    //new GenerateLatexTablesWithStatistics(org.uma.jmetal.experiment).run();
      new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    //new GenerateFriedmanTestTables<>(org.uma.jmetal.experiment).run();
    //new GenerateBoxplotsWithR<>(org.uma.jmetal.experiment).setRows(4).setColumns(4).run();
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

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm =
            new NSGAIIBuilder<>(
                    problemList.get(i).getProblem(),
                    new SBXCrossover(1.0, 5),
                    new PolynomialMutation(
                        1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0),
                    100)
                .setMaxEvaluations(25000)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAII", problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        Algorithm<List<DoubleSolution>> algorithm =
            new SMPSOBuilder(
                    (DoubleProblem) problemList.get(i).getProblem(),
                    new CrowdingDistanceArchive<DoubleSolution>(100))
                .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
                .setMaxIterations(250)
                .setSwarmSize(100)
                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        String problemName = problemList.get(i).getProblem().getClass().toString();
        problemName = problemName.substring(6);
        /*
                System.out.println(
                    "Problem: " + problemName + ". Front: " + problemList.get(i).getReferenceFront());
                String[] arguments = {
                  "--problemName",
                  problemName,
                  "--referenceFront",
                  problemList.get(i).getReferenceFront(),
                  "--crossover",
                  "BLX_ALPHA",
                  "--crossoverProbability",
                  "0.9983",
                  "--crossoverRepairStrategy",
                  "round",
                  "--blxAlphaCrossoverAlphaValue",
                  "0.7648",
                  "--mutation",
                  "polynomial",
                  "--mutationProbability",
                  "0.0078",
                  "--mutationRepairStrategy",
                  "round",
                  "--uniformMutationPerturbation",
                  "0.7294",
                  "--selection",
                  "tournament",
                  "--selectionTournamentSize",
                  "9",
                  "--offspringPopulationSize",
                  "1",
                  "--variation",
                  "crossoverAndMutationVariation",
                  "--createInitialSolutions",
                  "random"
                };
        */
        String argumentString =  "--problemName " + problemName +
                " --referenceFront " +  problemList.get(i).getReferenceFront() +
                /*
                " --algorithmResult externalArchive --populationSizeWithArchive 20 --offspringPopulationSize 200 " +
                "--variation crossoverAndMutationVariation --createInitialSolutions scatterSearch " +
                "--crossover BLX_ALPHA --crossoverProbability 0.9874 " +
                "--crossoverRepairStrategy bounds --blxAlphaCrossoverAlphaValue 0.5906 " +
                "--mutation polynomial --mutationProbability 0.0015 " +
                "--mutationRepairStrategy random --polynomialMutationDistributionIndex 158.0489 " +
                "--selection tournament --selectionTournamentSize 9  " ;
                */
                " --algorithmResult externalArchive --populationSizeWithArchive 20 --offspringPopulationSize 5 " +
                "--variation crossoverAndMutationVariation --createInitialSolutions latinHypercubeSampling " +
                "--crossover SBX --crossoverProbability 0.9791 " +
                "--crossoverRepairStrategy round --sbxCrossoverDistributionIndex 5.0587 " +
                "--mutation uniform --mutationProbability 0.0463 " +
                "--mutationRepairStrategy random --uniformMutationPerturbation 0.2307 " +
                "--selection tournament --selectionTournamentSize 4  " ;
                /*
                " --algorithmResult population --populationSize 100 " +
                "--crossover BLX_ALPHA --crossoverProbability 0.964 " +
                "--crossoverRepairStrategy bounds " +
                "--blxAlphaCrossoverAlphaValue 0.7965 " +
                "--mutation polynomial --mutationProbability 0.017 " +
                "--mutationRepairStrategy random " +
                "--polynomialMutationDistributionIndex 6.2659 " +
                "--selection tournament --selectionTournamentSize 10 " +
                "--offspringPopulationSize 1 " +
                "--variation crossoverAndMutationVariation " +
                "--createInitialSolutions random  ";*/

        String[] arguments = argumentString.split(" ") ;
        AutoNSGAIIConfigurator configurator =
            CommandLine.populateCommand(new AutoNSGAIIConfigurator(), arguments);

        EvolutionaryAlgorithm<DoubleSolution> algorithm = configurator.configureAndGetAlgorithm();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "AutoNSGAIIb", problemList.get(i), run));
      }
    }
    return algorithms;
  }
}
