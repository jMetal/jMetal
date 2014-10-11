//  ZDT4.java
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

package org.uma.jmetal45.problem.multiobjective.zdt;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.util.JMetalException;

/**
 * Class representing problem ZDT4
 */
public class ZDT4 extends Problem {
  private static final long serialVersionUID = -8130678685721634674L;

  public ZDT4() throws JMetalException {
	  this("Real", 10);
  }

  /**
   * Constructor.
   * Creates a default instance of problem ZDT4 (10 decision variables)
   *
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT4(String solutionType) throws ClassNotFoundException, JMetalException {
    // 10 variables by default
    this(solutionType, 10);
  }

  /**
   * Creates a instance of problem ZDT4.
   *
   * @param numberOfVariables Number of variables.
   * @param solutionType      The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT4(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "ZDT4";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

    lowerLimit[0] = 0.0;
    upperLimit[0] = 1.0;
    for (int var = 1; var < this.numberOfVariables; var++) {
      lowerLimit[var] = -5.0;
      upperLimit[var] = 5.0;
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
   * Returns the value of the ZDT4 function G.
   *
   * @param x Solution
   * @throws org.uma.jmetal45.util.JMetalException
   */
  public double evalG(XReal x) throws JMetalException {
    double g = 0.0;
    for (int var = 1; var < numberOfVariables; var++) {
      g += Math.pow(x.getValue(var), 2.0) +
        -10.0 * Math.cos(4.0 * Math.PI * x.getValue(var));
    }

    double constant = 1.0 + 10.0 * (numberOfVariables - 1);
    return g + constant;
  }

  /**
   * Returns the value of the ZDT4 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    return 1.0 - Math.sqrt(f / g);
  }
}
