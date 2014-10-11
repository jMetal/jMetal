//  IntRealSolutionType.java
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

package org.uma.jmetal45.encoding.solutiontype;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.Variable;
import org.uma.jmetal45.encoding.variable.Int;
import org.uma.jmetal45.encoding.variable.Real;

/**
 * Class representing a solution type including two kind of variables: integer and real.
 */
public class IntRealSolutionType implements GenericIntSolutionType, GenericRealSolutionType {
  private final Problem problem ;
  private final int numberOfIntVariables;
  private final int numberOfRealVariables;

  /**
   * Constructor
   *
   * @param problem       Problem to solve
   * @param intVariables  Number of integer variables
   * @param realVariables Number of real variables
   */
  public IntRealSolutionType(Problem problem, int intVariables, int realVariables) {
    this.problem = problem ;
    numberOfIntVariables = intVariables;
    numberOfRealVariables = realVariables;
  }

  /** Creates the variables of the solution type */
  public Variable[] createVariables() throws ClassNotFoundException {
    Variable[] variables = new Variable[problem.getNumberOfVariables()];

    for (int var = 0; var < numberOfIntVariables; var++) {
      variables[var] =
        new Int((int) problem.getLowerLimit(var), (int) problem.getUpperLimit(var));
    }

    for (int var = numberOfIntVariables; var < (numberOfIntVariables + numberOfRealVariables); var++) {
      variables[var] = new Real(problem.getLowerLimit(var), problem.getUpperLimit(var));
    }

    return variables;
  }

  @Override
  public int getIntValue(Solution solution, int index) {
    return ((Int)solution.getDecisionVariables()[index]).getValue();
  }

  @Override public void setIntValue(Solution solution, int index, int value) {
    ((Int)solution.getDecisionVariables()[index]).setValue(value);
  }

  @Override public int getIntUpperBound(Solution solution, int index) {
    return ((Int)solution.getDecisionVariables()[index]).getUpperBound();
  }

  @Override public int getIntLowerBound(Solution solution, int index) {
    return ((Int)solution.getDecisionVariables()[index]).getLowerBound();
  }

  @Override public int getNumberOfIntVariables(Solution solution) {
    return numberOfIntVariables;
  }

  @Override
  public double getRealValue(Solution solution, int index) {
    return ((Real)solution.getDecisionVariables()[index+ numberOfIntVariables]).getValue();
  }

  @Override public void setRealValue(Solution solution, int index, double value) {
    ((Real)solution.getDecisionVariables()[index+ numberOfIntVariables]).setValue(value) ;
  }

  @Override public double getRealUpperBound(Solution solution, int index) {
    return ((Real)solution.getDecisionVariables()[index+ numberOfIntVariables]).getUpperBound();
  }

  @Override public double getRealLowerBound(Solution solution, int index) {
    return ((Real)solution.getDecisionVariables()[index+ numberOfIntVariables]).getLowerBound();
  }

  @Override public int getNumberOfRealVariables(Solution solution_) {
    return numberOfRealVariables;
  }
}
