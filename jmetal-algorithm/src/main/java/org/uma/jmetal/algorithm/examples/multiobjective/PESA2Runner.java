package org.uma.jmetal.algorithm.examples.multiobjective;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the PESA2 algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PESA2Runner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.PESA2Runner problemName [referenceFront]
   */
  public static void main(String @NotNull [] args) throws JMetalException, FileNotFoundException {

    var referenceParetoFront = "";

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";
    }

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Algorithm<List<DoubleSolution>> algorithm = new PESA2Builder<>(problem, crossover, mutation)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setArchiveSize(100)
            .setBisections(5)
            .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var population = algorithm.getResult();

    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
