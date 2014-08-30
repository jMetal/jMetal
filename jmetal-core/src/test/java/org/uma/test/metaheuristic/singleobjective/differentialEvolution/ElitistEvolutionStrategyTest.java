//  ElitistEvolutionStrategyTest.java
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

package org.uma.test.metaheuristic.singleobjective.differentialEvolution;

import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.ElitistEvolutionStrategy;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialBitFlipMutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.util.AlgorithmRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by Antonio J. Nebro on 30/08/14.
 */
public class ElitistEvolutionStrategyTest {
  ElitistEvolutionStrategy algorithm;
  Problem problem;

  @Test
  public void solvingBinaryProblemTest() throws IOException, ClassNotFoundException {
    int binaryStringLength = 200 ;
    problem = new OneMax("Binary", binaryStringLength) ;

    Mutation mutation = new BitFlipMutation.Builder()
            .setProbability(1.0/binaryStringLength)
            .build() ;

    algorithm = new ElitistEvolutionStrategy.Builder(problem)
            .setMu(1)
            .setLambda(10)
            .setMaxEvaluations(30000)
            .setMutation(mutation)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    Solution solution = population.get(0) ;
    assertEquals(binaryStringLength, -1 * (int)solution.getObjective(0)) ;
  }

  @Test
  public void solvingRealProblemTest() throws IOException, ClassNotFoundException {
    problem = new Sphere("Real", 20) ;

    Mutation mutation = new PolynomialMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfVariables())
            .setDistributionIndex(20.0)
            .build() ;

    algorithm = new ElitistEvolutionStrategy.Builder(problem)
            .setMu(1)
            .setLambda(10)
            .setMaxEvaluations(100000)
            .setMutation(mutation)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    Solution solution = population.get(0) ;
    assertTrue(solution.getObjective(0) < 0.00001) ;
  }
}
