package org.uma.jmetal.lab.experiment.studies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Example of experimental study based on solving the ZDT1 problem but using five different number
 * of variables. This can be interesting to study the behaviour of the algorithms when solving an
 * scalable problem (in the number of variables). The used algorithms are NSGA-II, SPEA2 and SMPSO.
 * <p>
 * This org.uma.jmetal.experiment assumes that the reference Pareto front is of problem ZDT1 is not known, so a
 * reference front must be obtained.
 * <p>
 * Six quality indicators are used for performance assessment.
 * <p>
 * The steps to carry out the org.uma.jmetal.experiment are: 1. Configure the org.uma.jmetal.experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto sets and Pareto fronts 4. Compute the quality indicators 5.
 * Generate Latex tables reporting means and medians 6. Generate Latex tables with the result of
 * applying the Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by
 * applying the Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro
 */
public class ZDTScalabilityComputingReferenceParetoFrontsStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT1(10), "ZDT110"));
    problemList.add(new ExperimentProblem<>(new ZDT1(20), "ZDT120"));
    problemList.add(new ExperimentProblem<>(new ZDT1(30), "ZDT130"));
    problemList.add(new ExperimentProblem<>(new ZDT1(40), "ZDT140"));
    problemList.add(new ExperimentProblem<>(new ZDT1(50), "ZDT150"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZDTScalabilityStudy")
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setOutputParetoFrontFileName("FUN")
                    .setOutputParetoSetFileName("VAR")
                    .setReferenceFrontDirectory("resources/referenceFrontsCSV")
                    .setReferenceFrontDirectory(
                            experimentBaseDirectory + "/ZDTScalabilityStudy/referenceFronts")
                    .setIndicatorList(Arrays.asList(
                            new Epsilon(),
                            new PISAHypervolume(),
                            new InvertedGenerationalDistancePlus()))
                    .setIndependentRuns(INDEPENDENT_RUNS)
                    .setNumberOfCores(8)
                    .build();
    new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
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
      smpso(problemList, algorithms, run);
      nsgaii(problemList, algorithms, run);
      spea2(problemList, algorithms, run);
    }
    return algorithms;
  }

  private static void spea2(List<ExperimentProblem<DoubleSolution>> problemList,
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run) {
    for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
      Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(
          experimentProblem.getProblem(),
          new SBXCrossover(1.0, 10.0),
          new PolynomialMutation(1.0 / experimentProblem.getProblem().numberOfVariables(),
              20.0))
          .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
    }
  }

  private static void nsgaii(List<ExperimentProblem<DoubleSolution>> problemList,
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run) {
    for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(
          experimentProblem.getProblem(),
          new SBXCrossover(1.0, 20.0),
          new PolynomialMutation(1.0 / experimentProblem.getProblem().numberOfVariables(),
              20.0),
          100)
          .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
    }
  }

  private static void smpso(List<ExperimentProblem<DoubleSolution>> problemList,
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run) {
    for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
      double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
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
  }
}