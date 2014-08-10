//  ZDT3.java
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

package org.uma.jmetal.problem.zdt;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;

/**
 * Class representing problem ZDT3
 */
public class ZDT3 extends Problem {
  private static final long serialVersionUID = -773138580076559651L;

  /**
   * Constructor.
   * Creates default instance of problem ZDT3 (30 decision variables.
   *
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT3(String solutionType) throws ClassNotFoundException, JMetalException {
    // 30 variables by default
    this(solutionType, 30);
  }

  /**
   * Constructor.
   * Creates a instance of ZDT3 problem.
   *
   * @param numberOfVariables Number of variables.
   * @param solutionType      The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT3(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "ZDT3";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

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
   * Returns the value of the ZDT2 function G.
   *
   * @param x Solution
   * @throws org.uma.jmetal.util.JMetalException
   */
  private double evalG(XReal x) throws JMetalException {
    double g = 0.0;
    for (int i = 1; i < x.getNumberOfDecisionVariables(); i++) {
      g += x.getValue(i);
    }
    double constant = (9.0 / (numberOfVariables - 1));
    g = constant * g;
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the ZDT3 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h = 0.0;
    h = 1.0 - java.lang.Math.sqrt(f / g)
      - (f / g) * java.lang.Math.sin(10.0 * java.lang.Math.PI * f);
    return h;
  }
}
