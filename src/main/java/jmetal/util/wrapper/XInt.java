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

package jmetal.util.wrapper;

import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.encodings.solutiontype.ArrayIntSolutionType;
import jmetal.encodings.solutiontype.IntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.Configuration;
import jmetal.util.JMException;

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
   * Gets value of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The value of the encodings.variable
   * @throws JMException
   */
  public int getValue(int index) throws JMException {
    if (type_.getClass() == IntSolutionType.class) {
      return (int) solution_.getDecisionVariables()[index].getValue();
    } else if (type_.getClass() == ArrayIntSolutionType.class) {
      return ((ArrayInt) (solution_.getDecisionVariables()[0])).getArray()[index];
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XInt.getValue, solution type " +
        type_ + "+ invalid");
    }
    return 0;
  }

  /**
   * Sets the value of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @param value Value to be assigned
   * @throws JMException
   */
  public void setValue(int index, int value) throws JMException {
    if (type_.getClass() == IntSolutionType.class) {
      solution_.getDecisionVariables()[index].setValue(value);
    } else if (type_.getClass() == ArrayIntSolutionType.class) {
      ((ArrayInt) (solution_.getDecisionVariables()[0])).getArray()[index] = value;
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XInt.setValue, solution type " +
        type_ + "+ invalid");
    }
  }

  /**
   * Gets the lower bound of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The lower bound of the encodings.variable
   * @throws JMException
   */
  public int getLowerBound(int index) throws JMException {
    if (type_.getClass() == IntSolutionType.class) {
      return (int) solution_.getDecisionVariables()[index].getLowerBound();
    } else if (type_.getClass() == ArrayIntSolutionType.class) {
      return (int) ((ArrayInt) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XInt.getLowerBound, solution type " +
        type_ + "+ invalid");
    }
    return 0;
  }

  /**
   * Gets the upper bound of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The upper bound of the encodings.variable
   * @throws JMException
   */
  public int getUpperBound(int index) throws JMException {
    if (type_.getClass() == IntSolutionType.class) {
      return (int) solution_.getDecisionVariables()[index].getUpperBound();
    } else if (type_.getClass() == ArrayIntSolutionType.class) {
      return (int) ((ArrayInt) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XInt.getUpperBound, solution type " +
        type_ + "+ invalid");
    }

    return 0;
  }

  /**
   * Returns the number of variables of the solution
   */
  public int getNumberOfDecisionVariables() {
    if (type_.getClass() == IntSolutionType.class) {
      return solution_.getDecisionVariables().length;
    } else if (type_.getClass() == ArrayIntSolutionType.class) {
      return ((ArrayInt) (solution_.getDecisionVariables()[0])).getLength();
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XInt.size, solution type " +
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
