//  Schaffer.java
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
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem Schaffer
 */
public class Schaffer extends Problem {
  private static final long serialVersionUID = -2366503015218789989L;

  /**
   * Constructor.
   * Creates a default instance of problem Schaffer
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Schaffer(String solutionType) throws JMetalException {
    numberOfVariables = 1;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "Schaffer";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    lowerLimit[0] = -100000;
    upperLimit[0] = 100000;

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid") ;
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    double[] f = new double[numberOfObjectives];
    double value = XReal.getValue(solution, 0) ;
    f[0] = value * value;

    f[1] = (value - 2.0) * (value - 2.0);

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
}
