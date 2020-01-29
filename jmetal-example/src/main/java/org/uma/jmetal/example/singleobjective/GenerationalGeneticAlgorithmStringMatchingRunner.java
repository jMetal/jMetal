package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.CharSequenceRandomMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.singleobjective.StringMatching;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to configure and run a generational genetic algorithm. The target problem is {@link StringMatching}.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerationalGeneticAlgorithmStringMatchingRunner {
  public static void main(String[] args) {
    StringMatching problem;
    Algorithm<CharSequenceSolution> algorithm;
    CrossoverOperator<CharSequenceSolution> crossover;
    MutationOperator<CharSequenceSolution> mutation;
    SelectionOperator<List<CharSequenceSolution>, CharSequenceSolution> selection;

    problem = new StringMatching("jMetal is an optimization framework");

    crossover = new NullCrossover<>();

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    mutation = new CharSequenceRandomMutation(mutationProbability, problem.getAlphabet());

    selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

    algorithm =
        new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
            .setPopulationSize(50)
            .setMaxEvaluations(250000)
            .setSelectionOperator(selection)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    CharSequenceSolution solution = algorithm.getResult();
    List<CharSequenceSolution> population = new ArrayList<>(1);
    population.add(solution);

    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info(
        "Best found string: '"
            + solution.getVariables().stream().map(String::valueOf).collect(Collectors.joining())
            + "'");

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
