//

//

package org.uma.jmetal.lab.experiment.studies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHCBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateReferenceParetoFront;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.HUXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.multiobjective.OneZeroMax;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Example of experimental study based on solving two binary problems with four algorithms: NSGAII,
 * SPEA2, MOCell, and MOCHC
 *
 * This org.uma.jmetal.experiment assumes that the reference Pareto front are not known, so the must be produced.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the org.uma.jmetal.experiment are: 1. Configure the org.uma.jmetal.experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts 4. Compute que quality indicators 5. Generate Latex
 * tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
 * Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class BinaryProblemsStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<BinarySolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT5()));
    problemList.add(new ExperimentProblem<>(new OneZeroMax(512)));

    List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<BinarySolution, List<BinarySolution>> experiment;
    experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("BinaryProblemsStudy")
        .setAlgorithmList(algorithmList)
        .setProblemList(problemList)
        .setExperimentBaseDirectory(experimentBaseDirectory)
        .setOutputParetoFrontFileName("FUN")
        .setOutputParetoSetFileName("VAR")
        .setReferenceFrontDirectory(experimentBaseDirectory + "/BinaryProblemsStudy/referenceFronts")
        .setIndicatorList(Arrays.asList(
            new Epsilon(),
            new Spread(),
            new GenerationalDistance(),
            new PISAHypervolume(),
                new NormalizedHypervolume(),
                new InvertedGenerationalDistance(),
            new InvertedGenerationalDistancePlus())
        )
        .setIndependentRuns(INDEPENDENT_RUNS)
        .setNumberOfCores(8)
        .build();

    new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoFront(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(2).setDisplayNotch().run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */

  static List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> configureAlgorithmList(
      List<ExperimentProblem<BinarySolution>> problemList) {
    List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {

      for (ExperimentProblem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<>(
                problem.getProblem(),
                new SinglePointCrossover(1.0),
                new BitFlipMutation(
                        1.0 / ((BinaryProblem) problem.getProblem()).bitsFromVariable(0)),
                100)
                .setMaxEvaluations(25000)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problem, run));
      }

      for (ExperimentProblem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new SPEA2Builder<>(
                problem.getProblem(),
                new SinglePointCrossover(1.0),
                new BitFlipMutation(
                        1.0 / ((BinaryProblem) problem.getProblem()).bitsFromVariable(0)))
                .setMaxIterations(250)
                .setPopulationSize(100)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problem, run));
      }

      for (ExperimentProblem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new MOCellBuilder<>(
                problem.getProblem(),
                new SinglePointCrossover(1.0),
                new BitFlipMutation(
                        1.0 / ((BinaryProblem) problem.getProblem()).bitsFromVariable(0)))
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problem, run));
      }

      for (ExperimentProblem<BinarySolution> problem : problemList) {
        CrossoverOperator<BinarySolution> crossoverOperator;
        MutationOperator<BinarySolution> mutationOperator;
        SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection;
        SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;

        crossoverOperator = new HUXCrossover(1.0);
        parentsSelection = new RandomSelection<>();
        newGenerationSelection = new RankingAndCrowdingSelection<>(100);
        mutationOperator = new BitFlipMutation(0.35);
        Algorithm<List<BinarySolution>> algorithm = new MOCHCBuilder(
                (BinaryProblem) problem.getProblem())
                .setInitialConvergenceCount(0.25)
                .setConvergenceValue(3)
                .setPreservedPopulation(0.05)
                .setPopulationSize(100)
                .setMaxEvaluations(25000)
                .setCrossover(crossoverOperator)
                .setNewGenerationSelection(newGenerationSelection)
                .setCataclysmicMutation(mutationOperator)
                .setParentSelection(parentsSelection)
                .setEvaluator(new SequentialSolutionListEvaluator<>())
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, problem, run));
      }
    }
    return algorithms;
  }
}
