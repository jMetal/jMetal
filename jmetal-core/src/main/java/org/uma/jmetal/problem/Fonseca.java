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

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.wrapper.XReal;

/**
 * Class representing problem Fonseca
 */
public class Fonseca extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = -5170038987171863224L;

  /**
   * Constructor
   * Creates a default instance of the Fonseca problem
   *
   * @param solutionType The solutiontype type must "Real", "BinaryReal,
   *                     ArrayReal, or ArrayRealC".
   */
  public Fonseca(String solutionType) throws JMetalException {
    numberOfVariables_ = 3;
    numberOfObjectives_ = 2;
    numberOfConstraints_ = 0;
    problemName_ = "Fonseca";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++) {
      lowerLimit_[var] = -4.0;
      upperLimit_[var] = 4.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      solutionType_ = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      solutionType_ = new RealSolutionType(this);
    } else if (solutionType.compareTo("ArrayReal") == 0) {
      solutionType_ = new ArrayRealSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solutiontype
   *
   * @param solution The solutiontype to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    XReal x = new XReal(solution);

    double[] f = new double[numberOfObjectives_];
    double sum1 = 0.0;
    for (int var = 0; var < numberOfVariables_; var++) {
      sum1 += StrictMath.pow(x.getValue(var)
        - (1.0 / StrictMath.sqrt((double) numberOfVariables_)), 2.0);
    }
    double exp1 = StrictMath.exp((-1.0) * sum1);
    f[0] = 1 - exp1;

    double sum2 = 0.0;
    for (int var = 0; var < numberOfVariables_; var++) {
      sum2 += StrictMath.pow(x.getValue(var)
        + (1.0 / StrictMath.sqrt((double) numberOfVariables_)), 2.0);
    }
    double exp2 = StrictMath.exp((-1.0) * sum2);
    f[1] = 1 - exp2;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
}
