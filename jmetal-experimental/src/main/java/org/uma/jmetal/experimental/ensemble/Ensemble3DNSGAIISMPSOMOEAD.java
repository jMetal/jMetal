package org.uma.jmetal.experimental.ensemble;

import org.uma.jmetal.util.AbstractAlgorithmRunner;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class Ensemble3DNSGAIISMPSOMOEAD extends AbstractAlgorithmRunner {
/*
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;

    // problemName = "org.uma.jmetal.problem.multiobjective.maf.MaF01PF_M5";
    String referenceParetoFront = "resources/referenceFronts/MaF01PF_M5.csv";

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
             problem,
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
              .filter(solution -> algorithm.getName() == solution.attributes().get("ALGORITHM_NAME"))
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

 */
}
