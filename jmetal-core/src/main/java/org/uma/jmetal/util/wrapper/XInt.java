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
  private static final String EXCEPTION_MESSAGE = "The solution type of the solution is invalid: " ;

  private Solution solution;
  private SolutionType type;

  public XInt(Solution solution) {
    type = solution.getType();
    this.solution = solution;
  }

  public XInt(XInt solution) {
    type = solution.type;
    this.solution = solution.solution;
  }

  public double getValue(int index) {
    return ((IntSolutionTypeTemplate) type).getIntValue(solution, index) ;
  }

  public void setValue(int index, int value) {
    ((IntSolutionTypeTemplate) type).setIntValue(solution, index, value);
  }

  public int getNumberOfDecisionVariables() {
    return ((IntSolutionTypeTemplate) type).getNumberOfIntVariables(solution) ;
  }

  public int getUpperBound(int index) {
    return ((IntSolutionTypeTemplate) type).getIntUpperBound(solution, index) ;
  }

  public int getLowerBound(int index) {
    return ((IntSolutionTypeTemplate) type).getIntLowerBound(solution, index) ;
  }

  public int size() {
    return getNumberOfDecisionVariables();
  }

  public Solution getSolution() {
    return solution;
  }

  /* Static methods */
  public static int getValue(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntValue(solution, index);
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }

  public static void setValue(Solution solution, int index, int value) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      ((IntSolutionTypeTemplate) solution.getType()).setIntValue(solution, index, value);
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;  }
  }

  public static int getNumberOfDecisionVariables(Solution solution) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getNumberOfIntVariables(solution) ;
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }

  public static int getUpperBound(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntUpperBound(solution, index);
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }

  public static int getLowerBound(Solution solution, int index) {
    if (solution.getType() instanceof IntSolutionTypeTemplate) {
      return ((IntSolutionTypeTemplate) solution.getType()).getIntLowerBound(solution, index);
    } else {
      throw new JMetalException("The solution type of the solution is invalid: " + solution.getType()) ;
    }
  }
}
