//  ESRunner.java
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
package org.uma.jmetal.runner.singleObjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristics.singleObjective.evolutionStrategy.ElitistES;
import org.uma.jmetal.operators.mutation.MutationFactory;
import org.uma.jmetal.problems.singleObjective.OneMax;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class runs a single-objective Evolution Strategy (ES). The ES can be
 * a (mu+lambda) ES (class ElitistES) or a (mu,lambda) ES (class NonElitistGA).
 * The OneMax problem is used to org.uma.test the algorithms.
 */
public class ESRunner {

  public static void main(String[] args) throws JMetalException, ClassNotFoundException, IOException {
    Problem problem;
    Algorithm algorithm;
    Operator mutation;

    int bits; // Length of bit string in the OneMax problem

    bits = 512;
    problem = new OneMax("Binary", bits);

    int mu;
    int lambda;

    // Requirement: lambda must be divisible by mu
    mu = 1;
    lambda = 10;

    algorithm = new ElitistES(mu, lambda);
    algorithm.setProblem(problem);
    //algorithm = new NonElitistES(problem, mu, lambda);
    
    /* Algorithm params*/
    algorithm.setInputParameter("maxEvaluations", 20000);
    
    /* Mutation and Crossover for Real codification */
    /* Mutation for Real codification */
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 1.0 / bits);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", mutationParameters);

    algorithm.addOperator("mutation", mutation);
 
    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    Configuration.logger_.info("Total execution time: " + estimatedTime);

    /* Log messages */
    Configuration.logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    Configuration.logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
  }
}
