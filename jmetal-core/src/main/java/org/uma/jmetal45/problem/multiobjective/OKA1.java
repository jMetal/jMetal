//  OKA1.java
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

package org.uma.jmetal45.problem.multiobjective;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.util.JMetalException;

/**
 * Class representing problem OKA1
 */
public class OKA1 extends Problem {
  private static final long serialVersionUID = 7565696056543602357L;

  /**
   * Constructor.
   * Creates a new instance of the OKA2 problem.
   *
   * @param solutionType The solution type type must "Real" or "BinaryReal".
   */
  public OKA1(String solutionType) throws JMetalException {
    numberOfVariables = 2;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "OKA1";

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];

    lowerLimit[0] = 6 * Math.sin(Math.PI / 12.0);
    upperLimit[0] = 6 * Math.sin(Math.PI / 12.0) + 2 * Math.PI * Math.cos(Math.PI / 12.0);
    lowerLimit[1] = -2 * Math.PI * Math.sin(Math.PI / 12.0);
    upperLimit[1] = 6 * Math.cos(Math.PI / 12.0);

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

    double x0 = Math.cos(Math.PI / 12.0) * x[0] - Math.sin(Math.PI / 12.0) * x[1];
    double x1 = Math.sin(Math.PI / 12.0) * x[0] + Math.cos(Math.PI / 12.0) * x[1];

    fx[0] = x0;
    fx[1] = Math.sqrt(2 * Math.PI) - Math.sqrt(Math.abs(x0)) +
            2 * Math.pow(Math.abs(x1 - 3 * Math.cos(x0) - 3), 1.0 / 3.0);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }
}
