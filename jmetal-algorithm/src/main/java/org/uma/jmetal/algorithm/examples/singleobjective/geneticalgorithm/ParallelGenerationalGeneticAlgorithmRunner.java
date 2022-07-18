package org.uma.jmetal.algorithm.examples.singleobjective.geneticalgorithm;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to configure and run a parallel (multithreaded) generational genetic algorithm. The number
 * of cores is specified as an optional parameter. A default value is used is the parameter is not
 * provided. The target problem is OneMax
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ParallelGenerationalGeneticAlgorithmRunner {
  private static final int DEFAULT_NUMBER_OF_CORES = 0;
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.ParallelGenerationalGeneticAlgorithmRunner
   * [cores]
   */
  public static void main(String @NotNull [] args) throws Exception {
    Algorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(512);

    int numberOfCores;
    if (args.length == 1) {
      numberOfCores = Integer.valueOf(args[0]);
    } else {
      numberOfCores = DEFAULT_NUMBER_OF_CORES;
    }

    CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(0.9);
    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getBitsFromVariable(0));
    @NotNull SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator =
        new BinaryTournamentSelection<BinarySolution>();

    GeneticAlgorithmBuilder<BinarySolution> builder =
        new GeneticAlgorithmBuilder<BinarySolution>(problem, crossoverOperator, mutationOperator)
            .setPopulationSize(100)
            .setMaxEvaluations(25000)
            .setSelectionOperator(selectionOperator)
            .setSolutionListEvaluator(
                new MultiThreadedSolutionListEvaluator<BinarySolution>(numberOfCores));

    algorithm = builder.build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    builder.getEvaluator().shutdown();

    BinarySolution solution = algorithm.getResult();
    @NotNull List<BinarySolution> population = new ArrayList<>(1);
    population.add(solution);

    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
