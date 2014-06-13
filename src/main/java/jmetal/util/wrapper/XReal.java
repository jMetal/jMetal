//  XReal.java
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
import jmetal.encodings.solutiontype.ArrayRealAndBinarySolutionType;
import jmetal.encodings.solutiontype.ArrayRealSolutionType;
import jmetal.encodings.solutiontype.BinaryRealSolutionType;
import jmetal.encodings.solutiontype.RealSolutionType;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * Wrapper for accessing real-coded solutions
 */
public class XReal {
  private Solution solution_;
  private SolutionType type_;

  public XReal(Solution solution) {
    setType_(solution.getType());
    solution_ = solution;
  }

  public XReal(XReal solution) {
    solution_ = solution.solution_;
    setType_(solution.getType_());
  }

  /**
   * Gets value of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The value of the encodings.variable
   * @throws JMException
   */
  public double getValue(int index) throws JMException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getValue();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).gerArray()[index];
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).gerArray()[index];
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XReal.getValue, solution type " +
        getType_() + "+ invalid");
    }
    return 0.0;
  }

  /**
   * Sets the value of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @param value Value to be assigned
   * @throws JMException
   */
  public void setValue(int index, double value) throws JMException {
    if (getType_().getClass() == RealSolutionType.class) {
      solution_.getDecisionVariables()[index].setValue(value);
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      ((ArrayReal) (solution_.getDecisionVariables()[0])).gerArray()[index] = value;
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      ((ArrayReal) (solution_.getDecisionVariables()[0])).gerArray()[index] = value;
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XReal.setValue, solution type " +
        getType_() + "+ invalid");
    }
  }

  /**
   * Gets the lower bound of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The lower bound of the encodings.variable
   * @throws JMException
   */
  public double getLowerBound(int index) throws JMException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getLowerBound();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XReal.getLowerBound, solution type " +
        getType_() + "+ invalid");

    }
    return 0.0;
  }

  /**
   * Gets the upper bound of a encodings.variable
   *
   * @param index Index of the encodings.variable
   * @return The upper bound of the encodings.variable
   * @throws JMException
   */
  public double getUpperBound(int index) throws JMException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getUpperBound();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XReal.getUpperBound, solution type " +
        getType_() + "+ invalid");
    }

    return 0.0;
  }

  /**
   * Returns the number of variables of the solution
   */
  public int getNumberOfDecisionVariables() {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables().length;
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getLength();
    } else {
      Configuration.logger_.severe("jmetal.util.wrapper.XReal.size, solution type " +
        getType_() + "+ invalid");
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

public SolutionType getType_() {
	return type_;
}

public void setType_(SolutionType type_) {
	this.type_ = type_;
}
}
