package org.uma.jmetal.algorithm.examples.multiobjective.wasfga;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class WASFGABinaryRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    BinaryProblem problem = new ZDT5();

    double crossoverProbability = 0.9;
    var crossover = new SinglePointCrossover(crossoverProbability);

    double mutationProbability = 1.0 / problem.bitsFromVariable(0);
    var mutation = new BitFlipMutation(mutationProbability);

    SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<BinarySolution>();

    List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(10.0);
    referencePoint.add(4.0);

    double epsilon = 0.01;
    var algorithm = new WASFGA<BinarySolution>(problem, 100, 250, crossover, mutation, selection,
        new SequentialSolutionListEvaluator<BinarySolution>(), epsilon, referencePoint);

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<BinarySolution> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
  }
}
