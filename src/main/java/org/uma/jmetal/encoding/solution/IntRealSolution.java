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

package org.uma.jmetal.encoding.solution;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.variable.Int;
import org.uma.jmetal.encoding.variable.Real;

/**
 * Class representing  a solution type including two kind of variables: integer and real.
 */
public class IntRealSolution extends SolutionType implements IntSolutionType, RealSolutionType {
  private final int numberOfIntVariables_;
  private final int numberOfRealVariables_;

  /**
   * Constructor
   *
   * @param problem       Problem to solve
   * @param intVariables  Number of integer variables
   * @param realVariables Number of real variables
   */
  public IntRealSolution(Problem problem, int intVariables, int realVariables) {
    super(problem);
    numberOfIntVariables_ = intVariables;
    numberOfRealVariables_ = realVariables;
  }

  /**
   * Creates the variables of the solution
   *
   * @throws ClassNotFoundException
   */
  public Variable[] createVariables() throws ClassNotFoundException {
    Variable[] variables = new Variable[getProblem().getNumberOfVariables()];

    for (int var = 0; var < numberOfIntVariables_; var++) {
      variables[var] =
        new Int((int) getProblem().getLowerLimit(var), (int) getProblem().getUpperLimit(var));
    }

    for (int var = numberOfIntVariables_; var < (numberOfIntVariables_ + numberOfRealVariables_); var++) {
      variables[var] = new Real(getProblem().getLowerLimit(var), getProblem().getUpperLimit(var));
    }

    return variables;
  }

  @Override
  public int getIntValue(Solution solution, int index) {
    return (int)solution.getDecisionVariables()[index].getValue();
  }

  @Override public void setIntValue(Solution solution, int index, int value) {
    solution.getDecisionVariables()[index].setValue(value);
  }

  @Override public double getIntUpperBound(Solution solution, int index) {
    return solution.getDecisionVariables()[index].getUpperBound();
  }

  @Override public double getIntLowerBound(Solution solution, int index) {
    return solution.getDecisionVariables()[index].getLowerBound();
  }

  @Override public int getNumberOfIntVariables(Solution solution) {
    return numberOfIntVariables_;
  }

  @Override
  public double getRealValue(Solution solution, int index) {
    return solution.getDecisionVariables()[index+ numberOfIntVariables_].getValue();
  }

  @Override public void setRealValue(Solution solution, int index, double value) {
    solution.getDecisionVariables()[index+ numberOfIntVariables_].setValue(value) ;
  }

  @Override public double getRealUpperBound(Solution solution, int index) {
    return solution.getDecisionVariables()[index+ numberOfIntVariables_].getUpperBound();
  }

  @Override public double getRealLowerBound(Solution solution, int index) {
    return solution.getDecisionVariables()[index+ numberOfIntVariables_].getLowerBound();
  }

  @Override public int getNumberOfRealVariables(Solution solution_) {
    return numberOfRealVariables_;
  }
}
