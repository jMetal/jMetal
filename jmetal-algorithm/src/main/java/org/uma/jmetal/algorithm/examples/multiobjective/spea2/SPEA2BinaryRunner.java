package org.uma.jmetal.algorithm.examples.multiobjective.spea2;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the SPEA2 algorithm (binary encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class SPEA2BinaryRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.spea2.SPEA2BinaryRunner problemName [referenceFront]
   */
  public static void main(String @NotNull [] args) throws JMetalException, FileNotFoundException {

    var referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.OneZeroMax";
      referenceParetoFront = "" ;
    }

    var problem = (BinaryProblem) ProblemFactory.<BinarySolution>loadProblem(problemName);

    var crossoverProbability = 0.9 ;
    CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(crossoverProbability);

    var mutationProbability = 1.0 / problem.getTotalNumberOfBits() ;
    MutationOperator<BinarySolution> mutation = new BitFlipMutation(mutationProbability);

    SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<BinarySolution>();

    Algorithm<List<BinarySolution>> algorithm = new SPEA2Builder<>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxIterations(250)
            .setPopulationSize(100)
            .build();

    new AlgorithmRunner.Executor(algorithm).execute() ;

    var population = algorithm.getResult();

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
