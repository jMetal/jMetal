package org.uma.jmetal.algorithm.examples.multiobjective.smpso;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

/**
 * Class for configuring and running the SMPSO algorithm using an HypervolumeArchive, i.e, the
 * SMPSOhv algorithm described in:
 * A.J Nebro, J.J. Durillo, C.A. Coello Coello. Analysis of Leader Selection Strategies in a
 * Multi-Objective Particle Swarm Optimizer. 2013 IEEE Congress on Evolutionary Computation. June 2013
 * DOI: 10.1109/CEC.2013.6557955
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOHvRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.smpso.SMPSOHvRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    JMetalRandom.getInstance().setSeed(1);
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;
    }

    problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    @NotNull BoundedArchive<DoubleSolution> archive =
        new HypervolumeArchive(100, new PISAHypervolume<>()) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    algorithm = new SMPSOBuilder(problem, archive)
        .setMutation(mutation)
        .setMaxIterations(250)
        .setSwarmSize(100)
        .setRandomGenerator(new MersenneTwisterGenerator())
        .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
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
