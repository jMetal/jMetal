package org.uma.jmetal.algorithm.examples.singleobjective.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
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
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class to configure and run a steady-state genetic algorithm. The target problem is TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SteadyStateGeneticAlgorithmBinaryEncodingRunner {
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.SteadyStateGeneticAlgorithmBinaryEncodingRunner
   */
  public static void main(String[] args) throws Exception {

      BinaryProblem problem = new OneMax(1024);

      CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(0.9);

    var mutationProbability = 1.0 / problem.getBitsFromVariable(0) ;
      MutationOperator<BinarySolution> mutation = new BitFlipMutation(mutationProbability);

      SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<BinarySolution>();

    var algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
              .setPopulationSize(50)
              .setMaxEvaluations(25000)
              .setSelectionOperator(selection)
              .setVariant(GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    var computingTime = algorithmRunner.getComputingTime() ;

    var solution = algorithm.getResult() ;
    List<BinarySolution> population = new ArrayList<>(1) ;
    population.add(solution) ;

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    JMetalLogger.logger.info("Fitness: " + solution.objectives()[0]) ;
    JMetalLogger.logger.info("Solution: " + solution.variables().get(0)) ;
  }
}
