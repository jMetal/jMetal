//  SMPSOhvRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.runner;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.smpso.SMPSOE;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Kursawe;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.AlgorithmRunner;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.archive.Archive;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;
import jmetal.util.fileOutput.DefaultFileOutputContext;
import jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class executes:
 * - The SMPSO algorithm described in:
 *   Antonio J. Nebro, Juan José Durillo, Carlos Artemio Coello Coello:
 *   Analysis of leader selection strategies in a multi-objective Particle Swarm Optimizer.
 *   IEEE Congress on Evolutionary Computation 2013: 3153-3160
 * - The SMPSOhv algorithm described in:
 *   Antonio J. Nebro, Juan José Durillo, Carlos Artemio Coello Coello:
 *   Analysis of leader selection strategies in a multi-objective Particle Swarm Optimizer.
 *   IEEE Congress on Evolutionary Computation 2013: 3153-3160
 */
public class SMPSOhvRunner {
  public static Logger logger_;      
  public static FileHandler fileHandler_; 

  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   * @throws SecurityException       Usage: three options
   *                                 - jmetal.runner.SMPSORunner
   *                                 - jmetal.runner.SMPSORunner problemName
   *                                 - jmetal.runner.SMPSORunner problemName ParetoFrontFile
   */
  public static void main(String[] args) throws JMException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Mutation mutation;

    QualityIndicator indicators;

    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("SMPSO_main.log");
    logger_.addHandler(fileHandler_);

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicator(problem, args[1]);
    } else {
      problem = new Kursawe("Real", 3);
      /*
        Examples:
        problem = new Water("Real");
        problem = new ZDT3("ArrayReal", 30);
        problem = new ConstrEx("Real");
        problem = new DTLZ1("Real");
        problem = new OKA2("Real")
      */
    }

    /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator() // SMPSO
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem) // parallel SMPSO
     */
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();

    /*
     * Alternatives:
     * - archive = new CrowdingArchive(100, problem.getNumberOfObjectives()) ; // SMPSO
     * - archive = new FastHypervolumeArchive(100, problem.getNumberOfObjectives()); // SMPSOhv
     */
    Archive archive = new CrowdingArchive(100, problem.getNumberOfObjectives()) ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0 / problem.getNumberOfVariables())
      .build();

    algorithm = new SMPSOE.Builder(problem, archive, evaluator)
      .mutation(mutation)
      .maxIterations(250)
      .swarmSize(100)
      .archiveSize(100)
      .build();

    algorithm.addOperator("mutation", mutation);

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute();

    SolutionSet population = algorithmRunner.getSolutionSet();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionSetOutput.Printer(population)
      .separator("\t")
      .varFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .funFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    logger_.info("Total execution time: " + computingTime + "ms");
    logger_.info("Objectives values have been written to file FUN.tsv");
    logger_.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      logger_.info("Quality indicators");
      logger_.info("Hypervolume: " + indicators.getHypervolume(population));
      logger_.info("GD         : " + indicators.getGD(population));
      logger_.info("IGD        : " + indicators.getIGD(population));
      logger_.info("Spread     : " + indicators.getSpread(population));
      logger_.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
