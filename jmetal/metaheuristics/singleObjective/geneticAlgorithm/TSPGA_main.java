//  TSPGA_main.java
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.singleObjective.*  ; 
import jmetal.util.JMException;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be 
 * a steady-state GA (class SSGA) or a generational GA (class GGA). The TSP
 * is used to test the algorithms. The data files accepted as in input are from
 * TSPLIB.
 */
public class TSPGA_main {

  public static void main(String [] args)  throws FileNotFoundException, 
                                                  IOException, JMException, 
                                                  ClassNotFoundException {
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator

    HashMap  parameters ; // Operator parameters
        
    String problemName = "eil101.tsp" ;
    
    problem = new TSP(problemName);
    
    algorithm = new ssGA(problem);
    //algorithm = new gGA(problem) ;
    
    // Algorithm params
    algorithm.setInputParameter("populationSize",512);
    algorithm.setInputParameter("maxEvaluations",2000000);
    
    // Mutation and Crossover for Real codification
    parameters = new HashMap() ;
    parameters.put("probability", 0.95) ;
    crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", parameters);
    //crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover");
    
    parameters = new HashMap() ;
    parameters.put("probability", 0.2) ;
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);                    
  
    /* Selection Operator */
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            
    
    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    System.out.println("Total time of execution: "+estimatedTime);

    /* Log messages */
    System.out.println("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    System.out.println("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");          
  }//main
} // TSPGA_main
