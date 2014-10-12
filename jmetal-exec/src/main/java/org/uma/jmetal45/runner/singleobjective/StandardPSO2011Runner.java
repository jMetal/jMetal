//  StandardPSO2011Runner.java
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
import org.uma.jmetal45.metaheuristic.singleobjective.particleswarmoptimization.StandardPSO2011;
import org.uma.jmetal45.problem.singleobjective.CEC2005Problem;
import org.uma.jmetal45.util.AlgorithmRunner;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal45.util.fileoutput.SolutionSetOutput;

/**
 * Class for configuring and running a single-objective PSO algorithm
 */
public class StandardPSO2011Runner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal45.util.JMetalException
   * @throws java.io.IOException
   * @throws ClassNotFoundException
   */
  public static void main(String[] args)
          throws Exception {
    Problem problem;  
    Algorithm algorithm;  

		problem = new CEC2005Problem("Real", 5, 10);
		/* Examples
    //problem = new Sphere("Real", 20) ;
    // problem = new Griewank("Real", 10) ;
		 */

		algorithm = new StandardPSO2011.Builder(problem)
		.setSwarmSize(10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())))
		.setMaxIterations(80000)
		.setNumberOfParticlesToInform(3)
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
