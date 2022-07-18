package org.uma.jmetal.algorithm.examples.singleobjective;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the random search algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class RandomSearchRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.RandomSearchRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {

      Problem<BinarySolution> problem = new OneMax(1024);

      Algorithm<List<BinarySolution>> algorithm = new RandomSearchBuilder<>(problem)
              .setMaxEvaluations(25000)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    var population = algorithm.getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    JMetalLogger.logger.info("Fitness: " + population.get(0).objectives()[0]) ;
    JMetalLogger.logger.info("Solution: " + population.get(0).variables().get(0)) ;
  }
}
