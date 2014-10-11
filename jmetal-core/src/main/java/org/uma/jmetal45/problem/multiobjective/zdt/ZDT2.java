//  ZDT2.java
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
 * Class representing problem ZDT2
 */
public class ZDT2 extends Problem {
  private static final long serialVersionUID = 469083313731522351L;

  /**
   * Constructor.
   * Creates a default instance of  problem ZDT2 (30 decision variables)
   *
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public ZDT2(String solutionType) throws ClassNotFoundException, JMetalException {
    // 30 variables by default
    this(solutionType, 30);
  }

  /**
   * Constructor.
   * Creates a new ZDT2 problem instance.
   *
   * @param numberOfVariables Number of variables
   * @param solutionType      The solution type must "Real" or "BinaryReal".
   */
  public ZDT2(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "ZDT2";

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

    double[] fx = new double[numberOfObjectives];
    fx[0] = x.getValue(0);
    double g = this.evalG(x);
    double h = this.evalH(fx[0], g);
    fx[1] = h * g;

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }

  /**
   * Returns the value of the ZDT2 function G.
   *
   * @param x Solution
   * @throws org.uma.jmetal45.util.JMetalException
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
   * Returns the value of the ZDT2 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h ;
    h = 1.0 - java.lang.Math.pow(f / g, 2.0);
    return h;
  }
}
