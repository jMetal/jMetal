package org.uma.jmetal.algorithm.examples.singleobjective.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.impl.NPointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.IntegerSimpleRandomMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.problem.singleobjective.FAPProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run a generational genetic algorithm. The target
 * problem is Frequency Assignment Problem (FAP).
 *
 * @author Jose Alejandro Cornejo-Acosta
 */
public class GenerationalGeneticAlgorithmFAPRunner {

    public static void main(String[] args) throws Exception {

        JMetalRandom.getInstance().setSeed(0L);
        IntegerProblem problem;
        Algorithm<IntegerSolution> algorithm;
        NPointCrossover<IntegerSolution, Integer> crossover;
        MutationOperator<IntegerSolution> mutation;
        SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;

        final int F = 49;       // number of frequencies
        problem = new FAPProblem("resources/fapInstances/GSM2-272.ctr", F); 
        crossover = new NPointCrossover<>(1, 2);
        double mutationProbability = 1.0 / problem.numberOfVariables();
        mutation = new IntegerSimpleRandomMutation(mutationProbability);

        selection = new BinaryTournamentSelection<>();

        algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(25000)
                .setSelectionOperator(selection)
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        IntegerSolution solution = algorithm.result();
        List<IntegerSolution> population = new ArrayList<>(1);
        population.add(solution);

        long computingTime = algorithmRunner.getComputingTime();

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
        JMetalLogger.logger.info("Fitness: " + solution.objectives()[0]);
        JMetalLogger.logger.info("Number of variables: " + solution.variables().size());
    }
}
