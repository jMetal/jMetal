//  Operator.java
//
//  Authors:
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

package org.uma.jmetal.core;

import org.uma.jmetal.util.JMetalException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an operator
 */
public abstract class Operator implements Serializable {

  private static final long serialVersionUID = -8976295845748676798L;

  private List<Class<? extends SolutionType>> validSolutionTypes;

  /** Constructor */
  public Operator() {
    validSolutionTypes = new ArrayList<>() ;
  }

  /**
   * Execute the operator.
   *
   * @param object This parameter inherits from Object to allow different kinds of
   *               parameters for each operator. For example, a selection operator
   *               typically receives a <code>SolutionSet</code> as a parameter,
   *               while a mutation operator receives a <code>Solution</code>.
   * @return An object reference. The returned value depends on the operator.
   */
  public abstract Object execute(Object object) throws JMetalException;

  /**
   * Add a new valid solution type
   * @param newSolutionType
   */
  public void addValidSolutionType(Class newSolutionType) {
    validSolutionTypes.add(newSolutionType) ;
  }

  /**
   * Test for solutions having a solution type to which the operator is applicable
   * @param solution Solution to the checked
   * @return True if the solution type of the solution is valid
   */
  public boolean solutionTypeIsValid(Solution solution) {
    return validSolutionTypes.contains(solution.getType().getClass()) ;
  }

  /**
   * Test for solutions having a solution type to which the operator is applicable
   * @param solutions Array of solution to the be checked
   * @return True if the solution type of all the solutions in the array is valid
   */
  public boolean solutionTypeIsValid(Solution[] solutions) {
    boolean result = true; 
    for (Solution solution : solutions) {
      if(!validSolutionTypes.contains(solution.getType().getClass())) {
        result = false ;
      }
    }

    return result ;
  }
} 
