//  CellDERunner
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
package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.cellde.CellDE;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.FileHandler;

public class CellDERunner {
  private static java.util.logging.Logger logger;
  private static FileHandler fileHandler;

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException Usage: three choices
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMetalException, SecurityException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;

    QualityIndicatorGetter indicators;

    // Logger object and file to store log messages
    logger = JMetalLogger.logger;
    fileHandler = new FileHandler("CellDERunner.log");
    logger.addHandler(fileHandler);

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
      /* Examples
      //problem = new Water("Real");
      //problem = new ZDT4("ArrayReal");
      //problem = new WFG1("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
      */
    }
    crossover = new DifferentialEvolutionCrossover.Builder()
            .cr(0.5)
            .f(0.5)
            .build() ;

    selection = new BinaryTournament.Builder()
            .build() ;

    algorithm = new CellDE.Builder(problem)
            .populationSize(100)
            .archiveSize(100)
            .maxEvaluations(25000)
            .numberOfFeedbackSolutionsFromArchive(20)
            .crossover(crossover)
            .selection(selection)
            .build() ;

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

    if (indicators != null) {
      logger.info("Quality indicators");
      logger.info("Hypervolume: " + indicators.getHypervolume(population));
      logger.info("GD         : " + indicators.getGD(population));
      logger.info("IGD        : " + indicators.getIGD(population));
      logger.info("Spread     : " + indicators.getSpread(population));
      logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
