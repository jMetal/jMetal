//  DTLZ5.java
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

package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem DTLZ5
 */
public class DTLZ5 extends Problem {
  private static final long serialVersionUID = 6738670050602922667L;

  /**
   * Creates a default DTLZ5 problem instance (12 variables and 3 objectives)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public DTLZ5(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 12, 3);
  }

  /**
   * Creates a new DTLZ5 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   * @param solutionType       The solution type must "Real" or "BinaryReal".
   */
  public DTLZ5(String solutionType,
    Integer numberOfVariables,
    Integer numberOfObjectives) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    this.numberOfObjectives = numberOfObjectives;
    numberOfConstraints = 0;
    problemName = "DTLZ5";

    lowerLimit = new double[this.numberOfVariables];
    upperLimit = new double[this.numberOfVariables];
    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = 0.0;
      upperLimit[var] = 1.0;
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
    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];
    double[] theta = new double[numberOfObjectives - 1];
    double g = 0.0;
    int k = numberOfVariables - numberOfObjectives + 1;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
      g += (x[i] - 0.5) * (x[i] - 0.5);
    }

    double t = java.lang.Math.PI / (4.0 * (1.0 + g));

    theta[0] = x[0] * java.lang.Math.PI / 2.0;
    for (int i = 1; i < (numberOfObjectives - 1); i++) {
      theta[i] = t * (1.0 + 2.0 * g * x[i]);
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      f[i] = 1.0 + g;
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= java.lang.Math.cos(theta[j]);
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        f[i] *= java.lang.Math.sin(theta[aux]);
      }
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
