package org.uma.jmetal.algorithm.examples.multiobjective.moead;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;

/**
 * Class for configuring and running the MOEA/DD algorithm
 *
 * @author
 */
public class MOEADDRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   * org.uma.jmetal.runner.multiobjective.moead.MOEADRunner problemName
   * [referenceFront]
   */
  public static void main(String[] args) throws FileNotFoundException {
    String problemName;
    var referenceParetoFront = "";
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.UF.UF2";
      referenceParetoFront = "resources/referenceFrontsCSV/UF2.csv";
    }

    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 1.0;
    var crossoverDistributionIndex = 30.0;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    @NotNull MOEADBuilder builder =  new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADD);
    builder.setCrossover(crossover)
            .setMutation(mutation)
            .setMaxEvaluations(150000)
            .setPopulationSize(300)
            .setResultPopulationSize(300)
            .setNeighborhoodSelectionProbability(0.9)
            .setMaximumNumberOfReplacedSolutions(1)
            .setNeighborSize(20)
            .setFunctionType(AbstractMOEAD.FunctionType.PBI)
            .setDataDirectory("MOEAD_Weights");
    Algorithm<List<DoubleSolution>> algorithm = builder.build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    var population = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
