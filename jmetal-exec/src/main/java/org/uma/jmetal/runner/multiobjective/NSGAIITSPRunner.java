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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.MultiobjectiveTSP;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.IOException;
import java.util.List;

/**
 * Class for configuring and running the NSGA-II algorithm to solve the bi-objective TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIITSPRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIITSPRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, IOException {
    PermutationProblem<PermutationSolution<Integer>> problem;
    Algorithm<List<PermutationSolution<Integer>>> algorithm;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    problem = new MultiobjectiveTSP("/tspInstances/kroA100.tsp", "/tspInstances/kroB100.tsp");

    crossover = new PMXCrossover(0.9) ;

    double mutationProbability = 0.2 ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(10000)
            .setPopulationSize(100)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    List<PermutationSolution<Integer>> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
