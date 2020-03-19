package org.uma.jmetal.example.multiobjective.ensemble;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.ensemble.AlgorithmEnsemble;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOWithArchive;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.maf.MaF01;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class Ensemble3DNSGAIISMPSOMOEAD extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    String problemName;

    // problemName = "org.uma.jmetal.problem.multiobjective.maf.MaF01PF_M5";
    String referenceParetoFront = "resources/referenceFronts/MaF01PF_M5.txt";

    // problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);
    problem = new MaF01(12, 5);

    Archive<DoubleSolution> archive = new NonDominatedSolutionListArchive<>();

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(100000);

    Algorithm<List<DoubleSolution>> nsgaII =
        new NSGAII<>(
                problem,
                populationSize,
                offspringPopulationSize,
                new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
                new PolynomialMutation(mutationProbability, mutationDistributionIndex),
                termination)
            .withArchive(new NonDominatedSolutionListArchive<>());

    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    Algorithm<List<DoubleSolution>> smpso =
        new SMPSOWithArchive(
            (DoubleProblem) problem,
            swarmSize,
            leadersArchive,
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            evaluation,
            termination,
            new NonDominatedSolutionListArchive<>());

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    Archive<DoubleSolution> externalArchive = new NonDominatedSolutionListArchive<>();

    Algorithm<List<DoubleSolution>> moead =
        new MOEADDE(
                problem,
                495,
                cr,
                f,
                aggregativeFunction,
                neighborhoodSelectionProbability,
                maximumNumberOfReplacedSolutions,
                neighborhoodSize,
                "resources/weightVectorFiles/moead",
                termination)
            .withArchive(archive);

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaII);
    algorithmList.add(smpso);
    algorithmList.add(moead);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, archive);

    algorithmEnsemble.run();

    List<DoubleSolution> population =
        SolutionListUtils.distanceBasedSubsetSelection(algorithmEnsemble.getResult(), 240);
    JMetalLogger.logger.info(
        "Total execution time : " + algorithmEnsemble.getTotalComputingTime() + "ms");

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    for (Algorithm<List<DoubleSolution>> algorithm : algorithmList) {
      List<DoubleSolution> result =
          population.stream()
              .filter(solution -> algorithm.getName() == solution.getAttribute("ALGORITHM_NAME"))
              .collect(Collectors.toList());

      new SolutionListOutput(result)
          .setVarFileOutputContext(
              new DefaultFileOutputContext("VAR" + algorithm.getName() + ".csv", ","))
          .setFunFileOutputContext(
              new DefaultFileOutputContext("FUN" + algorithm.getName() + ".csv", ","))
          .print();
    }

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
