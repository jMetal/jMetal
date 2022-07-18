package org.uma.jmetal.algorithm.examples.multiobjective;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

/**
 * Class for configuring and running the GDE3 algorithm (parallel version)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ParallelGDE3Runner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.ParallelGDE3Runner problemName [referenceFront]
   */
  public static void main(String[] args) throws FileNotFoundException {

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

    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var cr = 0.5;
    var f = 0.5;
    var crossover = new DifferentialEvolutionCrossover(
              cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
    var selection = new DifferentialEvolutionSelection();

    SolutionListEvaluator<DoubleSolution> evaluator =
        new MultiThreadedSolutionListEvaluator<DoubleSolution>(0);

      Algorithm<List<DoubleSolution>> algorithm = new GDE3Builder(problem)
              .setCrossover(crossover)
              .setSelection(selection)
              .setMaxEvaluations(25000)
              .setPopulationSize(100)
              .setSolutionSetEvaluator(evaluator)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var population = ((GDE3) algorithm).getResult();
    var computingTime = algorithmRunner.getComputingTime();

    evaluator.shutdown();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
