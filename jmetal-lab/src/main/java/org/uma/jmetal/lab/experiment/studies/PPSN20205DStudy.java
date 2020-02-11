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
import org.uma.jmetal.lab.experiment.util.AlgorithmReturningASubSetOfSolutions;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.problem.multiobjective.maf.*;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
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
public class PPSN20205DStudy {

  private static final int INDEPENDENT_RUNS = 20;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new MaF01(12, 5)).setReferenceFront("MaF01PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF02(12, 5)).setReferenceFront("MaF02PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF03(12, 5)).setReferenceFront("MaF03PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF04(12, 5)).setReferenceFront("MaF04PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF05(12, 5)).setReferenceFront("MaF05PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF06(12, 5)).setReferenceFront("MaF06PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF07(12, 5)).setReferenceFront("MaF07PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF08(2, 5)).setReferenceFront("MaF08PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF09(2, 5)).setReferenceFront("MaF09PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF10(12, 5)).setReferenceFront("MaF10PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF11(12, 5)).setReferenceFront("MaF11PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF12(12, 5)).setReferenceFront("MaF12PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF13(5, 5)).setReferenceFront("MaF13PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF14(60, 5)).setReferenceFront("MaF14PF_M5.txt"));
    problemList.add(new ExperimentProblem<>(new MaF15(60, 5)).setReferenceFront("MaF15PF_M5.txt"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("PPSN2020S5Dtudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("resources/referenceFronts")
            .setIndicatorList(
                Arrays.asList(
                    new Epsilon<DoubleSolution>(),
                    new PISAHypervolume<DoubleSolution>(),
                    new InvertedGenerationalDistance<DoubleSolution>(),
                    new InvertedGenerationalDistancePlus<DoubleSolution>()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(32)
            .build();

    new ExecuteAlgorithms<>(experiment).run();

    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
  }

  public static Algorithm<List<DoubleSolution>> createAlgorithmToSelectPartOfTheResultSolutionList(
          Algorithm<List<DoubleSolution>> algorithm, int numberOfReturnedSolutions) {

    AlgorithmReturningASubSetOfSolutions algorithmReturningASubSetOfSolutions =
            new AlgorithmReturningASubSetOfSolutions(algorithm, numberOfReturnedSolutions);

    return algorithmReturningASubSetOfSolutions;
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
                    archive);

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
                    new NonDominatedSolutionListArchive<>());

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
                    cr,
                    f,
                    aggregativeFunction,
                    neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions,
                    neighborhoodSize,
                    "resources/weightVectorFiles/moead",
                    new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));

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
                    cr,
                    f,
                    aggregativeFunction,
                    neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions,
                    neighborhoodSize,
                    "resources/weightVectorFiles/moead",
                    externalArchive,
                    new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));
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
                    externalArchive);

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
                        createAlgorithmToSelectPartOfTheResultSolutionList(
                                createNSGAIIWithArchive(problemList.get(i).getProblem()), 100),
                        "NSGAIIA",
                        problemList.get(i),
                        run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createAlgorithmToSelectPartOfTheResultSolutionList(
                                createMOEADDE(problemList.get(i).getProblem()), 100),
                        "MOEAD",
                        problemList.get(i),
                        run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createAlgorithmToSelectPartOfTheResultSolutionList(
                                createMOEADDEWithArchive(problemList.get(i).getProblem()), 100),
                        "MOEADA",
                        problemList.get(i),
                        run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMPSO(problemList.get(i).getProblem()), "SMPSO", problemList.get(i), run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createAlgorithmToSelectPartOfTheResultSolutionList(
                                createSMPSOWithExternalArchive(problemList.get(i).getProblem()), 100),
                        "SMPSOA",
                        problemList.get(i),
                        run));
        /*
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMSEMOA(problemList.get(i).getProblem()), "SMSEMOA", problemList.get(i), run));
        algorithms.add(
                new ExperimentAlgorithm<>(
                        createSMSEMOAWithArchive(problemList.get(i).getProblem()),
                        "SMSEMOAA",
                        problemList.get(i),
                        run));

         */
      }
    }
    return algorithms;
  }
}
