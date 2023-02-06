package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;

/**
 * Class for configuring and running the NSGA-II algorithm (binary encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIIBinaryRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) {

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT5";

    BinaryProblem problem = (BinaryProblem) ProblemFactory.<BinarySolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(crossoverProbability);

    double mutationProbability = 1.0 / problem.bitsFromVariable(0);
    MutationOperator<BinarySolution> mutation = new BitFlipMutation(mutationProbability);

    SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<>();

    int populationSize = 100;
    Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation,
        populationSize)
        .setSelectionOperator(selection)
        .setMaxEvaluations(25000)
        .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<BinarySolution> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
  }
}
