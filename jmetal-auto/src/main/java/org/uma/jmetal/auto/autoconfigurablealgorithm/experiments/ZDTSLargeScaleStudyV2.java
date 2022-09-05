package org.uma.jmetal.auto.autoconfigurablealgorithm.experiments;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByQualityIndicator;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT3;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT6;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * TODO
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTSLargeScaleStudyV2 {

  private static final int EVALUATIONS_LIMIT = 25000000;

  public static void main(String[] args) throws RuntimeException, IOException {
    Check.that(args.length != 4, "The program needs three arguments");

    int variables = Integer.valueOf(args[0]);
    String problemName = args[1];
    String algorithmName = args[2];

    // 1.- Create the problems

    DoubleProblem problem = null;
    String referenceFront = null;
    switch (problemName) {
      case "ZDT1":
        problem = new ZDT1(variables);
        referenceFront = "resources/referenceFrontsCSV/ZDT1.csv";
        break;
      case "ZDT2":
        problem = new ZDT2(variables);
        referenceFront = "resources/referenceFrontsCSV/ZDT2.csv";
        break;
      case "ZDT3":
        problem = new ZDT3(variables);
        referenceFront = "resources/referenceFrontsCSV/ZDT3.csv";
        break;
      case "ZDT4":
        problem = new ZDT4(variables);
        referenceFront = "resources/referenceFrontsCSV/ZDT4.csv";
        break;
      case "ZDT6":
        problem = new ZDT6(variables);
        referenceFront = "resources/referenceFrontsCSV/ZDT6.csv";
        break;
    }

    EvolutionaryAlgorithm<DoubleSolution> algorithm;

    if (algorithmName.equals("NSGAII")) {
      algorithm = configureAndCreateNSGAII(problem, referenceFront);
    } else {
      algorithm = configureAndCreateAutoNSGAII(problem, referenceFront);
    }

    algorithm.run();
    TerminationByQualityIndicator termination = (TerminationByQualityIndicator) algorithm.getTermination();
    System.out.println(
        "Algorithm: " + algorithm.getName() + ". Problem: " + problemName + ". Variables: " + variables + ". Failure: "
            + termination.evaluationsLimitReached() +
            ". Evaluations: " + algorithm.getNumberOfEvaluations() + ". Time: "
            + algorithm.getTotalComputingTime());

    String outputFileName = algorithmName + "." + problemName + "." + variables +".csv" ;
    new SolutionListOutput(algorithm.getResult())
        .setFunFileOutputContext(new DefaultFileOutputContext(outputFileName, ","))
        .print();
  }

  private static EvolutionaryAlgorithm<DoubleSolution> configureAndCreateNSGAII(
      Problem<DoubleSolution> problem, String referenceFront) throws IOException {
    String name = "NSGAII";

    var densityEstimator = new CrowdingDistanceDensityEstimator<DoubleSolution>();
    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    var createInitialPopulation = new RandomSolutionsCreation<>(problem,
        100);

    var replacement =
        new RankingAndDensityEstimatorReplacement<DoubleSolution>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    var crossover = new SBXCrossover(0.9, 20);
    var mutation = new PolynomialMutation(
        1.0 / problem.getNumberOfVariables(),
        20.0);

    int offspringPopulationSize = 100;
    var variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    int tournamentSize = 2;
    var selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::getValue).reversed())));

    Termination termination = new TerminationByQualityIndicator(
        new PISAHypervolume(),
        readVectors(referenceFront, ","),
        0.90, EVALUATIONS_LIMIT);

    var evaluation = new SequentialEvaluation<>(problem);

    var algorithm = new EvolutionaryAlgorithm<>(name,
        createInitialPopulation, evaluation, termination, selection, variation, replacement);

    return algorithm;
  }

  private static EvolutionaryAlgorithm<DoubleSolution> configureAndCreateAutoNSGAII(
      Problem<DoubleSolution> problem, String referenceFront) throws IOException {
    String name = "AutoNSGAII";

    var densityEstimator = new CrowdingDistanceDensityEstimator<DoubleSolution>();
    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    var createInitialPopulation = new RandomSolutionsCreation<>(problem,
        56);

    var replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    var crossover = new BLXAlphaCrossover(0.885, 0.9408, new RepairDoubleSolutionWithBoundValue());
    var mutation = new NonUniformMutation(
        0.4534 * 1.0 / problem.getNumberOfVariables(),
        0.2995, 40000, new RepairDoubleSolutionWithOppositeBoundValue());

    int offspringPopulationSize = 100;
    var variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    int tournamentSize = 9;
    var selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::getValue).reversed())));

    var termination = new TerminationByQualityIndicator(
        new PISAHypervolume(),
        readVectors(referenceFront, ","),
        0.90, EVALUATIONS_LIMIT);

    Archive<DoubleSolution> externalArchive = new CrowdingDistanceArchive<>(100);

    var evaluation = new SequentialEvaluationWithArchive<>(problem,
        externalArchive);

    var algorithm = new EvolutionaryAlgorithm<>(name,
        createInitialPopulation, evaluation, termination, selection, variation, replacement);

    return algorithm;
  }
}