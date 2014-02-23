//  WorstSolutionSelection.java
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

package jmetal.operators.selection;

import jmetal.core.SolutionSet;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements a selection operator used for selecting the worst 
 * solution in a SolutionSet according to a given comparator
 */
public class WorstSolutionSelection extends Selection {
  
	// Comparator
  private Comparator comparator_;
    
  public WorstSolutionSelection(HashMap<String, Object> parameters) {
  	super(parameters) ;
  	
  	comparator_ = (Comparator)parameters.get("comparator") ;
  }
  
  /**
   * Constructor
   * @param comparator
   */
  //public WorstSolutionSelection(Comparator comparator) {
  //	comparator_ = comparator ;
  //}
  
  /**
  * Performs the operation
  * @param object Object representing a SolutionSet.
  * @return the best solution found
  */
  public Object execute(Object object) {
    SolutionSet solutionSet = (SolutionSet)object;
    
    if (solutionSet.size() == 0) {
      return null;
    }
    int worstSolution ;
    
    worstSolution = 0 ;
   	
    for (int i = 1; i < solutionSet.size(); i++) {
    	if (comparator_.compare(solutionSet.get(i), solutionSet.get(worstSolution)) > 0)  
    		worstSolution = i ;
    } // for
    
    return worstSolution ;    
  } // Execute     
} // WorstObjectiveSelection
