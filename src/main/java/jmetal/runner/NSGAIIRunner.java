//  NSGAIIRunner.java
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

package jmetal.runner;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAIITemplate;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.operators.selection.BinaryTournament2;
import jmetal.problems.Kursawe;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.AlgorithmRunner;
import jmetal.util.Configuration;
import jmetal.util.JMetalException;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;
import jmetal.util.fileOutput.DefaultFileOutputContext;
import jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class to configure and execute the NSGA-II algorithm.
 * <p/>
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba
 * "On the Effect of the Steady-State Selection Scheme in
 * Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197.
 * April 2009)
 */

public class NSGAIIRunner {
  private static Logger logger_;
  private static FileHandler fileHandler_;

  /**
   * @param args Command line arguments.
   * @throws jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner problemName
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMetalException,
    SecurityException,
    IOException,
    ClassNotFoundException {

    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicator indicators;

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("NSGAII_main.log");
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
     * - "NSGAII"
     * - "SteadyStateNSGAII"
     */
    String nsgaIIVersion = "NSGAII" ;

    /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator() // NSGAII
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem) // parallel NSGAII
     */
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    crossover = new SBXCrossover.Builder()
      .distributionIndex(20.0)
      .probability(0.9)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0/problem.getNumberOfVariables())
      .build();

    selection = new BinaryTournament2.Builder()
      .build();

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .maxEvaluations(25000)
      .populationSize(100)
      .build(nsgaIIVersion) ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

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
