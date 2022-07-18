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
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the NSGA-II algorithm (binary encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIIBinaryRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIBinaryRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {

      String problemName ;
    var referenceParetoFront = "" ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT5";
      referenceParetoFront = "" ;
    }

    var problem = (BinaryProblem) ProblemFactory.<BinarySolution>loadProblem(problemName);

    var crossoverProbability = 0.9 ;
      CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(crossoverProbability);

    var mutationProbability = 1.0 / problem.getBitsFromVariable(0) ;
      MutationOperator<BinarySolution> mutation = new BitFlipMutation(mutationProbability);

      SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<BinarySolution>();

    var populationSize = 100;
      Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<BinarySolution>(problem, crossover, mutation, populationSize)
              .setSelectionOperator(selection)
              .setMaxEvaluations(25000)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    var population = algorithm.getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
