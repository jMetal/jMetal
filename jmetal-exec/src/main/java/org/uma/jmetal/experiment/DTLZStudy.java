package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the problems (configured with 3 objectives) with the algorithms
 * NSGAII, SPEA2, and SMPSO
 *
 * This experiment assumes that the reference Pareto front are known and stored in files whose names are different
 * from the default name expected for every problem. While the default would be "problem_name.pf" (e.g. DTLZ1.pf),
 * the references are stored in files following the nomenclature "problem_name.3D.pf" (e.g. DTLZ1.3D.pf). This is
 * indicated when creating the ExperimentProblem instance of each of the evaluated poblems by using the method
 * changeReferenceFrontTo()
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
 * 3. Compute que quality indicators 4. Generate Latex tables reporting means and medians 5.
 * Generate R scripts to produce latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 6. Generate Latex tables with the ranking obtained by applying the Friedman test 7. Generate R
 * scripts to obtain boxplots
 *
 */

public class DTLZStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new DTLZ1()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ2()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ4()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ5()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ6()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ7()).changeReferenceFrontTo("DTLZ1.3D.pf"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("DTLZtudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setReferenceFrontDirectory("/pareto_fronts")
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndicatorList(Arrays.asList(
                new Epsilon<DoubleSolution>(),
                new Spread<DoubleSolution>(),
                new GenerationalDistance<DoubleSolution>(),
                new PISAHypervolume<DoubleSolution>(),
                new InvertedGenerationalDistance<DoubleSolution>(),
                new InvertedGenerationalDistancePlus<DoubleSolution>()))
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

      for (int i = 0; i < problemList.size(); i++) {
        double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
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
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(
            problemList.get(i).getProblem(),
            new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                20.0))
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(
            problemList.get(i).getProblem(),
            new SBXCrossover(1.0, 10.0),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                20.0))
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
      }
    }
    return algorithms;
  }
}
