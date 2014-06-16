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
 * their names, and another mapping between the operators and their names. The
 * class declares an abstract method called <code>execute</code>, which defines
 * the behavior of the algorithm.
 */
public abstract class Algorithm implements Serializable {

  private static final long serialVersionUID = -5414794431994226777L;
  protected Problem problem_;
  protected Map<String, Operator> operators_ = null;
  protected Map<String, Object> inputParameters_ = null;
  private Map<String, Object> outPutParameters_ = null;

  /**
   * Launches the execution of an specific algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   */
  public abstract SolutionSet execute() throws JMetalException,
    ClassNotFoundException, IOException;

  public void addOperator(String name, Operator operator) {
    if (operators_ == null) {
      operators_ = new HashMap<String, Operator>();
    }
    operators_.put(name, operator);
  }

  public Operator getOperator(String name) {
    return operators_.get(name);
  } // getOperator

  public void setInputParameter(String name, Object object) {
    if (inputParameters_ == null) {
      inputParameters_ = new HashMap<String, Object>();
    }
    inputParameters_.put(name, object);
  }

  public Object getInputParameter(String name) {
    return inputParameters_.get(name);
  } // getInputParameter

  public void setOutputParameter(String name, Object object) {
    if (outPutParameters_ == null) {
      outPutParameters_ = new HashMap<String, Object>();
    }
    outPutParameters_.put(name, object);
  }
  public Object getOutputParameter(String name) {
    if (outPutParameters_ != null) {
      return outPutParameters_.get(name);
    } else {
      return null;
    }
  }

  public Problem getProblem() {
    return problem_;
  }
  public void setProblem(Problem problem) {
	  problem_ = problem;
  }
}
