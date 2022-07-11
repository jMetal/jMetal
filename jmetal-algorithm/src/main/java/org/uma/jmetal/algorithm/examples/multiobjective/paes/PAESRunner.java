package org.uma.jmetal.algorithm.examples.multiobjective.paes;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.paes.PAES;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the PAES algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PAESRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.PAESRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    String referenceParetoFront = "";

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.Kursawe";
      referenceParetoFront = "resources/referenceFrontsCSV/Kursawe.csv";
    }

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    PAES<DoubleSolution> algorithm =
        new PAES<>(problem, 25000, 100, 5, mutation);
    // Alternative using a generic bounded archive:
    // new PAES<>(problem, 25000,
    //      new GenericBoundedArchive<>(100, new GridDensityEstimator<>(5, problem.objectives().length)),  mutation);

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
