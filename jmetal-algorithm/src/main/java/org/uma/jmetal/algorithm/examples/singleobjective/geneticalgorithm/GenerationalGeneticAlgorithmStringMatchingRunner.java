package org.uma.jmetal.algorithm.examples.singleobjective.geneticalgorithm;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
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

/**
 * Class to configure and run a generational genetic algorithm. The target problem is {@link StringMatching}.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerationalGeneticAlgorithmStringMatchingRunner {
  public static void main(String[] args) {

    var problem = new StringMatching("jMetal is an optimization framework");

    CrossoverOperator<CharSequenceSolution> crossover = new NullCrossover<>();

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    MutationOperator<CharSequenceSolution> mutation = new CharSequenceRandomMutation(mutationProbability, problem.getAlphabet());

    SelectionOperator<List<CharSequenceSolution>, CharSequenceSolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

    var algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
            .setPopulationSize(50)
            .setMaxEvaluations(250000)
            .setSelectionOperator(selection)
            .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var solution = algorithm.getResult();
    List<CharSequenceSolution> population = new ArrayList<>(1);
    population.add(solution);

    var computingTime = algorithmRunner.getComputingTime();

      @NotNull StringBuilder sb = new StringBuilder();
      for (var character : solution.variables()) {
        var s = String.valueOf(character);
          sb.append(s);
      }
      JMetalLogger.logger.info(
        "Best found string: '"
            + sb.toString()
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
