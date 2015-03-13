//  GDE3Runner.java
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

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;

/**
 * Class for configuring and running the GDE3 algorithm
 */
public class ParallelGDE3Runner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Usage: three choices
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner problemName
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner problemName paretoFrontFile
   */
  public static void main(String[] args) {
    DoubleProblem problem;
    Algorithm algorithm;
    DifferentialEvolutionSelection selection;
    DifferentialEvolutionCrossover crossover;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      //problemName = "org.uma.jmetal.problem.multiobjective.Srinivas";
    }

    problem = (DoubleProblem) ProblemUtils.loadProblem(problemName);

    double cr = 0.5 ;
    double f = 0.5 ;
    crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin") ;
    selection = new DifferentialEvolutionSelection() ;

    SolutionListEvaluator evaluator = new MultithreadedSolutionListEvaluator(0, problem) ;

    algorithm = new GDE3Builder(problem)
        .setCrossover(crossover)
        .setSelection(selection)
        .setMaxIterations(250)
        .setPopulationSize(100)
        .setSolutionSetEvaluator(evaluator)
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<DoubleSolution> population = ((GDE3)algorithm).getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    evaluator.shutdown();
  }
}
