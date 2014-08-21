//  DTLZ7.java
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

package org.uma.jmetal.problem.dtlz;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem DTLZ7
 */
public class DTLZ7 extends Problem {
  private static final long serialVersionUID = 6809131433448126086L;

  /**
   * Creates a default DTLZ7 problem instance (22 variables and 3 objectives)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public DTLZ7(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 22, 3);
  }

  /**
   * Creates a new DTLZ7 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   * @param solutionType       The solution type must "Real" or "BinaryReal".
   */
  public DTLZ7(String solutionType,
    Integer numberOfVariables,
    Integer numberOfObjectives) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    this.numberOfObjectives = numberOfObjectives;
    numberOfConstraints = 0;
    problemName = "DTLZ7";

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
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];
    int k = numberOfVariables - numberOfObjectives + 1;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    //Calculate g
    double g = 0.0;
    for (int i = this.numberOfVariables - k; i < numberOfVariables; i++) {
      g += x[i];
    }

    g = 1 + (9.0 * g) / k;

    // Calculate the value of f1,f2,f3,...,fM-1 (take acount of vectors start at 0)
    System.arraycopy(x, 0, f, 0, numberOfObjectives - 1);

    // Calculate fM
    double h = 0.0;
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      h += (f[i] / (1.0 + g)) * (1 + Math.sin(3.0 * Math.PI * f[i]));
    }

    h = numberOfObjectives - h;

    f[numberOfObjectives - 1] = (1 + g) * h;

    // Setting up the value of the objectives
    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}

