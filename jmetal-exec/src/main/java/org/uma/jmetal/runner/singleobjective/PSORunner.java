//  PSORunner.java
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

package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization.PSO;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.problem.singleobjective.Griewank;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;

/**
 * Class for configuring and running a single-objective PSO algorithm
 */
public class PSORunner {
  public static java.util.logging.Logger logger_;      // Logger object
  public static FileHandler fileHandler_; // FileHandler object

  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException
   */
  public static void main(String[] args)
    throws JMetalException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Mutation mutation;

    // Logger object and file to store log messages
    logger_ = JMetalLogger.logger;
    fileHandler_ = new FileHandler("PSO_main.log");
    logger_.addHandler(fileHandler_);

    problem = new Griewank("Real", 10);
    problem = new Sphere("Real", 20);
    //problem = new Easom("Real") ;

    //problem = new Rosenbrock("Real", 10);

    algorithm = new PSO();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", 50);
    algorithm.setInputParameter("maxIterations", 5000);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("setProbability", 1.0 / problem.getNumberOfVariables());
    mutationParameters.put("setDistributionIndex", 20.0);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    algorithm.addOperator("mutation", mutation);

    // Execute the Algorithm 
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages 
    logger_.info("Total execution time: " + estimatedTime + "ms");
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
  }
}
