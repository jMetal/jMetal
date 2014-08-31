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

  @Deprecated
  protected Map<String, Operator> operators = null;
  @Deprecated
  protected Map<String, Object> inputParameters = null;

  /** Executes the algorithm */
  public abstract SolutionSet execute() throws JMetalException, ClassNotFoundException, IOException;

  @Deprecated
  public Object getInputParameter(String name) {
    return inputParameters.get(name);
  }

  public Problem getProblem() {
    return problem;
  }
  public void setProblem(Problem problem) {
    this.problem = problem;
  }
}
