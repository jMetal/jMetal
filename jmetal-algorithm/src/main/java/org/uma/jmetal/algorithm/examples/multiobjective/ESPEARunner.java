package org.uma.jmetal.algorithm.examples.multiobjective;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.espea.ESPEABuilder;
import org.uma.jmetal.algorithm.multiobjective.espea.util.EnergyArchive.ReplacementStrategy;
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

/**
 * Class to configure and run the ESPEA algorithm
 *
 * @author Marlon Braun <marlon.braun@partner.kit.edu>
 */
public class ESPEARunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws FileNotFoundException Invoking command:
   *                               java org.uma.jmetal.runner.multiobjective.ESPEARunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    var referenceParetoFront = "";

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT3";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT3.csv";
    }

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var builder = new ESPEABuilder<DoubleSolution>(problem, crossover, mutation);
    builder.setMaxEvaluations(25000);
    builder.setPopulationSize(100);
    builder.setReplacementStrategy(ReplacementStrategy.WORST_IN_ARCHIVE);

//    ScalarizationWrapper wrapper = new ScalarizationWrapper(ScalarizationType.TRADEOFF_UTILITY);
//    builder.setScalarization(wrapper);

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
