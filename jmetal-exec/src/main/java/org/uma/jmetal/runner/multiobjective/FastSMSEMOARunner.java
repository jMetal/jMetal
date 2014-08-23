//  FastSMSEMOARunner.java
//
//  Author:
//       Antonio J. Nebro
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
/**
 * SMSEMOA_main.java
 *
 * @author Simon Wessing
 * @version 1.0
 *   This implementation of SMS-EMOA makes use of a QualityIndicator object
 *   to obtained the convergence speed of the algorithm.
 *
 */
package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.smsemoa.FastSMSEMOA;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.RandomSelection;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;

/**
 * Class for configuring and running the SMS-EMOA algorithm. This
 * implementation of SMS-EMOA makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm.
 */
public class FastSMSEMOARunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - org.uma.jmetal.runner.SMSEMOA_main
   *                           - org.uma.jmetal.runner.SMSEMOA_main problemName
   *                           - org.uma.jmetal.runner.SMSEMOA_main problemName paretoFrontFile
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

    int populationSize;
    int maxEvaluations;
    double mutationProbability;
    double crossoverProbability;
    double crossoverDistributionIndex;
    double mutationDistributionIndex;
    double offset;

    QualityIndicatorGetter indicators;

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      problem = new Kursawe("Real", 3);
      //problem = new Kursawe("BinaryReal", 3);
      //problem = new Water("Real");
      //problem = new ZDT1("ArrayReal", 100);
      //problem = new ConstrEx("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
    }

    populationSize = 100   ;
    maxEvaluations = 25000 ;
    mutationProbability = 1.0/ problem.getNumberOfVariables() ;
    crossoverProbability = 0.9   ;
    crossoverDistributionIndex = 20.0  ;
    mutationDistributionIndex = 20.0  ;
    offset = 100.0 ;

    crossover = new SBXCrossover.Builder()
      .distributionIndex(crossoverDistributionIndex)
      .probability(crossoverProbability)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(mutationDistributionIndex)
      .probability(mutationProbability)
      .build();

    selection = new RandomSelection.Builder()
      .build();

    algorithm = new FastSMSEMOA.Builder(problem)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .offset(offset)
      .maxEvaluations(maxEvaluations)
      .populationSize(populationSize)
      .build("FastSMSEMOA") ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
      .separator("\t")
      .varFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .funFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
