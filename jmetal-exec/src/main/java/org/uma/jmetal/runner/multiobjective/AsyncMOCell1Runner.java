//  AsyncMOCell1Runner.Java
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

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.mocell.MOCellTemplate;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityIndicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * * This class executes the algorithms described in
 * A.J. Nebro, J.J. Durillo, F. Luna, B. Dorronsoro, E. Alba
 * "Design Issues in a Multiobjective Cellular Genetic Algorithm."
 * Evolutionary Multi-Criterion Optimization. 4th International Conference,
 * EMO 2007. Sendai/Matsushima, Japan, March 2007.
 */
public class AsyncMOCell1Runner {
  public static Logger logger_;
  public static FileHandler fileHandler_;

  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - org.uma.jmetal.runner.MOCell_main
   *                           - org.uma.jmetal.runner.MOCell_main problemName
   *                           - org.uma.jmetal.runner.MOCell_main problemName ParetoFrontFile
   */
  public static void main(String[] args)
    throws JMetalException, SecurityException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicatorGetter indicators;

    // Logger object and file to store log messages
    logger_ = Configuration.logger;
    fileHandler_ = new FileHandler("MOCell_main.log");
    logger_.addHandler(fileHandler_);

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
      //problem = new Water("Real");
      //problem = new ZDT1("ArrayReal", 1000);
      //problem = new ZDT4("BinaryReal");
      //problem = new WFG1("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
    }

    crossover = new SBXCrossover.Builder()
      .probability(0.9)
      .distributionIndex(20.0)
      .build();

    mutation = new PolynomialMutation.Builder()
      .probability(1.0 / problem.getNumberOfVariables())
      .distributionIndex(20.0)
      .build();

    selection = new BinaryTournament.Builder()
      .build();

    algorithm = new MOCellTemplate.Builder(problem)
      .populationSize(100)
      .archiveSize(100)
      .maxEvaluations(25000)
      .numberOfFeedbackSolutionsFromArchive(20)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .build("AsyncMOCell1");

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
