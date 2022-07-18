package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.DNSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.comparator.RankingAndDirScoreDistanceComparator;
import org.uma.jmetal.util.fileinput.VectorFileUtils;

/**
 * created at 2:50 pm, 2019/1/29
 * runner of DIR-Enhanced NSGA-II
 *
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
public class DNSGAIIRunner extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws FileNotFoundException {

    @NotNull String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ1.3D.csv";

    // problem
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    // crossover
    var crossoverProbability = 0.9;
    double crossoverDistributionIndex = 30;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    // mutation
    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    // selection
    @NotNull SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
            new RankingAndDirScoreDistanceComparator<>());

    var populationSize = 300;
    Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
            .setMaxEvaluations(300000)
            .setVariant(NSGAIIBuilder.NSGAIIVariant.DNSGAII)
            .setSelectionOperator(selection).build();

    // reference vectors
    var referenceVectors = VectorFileUtils.readVectors("resources/weightVectorFiles/moead/W" + problem.getNumberOfObjectives() + "D_" + populationSize + ".dat");
    ((DNSGAII<DoubleSolution>) algorithm).setReferenceVectors(referenceVectors);

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
    var population = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);

    printQualityIndicators(population, referenceParetoFront);
  }
}
