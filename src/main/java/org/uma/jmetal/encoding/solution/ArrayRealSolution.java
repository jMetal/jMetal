//  ArrayRealSolutionType.java
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

package org.uma.jmetal.encoding.solution;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.variable.ArrayReal;

/**
 * Class representing the solution type of solutions composed of an ArrayReal
 * encoding.variable
 */
public class ArrayRealSolution extends SolutionType implements RealSolutionType {

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public ArrayRealSolution(Problem problem) {
    super(problem);
  }

  /**
   * Creates the variables of the solution
   */
  public Variable[] createVariables() {
    Variable[] variables = new Variable[1];

    variables[0] = new ArrayReal(getProblem().getNumberOfVariables(), getProblem());
    return variables;
  }

  /**
   * Copy the variables
   *
   * @param vars Variables
   * @return An array of variables
   */
  public Variable[] copyVariables(Variable[] vars) {
    Variable[] variables;

    variables = new Variable[1];
    variables[0] = vars[0].deepCopy();

    return variables;
  }


  @Override public double getRealValue(Solution solution, int index) {
    return ((ArrayReal)(solution.getDecisionVariables()[0])).getArray()[index] ;
  }

  @Override public void setRealValue(Solution solution, int index, double value) {
    ((ArrayReal) (solution.getDecisionVariables()[0])).getArray()[index] = value ;
  }

  @Override public int getNumberOfVariables(Solution solution) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).length();
  }

  @Override public double getRealUpperBound(Solution solution, int index) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).getUpperBound(index);
  }

  @Override public double getRealLowerBound(Solution solution, int index) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).getLowerBound(index);
  }
}
