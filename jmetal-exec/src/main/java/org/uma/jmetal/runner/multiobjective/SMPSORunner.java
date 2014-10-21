//  SMPSORunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.metaheuristic.multiobjective.smpso.SMPSO;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
public class SMPSORunner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * Usage: three options
   *          - org.uma.jmetal.runner.multiobjective.SMPSORunner
   *          - org.uma.jmetal.runner.multiobjective.SMPSORunner problemName
   *          - org.uma.jmetal.runner.multiobjective.SMPSORunner problemName ParetoFrontFile
   */
  public static void main(String[] args) throws Exception {
    ContinuousProblem problem;
    Algorithm algorithm;
    MutationOperator mutation;

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4" ;

    problem = (ContinuousProblem) ProblemUtils.loadProblem(problemName);

    Archive archive = new CrowdingDistanceArchive(100) ;

    mutation = new PolynomialMutation.Builder()
      .setDistributionIndex(20.0)
      .setProbability(1.0 / problem.getNumberOfVariables())
      .build();

    algorithm = new SMPSO.Builder(problem, archive)
      .setMutation(mutation)
      .setMaxIterations(250)
      .setSwarmSize(100)
      .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute();

    List<Solution<?>> population = algorithmRunner.getSolutionSet();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionSetOutput.Printer(population)
      .setSeparator("\t")
      .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
