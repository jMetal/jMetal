//  AsynchronousCellularGeneticAlgorithmRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 201r Antonio J. Nebro
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

package org.uma.jmetal.exec.runner.singleobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.AsynchronousCellularGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

/**
 * This class runs a single-objective asynchronous genetic algorithm
 */
public class AsynchronousCellularGeneticAlgorithmRunner {

  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;

    problem = new Sphere("Real", 30);

    crossover = new SBXCrossover.Builder()
            .setProbability(0.9)
            .setDistributionIndex(20)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfVariables())
            .setDistributionIndex(20)
            .build() ;

    algorithm = new AsynchronousCellularGeneticAlgorithm.Builder(problem)
            .setPopulationSize(100)
            .setMaxEvaluations(250000)
            .setCrossover(crossover)
            .setMutation(mutation)
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
  }
}
