//  SPEA2Runner.java
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

package org.uma.jmetal45.runner.multiobjective;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Operator;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.metaheuristic.multiobjective.spea2.SPEA2;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.operator.selection.BinaryTournament2;
import org.uma.jmetal45.problem.multiobjective.Kursawe;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal45.util.AlgorithmRunner;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal45.util.fileoutput.SolutionSetOutput;

import java.io.IOException;

/**
 * Class for configuring and running the SPEA2 algorithm
 */
public class SPEA2Runner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal45.util.JMetalException
   * @throws IOException
   * @throws SecurityException 
   * @throws ClassNotFoundException
   * Usage: three options
   *        - org.uma.jmetal45.runner.multiobjective.SPEA2Runner
   *        - org.uma.jmetal45.runner.multiobjective.SPEA2Runner problemName
   *        - org.uma.jmetal45.runner.multiobjective.SPEA2Runner problemName paretoFrontFile
   */
  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

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
    /*
      EXAMPLES:
      problem = new Water("Real");
      problem = new ZDT1("ArrayReal", 1000);
      problem = new ZDT4("BinaryReal");
      problem = new WFG1("Real");
      problem = new DTLZ1("Real");
      problem = new OKA2("Real") ;
    */
    }

    crossover = new SBXCrossover.Builder()
            .setDistributionIndex(20.0)
            .setProbability(0.9)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(20.0)
            .setProbability(1.0 / problem.getNumberOfVariables())
            .build();

    selection = new BinaryTournament2.Builder()
            .build();

    algorithm = new SPEA2.Builder(problem)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setArchiveSize(100)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
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
