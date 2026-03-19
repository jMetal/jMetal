package org.uma.jmetal.component.examples.multiobjective.agemoea;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.AGEMOEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class for configuring and running the AGE-MOEA algorithm to solve the bi-objective TSP
 * using the component-based architecture.
 *
 * @author Antonio J. Nebro
 */
public class AGEMOEABiObjectiveTSPExample {

  public static void main(String[] args) throws JMetalException, IOException {

    PermutationProblem<PermutationSolution<Integer>> problem = new BiObjectiveTSP(
        "resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");

    CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(0.9);

    double mutationProbability = 0.2;
    MutationOperator<PermutationSolution<Integer>> mutation = new PermutationSwapMutation<>(
        mutationProbability);

    int populationSize = 100;
    int offspringPopulationSize = 100;
    
    Termination termination = new TerminationByEvaluations(10000);

    EvolutionaryAlgorithm<PermutationSolution<Integer>> agemoea =
        new AGEMOEABuilder<>(problem, populationSize, offspringPopulationSize, crossover, mutation, AGEMOEABuilder.Variant.AGEMOEA)
            .setTermination(termination)
            .build();

    agemoea.run();

    List<PermutationSolution<Integer>> population = agemoea.result();
    long computingTime = agemoea.totalComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
