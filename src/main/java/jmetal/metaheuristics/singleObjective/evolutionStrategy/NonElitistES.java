//  NonElitistES.java
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

package jmetal.metaheuristics.singleObjective.evolutionStrategy;

import jmetal.core.*;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

import java.util.Comparator;

/** 
 * Class implementing a (mu,lambda) ES. Lambda must be divisible by mu.
 */
public class NonElitistES extends Algorithm {
  private int     mu_     ;
  private int     lambda_ ;
  
 /**
  * Constructor
  * Create a new NonElitistES instance.
  * @param problem Problem to solve.
  * @mu Mu
  * @lambda Lambda
  */
  public NonElitistES(Problem problem, int mu, int lambda){
    super(problem) ;
    mu_      = mu     ;
    lambda_  = lambda ;
  } // NonElitistES
  
 /**
  * Execute the NonElitistES algorithm
 * @throws JMException 
  */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations ;
    int evaluations    ;

    Solution bestIndividual ;
    
    SolutionSet population          ;
    SolutionSet offspringPopulation ;  

    Operator   mutationOperator ;
    Comparator comparator       ;
    
    comparator = new ObjectiveComparator(0) ; // Single objective comparator
    
    // Read the params
    maxEvaluations = ((Integer)this.getInputParameter("maxEvaluations")).intValue();                
   
    // Initialize the variables
    population          = new SolutionSet(mu_ + 1) ;   
    offspringPopulation = new SolutionSet(lambda_) ;
    
    evaluations  = 0;                

    // Read the operators
    mutationOperator  = this.operators_.get("mutation");

    System.out.println("(" + mu_ + " , " + lambda_+")ES") ;
     
    // Create the parent population of mu solutions
    Solution newIndividual;
    newIndividual = new Solution(problem_) ;
    problem_.evaluate(newIndividual);
    evaluations ++ ;
    population.add(newIndividual);
    bestIndividual = new Solution(newIndividual) ;
    
    for (int i = 1; i < mu_; i++) {
      System.out.println(i) ;
      newIndividual = new Solution(problem_);                    
      problem_.evaluate(newIndividual);            
      evaluations++;
      population.add(newIndividual);
      
      if (comparator.compare(bestIndividual, newIndividual) > 0 )
        bestIndividual = new Solution(newIndividual) ;
    } //for       
     
    // Main loop
    int offsprings ;
    offsprings = lambda_ / mu_ ; 
    while (evaluations < maxEvaluations) {
      // STEP 1. Generate the lambda population
      for (int i = 0; i < mu_; i++) {
        for (int j = 0; j < offsprings; j++) {
          Solution offspring = new Solution(population.get(i)) ;
          mutationOperator.execute(offspring);
          problem_.evaluate(offspring) ;
          offspringPopulation.add(offspring) ;
          evaluations ++ ;
        } // for
      } // for
   
      // STEP 2. Sort the lambda population
      offspringPopulation.sort(comparator) ;
            
      // STEP 3. Update the best individual 
      if (comparator.compare(bestIndividual, offspringPopulation.get(0)) > 0 )
        bestIndividual = new Solution(offspringPopulation.get(0)) ;
      
      // STEP 4. Create the new mu population
      population.clear() ;
      for (int i = 0; i < mu_; i++)
        population.add(offspringPopulation.get(i)) ;

      System.out.println("Evaluation: " + evaluations + 
          " Current best fitness: " +  population.get(0).getObjective(0) +
          " Global best fitness: " + bestIndividual.getObjective(0)) ;

      // STEP 5. Delete the lambda population
      offspringPopulation.clear() ;
    } // while
    
    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1) ;
    resultPopulation.add(population.get(0)) ;
    
    return resultPopulation ;
  } // execute
} // NonElitisES