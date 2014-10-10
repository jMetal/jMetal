//  Kursawe.java
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

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem Kursawe
 */
public class Kursawe extends Problem {
  private static final long serialVersionUID = -5505939545760890022L;

  /**
   * Constructor.
   * Creates a default instance of the Kursawe problem.
   *
   * @param solutionType The solutiontype type must "Real", "BinaryReal, and "ArrayReal".
   */
  public Kursawe(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 3);
  }

  /**
   * Constructor.
   * Creates a new instance of the Kursawe problem.
   *
   * @param numberOfVariables Number of variables of the problem
   * @param solutionType      The solution type must "Real", "BinaryReal, and "ArrayReal".
   */
  public Kursawe(String solutionType, Integer numberOfVariables) throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "Kursawe";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

    for (int i = 0; i < this.numberOfVariables; i++) {
      lowerLimit[i] = -5.0;
      upperLimit[i] = 5.0;
    } // for

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else if (solutionType.compareTo("ArrayReal") == 0) {
      this.solutionType = new ArrayRealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    XReal vars = new XReal(solution);

    double aux, xi, xj;
    double[] fx = new double[2];
    double[] x = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = vars.getValue(i);
    }

    fx[0] = 0.0;
    for (int var = 0; var < numberOfVariables - 1; var++) {
      xi = x[var] * x[var];
      xj = x[var + 1] * x[var + 1];
      aux = (-0.2) * Math.sqrt(xi + xj);
      fx[0] += (-10.0) * Math.exp(aux);
    }

    fx[1] = 0.0;

    for (int var = 0; var < numberOfVariables; var++) {
      fx[1] += Math.pow(Math.abs(x[var]), 0.8) +
        5.0 * Math.sin(Math.pow(x[var], 3.0));
    }

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }
}
