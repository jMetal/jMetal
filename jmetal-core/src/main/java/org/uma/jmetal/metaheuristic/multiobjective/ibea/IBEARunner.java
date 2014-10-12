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

package org.uma.jmetal.metaheuristic.multiobjective.ibea;

import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.util.List;

/**
 * Class for configuring and running the IBEA algorithm
 */
public class IBEARunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal45.util.JMetalException
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

    problem = new ZDT1() ;
      /* Examples
      //problem = new Water("Real");
      //problem = new ZDT4("ArrayReal");
      //problem = new WFG1("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
      */

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

    List<Solution> population = algorithmRunner.getSolutionSet() ;
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
