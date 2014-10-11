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

package org.uma.jmetal45.encoding.solutiontype.wrapper;

import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionType;
import org.uma.jmetal45.encoding.solutiontype.GenericRealSolutionType;
import org.uma.jmetal45.util.JMetalException;

/**
 * Wrapper for accessing real-coded solutions
 */
public class XReal {
  private static final String EXCEPTION_MESSAGE = "The solution type of the solution is invalid: " ;
  private Solution solution;
  private SolutionType type;

  public XReal(Solution solution) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      type = solution.getType();
      this.solution = solution;
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }

  public XReal(XReal solution) {
    this.solution = solution.solution;
    type = solution.type;
  }

  public double getValue(int index) {
    return ((GenericRealSolutionType) type).getRealValue(solution, index) ;
  }

  public void setValue(int index, double value) {
    ((GenericRealSolutionType) type).setRealValue(solution, index, value);
  }

  public int getNumberOfDecisionVariables() {
    return ((GenericRealSolutionType) type).getNumberOfRealVariables(solution) ;
  }

  public double getUpperBound(int index) {
    return ((GenericRealSolutionType) type).getRealUpperBound(solution,index) ;
  }

  public double getLowerBound(int index) {
    return ((GenericRealSolutionType) type).getRealLowerBound(solution,index) ;
  }

  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution;
  }

  /* Static methods */
  public static double getValue(Solution solution, int index) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      return ((GenericRealSolutionType) solution.getType()).getRealValue(solution, index);
    } else {
      throw new JMetalException(EXCEPTION_MESSAGE + solution.getType());
    }
  }

  public static void setValue(Solution solution, int index, double value) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      ((GenericRealSolutionType) solution.getType()).setRealValue(solution, index, value);
    } else {
      throw new JMetalException(EXCEPTION_MESSAGE + solution.getType());
    }
  }

  public static int getNumberOfDecisionVariables(Solution solution) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      return ((GenericRealSolutionType) solution.getType()).getNumberOfRealVariables(solution) ;
    } else {
      throw new JMetalException(EXCEPTION_MESSAGE + solution.getType());
    }
  }

  public static double getUpperBound(Solution solution, int index) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      return ((GenericRealSolutionType) solution.getType()).getRealUpperBound(solution, index);
    } else {
      throw new JMetalException(EXCEPTION_MESSAGE + solution.getType());
    }
  }

  public static double getLowerBound(Solution solution, int index) {
    if (solution.getType() instanceof GenericRealSolutionType) {
      return ((GenericRealSolutionType) solution.getType()).getRealLowerBound(solution, index);
    } else {
      throw new JMetalException(EXCEPTION_MESSAGE + solution.getType());
    }
  }
}

