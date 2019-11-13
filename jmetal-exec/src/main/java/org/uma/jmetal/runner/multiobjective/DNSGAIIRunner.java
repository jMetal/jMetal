package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.DNSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndDirScoreDistanceComparator;
import org.uma.jmetal.util.fileinput.VectorFileUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * created at 2:50 pm, 2019/1/29
 * runner of DIR-Enhanced NSGA-II
 *
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
public class DNSGAIIRunner extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws FileNotFoundException {

    String referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/DTLZ2.3D.pf";

    // problem
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    Problem<DoubleSolution> problem = ProblemUtils.loadProblem(problemName);

    // crossover
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 30;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    // mutation
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    // selection
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
            new RankingAndDirScoreDistanceComparator<>());

    int populationSize = 300;
    Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
            .setMaxEvaluations(300000)
            .setVariant(NSGAIIBuilder.NSGAIIVariant.DNSGAII)
            .setSelectionOperator(selection).build();

    // reference vectors
    double[][] referenceVectors = VectorFileUtils.readVectors("MOEAD_Weights/W" + problem.getNumberOfObjectives() + "D_" + populationSize + ".dat");
    ((DNSGAII<DoubleSolution>) algorithm).setReferenceVectors(referenceVectors);

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);

    printQualityIndicators(population, referenceParetoFront);
  }
}
