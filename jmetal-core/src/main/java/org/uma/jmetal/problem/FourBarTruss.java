//  FourBarTruss.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.wrapper.XReal;

/**
 * Class representing problem FourBarTruss
 */
public class FourBarTruss extends Problem {
  private static final long serialVersionUID = -9117986919170703133L;
  // 10kN
  double f = 10;
  // 20000 kN/cm2
  double e = 200000;
  // 200 cm
  double l = 200;
  // 10kN/cm2
  double sigma = 10;

  /**
   * Constructor
   * Creates a default instance of the FourBarTruss problem
   *
   * @param solutionType The solutiontype type must "Real" or "BinaryReal".
   */
  public FourBarTruss(String solutionType) throws JMetalException {
    numberOfVariables = 4;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "FourBarTruss";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    lowerLimit[0] = f / sigma;
    lowerLimit[1] = Math.sqrt(2.0) * (f / sigma);
    lowerLimit[2] = lowerLimit[1];
    lowerLimit[3] = lowerLimit[0];
    upperLimit[0] = 3 * (f / sigma);
    upperLimit[1] = upperLimit[0];
    upperLimit[2] = upperLimit[0];
    upperLimit[3] = upperLimit[0];

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
    XReal vars = new XReal(solution);

    double[] fx = new double[2];
    double[] x = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = vars.getValue(i);
    }

    fx[0] = l * (2 * x[0] + Math.sqrt(2.0) * x[1] + Math.sqrt(x[2]) + x[3]);
    fx[1] =
      (f * l / e) * (2 / x[0] + 2 * Math.sqrt(2) / x[1] - 2 * Math.sqrt(2) / x[2] + 2 / x[3]);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }
}
