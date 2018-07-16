package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
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
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II,
 * each of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * This experiment assumes that the reference Pareto front are not known, so the names of files
 * containing them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts 4. Compute the quality indicators 5. Generate Latex
 * tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
 * Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIStudy2 {

  private static final int INDEPENDENT_RUNS = 20;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
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
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("NSGAIIStudy2")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(experimentBaseDirectory + "/NSGAIIStudy2/referenceFronts")
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

    //new ExecuteAlgorithms<>(experiment).run();
    //new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    //new ComputeQualityIndicators<>(experiment).run();
    //new GenerateLatexTablesWithStatistics(experiment).run();
    //new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
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
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
            problemList.get(i).getProblem(),
            new SBXCrossover(1.0, 5),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                10.0))
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIa", problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
            problemList.get(i).getProblem(),
            new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                20.0))
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIb", problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
            problemList.get(i).getProblem(), new SBXCrossover(1.0, 40.0),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                40.0))
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIc", problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
            problemList.get(i).getProblem(), new SBXCrossover(1.0, 80.0),
            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
                80.0))
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIId", problemList.get(i), run));
      }
    }
    return algorithms;
  }

}