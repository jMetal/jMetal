package org.uma.jmetal.algorithm.examples.multiobjective;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the IBEA algorithm on the bi-objective TSP.
 *
 * <p>The {@code IBEABuilder} is specialized for {@code DoubleSolution}, so this example creates a
 * plain {@link IBEA} instance directly for permutation solutions.
 *
 * @author Antonio J. Nebro
 */
public class IBEABiObjectiveTSPRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws JMetalException, IOException {

    PermutationProblem<PermutationSolution<Integer>> problem = new BiObjectiveTSP(
        "resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");

    CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(0.9);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    MutationOperator<PermutationSolution<Integer>> mutation =
        new PermutationSwapMutation<>(mutationProbability);

    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection =
        new BinaryTournamentSelection<>(new FitnessComparator<>());

    int populationSize = 100;
    Algorithm<List<PermutationSolution<Integer>>> algorithm =
        new IBEA<>(
            problem,
            populationSize,
            populationSize,
            10000,
            selection,
            crossover,
            mutation);

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<PermutationSolution<Integer>> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
  }
}
