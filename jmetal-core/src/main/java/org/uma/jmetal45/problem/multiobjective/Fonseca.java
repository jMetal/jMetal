//  Fonseca.java
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
import org.uma.jmetal45.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.util.JMetalException;

/**
 * Class representing problem Fonseca
 */
public class Fonseca extends Problem {
  private static final long serialVersionUID = -5170038987171863224L;

  /**
   * Constructor
   * Creates a default instance of the Fonseca problem
   *
   * @param solutionType The solution type must "Real", "BinaryReal or ArrayReal".
   */
  public Fonseca(String solutionType) throws JMetalException {
    numberOfVariables = 3;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "Fonseca";

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = -4.0;
      upperLimit[var] = 4.0;
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
    double sum1 = 0.0;
    for (int var = 0; var < numberOfVariables; var++) {
      sum1 += StrictMath.pow(x.getValue(var)
        - (1.0 / StrictMath.sqrt((double) numberOfVariables)), 2.0);
    }
    double exp1 = StrictMath.exp((-1.0) * sum1);
    f[0] = 1 - exp1;

    double sum2 = 0.0;
    for (int var = 0; var < numberOfVariables; var++) {
      sum2 += StrictMath.pow(x.getValue(var)
        + (1.0 / StrictMath.sqrt((double) numberOfVariables)), 2.0);
    }
    double exp2 = StrictMath.exp((-1.0) * sum2);
    f[1] = 1 - exp2;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
}
