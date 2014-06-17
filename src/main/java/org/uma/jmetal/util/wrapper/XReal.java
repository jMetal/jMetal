//  XReal.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
import org.uma.jmetal.encoding.solution.RealSolutionType;
import org.uma.jmetal.util.JMetalException;

/**
 * Wrapper for accessing real-coded solutions
 */
public class XReal {
  private Solution solution_;
  private SolutionType type_;

  public XReal(Solution solution) {
    if (solution.getType() instanceof RealSolutionType) {
      type_ = solution.getType();
      solution_ = solution;
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }

  public XReal(XReal solution) {
    solution_ = solution.solution_;
    type_ = solution.type_;
  }

  public double getValue(int index) {
    return ((RealSolutionType)type_).getRealValue(solution_, index) ;
  }

  public void setValue(int index, double value) {
    ((RealSolutionType)type_).setRealValue(solution_, index, value);
  }

  public int getNumberOfDecisionVariables() {
    return ((RealSolutionType)type_).getNumberOfVariables(solution_) ;
  }

  public double getUpperBound(int index) {
    return ((RealSolutionType)type_).getRealUpperBound(solution_,index) ;
  }

  public double getLowerBound(int index) {
    return ((RealSolutionType)type_).getRealLowerBound(solution_,index) ;
  }

  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution_;
  }

  /*
   * Static methods
   */
  public static double getValue(Solution solution, int index) {
    if (solution.getType() instanceof RealSolutionType) {
      return ((RealSolutionType) solution.getType()).getRealValue(solution, index);
    } else {
      throw new JMetalException(
        "The solution type of the solution is invalid: " + solution.getType());
    }
  }
}

