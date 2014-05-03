//  MutationLocalSearch.java
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

package jmetal.operators.localSearch;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.mutation.Mutation;
import jmetal.util.JMException;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements an local search operator based in the use of a 
 * mutation operator. An archive is used to store the non-dominated solutions
 * found during the search.
 */
public class MutationLocalSearch extends LocalSearch {
    
  /**
   * Stores the problem to solve
   */
  private Problem problem_;
    
  /**
  * Stores a reference to the archive in which the non-dominated solutions are
  * inserted
  */
  private SolutionSet archive_;

  private int improvementRounds_ ; 

  /**
   * Stores comparators for dealing with constraints and dominance checking, 
   * respectively.
   */
  private Comparator constraintComparator_ ;
  private Comparator dominanceComparator_ ;
  
  /**
   * Stores the mutation operator 
   */
  private Operator mutationOperator_;
  
  /**
   * Stores the number of evaluations_ carried out
   */
  int evaluations_ ;  
  
  /**
  * Constructor. 
  * Creates a new local search object.
  * @param parameters The parameters

  */
  public MutationLocalSearch(HashMap<String, Object> parameters) {
  	super(parameters) ;
  	if (parameters.get("problem") != null)
  		problem_ = (Problem) parameters.get("problem") ;  		
  	if (parameters.get("improvementRounds") != null)
  		improvementRounds_ = (Integer) parameters.get("improvementRounds") ;  		
  	if (parameters.get("mutation") != null)
  	  mutationOperator_ = (Mutation) parameters.get("mutation") ;  		

    evaluations_          = 0      ;
    archive_              = null;
    dominanceComparator_  = new DominanceComparator();
    constraintComparator_ = new OverallConstraintViolationComparator();
  } //Mutation improvement


  /**
  * Constructor. 
  * Creates a new local search object.
  * @param problem The problem to solve
  * @param mutationOperator The mutation operator 
  */
  //public MutationLocalSearch(Problem problem, Operator mutationOperator) {
  //  evaluations_ = 0 ;
  //  problem_ = problem;
  //  mutationOperator_ = mutationOperator;
  //  dominanceComparator_ = new DominanceComparator();
  //  constraintComparator_ = new OverallConstraintViolationComparator();
  //} // MutationLocalSearch
  
 /**
   * Executes the local search. The maximum number of iterations is given by 
   * the param "improvementRounds", which is in the parameter list of the 
   * operator. The archive to store the non-dominated solutions is also in the 
   * parameter list.
   * @param object Object representing a solution
   * @return An object containing the new improved solution
 * @throws JMException 
   */
  public Object execute(Object object) throws JMException {
    int i = 0;
    int best = 0;
    evaluations_ = 0;        
    Solution solution = (Solution)object;

    int rounds = improvementRounds_;
    archive_ = (SolutionSet)getParameter("archive");

    if (rounds <= 0)
      return new Solution(solution);
        
    do 
    {
      i++;
      Solution mutatedSolution = new Solution(solution);
      mutationOperator_.execute(mutatedSolution);
            
      // Evaluate the getNumberOfConstraints
      if (problem_.getNumberOfConstraints() > 0)
      {
        problem_.evaluateConstraints(mutatedSolution);
        best = constraintComparator_.compare(mutatedSolution,solution);
        if (best == 0) //none of then is better that the other one
        {
          problem_.evaluate(mutatedSolution);
          evaluations_++;
          best = dominanceComparator_.compare(mutatedSolution,solution);
        } 
        else if (best == -1) //mutatedSolution is best
        {
          problem_.evaluate(mutatedSolution);
          evaluations_++;
        }
      }
      else
      {
        problem_.evaluate(mutatedSolution);
        evaluations_++;
        best = dominanceComparator_.compare(mutatedSolution,solution);
      }
      if (best == -1) // This is: Mutated is best
        solution = mutatedSolution;
      else if (best == 1) // This is: Original is best
        //delete mutatedSolution
        ;
      else // This is mutatedSolution and original are non-dominated
      {
        //this.archive_.addIndividual(new Solution(solution));                
        //solution = mutatedSolution;
        if (archive_ != null)
          archive_.add(mutatedSolution);
      }                            
    }
    while (i < rounds);
    return new Solution(solution);
  } // execute
  
   
  /** 
   * Returns the number of evaluations maded
   */
  public int getEvaluations() {
    return evaluations_;
  } // evaluations
} // MutationLocalSearch
