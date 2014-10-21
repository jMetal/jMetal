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

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.metaheuristic.multiobjective.gde3.GDE3;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Class for configuring and running the GDE3 algorithm
 */
public class GDE3Runner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal45.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Usage: three choices
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner problemName
   *        - org.uma.jmetal45.runner.multiobjective.GDE3Runner problemName paretoFrontFile
   */
  public static void main(String[] args) {
    ContinuousProblem problem;
    Algorithm algorithm;
    DifferentialEvolutionSelection selection;
    DifferentialEvolutionCrossover crossover;

    String problemName = "org.uma.jmetal.problem.multiobjective.Fonseca" ;

    try {
      problem = (ContinuousProblem)Class.forName(problemName).getConstructor().newInstance() ;
      //problem = (Problem) ClassLoader.getSystemClassLoader().loadClass("org.uma.jmetal.problem.multiobjective.Fonseca").newInstance();
    } catch (InstantiationException e) {
      throw new JMetalException("newInstance() cannot instantiate (abstract class)", e) ;
    } catch (IllegalAccessException e) {
      throw new JMetalException("newInstance() is not usable (uses restriction)", e) ;
    } catch (InvocationTargetException e) {
      throw new JMetalException("an exception was thrown during the call of newInstance()", e) ;
    } catch (NoSuchMethodException e) {
      throw new JMetalException("getConstructor() was not able to find the constructor without arguments", e) ;
    } catch (ClassNotFoundException e) {
      throw new JMetalException("Class.forName() did not recognized the name of the class", e) ;
    }

     /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator()
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem)
     */

    crossover = new DifferentialEvolutionCrossover.Builder()
      .setCr(0.5)
      .setF(0.5)
      .build() ;

    selection = new DifferentialEvolutionSelection.Builder()
      .build();

    algorithm = new GDE3.Builder(problem)
      .setCrossover(crossover)
      .setSelection(selection)
      .setMaxIterations(250)
      .setPopulationSize(100)
      .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    List<Solution<?>> population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

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
