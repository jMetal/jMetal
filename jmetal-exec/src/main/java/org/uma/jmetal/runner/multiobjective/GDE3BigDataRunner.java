package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition.BigOpt2015;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;

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
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    DifferentialEvolutionSelection selection;
    DifferentialEvolutionCrossover crossover;

    String instanceName ;

    if (args.length == 1) {
      instanceName = args[0] ;
    } else {
      instanceName = "D12" ;
    }

    problem = new BigOpt2015(instanceName) ;

     /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator()
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem)
     */

    double cr = 1.5 ;
    double f = 0.5 ;
    crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin") ;

    selection = new DifferentialEvolutionSelection() ;

    algorithm = new GDE3Builder(problem)
      .setCrossover(crossover)
      .setSelection(selection)
      .setMaxEvaluations(250000)
      .setPopulationSize(100)
      .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    List<DoubleSolution> population = ((GDE3)algorithm).getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
      .setSeparator("\t")
      .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
