package org.uma.jmetal.example.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.lab.plot.PlotFront;
import org.uma.jmetal.lab.plot.impl.Plot2DSmile;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.multiobjective.OneZeroMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm to solve a binary problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIBinaryProblemExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    BinaryProblem problem;
    NSGAII<BinarySolution> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;

    problem = new OneZeroMax(250);

    crossover = new SinglePointCrossover(0.9);

    mutation = new BitFlipMutation(1.0 / problem.getTotalNumberOfBits());

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(25000);

    algorithm =
        new NSGAII<>(
            problem, populationSize, offspringPopulationSize, crossover, mutation, termination);

    algorithm.run();

    List<BinarySolution> population = algorithm.getResult();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    PlotFront plot = new Plot2DSmile(new ArrayFront(population).getMatrix());
    plot.plot();
  }
}
