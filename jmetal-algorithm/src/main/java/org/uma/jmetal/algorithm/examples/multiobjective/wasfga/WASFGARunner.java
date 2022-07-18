package org.uma.jmetal.algorithm.examples.multiobjective.wasfga;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.MultiobjectiveTSP;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class WASFGARunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.WASFGA.WASFGABinaryRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, IOException {

      CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(0.9);

    var mutationProbability = 0.2;
      MutationOperator<PermutationSolution<Integer>> mutation = new PermutationSwapMutation<Integer>(mutationProbability);

      SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
              new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
    var referenceParetoFront = "";

      // problem = ProblemFactory.<DoubleSolution> loadProblem(problemName);
      PermutationProblem<PermutationSolution<Integer>> problem = new MultiobjectiveTSP("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");

      List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);
    /*
    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());*/

    var epsilon = 0.01;
      Algorithm<List<PermutationSolution<Integer>>> algorithm = new WASFGA<PermutationSolution<Integer>>(
              problem,
              100,
              250,
              crossover,
              mutation,
              selection,
              new SequentialSolutionListEvaluator<PermutationSolution<Integer>>(),
              epsilon,
              referencePoint);

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var population = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
