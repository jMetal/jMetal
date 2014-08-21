//  OKA2.java
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem OKA2
 */
public class OKA2 extends Problem {
  private static final long serialVersionUID = -5304272499032352455L;

  /**
   * Constructor.
   * Creates a new instance of the OKA2 problem.
   *
   * @param solutionType The solution type type must "Real" or "BinaryReal".
   */
  public OKA2(String solutionType) throws JMetalException {
    numberOfVariables = 3;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "OKA2";

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];

    lowerLimit[0] = -Math.PI;
    upperLimit[0] = Math.PI;
    for (int i = 1; i < numberOfVariables; i++) {
      lowerLimit[i] = -5.0;
      upperLimit[i] = 5.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    XReal sol = new XReal(solution) ;

    double[] fx = new double[numberOfObjectives];
    double[] x = new double[numberOfVariables];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = sol.getValue(i);
    }

    fx[0] = x[0];

    fx[1] = 1 - Math.pow((x[0] + Math.PI), 2) / (4 * Math.pow(Math.PI, 2)) +
      Math.pow(Math.abs(x[1] - 5 * Math.cos(x[0])), 1.0 / 3.0) +
      Math.pow(Math.abs(x[2] - 5 * Math.sin(x[0])), 1.0 / 3.0);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }
}
