package org.uma.jmetal.auto.autoconfigurablealgorithm.experiments;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
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
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT3;
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
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * TODO
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTSLargeScaleStudy {

  private static final int EVALUATIONS_LIMIT = 25000000;

  public static void main(String[] args) throws RuntimeException, IOException {
    Check.that(args.length != 3, "The program needs two arguments") ;

    int lowerBound = Integer.valueOf(args[0]) ;
    int upperBound = Integer.valueOf(args[1]) ;

    System.out.println("Range: " + lowerBound + ", " + upperBound) ;
    // 1.- Create the problems
    List<Integer> numberOfVariables = new ArrayList<>();
    //IntStream.range(11, 18).forEach(i -> numberOfVariables.add(((int) Math.pow(2, i))));
    IntStream.range(lowerBound, upperBound).forEach(i -> numberOfVariables.add(((int) Math.pow(2, i))));

    numberOfVariables.forEach(System.out::println);

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    for (int variables : numberOfVariables) {
      problemList.add(
          new ExperimentProblem<>(new ZDT1(variables), "ZDT1" + variables).setReferenceFront(
              "resources/referenceFrontsCSV/ZDT1.csv"));
      problemList.add(
          new ExperimentProblem<>(new ZDT2(variables), "ZDT2" + variables).setReferenceFront(
              "resources/referenceFrontsCSV/ZDT2.csv"));
      problemList.add(
          new ExperimentProblem<>(new ZDT3(variables), "ZDT3" + variables).setReferenceFront(
              "resources/referenceFrontsCSV/ZDT3.csv"));
      problemList.add(
          new ExperimentProblem<>(new ZDT6(variables), "ZDT6" + variables).setReferenceFront(
              "resources/referenceFrontsCSV/ZDT6.csv"));
      /*
      problemList.add(
          new ExperimentProblem<>(new ZDT4(variables), "ZDT4" + variables).setReferenceFront(
              "resources/referenceFrontsCSV/ZDT4.csv"));

      */
    }

    // 2.- Create the algorithms
    List<EvolutionaryAlgorithm<DoubleSolution>> algorithms = new ArrayList<>();
    problemList.forEach(problem -> {
      try {
        algorithms.add(configureAndCreateAutoNSGAII(problem));
        algorithms.add(configureAndCreateNSGAII(problem));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    algorithms.forEach(i -> System.out.println(i.getName()));
    algorithms.stream().forEach(algorithm -> {
      algorithm.run();
      TerminationByQualityIndicator termination = (TerminationByQualityIndicator) algorithm.getTermination();
      var problem = (ExperimentProblem<DoubleSolution>) algorithm.getAttributes().get("Problem");
      System.out.println(
          "Algorithm: " + algorithm.getName() + ". Problem: " + problem.getTag() + ". Failure: "
              + termination.evaluationsLimitReached() +
              ". Evaluations: " + algorithm.getNumberOfEvaluations() + ". Time: "
              + algorithm.getTotalComputingTime());
    });
  }

  private static EvolutionaryAlgorithm<DoubleSolution> configureAndCreateNSGAII(
      ExperimentProblem<DoubleSolution> experimentProblem) throws IOException {
    String name = "NSGAII";

    var densityEstimator = new CrowdingDistanceDensityEstimator<DoubleSolution>();
    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    var createInitialPopulation = new RandomSolutionsCreation<>(experimentProblem.getProblem(),
        100);

    var replacement =
        new RankingAndDensityEstimatorReplacement<DoubleSolution>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    var crossover = new SBXCrossover(0.9, 20);
    var mutation = new PolynomialMutation(
        1.0 / experimentProblem.getProblem().getNumberOfVariables(),
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
        readVectors(experimentProblem.getReferenceFront(), ","),
        0.90, EVALUATIONS_LIMIT);

    var evaluation = new SequentialEvaluation<>(experimentProblem.getProblem());

    var algorithm = new EvolutionaryAlgorithm<>(name,
        createInitialPopulation, evaluation, termination, selection, variation, replacement);

    algorithm.getAttributes().put("Problem", experimentProblem);

    return algorithm;
  }

  private static EvolutionaryAlgorithm<DoubleSolution> configureAndCreateAutoNSGAII(
      ExperimentProblem<DoubleSolution> experimentProblem) throws IOException {
    String name = "AutoNSGAII";

    var densityEstimator = new CrowdingDistanceDensityEstimator<DoubleSolution>();
    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    var createInitialPopulation = new RandomSolutionsCreation<>(experimentProblem.getProblem(),
        56);

    var replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    var crossover = new BLXAlphaCrossover(0.885, 0.9408, new RepairDoubleSolutionWithBoundValue());
    var mutation = new NonUniformMutation(
        0.4534 * 1.0 / experimentProblem.getProblem().getNumberOfVariables(),
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
        readVectors(experimentProblem.getReferenceFront(), ","),
        0.90, EVALUATIONS_LIMIT);

    Archive<DoubleSolution> externalArchive = new CrowdingDistanceArchive<>(100);

    var evaluation = new SequentialEvaluationWithArchive<>(experimentProblem.getProblem(),
        externalArchive);

    var algorithm = new EvolutionaryAlgorithm<>(name,
        createInitialPopulation, evaluation, termination, selection, variation, replacement);

    algorithm.getAttributes().put("Problem", experimentProblem);

    return algorithm;
  }
}