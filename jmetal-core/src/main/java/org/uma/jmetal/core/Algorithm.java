//  Algorithm.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//// 
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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a generic template for the algorithms developed in
 * jMetal. Every algorithm must have a mapping between the parameters and and
 * their names, and another mapping between the operator and their names. The
 * class declares an abstract method called <code>execute</code>, which defines
 * the behavior of the algorithm.
 */
public abstract class Algorithm implements Serializable {
  private static final long serialVersionUID = -5414794431994226777L;

  protected Problem problem;
  protected Map<String, Operator> operators = null;
  protected Map<String, Object> inputParameters = null;
  private Map<String, Object> outPutParameters = null;

  /**
   * Launches the execution of an specific algorithm.
   *
   * @return a <code>SolutionSet</code> 
   */
  public abstract SolutionSet execute() throws JMetalException,
    ClassNotFoundException, IOException;

  @Deprecated
  public void addOperator(String name, Operator operator) {
    if (operators == null) {
      operators = new HashMap<String, Operator>();
    }
    operators.put(name, operator);
  }

  @Deprecated
  public Operator getOperator(String name) {
    return operators.get(name);
  }

  @Deprecated
  public void setInputParameter(String name, Object object) {
    if (inputParameters == null) {
      inputParameters = new HashMap<String, Object>();
    }
    inputParameters.put(name, object);
  }

  @Deprecated
  public Object getInputParameter(String name) {
    return inputParameters.get(name);
  }

  @Deprecated
  public void setOutputParameter(String name, Object object) {
    if (outPutParameters == null) {
      outPutParameters = new HashMap<String, Object>();
    }
    outPutParameters.put(name, object);
  }

  @Deprecated
  public Object getOutputParameter(String name) {
    if (outPutParameters != null) {
      return outPutParameters.get(name);
    } else {
      return null;
    }
  }

  public Problem getProblem() {
    return problem;
  }
  public void setProblem(Problem problem) {
	  this.problem = problem;
  }
}
