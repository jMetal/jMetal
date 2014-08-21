//  ZDT1.java
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

package org.uma.jmetal.problem.multiobjective.zdt;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem ZDT1
 */
public class ZDT1 extends Problem {
  private static final long serialVersionUID = 7747361492814788270L;

  /**
   * Constructor.
   * Creates a default instance of problem ZDT1 (30 decision variables)
   *
   * @param solutionType The solution type must "Real", "BinaryReal, or ArrayReal".
   */
  public ZDT1(String solutionType) throws ClassNotFoundException, JMetalException {
    // 30 variables by default
    this(solutionType, 30);
  }

  /**
   * Creates a new instance of problem ZDT1.
   *
   * @param numberOfVariables Number of variables.
   * @param solutionType      The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT1(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "ZDT1";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

    // Establishes upper and lower limits for the variables
    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = 0.0;
      upperLimit[var] = 1.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else if (solutionType.compareTo("ArrayReal") == 0) {
      this.solutionType = new ArrayRealSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    XReal x = new XReal(solution);

    double[] f = new double[numberOfObjectives];
    f[0] = x.getValue(0);
    double g = this.evalG(x);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the ZDT1 function G.
   *
   * @param x Solution
   * @throws org.uma.jmetal.util.JMetalException
   */
  private double evalG(XReal x) throws JMetalException {
    double g = 0.0;
    for (int i = 1; i < x.getNumberOfDecisionVariables(); i++) {
      g += x.getValue(i);
    }
    double constant = 9.0 / (numberOfVariables - 1);
    g = constant * g;
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the ZDT1 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h ;
    h = 1.0 - java.lang.Math.sqrt(f / g);
    return h;
  }
}
