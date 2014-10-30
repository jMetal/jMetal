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

package org.uma.jmetal.algorithm.impl.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;

/**
 */
public class GARunner {
  /**
   */
  public static void main(String[] args) throws
          Exception {

    Algorithm algorithm;
    BinaryProblem problem = new OneMax(512) ;

    CrossoverOperator crossoverOperator = new SinglePointCrossover.Builder()
            .setProbability(0.9)
            .build() ;

    MutationOperator mutationOperator = new BitFlipMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfBits(0))
            .build();

    SelectionOperator selectionOperator = new BinaryTournamentSelection.Builder()
            .build();

    algorithm = new GenerationalGeneticAlgorithm.Builder(problem)
            .setPopulationSize(100)
            .setMaxIterations(250)
            .setCrossoverOperator(crossoverOperator)
            .setMutationOperator(mutationOperator)
            .setSelectionOperator(selectionOperator)
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
