package org.uma.jmetal.lab.experiment.studies;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.jmetal5version.SMPSOBuilder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with algorithms NSGAII, MOEA/D,
 * and SMPSO
 * <p>
 * This org.uma.jmetal.experiment assumes that the reference Pareto front are not known, so the names of files
 * containing them and the directory where they are located must be specified.
 * <p>
 * Six quality indicators are used for performance assessment.
 * <p>
 * The steps to carry out the org.uma.jmetal.experiment are: 1. Configure the org.uma.jmetal.experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts 4. Compute que quality indicators 5. Generate Latex
 * tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTComputingReferenceParetoFrontsStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = List.of(
            new ExperimentProblem<>(new ZDT1()),
            new ExperimentProblem<>(new ZDT2()),
            new ExperimentProblem<>(new ZDT3()),
            new ExperimentProblem<>(new ZDT4()),
            new ExperimentProblem<>(new ZDT6()));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    ExperimentBuilder<DoubleSolution, List<DoubleSolution>> zdt2Study =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZDTStudy2");
    zdt2Study.setAlgorithmList(algorithmList);
    zdt2Study.setProblemList(problemList);
    zdt2Study.setExperimentBaseDirectory(experimentBaseDirectory);
    zdt2Study.setOutputParetoFrontFileName("FUN");
    zdt2Study.setOutputParetoSetFileName("VAR");
    zdt2Study.setReferenceFrontDirectory(experimentBaseDirectory + "/ZDTStudy2/referenceFronts");
    zdt2Study.setIndicatorList(Arrays.asList(
            new Epsilon<>(),
            new Spread<>(),
            new GenerationalDistance<>(),
            new PISAHypervolume<>(),
            new NormalizedHypervolume<>(),
            new InvertedGenerationalDistance<>(),
            new InvertedGenerationalDistancePlus<>()));
    zdt2Study.setIndependentRuns(INDEPENDENT_RUNS);
    zdt2Study.setNumberOfCores(8);
    Experiment<DoubleSolution, List<DoubleSolution>> experiment = zdt2Study.build();

    new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}.
   *
   * @param problemList
   * @return
   */
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
        Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(experimentProblem.getProblem(), MOEADBuilder.Variant.MOEAD)
                .setCrossover(new DifferentialEvolutionCrossover(1.0, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN))
                .setMutation(new PolynomialMutation(1.0 / experimentProblem.getProblem().getNumberOfVariables(),
                        20.0))
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setResultPopulationSize(100)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(2)
                .setNeighborSize(20)
                .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
      }
    }
    return algorithms;
  }
}
