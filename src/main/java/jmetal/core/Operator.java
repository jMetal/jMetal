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

package jmetal.core;

import jmetal.util.JMException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an operator
 */
public abstract class Operator implements Serializable {

  private static final long serialVersionUID = -8976295845748676798L;

  private List<Class<? extends SolutionType>> validSolutionTypes_ ;

  /**
   * Stores the current operator parameters. It is defined as a Map of pairs 
   * (<code>String</code>, <code>Object</code>), and it allows objects to be
   * accessed by their names, which are specified by the string.
   */
  protected final Map<String, Object> parameters_;

  @Deprecated
  public Operator(Map<String, Object> parameters) {
    parameters_ = parameters;
    validSolutionTypes_ = new ArrayList<>() ;
  }

  public Operator() {
    validSolutionTypes_ = new ArrayList<>() ;
    parameters_ = new HashMap<>() ;
  }

  /**
   * Abstract method that must be defined by all the operators. When invoked,
   * this method executes the operator represented by the current object.
   *
   * @param object This parameter inherits from Object to allow different kinds of
   *               parameters for each operator. For example, a selection operator
   *               typically receives a <code>SolutionSet</code> as a parameter,
   *               while a mutation operator receives a <code>Solution</code>.
   * @return An object reference. The returned value depends on the operator.
   */
  public abstract Object execute(Object object) throws JMException;

  /**
   * Sets a new <code>Object</code> parameter to the operator.
   *
   * @param name  The parameter name.
   * @param value Object representing the parameter.
   */
  public void setParameter(String name, Object value) {
    parameters_.put(name, value);
  }

  /**
   * Returns an object representing a parameter of the <code>Operator</code>
   *
   * @param name The parameter name.
   * @return the parameter.
   */
  public Object getParameter(String name) {
    return parameters_.get(name);
  }

  /**
   * Add a new valid solution type
   * @param newSolutionType
   */
  public void addValidSolutionType(Class newSolutionType) {
    validSolutionTypes_.add(newSolutionType) ;
  }

  /**
   * Test for solutions having a solution type to which the operator is applicable
   * @param solution Solution to the checked
   * @return True if the solution type of the solution is valid
   */
  public boolean solutionTypeIsValid(Solution solution) {
    boolean result ;
    if (validSolutionTypes_.contains(solution.getType().getClass())) {
      result = true ;
    }
    else {
      result = false ;
    }

    return result ;
  }

  /**
   * Test for solutions having a solution type to which the operator is applicable
   * @param solutions Array of solution to the checked
   * @return True if the solution type of all the solutions in the array is valid
   */
  public boolean solutionTypeIsValid(Solution[] solutions) {
    boolean result = true; // true by default
    for (Solution solution : solutions) {
      if(!validSolutionTypes_.contains(solution.getType().getClass())) {
        result = false ;
      }
    }

    return result ;
  }
} 
