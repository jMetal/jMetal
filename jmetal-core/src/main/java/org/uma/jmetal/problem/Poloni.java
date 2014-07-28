//  Poloni.java
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

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem Poloni. This problem has two objectives to be
 * MAXIMIZED. As jMetal always minimizes, the rule Max(f(x)) = -Min(f(-x)) must
 * be applied.
 */
public class Poloni extends Problem {
  private static final long serialVersionUID = -530548126202625574L;

  /**
   * Constructor.
   * Creates a default instance of the Poloni problem
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Poloni(String solutionType) throws JMetalException {
    numberOfVariables = 2;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "Poloni";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = -1 * Math.PI;
      upperLimit[var] = Math.PI;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    final double a1 = 0.5 * Math.sin(1.0) - 2 * Math.cos(1.0) +
      Math.sin(2.0) - 1.5 * Math.cos(2.0); //!< Constant A1
    final double a2 = 1.5 * Math.sin(1.0) - Math.cos(1.0) +
      2 * Math.sin(2.0) - 0.5 * Math.cos(2.0); //!< Constant A2

    Variable[] decisionVariables = solution.getDecisionVariables();

    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];

    x[0] = decisionVariables[0].getValue();
    x[1] = decisionVariables[1].getValue();

    double b1 = 0.5 * Math.sin(x[0]) - 2 * Math.cos(x[0]) + Math.sin(x[1]) -
      1.5 * Math.cos(x[1]);
    double b2 = 1.5 * Math.sin(x[0]) - Math.cos(x[0]) + 2 * Math.sin(x[1]) -
      0.5 * Math.cos(x[1]);

    f[0] = -(1 + Math.pow(a1 - b1, 2) + Math.pow(a2 - b2, 2));
    f[1] = -(Math.pow(x[0] + 3, 2) + Math.pow(x[1] + 1, 2));

    // The two objectives to be minimized. According to Max(f(x)) = -Min(f(-x)), 
    // they must be multiplied by -1. Consequently, the obtained solutions must
    // be also multiplied by -1 

    solution.setObjective(0, -1 * f[0]);
    solution.setObjective(1, -1 * f[1]);
  }
}

