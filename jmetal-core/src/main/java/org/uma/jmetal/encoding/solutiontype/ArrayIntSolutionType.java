//  ArrayIntSolutionType.java
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
import org.uma.jmetal.encoding.variable.ArrayInt;

/**
 * Class representing the solution type of solutions composed of an ArrayInt
 * encoding.variable
 */
public class ArrayIntSolutionType extends SolutionType implements GenericIntSolutionType {

  /** Constructor */
  public ArrayIntSolutionType(Problem problem) {
    super(problem);
  }

  /** Creates the variables of the solution type */
  public Variable[] createVariables() {
    Variable[] variables = new Variable[1];

    variables[0] = new ArrayInt(getProblem().getNumberOfVariables(), getProblem());
    return variables;
  }

  @Override
  public int getIntValue(Solution solution, int index) {
    return ((ArrayInt) (solution.getDecisionVariables()[0])).getArray()[index] ;
  }

  @Override public void setIntValue(Solution solution, int index, int value) {
    ((ArrayInt) (solution.getDecisionVariables()[0])).getArray()[index]=value ;
  }

  @Override public int getNumberOfIntVariables(Solution solution) {
    return ((ArrayInt) (solution.getDecisionVariables()[0])).getArray().length ;
  }

  @Override public int getIntUpperBound(Solution solution, int index) {
    return ((ArrayInt) (solution.getDecisionVariables()[0])).getLowerBound(index) ;
  }

  @Override public int getIntLowerBound(Solution solution, int index) {
    return ((ArrayInt) (solution.getDecisionVariables()[0])).getLowerBound(index) ;
  }
}
