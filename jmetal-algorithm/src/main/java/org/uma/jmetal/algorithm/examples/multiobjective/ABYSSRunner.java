package org.uma.jmetal.algorithm.examples.multiobjective;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.abyss.ABYSSBuilder;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class is the main program used to configure and run AbYSS, a
 * multiobjective scatter search metaheuristics, which is described in:
 *   A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 *   "AbYSS: Adapting Scatter Search to Multiobjective Optimization."
 *   IEEE Transactions on Evolutionary Computation. Vol. 12,
 *   No. 4 (August 2008), pp. 439-457
 *
 *   @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ABYSSRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   * @throws FileNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.AbYSSRunner problemName [referenceFront]
   */
  public static void main(String @NotNull [] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    String problemName ;

    String referenceParetoFront = "" ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.csv" ;
    }

    problem = (DoubleProblem) ProblemFactory.<DoubleSolution> loadProblem(problemName);

    Archive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

    algorithm = new ABYSSBuilder(problem, archive)
        .setMaxEvaluations(25000)
        .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
