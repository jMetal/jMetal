//  PESA2.java
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

package jmetal.metaheuristics.pesa2;

import jmetal.core.*;
import jmetal.operators.selection.PESA2Selection;
import jmetal.util.JMException;
import jmetal.util.archive.AdaptiveGridArchive;

import java.util.HashMap;

/**
 * This class implements the PESA2 algorithm. 
 */
public class PESA2 extends Algorithm{
    
  /**
  * Constructor
  * Creates a new instance of PESA2
  */
  public PESA2(Problem problem) {
    super (problem) ;
  } // PESA2
    
  /**   
  * Runs of the PESA2 algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
   * @throws JMException 
  */  
  public SolutionSet execute() throws JMException, ClassNotFoundException {        
    int archiveSize, bisections, maxEvaluations, evaluations, populationSize;        
    AdaptiveGridArchive archive;
    SolutionSet solutionSet;
    Operator crossover,mutation, selection;
        
    // Read parameters
    populationSize = ((Integer)(inputParameters_.get("populationSize"))).intValue();
    archiveSize    = ((Integer)(inputParameters_.get("archiveSize"))).intValue()   ;
    bisections     = ((Integer)(inputParameters_.get("bisections"))).intValue()    ;
    maxEvaluations = ((Integer)(inputParameters_.get("maxEvaluations"))).intValue();
    
    // Get the operators
    crossover = operators_.get("crossover");
    mutation  = operators_.get("mutation");
            
    // Initialize the variables
    evaluations = 0;    
    archive = new AdaptiveGridArchive(archiveSize,bisections,
                                        problem_.getNumberOfObjectives());
    solutionSet  = new SolutionSet(populationSize);
    HashMap  parameters = null ;
    selection    = new PESA2Selection(parameters);

    //-> Create the initial individual and evaluate it and his constraints
    for (int i = 0; i < populationSize; i++){
      Solution solution = new Solution(problem_);
      problem_.evaluate(solution);        
      problem_.evaluateConstraints(solution);
      evaluations++;    
      solutionSet.add(solution);      
    }
    //<-                
        
    // Incorporate non-dominated solution to the archive
    for (int i = 0; i < solutionSet.size();i++){
      archive.add(solutionSet.get(i)); // Only non dominated are accepted by 
                                      // the archive
    }
    
    // Clear the init solutionSet
    solutionSet.clear();
    
    //Iterations....
    Solution [] parents = new Solution[2];
    do {
      //-> Create the offSpring solutionSet                    
      while (solutionSet.size() < populationSize){                        
        parents[0] = (Solution) selection.execute(archive);
        parents[1] = (Solution) selection.execute(archive);
        
        Solution [] offSpring = (Solution []) crossover.execute(parents);        
        mutation.execute(offSpring[0]);
        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        evaluations++;
        solutionSet.add(offSpring[0]);                
      }
            
      for (int i = 0; i < solutionSet.size(); i++)
        archive.add(solutionSet.get(i));
      
      // Clear the solutionSet
      solutionSet.clear();
                                                                        
    }while (evaluations < maxEvaluations);                                            
    //Return the  solutionSet of non-dominated individual
    return archive;                
  } // execute      
} // PESA2
