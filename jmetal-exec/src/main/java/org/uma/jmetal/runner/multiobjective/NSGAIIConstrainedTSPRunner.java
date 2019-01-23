package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.MultiobjectiveTSP;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Class for configuring and running the NSGA-II algorithm to solve the bi-objective TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIIConstrainedTSPRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIITSPRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, IOException {
    JMetalRandom.getInstance().setSeed(100L);

    MultiobjectiveTSP problem;
    Algorithm<List<PermutationSolution<Integer>>> algorithm;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    problem = new MultiobjectiveTSP("/tspInstances/kroA100.tsp", "/tspInstances/kroB100.tsp");

    Function<PermutationSolution<Integer>, Double> constraint1 ;
    Function<PermutationSolution<Integer>, Double> constraint2 ;
    Function<PermutationSolution<Integer>, Double> constraint3 ;
    Function<PermutationSolution<Integer>, Double> constraint4 ;
    constraint1 = solution -> solution.getVariableValue(0) == 78 ? 0.0 : -1.0 * solution.getVariables().indexOf(78);
    constraint2 = solution -> solution.getVariables().indexOf(52) == (solution.getVariables().indexOf(9) - 1) ? 0.0 : -1.0 *Math.abs(solution.getVariables().indexOf(52)-solution.getVariables().indexOf(9));
    constraint3 = solution -> solution.getVariableValue(solution.getNumberOfVariables()-1) == 71 ? 0.0 : -1.0 * (solution.getNumberOfVariables()-solution.getVariables().indexOf(71));
    constraint4 = solution -> solution.getVariableValue(solution.getNumberOfVariables()/2) == 51 ? 0.0 : -1.0 * Math.abs((solution.getNumberOfVariables()/2)-solution.getVariables().indexOf(51));
    //problem.addConstraint(solution -> (solution.getVariableValue(0) == 78 ? 0.0 : -1.0)) ;
    problem.addConstraint(solution -> (constraint1.apply(solution))) ;
    problem.addConstraint(solution -> (constraint2.apply(solution))) ;
    problem.addConstraint(solution -> (constraint3.apply(solution))) ;
    problem.addConstraint(solution -> (constraint4.apply(solution))) ;

    crossover = new PMXCrossover(0.9) ;

    double mutationProbability = 0.2 ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
/**
 * List<Double> inters = new ArrayList<>();
 inters.add(0.0);
 inters.add(0.0);
 double epsilon =0.0001;
 algorithm = new RNSGAIIBuilder<>(problem, crossover, mutation,inters,epsilon)

 */
    int populationSize = 100;
    int maxEvaluations = 100000 ;
    algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover,
             mutation,populationSize)
            .setSelectionOperator(selection)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    List<PermutationSolution<Integer>> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
