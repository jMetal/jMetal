//  IBEARunner.java
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

import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Class for configuring and running the IBEA algorithm
 */
public class IBEARunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Usage: three choices
   *       - org.uma.jmetal45.runner.multiobjective.IBEARunner
   *       - org.uma.jmetal45.runner.multiobjective.IBEARunner problemName
   *       - org.uma.jmetal45.runner.multiobjective.IBEARunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    CrossoverOperator crossover;
    MutationOperator mutation;
    SelectionOperator selection;

    String problemName = "org.uma.jmetal.problem.multiobjective.Fonseca" ;

    try {
      problem = (Problem)Class.forName(problemName).getConstructor().newInstance() ;
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

    crossover = new SBXCrossover.Builder()
      .setProbability(0.9)
      .setDistributionIndex(20.0)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .setProbability(1.0 / problem.getNumberOfVariables())
      .setDistributionIndex(20.0)
      .build() ;

    selection = new BinaryTournamentSelection.Builder()
      .setComparator(new FitnessComparator())
      .build() ;

    algorithm = new IBEA.Builder(problem)
      .setArchiveSize(100)
      .setPopulationSize(100)
      .setMaxEvaluations(25000)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setSelection(selection)
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
