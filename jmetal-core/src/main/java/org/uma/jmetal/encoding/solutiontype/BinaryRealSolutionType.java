//  BinaryRealSolutionType.java
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

package org.uma.jmetal.encoding.solutiontype;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.variable.BinaryReal;
import org.uma.jmetal.encoding.variable.GenericRealVariable;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing the solution type of solutions composed of BinaryReal
 * variables
 */
public class BinaryRealSolutionType extends SolutionType implements GenericRealSolutionType {

  /** Constructor */
  public BinaryRealSolutionType(Problem problem) {
    super(problem);
  }

  /** Creates the variables of the solution type */
  public Variable[] createVariables() {
    Variable[] variables = new Variable[getProblem().getNumberOfVariables()];

    for (int var = 0; var < getProblem().getNumberOfVariables(); var++) {
      if (getProblem().getPrecision() == null) {
        int[] precision = new int[getProblem().getNumberOfVariables()];
        for (int i = 0; i < getProblem().getNumberOfVariables(); i++) {
          precision[i] = org.uma.jmetal.encoding.variable.BinaryReal.DEFAULT_PRECISION;
        }
        getProblem().setPrecision(precision);
      }
      variables[var] = new BinaryReal(getProblem().getPrecision(var),
        getProblem().getLowerLimit(var),
        getProblem().getUpperLimit(var));
    }
    return variables;
  }

  @Override
  public double getRealValue(Solution solution, int index) {
    return ((GenericRealVariable)solution.getDecisionVariables()[index]).getValue() ;
  }

  @Override
  public void setRealValue(Solution solution, int index, double value) {
    throw new JMetalException("Cannot assign a real value to a BinaryRealSolutionType solution type") ;
  }

  @Override public int getNumberOfRealVariables(Solution solution) {
    return solution.getDecisionVariables().length ;
  }

  @Override public double getRealUpperBound(Solution solution, int index) {
    return ((GenericRealVariable)solution.getDecisionVariables()[index]).getUpperBound() ;
  }

  @Override public double getRealLowerBound(Solution solution, int index) {
    return ((GenericRealVariable)solution.getDecisionVariables()[index]).getLowerBound() ;
  }
}
