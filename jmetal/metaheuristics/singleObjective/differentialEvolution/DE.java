//  DE.java
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

package jmetal.metaheuristics.singleObjective.differentialEvolution;

import java.util.Comparator;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;

/**
 * This class implements a differential evolution algorithm. 
 */
public class DE extends Algorithm {
  
  /**
  * Constructor
  * @param problem Problem to solve
  */
  public DE(Problem problem){
    super(problem) ;
  } // gDE
  
  /**   
   * Runs of the DE algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution  
    * @throws JMException 
   */  
   public SolutionSet execute() throws JMException, ClassNotFoundException {
     int populationSize ;
     int maxEvaluations ;
     int evaluations    ;
     
     SolutionSet population          ;
     SolutionSet offspringPopulation ;
          
     Operator selectionOperator ;
     Operator crossoverOperator ;
               
     Comparator  comparator ;
     comparator = new ObjectiveComparator(0) ; // Single objective comparator
     
     // Differential evolution parameters
     int r1    ;
     int r2    ;
     int r3    ;
     int jrand ;

     Solution parent[] ;
     
     //Read the parameters
     populationSize = ((Integer)this.getInputParameter("populationSize")).intValue();
     maxEvaluations  = ((Integer)this.getInputParameter("maxEvaluations")).intValue();     
    
     selectionOperator = operators_.get("selection");   
     crossoverOperator = operators_.get("crossover") ;
     
     //Initialize the variables
     population  = new SolutionSet(populationSize);        
     evaluations = 0;                

     // Create the initial solutionSet
     Solution newSolution;
     for (int i = 0; i < populationSize; i++) {
       newSolution = new Solution(problem_);                    
       problem_.evaluate(newSolution);            
       problem_.evaluateConstraints(newSolution);
       evaluations++;
       population.add(newSolution);
     } //for       
   
     // Generations ...
     population.sort(comparator) ;
     while (evaluations < maxEvaluations) {
       
       // Create the offSpring solutionSet      
       offspringPopulation  = new SolutionSet(populationSize);        

       //offspringPopulation.add(new Solution(population.get(0))) ;	
      
       for (int i = 0; i < populationSize; i++) {   
         // Obtain parents. Two parameters are required: the population and the 
         //                 index of the current individual
         parent = (Solution [])selectionOperator.execute(new Object[]{population, i});

         Solution child ;
         
         // Crossover. Two parameters are required: the current individual and the 
         //            array of parents
         child = (Solution)crossoverOperator.execute(new Object[]{population.get(i), parent}) ;

         problem_.evaluate(child) ;

         evaluations++ ;
         
         if (comparator.compare(population.get(i), child) < 0) 
           offspringPopulation.add(new Solution(population.get(i))) ;
         else
           offspringPopulation.add(child) ;
       } // for
       
       // The offspring population becomes the new current population
       population.clear();
       for (int i = 0; i < populationSize; i++) {
         population.add(offspringPopulation.get(i)) ;
       }
       offspringPopulation.clear();
       population.sort(comparator) ;
     } // while
     
     // Return a population with the best individual
     SolutionSet resultPopulation = new SolutionSet(1) ;
     resultPopulation.add(population.get(0)) ;
     
     System.out.println("Evaluations: " + evaluations ) ;
     return resultPopulation ;
   } // execute
} // DE
