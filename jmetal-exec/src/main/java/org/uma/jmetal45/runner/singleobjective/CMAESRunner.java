//  CMAESRunner.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
//
//  Copyright (c) 2013 Esteban López-Camacho
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
import org.uma.jmetal45.metaheuristic.singleobjective.cmaes.CMAES;
import org.uma.jmetal45.problem.singleobjective.Rosenbrock;
import org.uma.jmetal45.util.AlgorithmRunner;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal45.util.fileoutput.SolutionSetOutput;

/**
 * This class runs a single-objective CMA-ES algorithm.
 */
public class CMAESRunner {

  public static void main(String[] args) throws Exception {
    int numberOfVariables = 20;
    int populationSize = 10;
    int maxEvaluations = 1000000;

    Problem problem;
    Algorithm algorithm;

    //problem = new Sphere("Real", numberOfVariables) ;
    //problem = new Griewank("Real", populationSize) ;
    //problem = new Schwefel("Real", numberOfVariables) ;
    problem = new Rosenbrock("Real", numberOfVariables);
    //problem = new Rastrigin("Real", numberOfVariables) ;

    algorithm = new CMAES.Builder(problem)
            .setPopulationSize(populationSize)
            .setMaxEvaluations(maxEvaluations)
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
