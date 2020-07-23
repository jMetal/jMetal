package org.uma.jmetal.component.example.singleobjective.geneticalgorithm;

import org.uma.jmetal.component.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithm;
import org.uma.jmetal.component.catalogue.termination.Termination;
import org.uma.jmetal.component.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.List;

/**
 * Class to configure and run a genetic algorithm to solve an instance of the TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GeneticAlgorithmTSPExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    PermutationProblem<PermutationSolution<Integer>> problem;
    GeneticAlgorithm<PermutationSolution<Integer>> algorithm;
    NaryTournamentSelection<PermutationSolution<Integer>> selection ;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;

    problem = new TSP("resources/tspInstances/kroA100.tsp");

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    selection = new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0)) ;

    crossover = new PMXCrossover(0.9) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    Termination termination = new TerminationByEvaluations(150000);

    algorithm =
            new GeneticAlgorithm<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    selection,
                    crossover,
                    mutation,
                    termination);

    algorithm.run();

    List<PermutationSolution<Integer>> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Best found solution: " + population.get(0).getObjective(0)) ;
  }
}
