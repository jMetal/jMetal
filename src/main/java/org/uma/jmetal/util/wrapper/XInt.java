//  XInt.java
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

package org.uma.jmetal.util.wrapper;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.encoding.solution.ArrayIntSolution;
import org.uma.jmetal.encoding.solution.IntSolution;
import org.uma.jmetal.encoding.variable.ArrayInt;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

/**
 * Wrapper class for accessing integer-coded solutions
 */
public class XInt {
  private Solution solution_;
  private SolutionType type_;

  public XInt(Solution solution) {
    type_ = solution.getType();
    solution_ = solution;
  }

  public XInt(XInt solution) {
    type_ = solution.type_;
    solution_ = solution.solution_;
  }

  /**
   * Gets value of a encoding.variable
   *
   * @param index Index of the encoding.variable
   * @return The value of the encoding.variable
   * @throws org.uma.jmetal.util.JMetalException
   */
  public int getValue(int index) throws JMetalException {
    if (type_.getClass() == IntSolution.class) {
      return (int) solution_.getDecisionVariables()[index].getValue();
    } else if (type_.getClass() == ArrayIntSolution.class) {
      return ((ArrayInt) (solution_.getDecisionVariables()[0])).getArray()[index];
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XInt.getValue, solution type " +
        type_ + "+ invalid");
    }
    return 0;
  }

  /**
   * Sets the value of a encoding.variable
   *
   * @param index Index of the encoding.variable
   * @param value Value to be assigned
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void setValue(int index, int value) throws JMetalException {
    if (type_.getClass() == IntSolution.class) {
      solution_.getDecisionVariables()[index].setValue(value);
    } else if (type_.getClass() == ArrayIntSolution.class) {
      ((ArrayInt) (solution_.getDecisionVariables()[0])).getArray()[index] = value;
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XInt.setValue, solution type " +
        type_ + "+ invalid");
    }
  }

  /**
   * Gets the lower bound of a encoding.variable
   *
   * @param index Index of the encoding.variable
   * @return The lower bound of the encoding.variable
   * @throws org.uma.jmetal.util.JMetalException
   */
  public int getLowerBound(int index) throws JMetalException {
    if (type_.getClass() == IntSolution.class) {
      return (int) solution_.getDecisionVariables()[index].getLowerBound();
    } else if (type_.getClass() == ArrayIntSolution.class) {
      return (int) ((ArrayInt) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XInt.getLowerBound, solution type " +
        type_ + "+ invalid");
    }
    return 0;
  }

  /**
   * Gets the upper bound of a encoding.variable
   *
   * @param index Index of the encoding.variable
   * @return The upper bound of the encoding.variable
   * @throws org.uma.jmetal.util.JMetalException
   */
  public int getUpperBound(int index) throws JMetalException {
    if (type_.getClass() == IntSolution.class) {
      return (int) solution_.getDecisionVariables()[index].getUpperBound();
    } else if (type_.getClass() == ArrayIntSolution.class) {
      return (int) ((ArrayInt) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XInt.getUpperBound, solution type " +
        type_ + "+ invalid");
    }

    return 0;
  }

  /**
   * Returns the number of variables of the solution
   */
  public int getNumberOfDecisionVariables() {
    if (type_.getClass() == IntSolution.class) {
      return solution_.getDecisionVariables().length;
    } else if (type_.getClass() == ArrayIntSolution.class) {
      return ((ArrayInt) (solution_.getDecisionVariables()[0])).length();
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XInt.size, solution type " +
        type_ + "+ invalid");
    }
    return 0;
  }

  /**
   * Returns the number of variables of the solution
   */
  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution_;
  }
}
