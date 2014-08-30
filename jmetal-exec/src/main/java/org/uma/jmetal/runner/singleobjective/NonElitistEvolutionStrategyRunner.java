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

package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.ElitistEvolutionStrategy;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.NonElitistEvolutionStrategy;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;

/**
 * This class runs a single-objective Evolution Strategy (ES). The ES can be
 * a (mu+lambda) ES (class ElitistEvolutionStrategy) or a (mu,lambda) ES (class NonElitistGA).
 * The OneMax problem is used to test the algorithms.
 */
public class NonElitistEvolutionStrategyRunner {

  public static void main(String[] args) throws JMetalException, ClassNotFoundException, IOException {
    Problem problem;
    Algorithm algorithm;
    Mutation mutation;

    problem = new Sphere("Real", 20) ;

    int mu;
    int lambda;

    // Requirement: lambda must be divisible by mu
    mu = 1;
    lambda = 10;

    mutation = new PolynomialMutation.Builder()
            .setProbability(1.0/problem.getNumberOfVariables())
            .setDistributionIndex(20.0)
            .build() ;

    algorithm = new NonElitistEvolutionStrategy.Builder(problem)
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
