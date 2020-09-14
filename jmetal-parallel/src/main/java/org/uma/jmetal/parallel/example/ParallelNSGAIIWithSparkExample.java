package org.uma.jmetal.parallel.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.parallel.synchronous.SparkSolutionListEvaluator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static java.lang.Math.sin;

/**
 * Class for configuring and running the NSGA-II algorithm (parallel version)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ParallelNSGAIIWithSparkExample extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.nsgaii.ParallelNSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    String referenceParetoFront = "";

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

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    selection = new BinaryTournamentSelection<DoubleSolution>();

    SolutionListEvaluator<DoubleSolution> evaluator =
        new MultiThreadedSolutionListEvaluator<DoubleSolution>(8);

    int populationSize = 100;
    NSGAIIBuilder<DoubleSolution> builder =
        new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .setSolutionListEvaluator(evaluator);

    algorithm = builder.build();

    algorithm.run();

    builder.getSolutionListEvaluator().shutdown();

    List<DoubleSolution> population = algorithm.getResult();

    evaluator.shutdown();

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }

  /**
   * Class to configure and run the NSGA-II algorithm (variant with measures) with the G-Dominance Comparator.
   */
  public static class GNSGAIIMeasuresWithChartsRunner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments.
     * @throws SecurityException Invoking command: java org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIMeasuresRunner
     *                           problemName [referenceFront]
     */
    public static void main(String[] args) throws JMetalException, InterruptedException, IOException {
      Problem<DoubleSolution> problem;
      Algorithm<List<DoubleSolution>> algorithm;
      CrossoverOperator<DoubleSolution> crossover;
      MutationOperator<DoubleSolution> mutation;
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

      problem = new ZDT2() {
        @Override
        public DoubleSolution evaluate(DoubleSolution solution) {
          super.evaluate(solution);
          computingDelay();

          return solution;
        }

        private void computingDelay() {
          for (long i = 0; i < 1000; i++)
            for (long j = 0; j < 1000; j++) {
              double a = sin(i) * Math.cos(j);
            }
        }
      };

      double crossoverProbability = 0.9;
      double crossoverDistributionIndex = 20.0;
      crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

      double mutationProbability = 1.0 / problem.getNumberOfVariables();
      double mutationDistributionIndex = 20.0;
      mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

      int maxEvaluations = 25000;
      int populationSize = 100;

      Logger.getLogger("org").setLevel(Level.OFF) ;

      SparkConf sparkConf = new SparkConf()
              .setMaster("local[8]")
              .setAppName("NSGA-II with Spark");

      JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

      algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
              .setSelectionOperator(selection)
              .setMaxEvaluations(maxEvaluations)
              .setSolutionListEvaluator(new SparkSolutionListEvaluator<>(sparkContext))
              .build();

      algorithm.run();

      printFinalSolutionSet(algorithm.getResult());

      JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
      JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
    }
  }
}
