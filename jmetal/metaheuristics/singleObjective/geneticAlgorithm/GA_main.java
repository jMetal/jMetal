//  GA_main.java
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

package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.singleObjective.OneMax;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be 
 * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax
 * problem is used to test the algorithms.
 */
public class GA_main {

  public static void main(String [] args) throws JMException, ClassNotFoundException {
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator
            
    //int bits ; // Length of bit string in the OneMax problem
    HashMap  parameters ; // Operator parameters

    int bits = 512 ;
    problem = new OneMax("Binary", bits);
 
    //problem = new Sphere("Real", 10) ;
    //problem = new Easom("Real") ;
    //problem = new Griewank("Real", 10) ;
    
    algorithm = new gGA(problem) ; // Generational GA
    //algorithm = new ssGA(problem); // Steady-state GA
    //algorithm = new scGA(problem) ; // Synchronous cGA
    //algorithm = new acGA(problem) ;   // Asynchronous cGA
    
    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize",100);
    algorithm.setInputParameter("maxEvaluations", 25000);
    /*
    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
    */
    
    // Mutation and Crossover for Binary codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/bits) ;
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
    
    /* Selection Operator */
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            
    
    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
 
    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    System.out.println("Total execution time: " + estimatedTime);

    /* Log messages */
    System.out.println("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    System.out.println("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");          
  } //main
} // GA_main
