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
import org.uma.jmetal.encoding.solutiontype.IntSolutionTypeTemplate;
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

  public double getValue(int index) {
    return ((IntSolutionTypeTemplate)type_).getIntValue(solution_, index) ;
  }

  public void setValue(int index, int value) {
    ((IntSolutionTypeTemplate)type_).setIntValue(solution_, index, value);
  }

  public int getNumberOfDecisionVariables() {
    return ((IntSolutionTypeTemplate)type_).getNumberOfIntVariables(solution_) ;
  }

  public int getUpperBound(int index) {
    return ((IntSolutionTypeTemplate)type_).getIntUpperBound(solution_, index) ;
  }

  public int getLowerBound(int index) {
    return ((IntSolutionTypeTemplate)type_).getIntLowerBound(solution_, index) ;
  }

  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution_;
  }

  /* Static methods */
  public static int getValue(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntValue(solution, index);
    } else {
      throw new JMetalException(
        "The solutiontype type of the solution is invalid: " + solution.getType());
    }
  }

  public static void setValue(Solution solution, int index, int value) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      ((IntSolutionTypeTemplate) solution.getType()).setIntValue(solution, index, value);
    } else {
      throw new JMetalException(
        "The solutiontype type of the solution is invalid: " + solution.getType());
    }
  }

  public static int getNumberOfDecisionVariables(Solution solution) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getNumberOfIntVariables(solution) ;
    } else {
      throw new JMetalException(
        "The solutiontype type of the solution is invalid: " + solution.getType());
    }
  }

  public static int getUpperBound(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntUpperBound(solution, index);
    } else {
      throw new JMetalException(
        "The solutiontype type of the solution is invalid: " + solution.getType());
    }
  }

  public static int getLowerBound(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntLowerBound(solution, index);
    } else {
      throw new JMetalException(
        "The solutiontype type of the solution is invalid: " + solution.getType());
    }
  }
}
