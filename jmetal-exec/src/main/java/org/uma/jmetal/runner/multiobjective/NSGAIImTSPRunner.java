//  NSGAII_MOTSP_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro, Juan J. Durillo
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

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIITemplate;
import org.uma.jmetal.operator.crossover.PMXCrossover;
import org.uma.jmetal.operator.mutation.SwapMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.problem.multiobjective.MultiObjectiveTSP;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.util.logging.FileHandler;

/**
 * Class to configure and execute the NSGA-II algorithm. The experiment.settings are aimed
 * at solving the mTSP problem.
 */

public class NSGAIImTSPRunner {
  public static java.util.logging.Logger logger;
  public static FileHandler fileHandler;

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage:
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_mTSP_main
   */
  public static void main(String[] args) throws Exception {
    Problem problem; 
    Algorithm algorithm; 
    Operator crossover; 
    Operator mutation; 
    Operator selection; 

    QualityIndicatorGetter indicators;

    // Logger object and file to store log messages
    logger = JMetalLogger.logger;
    fileHandler = new FileHandler("NSGAIImTSPRunner.log");
    logger.addHandler(fileHandler);

    indicators = null;
    problem = new MultiObjectiveTSP("Permutation", "kroA100.tsp", "kroB100.tsp");

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

    crossover = new PMXCrossover.Builder()
            .probability(0.95)
            .build() ;

    mutation = new SwapMutation.Builder()
            .probability(0.2)
            .build() ;

    selection = new BinaryTournament2.Builder()
            .build() ;

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
            .populationSize(100)
            .maxEvaluations(1000000)
            .crossover(crossover)
            .mutation(mutation)
            .selection(selection)
            .build("NSGAII") ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
            .separator("\t")
            .varFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .funFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    logger.info("Total execution time: " + computingTime + "ms");
    logger.info("Objectives values have been written to file FUN.tsv");
    logger.info("Variables values have been written to file VAR.tsv");
  } 
} 
