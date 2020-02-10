package org.uma.jmetal.lab.experiment.studies;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDEWithArchive;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIWithArchive;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOWithArchive;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOA;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOAWithArchive;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class PPSN20203DStudy {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new DTLZ1()).changeReferenceFrontTo("DTLZ1.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ2()).changeReferenceFrontTo("DTLZ2.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).changeReferenceFrontTo("DTLZ3.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ4()).changeReferenceFrontTo("DTLZ4.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ5()).changeReferenceFrontTo("DTLZ5.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ6()).changeReferenceFrontTo("DTLZ6.3D.pf"));
    problemList.add(new ExperimentProblem<>(new DTLZ7()).changeReferenceFrontTo("DTLZ7.3D.pf"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("PPSN2020Study")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("referenceFronts")
            .setIndicatorList(
                Arrays.asList(
                    new Epsilon<DoubleSolution>(),
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
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
  }

  public static Algorithm<List<DoubleSolution>> createNSGAII(Problem<DoubleSolution> problem) {
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(50000);

    Algorithm<List<DoubleSolution>> algorithm =
        new NSGAII<>(
            problem,
            populationSize,
            offspringPopulationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createNSGAIIWithArchive(
      Problem<DoubleSolution> problem) {
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(50000);

    Archive<DoubleSolution> archive = new NonDominatedSolutionListArchive<>();

    Algorithm<List<DoubleSolution>> algorithm =
        new NSGAIIWithArchive<>(
            problem,
            populationSize,
            offspringPopulationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination,
            archive,
            100);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createSMSEMOA(Problem<DoubleSolution> problem) {
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;

    Termination termination = new TerminationByEvaluations(50000);

    Algorithm<List<DoubleSolution>> algorithm =
        new SMSEMOA<>(
            problem,
            populationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createSMSEMOAWithArchive(
      Problem<DoubleSolution> problem) {
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;

    Termination termination = new TerminationByEvaluations(50000);

    Algorithm<List<DoubleSolution>> algorithm =
        new SMSEMOAWithArchive<>(
            problem,
            populationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination,
            new NonDominatedSolutionListArchive<>(),
            100);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createSMPSO(Problem<DoubleSolution> problem) {
    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>();
    Termination termination = new TerminationByEvaluations(50000);

    Algorithm<List<DoubleSolution>> algorithm =
        new SMPSO(
            (DoubleProblem) problem,
            swarmSize,
            leadersArchive,
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            evaluation,
            termination);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createMOEADDE(Problem<DoubleSolution> problem) {
    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 50000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    Algorithm<List<DoubleSolution>> algorithm =
        new MOEADDE(
            problem,
            populationSize,
            maximumNumberOfFunctionEvaluations,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "MOEAD_WEIGHTS");

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createMOEADDEWithArchive(
      Problem<DoubleSolution> problem) {
    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 50000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    Archive<DoubleSolution> externalArchive = new NonDominatedSolutionListArchive<>();

    Algorithm<List<DoubleSolution>> algorithm =
        new MOEADDEWithArchive(
            problem,
            populationSize,
            maximumNumberOfFunctionEvaluations,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "MOEAD_WEIGHTS",
            externalArchive,
            100);

    return algorithm;
  }

  public static Algorithm<List<DoubleSolution>> createSMPSOWithExternalArchive(
      Problem<DoubleSolution> problem) {
    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>();
    Termination termination = new TerminationByEvaluations(50000);

    Archive<DoubleSolution> externalArchive = new NonDominatedSolutionListArchive<>();

    Algorithm<List<DoubleSolution>> algorithm =
        new SMPSOWithArchive(
            (DoubleProblem) problem,
            swarmSize,
            leadersArchive,
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            evaluation,
            termination,
            externalArchive,
            100);

    return algorithm;
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
        algorithms.add(
            new ExperimentAlgorithm<>(
                createNSGAII(problemList.get(i).getProblem()), "NSGAII", problemList.get(i), run));
        algorithms.add(
            new ExperimentAlgorithm<>(
                createNSGAIIWithArchive(problemList.get(i).getProblem()),
                "NSGAIIA",
                problemList.get(i),
                run));
        algorithms.add(
            new ExperimentAlgorithm<>(
                createMOEADDE(problemList.get(i).getProblem()), "MOEAD", problemList.get(i), run));
        algorithms.add(
            new ExperimentAlgorithm<>(
                createMOEADDEWithArchive(problemList.get(i).getProblem()),
                "MOEADA",
                problemList.get(i),
                run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMPSO(problemList.get(i).getProblem()), "SMPSO", problemList.get(i), run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMPSOWithExternalArchive(problemList.get(i).getProblem()),
                        "SMPSOA",
                        problemList.get(i),
                        run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMSEMOA(problemList.get(i).getProblem()), "SMSEMOA", problemList.get(i), run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMSEMOAWithArchive(problemList.get(i).getProblem()),
                        "SMPSOAA",
                        problemList.get(i),
                        run));
      }
    }
    return algorithms;
  }
}
