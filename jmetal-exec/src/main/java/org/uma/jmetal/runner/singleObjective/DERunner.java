//  DERunner.java
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
import org.uma.jmetal.metaheuristic.singleobjective.differentialevolution.DE;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.singleObjective.CEC2005Problem;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class runs a single-objective DE algorithm.
 */
public class DERunner {

  public static void main(String[] args) throws JMetalException, ClassNotFoundException, IOException {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator selection;

    //int bits ; // Length of bit string in the OneMax problem

    //bits = 512 ;
    //problem = new OneMax(bits);

    //problem = new Sphere("Real", 20) ;
    //problem = new Easom("Real") ;
    //problem = new Griewank("Real", 10) ;
    problem = new CEC2005Problem("Real", 5, 10);

    //problem = new Sphere("Real", 20) ;
    //problem = new Easom("Real") ;
    //problem = new Griewank("Real", 10) ;

    algorithm = new DE();   // Asynchronous cGA
    algorithm.setProblem(problem);
    
    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 1000000);

    // Crossover operator 
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("CR", 0.5);
    crossoverParameters.put("F", 0.5);
    crossoverParameters.put("DE_VARIANT", "rand/1/bin");
    crossover =
      CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", crossoverParameters);

    // Add the operator to the algorithm
    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    selection =
      SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", selectionParameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("selection", selection);
 
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
