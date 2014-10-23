//  NonElitistEvolutionStrategyTest.java
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

package org.uma.test.metaheuristic.singleobjective.evolutionstrategy;

import org.junit.Test;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.metaheuristic.singleobjective.evolutionstrategy.NonElitistEvolutionStrategy;
import org.uma.jmetal45.operator.mutation.BitFlipMutation;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.problem.singleobjective.OneMax;
import org.uma.jmetal45.problem.singleobjective.Sphere;
import org.uma.jmetal45.util.AlgorithmRunner;

import static org.junit.Assert.assertTrue;


/**
 * Created by Antonio J. Nebro on 30/08/14.
 */
public class NonElitistEvolutionStrategyTest {
  NonElitistEvolutionStrategy algorithm;
  Problem problem;

  @Test
  public void solvingBinaryProblemTest() throws Exception {
    int binaryStringLength = 150 ;
    problem = new OneMax("Binary", binaryStringLength) ;

    Mutation mutation = new BitFlipMutation.Builder()
            .setProbability(1.0/binaryStringLength)
            .build() ;

    algorithm = new NonElitistEvolutionStrategy.Builder(problem)
            .setMu(1)
            .setLambda(10)
            .setMaxEvaluations(100000)
            .setMutation(mutation)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    Solution solution = population.get(0) ;
    assertTrue((-1 * (int)solution.getObjective(0)) > (binaryStringLength-10)) ;
  }

  @Test
  public void solvingRealProblemTest() throws Exception {
    problem = new Sphere("Real", 10) ;

    Mutation mutation = new PolynomialMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfVariables())
            .setDistributionIndex(20.0)
            .build() ;

    algorithm = new NonElitistEvolutionStrategy.Builder(problem)
            .setMu(1)
            .setLambda(10)
            .setMaxEvaluations(250000)
            .setMutation(mutation)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    Solution solution = population.get(0) ;
    assertTrue(solution.getObjective(0) < 0.01) ;
  }
}
