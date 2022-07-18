package org.uma.jmetal.algorithm.examples.multiobjective;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition.BigOpt2015;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class for configuring and running the GDE3 algorithm for solving a problem of the Big Optimization
 * competition at CEC2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GDE3BigDataRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
   mvn
    -pl jmetal-exec
    exec:java -Dexec.mainClass="org.uma.jmetal.runner.multiobjective.GDE3BigDataRunner"
    -Dexec.args="[problemName]"
   */
  public static void main(String[] args) {

      String instanceName ;

    if (args.length == 1) {
      instanceName = args[0] ;
    } else {
      instanceName = "D12" ;
    }

      DoubleProblem problem = new BigOpt2015(instanceName);

     /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator()
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem)
     */

    var cr = 1.5 ;
    var f = 0.5 ;
    var crossover = new DifferentialEvolutionCrossover(cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    var selection = new DifferentialEvolutionSelection();

      Algorithm<List<DoubleSolution>> algorithm = new GDE3Builder(problem)
              .setCrossover(crossover)
              .setSelection(selection)
              .setMaxEvaluations(250000)
              .setPopulationSize(100)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    var population = ((GDE3)algorithm).getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
      .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
