//  Problem.java
//
//  Authors:
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

package org.uma.jmetal.core;

import org.uma.jmetal.util.JMetalException;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Abstract class representing a multiobjective optimization problem
 */
public abstract class Problem implements Serializable {
  private static final long serialVersionUID = 7027317068597003106L;

  private static final int DEFAULT_PRECISION = 16;
  protected int numberOfVariables;
  protected int numberOfObjectives;
  protected int numberOfConstraints;
  protected String problemName;
  protected SolutionType solutionType;
  protected double[] lowerLimit;
  protected double[] upperLimit;
  protected int[] precision;
  protected int[] length;

  public Problem() {
    solutionType = null;
  }

  public Problem(SolutionType solutionType) {
    this.solutionType = solutionType;
  }

  public int getNumberOfVariables() {
    return numberOfVariables;
  }

  public void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables;
  }

  public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  public double getLowerLimit(int i) {
    return lowerLimit[i];
  }

  public double getUpperLimit(int i) {
    return upperLimit[i];
  }

  public abstract void evaluate(Solution solution) throws JMetalException;

  public int getNumberOfConstraints() {
    return numberOfConstraints;
  }

  public void evaluateConstraints(Solution solution) throws JMetalException {
    // The default behavior is to do nothing. Only constrained problem have to
    // re-define this method
  }

  public int getPrecision(int var) {
    return precision[var];
  }

  public int[] getPrecision() {
    return precision;
  }

  public void setPrecision(int[] precision) {
    this.precision = Arrays.copyOf(precision, precision.length);
  }

  public int getLength(int var) {
    if (length == null) {
      return DEFAULT_PRECISION;
    }
    return length[var];
  }

  public SolutionType getSolutionType() {
    return solutionType;
  }

  public void setSolutionType(SolutionType type) {
    solutionType = type;
  } 

  public String getName() {
    return problemName;
  }

  public int getNumberOfBits() {
    int result = 0;
    for (int var = 0; var < numberOfVariables; var++) {
      result += getLength(var);
    }
    return result;
  }
}
