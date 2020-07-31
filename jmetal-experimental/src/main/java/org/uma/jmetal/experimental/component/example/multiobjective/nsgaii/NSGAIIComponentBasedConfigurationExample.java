package org.uma.jmetal.experimental.component.example.multiobjective.nsgaii;

import org.uma.jmetal.experimental.component.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.experimental.component.catalogue.densityestimator.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.experimental.component.catalogue.densityestimator.DensityEstimator;
import org.uma.jmetal.experimental.component.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.component.catalogue.evaluation.SequentialEvaluation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.ranking.Ranking;
import org.uma.jmetal.experimental.component.catalogue.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.experimental.component.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.component.catalogue.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.component.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.termination.Termination;
import org.uma.jmetal.experimental.component.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.experimental.component.catalogue.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm configured with standard settings.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIComponentBasedConfigurationExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    NSGAII<DoubleSolution> algorithm;

    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ2.3D.csv";

    problem = ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int populationSize = 100;
    int offspringPopulationSize = 100 ;
    int maxNumberOfEvaluations = 25000;

    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation =
        new RandomSolutionsCreation<>(problem, populationSize);

    RankingAndDensityEstimatorReplacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    CrossoverAndMutationVariation<DoubleSolution> variation =
        new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    MatingPoolSelection<DoubleSolution> selection =
        new NaryTournamentMatingPoolSelection<>(
            2,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    ranking.getSolutionComparator(), densityEstimator.getSolutionComparator())));

    Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    algorithm =
        new NSGAII<>(
            evaluation,
            initialSolutionsCreation,
            termination,
            selection,
            variation,
            replacement);

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
