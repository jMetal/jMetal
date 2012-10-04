//  ssGAS.java
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

import jmetal.core.*;
import jmetal.encodings.variable.Permutation;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.util.comparators.*;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.operators.selection.WorstSolutionSelection;
import jmetal.util.*;

/** 
 * Class implementing a steady-state genetic algorithm
 */
public class ssGA extends Algorithm {
  
 /**
  *
  * Constructor
  * Create a new SSGA instance.
  * @param problem Problem to solve
  *
  */
  public ssGA(Problem problem){
    super(problem) ;
  } // SSGA
  
 /**
  * Execute the SSGA algorithm
 * @throws JMException 
  */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize ;
    int maxEvaluations ;
    int evaluations    ;

    SolutionSet population        ;
    Operator    mutationOperator  ;
    Operator    crossoverOperator ;
    Operator    selectionOperator ;
    
    Comparator  comparator        ;
    
    comparator = new ObjectiveComparator(0) ; // Single objective comparator
    
    Operator findWorstSolution ;
    HashMap  parameters ; // Operator parameters
    parameters = new HashMap() ;
    parameters.put("comparator", comparator) ;

    findWorstSolution = new WorstSolutionSelection(parameters) ;

    // Read the parameters
    populationSize = ((Integer)this.getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer)this.getInputParameter("maxEvaluations")).intValue();                
   
    // Initialize the variables
    population   = new SolutionSet(populationSize);        
    evaluations  = 0;                

    // Read the operators
    mutationOperator  = this.operators_.get("mutation");
    crossoverOperator = this.operators_.get("crossover");
    selectionOperator = this.operators_.get("selection");  

    // Create the initial population
    Solution newIndividual;
    for (int i = 0; i < populationSize; i++) {
      newIndividual = new Solution(problem_);                    
      problem_.evaluate(newIndividual);            
      evaluations++;
      population.add(newIndividual);
    } //for       

    // main loop
    while (evaluations < maxEvaluations) {
      Solution [] parents = new Solution[2];
      
      // Selection
      parents[0] = (Solution)selectionOperator.execute(population);
      parents[1] = (Solution)selectionOperator.execute(population);
 
      // Crossover
      Solution [] offspring = (Solution []) crossoverOperator.execute(parents);  

      // Mutation
      mutationOperator.execute(offspring[0]);

      // Evaluation of the new individual
      problem_.evaluate(offspring[0]);            
          
      evaluations ++;
    
      // Replacement: replace the last individual is the new one is better
      int worstIndividual = (Integer)findWorstSolution.execute(population) ;
          
      if (comparator.compare(population.get(worstIndividual), offspring[0]) > 0) {
        population.remove(worstIndividual) ;
        population.add(offspring[0]);
      } // if
    } // while
    
    // Return a population with the best individual
    

    SolutionSet resultPopulation = new SolutionSet(1) ;    
    resultPopulation.add(population.best(comparator));
    
    System.out.println("Evaluations: " + evaluations ) ;
    
    return resultPopulation ;
  } // execute
} // ssGA
