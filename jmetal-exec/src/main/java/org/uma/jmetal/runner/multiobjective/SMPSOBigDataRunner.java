package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition.BigOpt2015;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Class for configuring and running the SMPSO algorithm to solve a problem of the CEC2015
 * Big Optimization competition
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class SMPSOBigDataRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.SMPSOBigDataRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String instanceName ;

    if (args.length == 1) {
      instanceName = args[0] ;
    } else {
      instanceName = "D12" ;
    }

    problem = new BigOpt2015(instanceName) ;

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(20) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    algorithm = new SMPSOBuilder(problem, archive)
            .setMutation(mutation)
            .setMaxIterations(250)
            .setSwarmSize(20)
     //       .setRandomGenerator(new MersenneTwisterGenerator())
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<DoubleSolution> population = ((SMPSO)algorithm).getResult();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed()) ;
  }
}
