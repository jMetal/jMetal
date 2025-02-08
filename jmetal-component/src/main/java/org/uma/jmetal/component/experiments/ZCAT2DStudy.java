package org.uma.jmetal.component.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.algorithm.Algorithm;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.*;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanHolmTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.DefaultZCATSettings;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

/**
 * Example of experimental study based on solving the ZCAT problems with two objectives using
 * several metaheuristics. The ZCAT benchmark must be properly configured by setting two objectives
 * in class {@link DefaultZCATSettings}
 *
 * <p>Four quality indicators are used for performance assessment: {@link Epsilon}, {@link
 * NormalizedHypervolume}, {@link PISAHypervolume}, and {@link InvertedGenerationalDistancePlus}.
 *
 * <p>The steps to carry out are: 1. Configure the experiment 2. Execute the algorithms 3. Compute
 * que quality indicators 4. Generate Latex tables reporting means and medians, and tables with
 * statistical tests 5. Generate HTML pages with tables, boxplots, and fronts.
 *
 * @author Antonio J. Nebro
 */
public class ZCAT2DStudy {
  private static final int INDEPENDENT_RUNS = 15;
  private static final int POPULATION_SIZE = 100;
  private static final int MAX_EVALUATIONS = 1000 * POPULATION_SIZE;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    // Create a list with the problems
    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    IntStream.rangeClosed(1, 20)
        .forEach(
            i -> {
              Problem<DoubleSolution> problem =
                  ProblemFactory.loadProblem("org.uma.jmetal.problem.multiobjective.zcat.ZCAT" + i);
              problemList.add(
                  new ExperimentProblem<>(problem).setReferenceFront("ZCAT" + i + ".2D.csv"));
            });

    // Create a list with the algorithms
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    // Experiment configuration
    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZCAT2DStudyLevel2100k")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setReferenceFrontDirectory("resources/referenceFrontsCSV")
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndicatorList(
                List.of(
                    new Epsilon(),
                    new PISAHypervolume(),
                    new NormalizedHypervolume(),
                    new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    // Execution and generation of statistical stuff
    new ExecuteAlgorithms<>(experiment).run() ;
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateFriedmanHolmTestTables<>(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(5).setColumns(4).run();

    //new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        nsgaii(algorithms, run, experimentProblem);
        nsgaiide(algorithms, run, experimentProblem);
        moead(algorithms, run, experimentProblem);
        moeadde(algorithms, run, experimentProblem);
        smpso(algorithms, run, experimentProblem);
        smsemoa(algorithms, run, experimentProblem);
        smsemoade(algorithms, run, experimentProblem);
      }
    }
    return algorithms;
  }

  private static void nsgaii(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
      int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int offspringPopulationSize = POPULATION_SIZE;

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new NSGAIIBuilder<>(
                experimentProblem.getProblem(),
                POPULATION_SIZE,
                offspringPopulationSize,
                crossover,
                mutation)
            .setTermination(termination)
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAII", experimentProblem, run));
  }

  private static void nsgaiide(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
      int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    double cr = 1.0;
    double f = 0.5;

    Algorithm<List<DoubleSolution>> algorithm =
        new NSGAIIDEBuilder(
                experimentProblem.getProblem(),
                POPULATION_SIZE,
                cr,
                f,
                new PolynomialMutation(
                    1.0 / experimentProblem.getProblem().numberOfVariables(), 20.0),
                DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
            .setTermination(termination)
            .build();
    algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIDE", experimentProblem, run));
  }

  private static void moead(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
      int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(POPULATION_SIZE);
    boolean normalizeObjectives = true;

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new MOEADBuilder<>(
                experimentProblem.getProblem(),
                POPULATION_SIZE,
                crossover,
                mutation,
                weightVectorDirectory,
                sequenceGenerator,
                normalizeObjectives)
            .setTermination(termination)
            .setAggregationFunction(new PenaltyBoundaryIntersection(5.0, normalizeObjectives))
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "MOEAD", experimentProblem, run));
  }

  private static void moeadde(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
      int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    double cr = 1.0;
    double f = 0.5;

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(POPULATION_SIZE);

    boolean normalizeObjectives = false;
    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new MOEADDEBuilder(
                experimentProblem.getProblem(),
                POPULATION_SIZE,
                cr,
                f,
                mutation,
                weightVectorDirectory,
                sequenceGenerator,
                normalizeObjectives)
            .setTermination(termination)
            .setMaximumNumberOfReplacedSolutionsy(2)
            .setNeighborhoodSelectionProbability(0.9)
            .setNeighborhoodSize(20)
            .setAggregationFunction(new Tschebyscheff(normalizeObjectives))
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "MOEADDE", experimentProblem, run));
  }

  private static void smpso(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
      int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    ParticleSwarmOptimizationAlgorithm algorithm =
        new SMPSOBuilder((DoubleProblem) experimentProblem.getProblem(), POPULATION_SIZE)
            .setTermination(termination)
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "SMPSO", experimentProblem, run));
  }

  private static void smsemoa(
          List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
          int run,
          ExperimentProblem<DoubleSolution> experimentProblem) {

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    var algorithm = new SMSEMOABuilder<>(
            experimentProblem.getProblem(),
            POPULATION_SIZE,
            crossover,
            mutation)
            .setTermination(termination)
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "SMSEMOA", experimentProblem, run));
  }

  private static void smsemoade(
          List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms,
          int run,
          ExperimentProblem<DoubleSolution> experimentProblem) {

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    double cr = 1.0;
    double f = 0.5;
    EvolutionaryAlgorithm<DoubleSolution> algorithm = new SMSEMOADEBuilder(
            experimentProblem.getProblem(),
            POPULATION_SIZE,
            cr,
            f,
            mutation,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
            .setTermination(termination)
            .build();

    algorithms.add(new ExperimentAlgorithm<>(algorithm, "SMSEMOADE", experimentProblem, run));
  }
}
