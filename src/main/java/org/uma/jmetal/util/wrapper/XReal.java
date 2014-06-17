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

package org.uma.jmetal.util.wrapper;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.encodings.solutiontype.RealSolution;
import org.uma.jmetal.util.JMetalException;

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


  public double getValue(int index) {
    return ((RealSolution)type_).getRealValue(solution_, index) ;
  }

  public void setValue(int index, double value) {
    ((RealSolution)type_).setRealValue(solution_, index, value);
  }

  public int getNumberOfDecisionVariables() {
    return ((RealSolution)type_).getNumberOfVariables(solution_) ;
  }

  public double getUpperBound(int index) {
    return ((RealSolution)type_).getRealUpperBound(solution_,index) ;
  }

  public double getLowerBound(int index) {
    return ((RealSolution)type_).getRealLowerBound(solution_,index) ;
  }
  /*
  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution_;
  }
   */
  /*
   * Static methods
   */

  public static double getValue(Solution solution, int index) {
    if (solution.getType() instanceof RealSolution) {
      return ((RealSolution) solution.getType()).getRealValue(solution, index);
    } else {
      throw new JMetalException(
        "The solution type of the solution is invalid: " + solution.getType());
    }
  }
        /*
  public double getValue(int index) throws JMetalException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getValue();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getArray()[index];
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getArray()[index];
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XReal.getValue, solution type " +
        getType_() + "+ invalid");
    }
    return 0.0;
  }

  public void setValue(int index, double value) throws JMetalException {
    if (getType_().getClass() == RealSolutionType.class) {
      solution_.getDecisionVariables()[index].setValue(value);
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      ((ArrayReal) (solution_.getDecisionVariables()[0])).getArray()[index] = value;
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      ((ArrayReal) (solution_.getDecisionVariables()[0])).getArray()[index] = value;
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XReal.setValue, solution type " +
        getType_() + "+ invalid");
    }
  }


  public double getLowerBound(int index) throws JMetalException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getLowerBound();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getLowerBound(index);
    } else {
      Configuration.logger_
        .severe("org.uma.jmetal.util.wrapper.XReal.getLowerBound, solution type " +
          getType_() + "+ invalid");

    }
    return 0.0;
  }


  public double getUpperBound(int index) throws JMetalException {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables()[index].getUpperBound();
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else if (getType_().getClass() == ArrayRealAndBinarySolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).getUpperBound(index);
    } else {
      Configuration.logger_
        .severe("org.uma.jmetal.util.wrapper.XReal.getUpperBound, solution type " +
          getType_() + "+ invalid");
    }

    return 0.0;
  }

  public int getNumberOfDecisionVariables() {
    if ((getType_().getClass() == RealSolutionType.class) ||
      (getType_().getClass() == BinaryRealSolutionType.class)) {
      return solution_.getDecisionVariables().length;
    } else if (getType_().getClass() == ArrayRealSolutionType.class) {
      return ((ArrayReal) (solution_.getDecisionVariables()[0])).length();
    } else {
      Configuration.logger_.severe("org.uma.jmetal.util.wrapper.XReal.size, solution type " +
        getType_() + "+ invalid");
    }
    return 0;
  }
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

  public void setType_(SolutionType solutionType) {
    this.type_ = solutionType;
  }
}

