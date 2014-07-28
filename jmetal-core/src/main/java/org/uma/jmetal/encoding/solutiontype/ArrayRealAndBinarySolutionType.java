//  ArrayRealAndBinarySolutionType.java
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
import org.uma.jmetal.encoding.variable.ArrayReal;
import org.uma.jmetal.encoding.variable.Binary;

/**
 * Class representing the solution type of solutions composed of array of reals
 * and a binary string.
 * ASSUMPTIONs:
 * - The numberOfVariables field in class Problem must contain the number
 * of real variables. This field is used to apply real operator (e.g.,
 * mutation probability)
 * - The upperLimit and lowerLimit arrays must have the length indicated
 * by numberOfVariables.
 */
public class ArrayRealAndBinarySolutionType extends SolutionType implements RealSolutionTypeTemplate {

  private final int binaryStringLength;
  private final int numberOfRealVariables;

  /**
   * Constructor
   *
   * @param problem            Problem being solved
   * @param realVariables      Number of real variables
   * @param binaryStringLength Length of the binary string
   */
  public ArrayRealAndBinarySolutionType(Problem problem,
    int realVariables,
    int binaryStringLength) {
    super(problem);
    this.binaryStringLength = binaryStringLength;
    numberOfRealVariables = realVariables;
  }

  /** Creates the variables of the solution type */
  public Variable[] createVariables() throws ClassNotFoundException {
    Variable[] variables = new Variable[2];

    variables[0] = new ArrayReal(numberOfRealVariables, getProblem());
    variables[1] = new Binary(binaryStringLength);
    return variables;
  }

  @Override
  public double getRealValue(Solution solution, int index) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).getArray()[index];
  }

  @Override
  public void setRealValue(Solution solution, int index, double value) {
    ((ArrayReal) (solution.getDecisionVariables()[0])).getArray()[index] = value ;
  }

  @Override public int getNumberOfRealVariables(Solution solution) {
    return numberOfRealVariables;
  }

  @Override public double getRealUpperBound(Solution solution, int index) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).getUpperBound(index) ;
  }

  @Override public double getRealLowerBound(Solution solution, int index) {
    return ((ArrayReal) (solution.getDecisionVariables()[0])).getLowerBound(index) ;
  }
}

