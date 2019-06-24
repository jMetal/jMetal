package org.uma.jmetal.runner.multiobjective;

import java.util.function.Function;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.RadioNetworkDesign;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRNDSemanticRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException Invoking command:
   *                               java org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<BinarySolution> problem;
    Algorithm<List<BinarySolution>> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    String referenceParetoFront = "";


    problem = new RadioNetworkDesign(100);
    crossover = new SinglePointCrossover(1.0);
    mutation =  new BitFlipMutation(
            1.0 / ((BinaryProblem) problem).getNumberOfBits(0));

    Function<BinarySolution, Double> constraint;
    constraint = solution -> !solution.getVariableValue(0).get(0) ? 0.0 : -1.0 * Double.MAX_VALUE;

    ((RadioNetworkDesign) problem).addConstraint(constraint);
    int populationSize = 100 ;
     algorithm = new NSGAIIBuilder<BinarySolution>(
           problem,
            crossover,
            mutation,
             populationSize)
            .setMaxEvaluations(25000)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<BinarySolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
