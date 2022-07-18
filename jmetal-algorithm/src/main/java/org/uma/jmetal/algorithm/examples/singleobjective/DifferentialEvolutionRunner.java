package org.uma.jmetal.algorithm.examples.singleobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class to configure and run a differential evolution algorithm. The algorithm can be configured to
 * use threads. The number of cores is specified as an optional parameter. The target problem is
 * Sphere.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DifferentialEvolutionRunner {
  private static final int DEFAULT_NUMBER_OF_CORES = 1;

  /** Usage: java org.uma.jmetal.runner.singleobjective.DifferentialEvolutionRunner [cores] */
  public static void main(String[] args) throws Exception {

      SolutionListEvaluator<DoubleSolution> evaluator;

      DoubleProblem problem = new Sphere(20);

    int numberOfCores;
    if (args.length == 1) {
      numberOfCores = Integer.valueOf(args[0]);
    } else {
      numberOfCores = DEFAULT_NUMBER_OF_CORES;
    }

    if (numberOfCores == 1) {
      evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
    } else {
      evaluator = new MultiThreadedSolutionListEvaluator<DoubleSolution>(numberOfCores);
    }

    var crossover = new DifferentialEvolutionCrossover(
              0.5, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
    var selection = new DifferentialEvolutionSelection();

      Algorithm<DoubleSolution> algorithm = new DifferentialEvolutionBuilder(problem)
              .setCrossover(crossover)
              .setSelection(selection)
              .setSolutionListEvaluator(evaluator)
              .setMaxEvaluations(25000)
              .setPopulationSize(100)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var solution = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    List<DoubleSolution> population = new ArrayList<>(1);
    population.add(solution);
    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    JMetalLogger.logger.info("Fitness: " + solution.objectives()[0]);

    evaluator.shutdown();
  }
}
