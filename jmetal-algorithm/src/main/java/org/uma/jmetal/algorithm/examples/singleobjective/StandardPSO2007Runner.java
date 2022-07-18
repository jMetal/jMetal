package org.uma.jmetal.algorithm.examples.singleobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2007;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class to configure and run a StandardPSO2007. The algorithm can be configured to use threads. The
 * number of cores is specified as an optional parameter. The target problem is Sphere.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StandardPSO2007Runner {
  private static final int DEFAULT_NUMBER_OF_CORES = 1;

  /** Usage: java org.uma.jmetal.runner.singleobjective.StandardPSO2007Runner [cores] */
  public static void main(String[] args) throws Exception {

      SolutionListEvaluator<DoubleSolution> evaluator;

    var problemName = "org.uma.jmetal.problem.singleobjective.Sphere";

    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

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

      Algorithm<DoubleSolution> algorithm = new StandardPSO2007(
              problem,
              10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())),
              25000,
              3,
              evaluator);

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
    JMetalLogger.logger.info("Solution: " + solution.variables().get(0));
    evaluator.shutdown();
  }
}
