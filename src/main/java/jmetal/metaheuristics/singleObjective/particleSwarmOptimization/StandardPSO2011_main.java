//  StandardPSO2011_main.java
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

package jmetal.metaheuristics.singleObjective.particleSwarmOptimization;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.problems.singleObjective.CEC2005Problem;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class for configuring and running a single-objective PSO algorithm
 */
public class StandardPSO2011_main {
  public static Logger logger_;      // Logger object
  public static FileHandler fileHandler_; // FileHandler object

  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   * @throws SecurityException
   */
  public static void main(String[] args)
    throws JMException, IOException, ClassNotFoundException {
    Problem problem;  // The problem to solve
    Algorithm algorithm;  // The algorithm to use

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("PSO_main.log");
    logger_.addHandler(fileHandler_);

    //problem = new Sphere("Real", 20) ;
    //problem = new Easom("Real") ;
    // problem = new Griewank("Real", 10) ;

    //problem = new Sphere("Real", 20);
    problem = new CEC2005Problem("Real", 5, 10);

    algorithm = new StandardPSO2011();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", 100);
    algorithm.setInputParameter("maxIterations", 8000);
    algorithm.setInputParameter("numberOfParticlesToInform", 3);

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
