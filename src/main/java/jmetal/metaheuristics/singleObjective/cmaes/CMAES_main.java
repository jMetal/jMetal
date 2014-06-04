//  CMAES_main.java
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
package jmetal.metaheuristics.singleObjective.cmaes;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.problems.singleObjective.Rosenbrock;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;

/**
 * This class runs a single-objective CMA-ES algorithm.
 */
public class CMAES_main {

  public static void main(String[] args) throws JMException, ClassNotFoundException, IOException {
    int numberOfVariables = 20;
    int populationSize = 10;
    int maxEvaluations = 1000000;

    Problem problem;
    Algorithm algorithm;

    //problem = new Sphere("Real", numberOfVariables) ;
    //problem = new Easom("Real") ;
    //problem = new Griewank("Real", populationSize) ;
    //problem = new Schwefel("Real", numberOfVariables) ;
    problem = new Rosenbrock("Real", numberOfVariables);
    //problem = new Rastrigin("Real", numberOfVariables) ;

    algorithm = new CMAES();
    algorithm.setProblem(problem);
    
    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);
 
    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    Configuration.logger_.info("Total execution time: " + estimatedTime);

    /* Log messages */
    Configuration.logger_.info("Objectives values have been written to file FUN");
    population.printObjectivesToFile("FUN");
    Configuration.logger_.info("Variables values have been written to file VAR");
    population.printVariablesToFile("VAR");
  }
}
