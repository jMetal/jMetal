//  EvolutionStrategyRunner.java
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

package org.uma.jmetal45.runner.singleobjective;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.metaheuristic.singleobjective.evolutionstrategy.ElitistEvolutionStrategy;
import org.uma.jmetal45.operator.mutation.BitFlipMutation;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.problem.singleobjective.OneMax;
import org.uma.jmetal45.util.AlgorithmRunner;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal45.util.fileOutput.SolutionSetOutput;

/**
 * This class runs a single-objective Evolution Strategy (ES). The ES can be
 * a (mu+lambda) ES (class ElitistEvolutionStrategy) or a (mu,lambda) ES (class NonElitistGA).
 * The OneMax problem is used to test the algorithms.
 */
public class ElitistEvolutionStrategyRunner {

  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    Mutation mutation;

    int binaryStringLength;

    binaryStringLength = 512;
    problem = new OneMax("Binary", binaryStringLength);

    int mu;
    int lambda;

    // Requirement: lambda must be divisible by mu
    mu = 1;
    lambda = 10;

    mutation = new BitFlipMutation.Builder()
            .setProbability(1.0/binaryStringLength)
            .build() ;

    algorithm = new ElitistEvolutionStrategy.Builder(problem)
            .setMu(mu)
            .setLambda(lambda)
            .setMaxEvaluations(25000)
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
