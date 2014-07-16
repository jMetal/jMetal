//  GARunner.java
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
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.GenerationalGA;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.singleObjective.OneMax;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be
 * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class SynchronousCellularGA) or an asynchronous cGA (class AsynchronousCellularGA). The OneMax
 * problem is used to org.uma.test the algorithms.
 */
public class GARunner {

  public static void main(String[] args) throws JMetalException, ClassNotFoundException, IOException {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    int bits = 512;
    problem = new OneMax("Binary", bits);

    //problem = new Sphere("Real", 10) ;
    //problem = new Easom("Real") ;
    //problem = new Griewank("Real", 10) ;

    algorithm = new GenerationalGA(); // Generational GA
    algorithm.setProblem(problem);
    
    //algorithm = new ssGA(problem); // Steady-state GA
    //algorithm = new SynchronousCellularGA(problem) ; // Synchronous cGA
    //algorithm = new AsynchronousCellularGA(problem) ;   // Asynchronous cGA
    
    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 25000);
    /*
    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfRealVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
    */

    // Mutation and Crossover for Binary codification 
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 0.9);
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", crossoverParameters);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 1.0 / bits);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", mutationParameters);                    
    
    /* Selection Operator */
    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", selectionParameters);
    
    /* Add the operator to the algorithm*/
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);
 
    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    Configuration.logger.info("Total execution time: " + estimatedTime);

    /* Log messages */
    Configuration.logger.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    Configuration.logger.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
  }
}
