//  BinaryTournament.java
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

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements an binary tournament selection operator
 */
public class BinaryTournament extends Selection {

  /**
   * Stores the <code>Comparator</code> used to compare two
   * solutions
   */
  private Comparator comparator_;

  /**
   * Constructor
   * Creates a new Binary tournament operator using a BinaryTournamentComparator
   */
  public BinaryTournament(HashMap<String, Object> parameters){
  	super(parameters) ;
  	if ((parameters != null) && (parameters.get("comparator") != null))
  		comparator_ = (Comparator) parameters.get("comparator") ;  	
  	else
      //comparator_ = new BinaryTournamentComparator();
      comparator_ = new DominanceComparator();
  } // BinaryTournament
  
  /**
  * Performs the operation
  * @param object Object representing a SolutionSet
  * @return the selected solution
  */
  public Object execute(Object object){
    SolutionSet solutionSet = (SolutionSet)object;
    Solution solution1, solution2;
    solution1 = solutionSet.get(PseudoRandom.randInt(0,solutionSet.size()-1));
    solution2 = solutionSet.get(PseudoRandom.randInt(0,solutionSet.size()-1));

    if (solutionSet.size() >= 2)
    	while (solution1 == solution2)
        solution2 = solutionSet.get(PseudoRandom.randInt(0,solutionSet.size()-1));
    
    int flag = comparator_.compare(solution1,solution2);
    if (flag == -1)
      return solution1;
    else if (flag == 1)
      return solution2;
    else
      if (PseudoRandom.randDouble()<0.5)
        return solution1;
      else
        return solution2;                       
  } // execute
} // BinaryTournament
